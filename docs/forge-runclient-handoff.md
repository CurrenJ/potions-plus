# Forge `runClient` — startup-blocker handoff (2026-08-30, updated)

> Status at handoff: `:forge:build` is green. `:forge:runClient` had **four** blockers. The first three (mixin, `@Mod` constructor, client wiring) were fixed earlier; the fourth (texture-atlas mip crash) is now **root-caused and fixed** in the working tree. This doc records the complete chain so the next session can verify and commit.

---

## TL;DR

The Phase 6 note (`docs/multi-loader-expansion.md`, "Forge crashed at Mixin bootstrap … `JAVA_25` not recognised … Mixin bundled inside `forge-universal.jar`") was **wrong about the mechanism**, but right that Forge never ran. The real blockers were:

| # | Blocker | Root cause | Status |
|---|---|---|---|
| 1 | `compatibility level JAVA_25 which is not recognised` | Forge dev runtime resolves `dev.architectury:mixin-patched:0.8.7.12`, whose `CompatibilityLevel` stops at `JAVA_21` | ✅ fixed |
| 2 | `NoSuchMethodException: PotionsPlusForge.<init>()` | Forge 26.1.2 injects `FMLJavaModLoadingContext`, not `FMLModContainer`, into the `@Mod` constructor | ✅ fixed |
| 3 | Client wiring silently never runs | A second `@Mod` class with the same modid is **dropped** by Forge (first-wins dedup) | ✅ fixed |
| 4 | `IllegalArgumentException: mipLevels must be at most 3 for a texture of width 5 and height 5 (asked for 5 mipLevels)` | `atlases/blocks.json` pulled vanilla 5×5 leaf particles into the **mipmapped** blocks atlas | ✅ **fixed (this session)** |

Two run modes, two different failure surfaces (important — they are **not** the same bug):

- **Command line (`./gradlew :forge:runClient`)** → atlas mip crash (blocker 4).
- **IDE run config (IntelliJ)** → `(AXFORM) Invalid access transformer line …` spam. This is **IDE-only**: the run-config doesn't carry the `architectury.naming.*` jvmArgs. Not a crash of the mod itself (see "AXFORM is IDE-only" below).

---

## Blocker 4 (FIXED) — texture-atlas mip-level crash

### What actually happens

With 1–3 fixed, the mod loads (`potionsplus` = `DONE`), the initial resource reload starts (`Packs: vanilla, mod_resources`), the atlases stitch, then:

```
java.util.concurrent.CompletionException: java.lang.IllegalArgumentException:
  mipLevels must be at most 3 for a texture of width 5 and height 5 (asked for 5 mipLevels)
    at com.mojang.blaze3d.systems.GpuDevice.verifyTextureCreationArgs(GpuDevice.java:114)
    at com.mojang.blaze3d.systems.GpuDevice.createTexture(GpuDevice.java:60)
    at net.minecraft.client.renderer.texture.TextureAtlas.uploadInitialContents(TextureAtlas.java:152)
    at net.minecraft.client.renderer.texture.TextureAtlas.upload(TextureAtlas.java:125)
    at net.minecraft.client.resources.model.sprite.AtlasManager$PendingStitch.joinAndUpload(AtlasManager.java:168)
```

Crash report (pre-fix): `forge/run/crash-reports/crash-2026-08-30_15.22.41-client.txt`.

### Root cause (verified)

`common/src/main/resources/assets/minecraft/atlases/blocks.json` added two **`directory`** sprite sources to the **blocks** atlas:

```json
{ "type": "directory", "source": "mob_effect", "prefix": "mob_effect/" },
{ "type": "directory", "source": "particle",  "prefix": "particle/"  }
```

The `directory` source (`DirectoryLister` → `FileToIdConverter("textures/<source>", ".png")` → `MultiPackResourceManager.listResources`) lists **every namespace's** `textures/<source>/` folder — not just the mod's. So the `particle/` source dragged **vanilla's** particle textures into the blocks atlas, including (verified by scanning `minecraft-client.jar` for 26.1.2):

