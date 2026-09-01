# Forge production build — recipe-mixin handoff (2026-08-31)

> **RESOLVED (same session).** Root cause found and fix applied — see the **"RESOLVED — root cause + fix"** section immediately below. The rest of this doc is retained as the historical evidence chain that led to it.
>
> Status at handoff: NeoForge production (packaged jar, real CurseForge install) is **fixed and confirmed working**. Forge production is **still broken** — same original symptom, but the leading theory that explained NeoForge does not explain Forge. This doc records the full evidence chain so the next session doesn't re-walk it.

---

## RESOLVED — root cause + fix

**Root cause:** production Forge (`forge_version = 64.1.0`) ships **stock `org.spongepowered:mixin:0.8.7`** — resolved from the installer's `version.json` (`net.minecraftforge:forge:26.1.2-64.1.0` installer → `version.json` libraries list, sha1 `8ab114ac385e6dbdad5efafe28aba4df8120915f`). That Mixin build's `MixinEnvironment$CompatibilityLevel` enum contains **only `JAVA_6`…`JAVA_21`** — no `JAVA_22`…`JAVA_25`. Every mixin config in this repo declared `"compatibilityLevel": "JAVA_25"`.

When stock 0.8.7 loads such a config, `MixinConfig.initCompatibilityLevel()` (verified by decompiling the jar) does:

```java
try {
    this.compatibilityLevel = CompatibilityLevel.valueOf("JAVA_25");   // throws IllegalArgumentException
} catch (IllegalArgumentException e) {
    throw new MixinInitialisationError(
        "Mixin config %s specifies compatibility level %s which is not recognised", name, "JAVA_25");
}
```