- `leaf_0`…`leaf_11` — **5×5**
- `pale_oak_0`…`pale_oak_11` — **5×5**
- `raid_omen`, `trial_omen` — **5×5**
- `cherry_0`…`cherry_11` — **3×3**
- plus ~200 more 8×8 / 16×16 / 32×32 particles

The **blocks** atlas is the one atlas that's mipmapped at the user's mipmap setting (mip 4 → 5 mip levels; the log shows `Created: 2048x2048x4 minecraft:textures/atlas/blocks.png-atlas`). `GpuDevice.verifyTextureCreationArgs` computes `maxMipLevels = floor(log2(min(w,h))) + 1`; for a 5×5 sprite that's **3**, so asking for 5 throws. (8×8 would also throw at mip 4 — max 4 — so any sub-16 particle in that atlas is fatal, not just the 5×5.)

Vanilla never hits this because vanilla keeps `particle/` in the **particles** atlas and `mob_effect/` in the **gui** atlas — both of which are mip 0 (unmipmapped). The `blocks.json` file was a 1.18.2→26.1.2 port leftover: in 1.18.2 item and block models shared one mipmapped atlas; in 26.1.2 they're split into `blocks` (mip 4) and `items` (mip 0).

### The fix

1. **Deleted** `assets/minecraft/atlases/blocks.json` — potions-plus block textures are already pulled by vanilla's own `blocks.json` `block/` source; a custom blocks atlas is unnecessary.
2. **Created** `assets/minecraft/atlases/items.json` (the **mip-0** item atlas) carrying the sources the item models actually need:
   - `mob_effect/` directory source — `ItemOverrideUtility` (line 78) builds item models whose `layer0` is `potionsplus:mob_effect/<effect>`.
   - four `single` sources for `minecraft:particle/sga_a/b/c/d` — the only particle textures referenced by generated item models (`generic_icon_sga_*.json`), instead of the old broad `particle/` directory (which is what pulled the 5×5 leaves).

Net effect: the mipmapped blocks atlas no longer contains any sub-16 texture; the small textures live in the unmipmapped items atlas where they belong.

---

## AXFORM errors are IDE-only (do not chase as a mod bug)

The `(AXFORM) Invalid access transformer line in forge-universal.jar: …` spam (hundreds of lines, every Forge AT) appears **only** when launching from the IDE run config, and the run stalls right after. From the command line the same run sails past AXFORM to the atlas stage.

Why: `forge/build.gradle` wires the naming-service properties through

```gradle
tasks.withType(JavaExec).matching { it.name.startsWith('run') }.configureEach {
    dependsOn('generateEmptyMappings')
    dependsOn(configurations.common)
    jvmArgs('-Darchitectury.naming.sourceNamespace=official',
            "-Darchitectury.naming.mappingsPath=${emptyMappings.get().asFile.absolutePath}")
}
```

`tasks.withType(JavaExec)` only affects Gradle-launched JVMs. Loom's IntelliJ run configuration (generated by `platformSetupLoomIde()`) does **not** inherit these jvmArgs, so `ArchitecturyNamingService` (the "srg" mapping provider in the loom-no-remap dev runtime) throws `Missing required system property` for every AT line, surfaced as `Invalid access transformer line`.

**Fix direction (not yet applied):** add the same vm args inside the `loom { runs { client { … } } }` block so Loom bakes them into the generated IDE run config (loom run configs accept `vmArg`/`vmArgs`). Confirm the exact method against the loom version in use. Command-line runs already work, so this is a developer-QoL fix, not a correctness fix.

---

## Blocker 1 — Mixin is `mixin-patched` (JAVA_21 max), not "Mixin bundled in the Forge jar"

### What actually happens

Forge's dev runtime does **not** use `org.spongepowered:mixin` bundled inside `forge-universal.jar` (that jar contains no Mixin classes at all). Loom resolves the Mixin from Forge's **userdev `config.json` `libraries` list**, which pins `org.spongepowered:mixin:0.8.7`, and then **swaps it to `dev.architectury:mixin-patched`** because loom's `useCustomMixin` property **defaults to `true`**.

Verified in `architectury-loom-1.17.491`:

- `ForgeLibrariesProvider.provide(...)` — iterates `UserdevConfig.libraries()`; when an entry `startsWith("org.spongepowered:mixin:")` **and** `ForgeExtensionAPI.getUseCustomMixin()` is `true`, it rewrites the coordinate to `dev.architectury:mixin-patched:0.8.7.+`.
- `ForgeExtensionImpl` — `useCustomMixin` is a `Property<Boolean>` with `convention(true)`.
- Runtime log confirms it: `SpongePowered MIXIN Subsystem Version=0.8.7 Source=…/dev.architectury/mixin-patched/0.8.7.12/…`.

### Why it breaks

`mixin-patched:0.8.7.12` is built on Mixin `0.8.7`, and its `MixinEnvironment$CompatibilityLevel` enum contains **only `JAVA_6` … `JAVA_21`** — no `JAVA_22`…`JAVA_25`. Every mixin config in this repo declares `"compatibilityLevel": "JAVA_25"` (required for MC 26.1.2 per `modding-guide/07-mixins.md`).

### The fix

`forge/build.gradle` carries a `configurations.all { resolutionStrategy.eachDependency { … } }` block that substitutes either mixin coordinate for `net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7` (the exact Mixin NeoForge 26.1.2 resolves; `CompatibilityLevel` up to `JAVA_25`).

---

## Blocker 2 — `@Mod` constructor must take `FMLJavaModLoadingContext`

Forge 26.1.2 `FMLModContainer.constructMod()`: tries `getDeclaredConstructor(FMLJavaModLoadingContext.class)`, falls back to no-arg, never `FMLModContainer`. Fix: `PotionsPlusForge(FMLJavaModLoadingContext)`, `context.getModBusGroup()`, `context.registerConfig(...)`.

---

## Blocker 3 — a second `@Mod` class with the same modid is silently dropped

Forge de-duplicates `@Mod` classes by modid (`Collectors.toMap(FMLModTarget::modId, identity, (a,b) -> a)` — first wins). `PotionsPlusForgeClient` was never constructed, so its `FMLClientSetupEvent` listener (which called `ForgeClientEventListeners.register()` and registered the item-tint source) never ran. Fix: moved that wiring into `Renderers.onClientSetup(FMLClientSetupEvent)`, deleted `PotionsPlusForgeClient.java`.

---

## Uncommitted changes (working tree)

```
 M forge/build.gradle                                               # Blocker 1 (mixin swap)
 M forge/src/main/java/grill24/potionsplus/core/forge/PotionsPlusForge.java   # Blocker 2
 M forge/src/main/java/grill24/potionsplus/core/forge/Renderers.java           # Blocker 3 (onClientSetup)
 D forge/src/main/java/grill24/potionsplus/core/forge/PotionsPlusForgeClient.java  # Blocker 3 (deleted)
 D common/src/main/resources/assets/minecraft/atlases/blocks.json               # Blocker 4 (deleted)
?? common/src/main/resources/assets/minecraft/atlases/items.json               # Blocker 4 (added)
```

---

## Verified API facts (do not re-derive)