`MixinInitialisationError` aborts the config's `onLoad()`, so the config is **rejected** and every mixin in it is silently dropped. Forge's production Mixin bootstrap catches config-load errors (unlike the dev `--mixin.config` path, which propagates them into a crash — that's the "compatibility level JAVA_25 which is not recognised" crash documented in `forge-runclient-handoff.md` Blocker 1). This is why production Forge showed "registered but inert": the config *name* is registered (hence the duplicate-registration crash when the manifest was added), but its mixins never apply.

**Why dev + NeoForge worked:** both use `net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7` (Fabric's JAVA_25-aware fork), whose enum runs `JAVA_6`…`JAVA_25`. The Gradle `resolutionStrategy` swap in `forge/build.gradle` only rewrites the *dev* classpath; it cannot touch production, because production's Mixin coordinate is fixed in Forge's shipped `version.json`, outside the mod's control entirely.

**There are TWO independent root causes, both required to be fixed.** The `JAVA_25` rejection (above) was the first; removing it let the config *load* if discovered, but a redeploy then still showed `Loaded 1515 recipes`. The second is **config discovery**:

**Second root cause — configs never discovered in production.** Forge 64.1.0 has *no* mixin-config delivery of its own: `net.minecraftforge.fml.loading.targets.CommonLaunchHandler` has no `getMixinConfigs()` (decompiled), and no class in `fmlloader`/`fmlcore`/`forge` references the mixin table. Mixin's own platform-agent path is also dead in this environment — `MixinServiceModLauncher.getPlatformAgents()` returns only `MixinPlatformAgentMinecraftForge`, whose `accept()` returns `REJECTED` unconditionally and `getMixinContainers()` returns `null`. So `mods.toml` `[[mixins]]` is **ignored** in production, and the only remaining discovery path is the **`MixinConfigs` manifest attribute** (read by `MixinContainer`, per the SpongePowered wiki "Mixins on Minecraft Forge"). The shipped jar never set that attribute, so Mixin discovered zero configs. This is also what the earlier manifest experiment actually demonstrated: adding `MixinConfigs` made Mixin *find* the configs for the first time, and the crash was the `JAVA_25` rejection (which the first fix now removes) — not "duplicate registration".

**Complete fix applied (both parts):**
1. `"compatibilityLevel": "JAVA_25"` → `"JAVA_21"` in all four configs (`common`, `forge`, `fabric`, `neoforge`). `JAVA_21` is the highest level stock 0.8.7 recognizes; `sponge-mixin` accepts it with no downgrade (its `setCompatibilityLevel` only raises — verified in bytecode).
2. `forge/build.gradle` `jar { manifest { attributes 'MixinConfigs': 'potionsplus.mixins.json,potionsplus.forge.mixins.json' } }` — lands in the `shadowJar` manifest via the already-working `from(zipTree(jar.archiveFile))` merge path. Verified the built `potionsplus-forge-2.0.0b1+26.1.2.jar` manifest now carries `MixinConfigs: potionsplus.mixins.json,potionsplus.forge.mixins.json` alongside the existing `Specification-Title`/`Implementation-Title` attrs.

**Verification status: ✅ CONFIRMED WORKING** (2026-08-31, `PP2 26.1.2 Forge` production log at 19:08):

```
[main/WARN]: Compatibility level JAVA_21 specified by potionsplus.mixins.json is higher than the maximum level supported by this version of mixin (JAVA_13).   <- cosmetic, expected (MAX_SUPPORTED=JAVA_13 in 0.8.7)
[main/INFO]: Compatibility level set to JAVA_21
...
[Render thread/INFO]: Loaded 1515 recipes                                    <- vanilla, pre-injection
[Worker-Main-21/INFO]: Injected 450 runtime recipes for type: brewing_cauldron_recipe
[Worker-Main-21/INFO]: Injected 48 runtime recipes for type: sanguine_altar_recipe
[Worker-Main-21/INFO]: Total recipes injected: 498
[Server thread/INFO]: Loaded 2013 recipes                                     <- 1515 + 498 ✓ (dev showed 494; the small delta is runtime data, not a bug)
```

The `No recipe map available to sync` warning is gone. The single `Compatibility level JAVA_21 … higher than … (JAVA_13)` WARN is the harmless, predicted one (stock 0.8.7's `MAX_SUPPORTED` is `JAVA_13`; `JAVA_14`–`JAVA_21` are experimental-but-functional). User confirmed in-game behavior is correct.

---

## TL;DR

**Symptom** (as originally reported): in a packaged, non-dev install, the Abyssal Trove block shows no known ingredients and item tooltips show no brewing-cauldron ingredient hints. Root cause: `Recipes.recipes` (`common/src/main/java/grill24/potionsplus/core/Recipes.java`) — and the `*_ANALYSIS` statics derived from it — never get populated, because `RecipeManagerMixin` (`common/src/main/java/grill24/potionsplus/mixin/RecipeManagerMixin.java`, injects into `RecipeManager.prepare()`) never runs its injected code on a packaged/production install.

| Loader | Status | Root cause |
|---|---|---|
| NeoForge (packaged, `PP2 26.1.2 NeoForge` CurseForge instance) | ✅ **fixed**, confirmed by user | Missing `potionsplus.refmap.json` (declared in every `*.mixins.json` but never generated — no Mixin annotation processor configured anywhere in the build). NeoForge's production Mixin service explicitly logs this as unsafe outside dev; fix was to stop declaring a refmap that's never produced. |
| Forge (packaged, `PP2 26.1.2 Forge` CurseForge instance) | ❌ **still broken** | Unknown. The refmap fix did nothing for Forge (its plain Sponge Mixin build never even warned about the missing refmap). Confirmed the mixin *config* registers correctly in production (see the crash below) but the actual `@Inject` callback bodies never execute — no crash, no log line, nothing. |

Both loaders' **dev** environments (`:forge:runGametest`, `:neoforge:runGametest`/dev logs) work correctly and always have — this is exclusively a packaged/production-build problem.

---

## Fix applied (NeoForge) — keep this

Removed the dangling `"refmap": "potionsplus.refmap.json"` line from all four mixin configs:

- `common/src/main/resources/potionsplus.mixins.json`
- `fabric/src/main/resources/potionsplus.fabric.mixins.json`
- `forge/src/main/resources/potionsplus.forge.mixins.json`
- `neoforge/src/main/resources/potionsplus.neoforge.mixins.json`

### Evidence

- `grep`-ing every `build.gradle` in the repo for `refmap`/mixin-AP config returns nothing — no annotation processor is wired up anywhere, so `potionsplus.refmap.json` is never generated (confirmed absent from every build output directory, under every name, for both `common` and each platform module).
- NeoForge's packaged-jar `latest.log` (`PP2 26.1.2 NeoForge` instance) showed, right at mixin bootstrap:
  ```
  [main/WARN]: Reference map 'potionsplus.refmap.json' for potionsplus.mixins.json could not be read. If this is a development environment you can ignore this message
  [main/WARN]: Reference map 'potionsplus.refmap.json' for potionsplus.neoforge.mixins.json could not be read. If this is a development environment you can ignore this message
  ```
  NeoForge's production Mixin service is `net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7` (`Service=FML`) — it explicitly distinguishes dev vs. production tolerance for a missing refmap.
- Since this project uses official (unobfuscated) Mojang mappings everywhere, a refmap was never actually needed — Mixin resolves `@Inject` targets directly against literal names just fine without one, as proven by every dev run.
- After removing the `refmap` line and redeploying: **user confirmed** NeoForge production now loads recipes correctly (Abyssal Trove populated, tooltips work).

### Why this didn't touch Forge

Forge's packaged-jar `latest.log` (`PP2 26.1.2 Forge` instance) **never showed the "Reference map … could not be read" warning at all**, before or after the fix — confirmed via `grep -n "Reference map\|refmap"` returning zero matches across every test run. Forge's production Mixin service is the plain `org.spongepowered:mixin:0.8.7` (`Service=ModLauncher`), a different distribution than NeoForge's, and it apparently tolerates (or silently ignores) a missing refmap without logging anything — so the refmap was never Forge's problem to begin with.

---

## Forge — what's confirmed, what's ruled out

### The symptom, precisely

In the packaged Forge jar (`PP2 26.1.2 Forge` instance), across every relaunch:

```
[Render thread/INFO]: Loaded 1515 recipes      <- vanilla-only count, no seeded recipes added
[Server thread/INFO]: Loaded 1515 recipes
[Server thread/WARN]: No recipe map available to sync to client - runtime recipes have not been injected yet.
```

`RecipeManagerMixin`'s injected code (`common/src/main/java/grill24/potionsplus/mixin/RecipeManagerMixin.java:27-58`) never logs *either* of its two branches — not `"Total recipes injected: {}"` (success) nor `"MinecraftServer not ready, can't inject seeded runtime recipes."` (its own fallback). Both are plain `PotionsPlus.LOGGER.info/warn` calls, unconditional once the method starts executing. Their total absence — confirmed even after adding an **unconditional** log statement as the very first line of the method, before any branch — means the injected bytecode itself never runs, not merely "runs but sees empty/null state."

A second, independent probe (plain `System.out.println`, no dependency on any shared state) was added to `common/src/main/java/grill24/potionsplus/mixin/BootstrapMixin.java`'s `potions_plus$bootStrapCauldronInteractions()` — a completely different `@Mixin` target class (`Bootstrap.bootStrap()`), fires once, very early, unconditionally. **Also never printed**, across 5+ separate relaunches. This rules out anything specific to `RecipeManager`/timing/`ModState` — it's the whole `common` mixin config (or at least these two mixins in it) that's silently inert in this loader's production install.

*(Both probes have since been reverted — see `common/src/main/java/grill24/potionsplus/mixin/RecipeManagerMixin.java` and `BootstrapMixin.java`, which are back to their pre-investigation state.)*

### Ruled out

1. **Async exception silently swallowed.** `injectRuntimeRecipes()` (`forge/src/main/java/grill24/potionsplus/core/forge/ServerLifecycleListeners.java`) calls `server.reloadResources(selectedIds)`, which returns a `CompletableFuture<Void>` that was previously discarded unobserved. Added `.exceptionally(...)` logging (kept in the working tree — see current diff) and redeployed: **no error was ever logged**. Ruled out.

2. **Mixin config never discovered/registered by FML in production.** Tested by adding a `MixinConfigs: potionsplus.mixins.json,potionsplus.forge.mixins.json` manifest attribute to the packaged jar (`forge/build.gradle`, `jar { manifest { attributes([...]) } }`) — the exact mechanism this project's own dev environment already relies on for the common config (see `forge/build.gradle` around the `loom.runs.configureEach` block, ~line 250-261, and its comment on `MixinPlatformAgentMinecraftForge`). This **crashed the game at startup** — log cuts off immediately after mod discovery, before "Preparing mixins", with no crash report generated (the failure happens too early in FML's own bootstrap for Minecraft's crash-report machinery to engage) and no exception text landed in `latest.log` either.

   This crash is itself the important finding: it strongly implies FML/Mixin already knew about both configs (from `mods.toml`'s `[[mixins]]` entries) and rejected the second, duplicate registration attempt from the manifest. **This means `mods.toml`'s `[[mixins]]` entries ARE being read and registered on real, packaged Forge** — contradicting the naive theory that the config simply never loads. The manifest change was reverted immediately (`forge/build.gradle` is back to its original `jar {}` block, no manifest attribute) to restore a bootable state.

3. **Static-field / classloader duplication** (`ModState.SERVER` set by one copy of the class, read as null by another). Ruled out by the same unconditional-probe evidence above — if this were the issue, the probes would still print (just observing `SERVER=null`), not stay completely silent.

4. **Verbose Mixin debug logging.** Set `-Dmixin.debug.verbose=true -Dmixin.debug.export=true` via CurseForge's "Additional Java Arguments". Confirmed via `Get-CimInstance Win32_Process` (PowerShell) that the flags **did** reach the live `java.exe` process's actual command line. Produced **zero** additional log output and no `.mixin.out` export directory anywhere on disk. Root cause: Forge's client log4j config (`-Dlog4j.configurationFile=...\assets\log_configs\client-1.21.2.xml`, a Mojang-shipped config CurseForge points at) filters everything below `INFO` — confirmed zero `DEBUG`-level lines anywhere in `latest.log`, and this CurseForge instance has no separate `debug.log` at all (unlike this project's own dev run configs, e.g. `forge/run/logs/debug.log`, which do capture `DEBUG`). **This diagnostic path is a dead end** without editing Forge's log4j XML, which was deliberately avoided as launcher-owned/out-of-repo config.

### Confirmed working (for contrast)

- `:forge:runGametest` (dev, headless game-test server) — `RecipeManagerMixin` fires correctly:
  ```
  forge/run/logs/debug.log:
  [Worker-Main-23/INFO] [grill24.potionsplus.core.PotionsPlus/]: Injected 448 runtime recipes for type: brewing_cauldron_recipe
  [Worker-Main-23/INFO] [grill24.potionsplus.core.PotionsPlus/]: Injected 46 runtime recipes for type: sanguine_altar_recipe
  [Worker-Main-23/INFO] [grill24.potionsplus.core.PotionsPlus/]: Total recipes injected: 494
  [Server thread/INFO] [net.minecraft.world.item.crafting.RecipeManager/]: Loaded 2009 recipes   (1515 + 494, checks out)
  ```
  This dev run's common-mixin-config delivery is a *different* mechanism than production: per the `loom.runs.configureEach` comment in `forge/build.gradle`, `potionsplus.mixins.json` reaches the dev environment via "the Architectury transformer reading the `MixinConfigs` manifest attribute of the transformed `:common` dev jar" — **not** via `mods.toml`. So dev success proves the mixin *code* is correct; it does not validate the production `mods.toml`-based delivery path at all.
- `mods.toml` inside the shipped jar correctly declares both configs (verified via `unzip -p ... META-INF/mods.toml`):
  ```toml
  [[mixins]]
  config = "potionsplus.mixins.json"

  [[mixins]]
  config = "potionsplus.forge.mixins.json"
  ```
- The shipped jar's class bytecode was verified (via `strings` on extracted `.class` files) to actually contain whatever source changes were most recently built — ruled out "stale jar" as an explanation for any test result in this investigation.
- Sponge Mixin subsystem itself loads without any hard error in every test — the game boots to main menu / joins a world fine (aside from the one self-inflicted manifest-duplicate-config crash, already reverted).

---

## Open mystery

The mixin config is registered (proven by the duplicate-registration crash), Mixin doesn't hard-fail (`"required": true` / `"injectors": {"defaultRequire": 1}` would normally throw on a genuinely-missing injection point, and it doesn't), yet the actual `@Inject` bodies inside `RecipeManagerMixin` and `BootstrapMixin` never execute. Something between "config registered with Mixin" and "mixin actually applied to + invoked on the loaded target class" is silently failing, specific to:

- **real, non-NeoForge Forge** (`net.minecraftforge`, this repo's `forge` module, `forge_version = 64.1.0`) — NeoForge production now works.
- **packaged/production installs specifically** (mods-folder locator) — this exact loader's own **dev** environment works fine for the same mixins.

Not yet tested: whether `potionsplus.forge.mixins.json`'s own mixins (`RegistryMixin`, `EnchantmentHelperMixin`, `BucketItemMixin`, etc. — declared "not optional" per the `forge/build.gradle` comment near the dev-run workaround) are *also* silently inert in this same production run. That's the single highest-value next diagnostic:

- **If they also fail** → this is a project/config-wide production-Forge mixin-application bug, unrelated to `RecipeManager`/`Bootstrap` specifically.
- **If they work fine** → the bug is isolated to the `common` config, or to these two particular target classes (`RecipeManager`, `Bootstrap` — both vanilla, both loaded relatively late/early respectively; worth checking whether *any* `common`-config mixin touching a vanilla class works in production Forge, e.g. `ItemMixin`'s tooltip-image override, which is easy to test live by hovering a potion item — no rebuild required, just play the current jar).

---

## Suggested next steps (in order)

1. **Test whether `potionsplus.forge.mixins.json` (loader-specific config) also silently fails in production.** Add the same kind of unconditional, always-fire probe to one of its mixins (e.g. `RegistryMixin`), rebuild `:forge:shadowJar`, redeploy to `PP2 26.1.2 Forge/mods`, relaunch, check `latest.log`. This is the fastest way to narrow "config-wide" vs. "common-config-only".
2. **Play-test an existing common-config mixin with an observable in-game effect**, no rebuild needed: hover a potion item in the current jar and see whether `ItemMixin.getTooltipImage` (`common/src/main/java/grill24/potionsplus/mixin/ItemMixin.java:31`) shows *anything* different from vanilla (it may legitimately show nothing since `Recipes.*_ANALYSIS` is empty — but if it throws, silently no-ops differently than expected, or otherwise misbehaves, that's a live signal without touching the build).
3. **Compare the actual Mixin distribution resolved by the production Forge dependency** (`net.minecraftforge:forge:26.1.2-64.1.0`, an MDK-style dependency, not loom's dev-only `useCustomMixin`/`mixin-patched` swap documented in `docs/forge-runclient-handoff.md` Blocker 1) against what dev actually uses. The production jar's Sponge Mixin banner in `latest.log` says `org.spongepowered:mixin:0.8.7` (`Service=ModLauncher`) — confirm this is genuinely the same functional behavior as `mixin-patched:0.8.7.12` (`Service=ModLauncher` too, per the other handoff doc) or a meaningfully different build. Two different `useCustomMixin` code paths (dev swap vs. real MDK resolution) is exactly the kind of divergence that could explain "works in dev, not in production, same loader."
4. **If stuck, get a real DEBUG-level log from the CurseForge launch** without editing Forge's log4j XML directly: try `-Dlog4j2.debug=true` (prints Log4j's own internal config resolution to stderr — may reveal a duplicate/overriding log4j config), or determine whether CurseForge's "Additional Java Arguments" field appends *before* or *after* CurseForge's own `-Dlog4j.configurationFile=...` argument (last `-D` for the same key generally wins in the JVM) — if ours can be made to land last, pointing it at this project's own working `forge/run/log4j2.xml`-equivalent (which does produce a `debug.log` in dev) would unblock verbose Mixin logging entirely.
5. Keep in mind this "Forge" is a fictional/hypothetical continuation of MinecraftForge far beyond any real released version (`forge_version = 64.1.0` for MC 26.1.2) — if the above narrows this down to "Mixin config discovery is genuinely broken for packaged mods-folder installs on this specific Forge build, unrelated to anything this mod's code controls," that's a legitimate conclusion to land on, not a sign more code-level chasing will find a fix.

---

## Current working-tree state (as of this handoff)

```
 M common/src/main/resources/potionsplus.mixins.json                          # refmap removed (NeoForge fix) + compatibilityLevel JAVA_25->JAVA_21 (Forge fix)
 M fabric/src/main/resources/potionsplus.fabric.mixins.json                   # refmap removed + JAVA_25->JAVA_21 (consistency)
 M forge/src/main/resources/potionsplus.forge.mixins.json                     # refmap removed + JAVA_25->JAVA_21 (Forge fix)
 M neoforge/src/main/resources/potionsplus.neoforge.mixins.json               # refmap removed + JAVA_25->JAVA_21 (consistency)
 M forge/build.gradle                                                          # MixinConfigs manifest attribute + updated mixin-swap comment
?? docs/forge-production-mixin-handoff.md                                      # this doc
```

All diagnostic probes have been cleaned up: `BootstrapMixin` println, `RecipeManagerMixin` unconditional log, the `.exceptionally()` logging on `reloadResources()` (the "async exception swallowed" theory — ruled out), and the original manifest-attribute experiment (now superseded by the real `MixinConfigs` fix). The tree contains only the two-part production fix plus this doc. Nothing has been committed.