- **Forge Mixin source**: `dev.architectury:mixin-patched` (via loom `useCustomMixin`, default `true`), **not** anything in `forge-universal.jar`. `mixin-patched:0.8.7.12` = `JAVA_21` max; `net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7` = `JAVA_25`.
- **Forge `@Mod` ctor injection**: `FMLJavaModLoadingContext` (or no-arg), never `FMLModContainer`.
- **Forge `@Mod` dedup**: first `@Mod` class per modid wins; extra ones are dropped silently.
- **Atlas sprite sources** (`SpriteSources`): `single`, `directory`, `filter`, `unstitch`, `paletted_permutations`. The `type` field is an `Identifier` — unqualified (`"directory"`) resolves to `minecraft:directory`.
- **`directory` source pulls every namespace**: `DirectoryLister` → `MultiPackResourceManager.listResources` iterates all `namespacedManagers`, so `source: "particle"` includes `minecraft:…/particle/*` as well as the mod's own.
- **`single` source**: `{"type":"single","resource":"<ns>:<path>"}` adds one sprite whose id defaults to `<ns>:<path>`.
- **Vanilla 26.1.2 atlas layout** (from `minecraft-client.jar` `assets/minecraft/atlases/*.json`): `blocks` = `block/` + `entity/conduit` + 2 singles (**mip 4**); `items` = `item/` + trims (**mip 0**); `particles` = `particle/` (**mip 0**); `gui` = `gui/sprites` + `mob_effect/` (**mip 0**). Only `blocks` is mipmapped.
- **`GpuDevice.verifyTextureCreationArgs`**: `maxMipLevels = floor(log2(min(w,h))) + 1`. mip 4 = 5 mip levels ⇒ any texture smaller than 16×16 in a mip-4 atlas throws.
- **Config wiring**: `runtimeClasspath extendsFrom forgeRuntimeLibrary extendsFrom forgeDependencies`; Loom puts userdev `libraries` into `forgeDependencies` with `transitive=false`.
- The AXFORM `"Invalid access transformer line"` errors (and the `ArchitecturyMixinRemapperInjector` NPE "failed to inject our remapper") are **non-fatal noise** in the loom-no-remap environment — the command-line run proceeds past them. The **IDE** run config is a different story (missing naming jvmArgs, see above).

---

## Next steps (in order)

1. **✅ Verified `:forge:runClient` reaches the main menu** (command line). The atlas crash is gone — the log shows `Created: 2048x2048x4 blocks.png-atlas` followed by `chest`, `celestials`, `banner_patterns`, `beds`, `items`, `gui`, `map_decorations`, `signs`, `shulker_boxes` all stitching cleanly, and no new crash report. Still to confirm on a real interactive run: the item-tint source / tooltips / particle providers actually wired up (Blocker 3 means that path has only just started executing).
2. **Confirm `:neoforge:runClient` is still green** — the earlier handoff noted a possible NeoForge regression that was never re-verified; the blocks→items atlas move affects all three loaders, so sanity-check NeoForge and Fabric too (they share `common/`).
3. **(Optional) Fix the IDE AXFORM path** — move the `architectury.naming.*` vm args into the `loom { runs { … } }` block so the IntelliJ run config carries them.
4. Commit the four fixes + the atlas change, then correct `docs/multi-loader-expansion.md`: the Phase 6 note's "Mixin bundled inside forge-universal.jar" claim is wrong ("mixin-patched via loom useCustomMixin"), and the Phase 3/8 `PotionsPlusForgeClient` references are obsolete.

### Minor follow-up (non-fatal): duplicate-sprite warnings

The `mob_effect/` directory source in the new `items.json` intentionally overlaps vanilla's `gui.json` `mob_effect/` source (both pull every namespace's `textures/mob_effect/`). Result: the log fills with `Duplicate sprite minecraft:mob_effect/X … already defined in atlas gui/items` warnings — harmless today, but vanilla logs "This will be rejected in a future version". If the noise matters, the clean fix is to replace the `mob_effect/` **directory** source in `items.json` with per-sprite `single` sources for the ~28 `potionsplus:mob_effect/*` entries that `ItemOverrideUtility` actually references (a `filter` source won't do — it operates on the whole atlas, so `namespace: minecraft` would strip vanilla `item/` too). The `minecraft:mob_effect/*` (18×18) entries are pulled in needlessly and are the bulk of the noise.

### Flag worth a follow-up (not blocking)

`SpriteLoader.stitch` *should* have dropped the blocks atlas's mip level when it saw the 5×5 leaf (its `Integer.lowestOneBit(5)==1` → mip 0), yet the blocks atlas stayed at mip 4 (the log's only `limits mip level` warning is for the *particles* atlas). Not chased to the bottom because the fix removes the offending textures entirely, but if a future loader re-introduces sub-16 textures into a mipmapped atlas, worth understanding why the drop didn't fire in the Forge runtime.
