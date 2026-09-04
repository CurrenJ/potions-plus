# Potions Plus (1.21.1) — Multi-loader expansion (NeoForge + Fabric + Forge)

> Living plan + progress tracker. Tick boxes as phases complete. Update the **Status** table and
> **Progress log** at the bottom each session so we can pick up where we left off.
>
> Sibling document: `docs/multi-loader-expansion.md` on `dev/26.1.2/multi-loader-expansion` — the
> same job, already done, on MC 26.1.2. **Read it first.** This plan deliberately mirrors its
> structure, phase names and decisions so the two branches stay diffable (Decision 4).

## Context

`mc/1.21.1` ships **NeoForge-only** as a single flat module built with **NeoForged ModDevGradle**.
The goal is the same three loaders the 26.1.2 branch reached — NeoForge + Fabric + **regular
MinecraftForge** — with full feature parity, JEI everywhere it exists, and NeoForge datagen as the
source of truth.

### How this differs from 26.1.2's starting point

This is the load-bearing section. The 26.1.2 plan opened with "the codebase is already most of the
way there." **1.21.1 is not.** The 26.1.2 branch had already absorbed the Architectury conversion
as part of its 1.21.5 → 26.1.2 port (workstream B in `2_0_backport_plan.txt`, explicitly marked
*out of scope* for the backport). That conversion has to happen here, from scratch, before any of
the 26.1.2 phase structure applies.

| | 26.1.2 at plan start | 1.21.1 today (tip `b4fc36b`) |
|---|---|---|
| Build system | `architectury-loom-no-remap` + `architectury-plugin` wired | **`net.neoforged.moddev` 1.0.17** — full build-system swap needed |
| Module layout | `common/` + `neoforge/` split done | **single flat `neoforge/`**, 294 `.java` files, no `common/` |
| Platform abstraction | `platform/Platform.java` (7 `@ExpectPlatform`) + `PacketNetwork.java` (5) | **none** — no `platform/` package exists |
| Registration | every hub already `BiFunction<String, Supplier<T>, Holder<T>>` | **partial** — `core/blocks/*` + `core/items/*` yes; `Blocks`, `Items`, `Potions`, `MobEffects` hold `DeferredRegister` statics directly |
| Event coupling | ~16 listener classes, already isolated in `event/neoforge/` | **36 `@EventBusSubscriber` classes spread through gameplay code** — 7 `effect/*Effect.java`, tooltips, tick handlers, `ServerPlayerUtility` |
| Obfuscation | MC 26.1+ is unobfuscated → `loom-no-remap`, no mappings, no refmaps | **1.21.1 is obfuscated** → real `dev.architectury.loom`, `officialMojangMappings()`, mixin **refmaps**, a `remapJar` step, Forge SRG concerns |
| Java | 25 | **21** |

Net effect: **three new front-loaded phases (0–2)** that 26.1.2 never needed, after which phases
3–13 are a renumbered mirror of 26.1.2's phases 0–10, with per-version API deltas called out.

### Where 1.21.1 is *easier* than 26.1.2

Not everything is worse. Several 26.1.2 workarounds are unnecessary here (all verified below):

- `BlockEntityType.Builder.of(supplier, Block...)` **exists in vanilla 1.21.1** → no Forge
  BE-constructor workaround (26.1.2's `new BlockEntityType<>(factory, Set.of(block))` hack).
- `CreativeModeTab.builder()` (no-arg) **exists in vanilla 1.21.1** → no rebuild of the tab against
  the `builder(Row, int)` form.
- `Registry.holders()` **exists in 1.21.1** → creative-tab iteration doesn't need the `entrySet()`
  workaround.
- `Holder<T>` already **extends `Supplier<T>`** in 1.21.1, and has **no `getDelegate()`** → the
  `ForgeHolder` adapter is smaller than 26.1.2's.
- **JEI 19.18.10.218 ships a MinecraftForge artifact for 1.21.1** (unlike JEI for 26.1.2, which is
  Fabric+NeoForge only). Decision 3 — "JEI on all three" — is actually satisfiable here.
- The `Potion.name()` trap flagged in `CLAUDE.md` is a *port* hazard, not a 1.21.1 hazard: 1.21.1's
  `Potion` has both `(MobEffectInstance...)` and `(String, MobEffectInstance...)` ctors and this
  branch uses the former. Nothing to fix — just don't "helpfully" add a name argument.

### Prerequisite — the 2.0 backport

**Confirmed 2026-09-01 (user):** `2_0_backport_plan.txt` Phases 1–5 are landed (tip `b4fc36b`,
"Phase 5: Stand up test infrastructure"). **Phase 6 (verification + datagen) remains.**

Sequencing decision (Decision 2): **finish backport Phase 6 first.** Rationale — Phase 1 of the
backport removed ~230 files. Splitting the post-devolution tree into `common/` means splitting a
smaller, already-2.0-shaped codebase whose structure matches 26.1.2's, so the split diffs cleanly
against the branch we're mirroring. Every file split before it is deleted is wasted work, and every
backport step applied after the split has to be applied across four modules instead of one.

- [x] **P0 — Backport Phase 6 complete and verified** (`2_0_backport_plan.txt` line 332 onward:
      datagen migration, `:neoforge:runData`, `:neoforge:runGametest`, unit tests green).
      *Do not start Phase 0 below until this is ticked.*
      Verified 2026-09-01: `:neoforge:build`, `:neoforge:test`, `:neoforge:runData`, and
      `:neoforge:runGametest` all green (33/33 required tests passed). `mod_version` bumped
      1.5.8 → 1.6.0. Save-compat note: worldgen removal (Versatile Plants et al.) and the
      seeded-recipe re-roll from the earlier devolution phases mean existing worlds are affected;
      no migration path — same acceptance as the 26.1.2 branch.

### Decisions (confirmed with user, 2026-09-01)

1. **Loaders**: NeoForge (existing) + **Fabric** + **Forge** (`net.minecraftforge:forge:1.21.1-52.1.2`).
2. **Parity**: **full** — every NeoForge-only system is reimplemented on both new loaders, not
   stubbed. Same as 26.1.2, **plus** `core/DataAttachments.java` (see Phase 8). *Corrected
   2026-09-01:* 26.1.2's plan never covered it because 26.1.2 **deleted the problem** rather than
   abstracting it — its tree contains no `AttachmentType` usage anywhere. Phase 8 mirrors that
   removal; it does not design a new abstraction.
3. **Recipe viewer**: **JEI on all three** — and unlike 26.1.2 this is achievable (JEI 19.18.x has a
   Forge artifact for 1.21.1). REI/EMI recorded as a future add.
4. **Common-layer API shape**: **mirror 26.1.2 exactly** where the MC API allows — same package
   names (`platform/`, `core/fabric/`, `core/forge/`, `mixin/{fabric,forge,neoforge}/`), same
   `Platform` + `PacketNetwork` split, same `ForgeHolder` adapter, same NeoForge-datagen-as-source-
   of-truth, same `docs/multi-loader-expansion.md` filename. Costs more refactor here; keeps every
   future 1.21.1 ↔ 26.1.2 cherry-pick legible.

   **4a. The package-suffix rule is a hard invariant, not a naming preference.** *Every* file in a
   platform module lives in a package suffixed with that loader — `….neoforge`, `….fabric`,
   `….forge` — and **no package may be occupied by both `common/` and a platform module.** On
   26.1.2 this holds for 100% of platform files (60/60 neoforge, 32/32 fabric, 33/33 forge), giving
   an intersection of exactly zero.

   The mechanism, not just the aesthetics: in a dev run FML/securejarhandler puts each mod jar in its
   own JPMS module, and **JPMS forbids two modules exporting the same package**. A single shared
   package makes the game fail to boot with
   `java.lang.module.ResolutionException: Modules generated_XXXXXXX and potionsplus export package
   … to module neoforge`. It is invisible to `build` — `shadowJar` merges everything into one jar
   with no module boundary — so **a green build proves nothing here.**

   Verify after every phase that moves or adds files, in each platform module:

   ```sh
   comm -12 \
     <(find common/src/main/java   -name '*.java' | sed 's|^common/src/main/java/||;s|/[^/]*\.java$||'   | sort -u) \
     <(find neoforge/src/main/java -name '*.java' | sed 's|^neoforge/src/main/java/||;s|/[^/]*\.java$||' | sort -u)
   ```

   **Empty output is the requirement.** Repeat for `fabric/` and `forge/` once they exist.
5. **Datagen**: keep **NeoForge** as source of truth; a `commonDatagen` Copy task shares its output
   into `common/src/generated/resources` (26.1.2 Decision 4, verbatim).
6. **Architectury dependency**: `dev.architectury:architectury-injectables:1.0.13` **only** — not
   the full `dev.architectury:architectury` runtime library. Mirrors 26.1.2 and every sibling mod.
   Note that `apt-ores-worktrees/mc-1.21.1` *does* pull the full API (`13.0.8`); do not copy that
   line when cribbing its build files.

### Canonical references

| Reference | Path | Use for |
|---|---|---|
| **The 26.1.2 tree** (*the* reference — read the code) | `D:\GitHub\potions-plus` @ `dev/26.1.2/multi-loader-expansion` | **The finished job.** Real `common/`+`fabric/`+`forge/`+`neoforge/` source, all four `build.gradle` files, the actual package layout, `Platform`/`PacketNetwork`/`PacketContext`, every mixin config. When this plan's prose and that tree disagree, **the tree wins** |
| The 26.1.2 plan | same checkout → `docs/multi-loader-expansion.md` | Phase structure, every decision, and ~40 hard-won "VERIFIED API FACTS" plus bug post-mortems |
| **apt-ores @ 1.21.1** | `D:\GitHub\apt-ores-worktrees\mc-1.21.1\` | **The** 3-loader reference *on this exact MC version* — working `build.gradle` for all four modules, `docs/DEVELOPMENT.md` |
| apt-ores @ 26.1 | `D:\GitHub\apt-ores-worktrees\mc-26.1\` | `docs/PORTING.md` Forge-vs-NeoForge divergence table |
| fishtastic | `D:\GitHub\fishtastic` | Canonical multi-loader content mod: registration, `IPacketContext`, mixin layout, `fabric.mod.json` |
| architectury template | `D:\GitHub\architectury-mod-template-1.21.1` | Clean 1.21.1 Architectury skeleton |
| modding-guide | `D:\GitHub\modding-guide` | Topics `01`, `02`, `06`, `07`, `08` |

### Mirror discipline — a standing rule for every phase

Decision 4 makes 26.1.2 the specification, not merely an inspiration. That obligation is procedural,
not just aspirational, so it gets a rule:

> **Before starting any phase, open the corresponding code in the 26.1.2 tree — not just its section
> of the 26.1.2 plan — and diff your intended change against what is actually there. Before declaring
> a phase done, diff again.**

This is written down because ignoring it has already cost a full session. Phase 1 spent that session
concluding that a `neoforge/` mixin cannot reference a `common/` class, that mixin classes must live
in `neoforge/`, and that `runtimeClasspath.extendsFrom common` had to be removed. All three are false,
and all three were refuted by files sitting in the 26.1.2 checkout the whole time:

- 26.1.2 keeps **21 mixins in `common/`** — including `BoatMixin`, the exact class whose relocation
  was taken as proof that mixins cannot live there.
- 26.1.2's `neoforge/build.gradle` carries `runtimeClasspath.extendsFrom common` with a comment
  explaining precisely the failure Phase 1 then re-derived from scratch: *"Without this, `common/` is
  on the compile and dev classpaths but not the run's, and the mod's classloader fails with
  `NoClassDefFoundError` on the first common class it touches."*
- 26.1.2 has **zero** split packages, which is why it never hit the blocker that stopped Phase 1.

A phase that reaches a conclusion contradicting the 26.1.2 tree has found a bug in its own reasoning,
not a genuine 1.21.1 divergence — until a `javap`-grade API difference proves otherwise, in which case
record it under "VERIFIED API FACTS" with the evidence.

---

## Status

| Phase | Title | Status |
|---|---|---|
| P0 | *(prereq)* 2.0 backport Phase 6 | ✅ done 2026-09-01 |
| P1 | *(pre-flight)* Fix the three live `common` bugs (section D) | ✅ done 2026-09-01 |
| 0 | Build-system swap (ModDevGradle → architectury-loom) | ✅ done 2026-09-01 |
| 1 | Source split into `common/` | ✅ **closed 2026-09-01** — split + 107-file `.neoforge` repackage (Decision 4a), all exit criteria met: `comm -12` package intersection empty, `clean` full build green, `runClient` reaches main menu. Committed on `dev/1.21.1/multi-loader-expansion` |
| 2 | Platform abstraction layer | ✅ done 2026-09-01 — 7-method `Platform`, 5-method `PacketNetwork`, 3-method `PacketContext`; 12 packet handlers rewritten + `NeoPacketContext`-wrapped; 9 senders + 4 call sites converted to the @ExpectPlatform surface; `:common:compileJava :neoforge:compileJava` green, build green modulo the known Phase-12 junit red; `net.neoforged` = 0 in `common/src/main/java`; `comm -12` empty |
| 3 | Fabric + Forge module scaffold | ✅ done 2026-09-01 — `settings.gradle` includes re-added; `fabric`/`forge` `gradle.properties` + `build.gradle` authored (Forge `runtimeClasspath` asymmetry kept); placeholder `fabric.mod.json` + `mods.toml`; exit criterion met: `:fabric:build :forge:build :neoforge:build -x test` → `BUILD SUCCESSFUL`, all three jars produced. **Divergence required:** removed `RecipeInput` from `InventoryBlockEntity` (1.21.1 mapping collision, see VERIFIED API FACTS) + added `ContainerRecipeInput` wrapper; `BrewingCauldronBlockEntity` now wraps `this` at its one `matches(this, …)` call site |
| 4 | Registration hubs (Fabric + Forge) | ✅ **closed 2026-09-02** — Fabric (26 files) + Forge (29 files) hubs written, all three loader modules compile green, `comm -12` empty (Decision 4a). **Exit criteria met:** runClient smoke on **all three loaders** boots to main menu — fabric + forge both log "Potions Plus (Fabric|Forge) initializing" + "Sound engine started", zero crash markers, item/block registration proven by the item-model lookups the game attempts; neoforge regression clean after the hub refactor + mixin split; clean three-loader build `BUILD SUCCESSFUL`. Two blockers found only by the smokes (invisible to compile): **mixin split** (common's config listed 16 mixins that lived in neoforge/ → 10 vanilla-only javap-verified → common, 8 neoforge-dependent → new `mixin.neoforge` package + own config) and **fabric class-init ordering NPE** (eager fabric registration: `BrewingItems` derefs `FlowerBlocks.LUNAR_BERRY_BUSH` for the `ItemNameBlockItem` → `FlowerBlocks.init` must run FIRST in the fabric `Blocks` static block). Committed `d9b2cf4`. |
| 5 | `@ExpectPlatform` impls + networking | 🟡 **partially done 2026-09-02** — networking infra (PacketContext adapters, Forge Channel/PacketNetworkImpl, both `core/{fabric,forge}/Packets.java`) complete on all three loaders; 6/12 payloads ported to `common/` and registered cross-loader, 6 remain neoforge-only pending Phases 7/8-9/11; runtime-recipe client sync unresolved. See phase notes. |
| 6 | Entrypoints | ✅ **closed 2026-09-02** — all checklist items were already in place from Phases 4-5 (hub work required working entrypoints to test); this phase's job was verifying the stronger exit criterion. See phase notes. |
| 7 | Event surface (36 subscriber classes) | 🟡 **all six buckets touched 2026-09-02, none fully closed — fan-out complete, fan-in blocked on Phases 5/8/11.** Mob-effect behaviour: ✅ done (7 classes, full parity). Registration hubs: ✅ confirmed no-op (already correctly scoped). Explicit listeners: 2/6 ported (`EnchantmentListeners`, `ItemListenersMod`), 4 blocked. Tick/lifecycle: 4 core classes ported, 2 blocked (`ServerLifecycleListeners`, `ServerPlayerUtility`). Client tooltips: `AnimatedItemTooltipEvent` redesigned + `PotionEffectTooltips`/`ItemListenersGame`/`BrewingTooltips` (done 2026-09-04, Phase 11a 8th session) ported, 1 reclassified (`ClotheslineBlockEntityRenderer`→Phase 11). Commands/input: `CommonCommands`→`common/command/PpCommands.java` ported, 2 blocked (`ClientCommands`, `KeyMappingsListener`), `ClientEvents` reclassified to Phase 11. Every blocked item has grep/javap evidence in the phase notes, not a guess — the recurring blockers are Phase 5's runtime-recipe remainder (`RecipesRegistrar`/`PotionsRegistrar`), Phase 8's unsplit `behaviour` package, and Phase 11's client-registration hubs (`DynamicIconItems`, `KeyMappings`, JEI-on-all-three). See phase notes for full detail. |
| 8 | NeoForge-only systems (full parity) | ✅ **closed 2026-09-04.** `DataAttachments`: deleted, moved onto `common/SavedData` (no NeoForge-only system left there — Decision 2's carve-out for this phase closes to plain 26.1.2-Phase-5 parity). Global loot modifiers (Wormroot, AddMobEffects): done, all three loaders, logic shared via `common/behaviour/`. Biome modifiers (lunar berry bush add/remove, dense diamond ore): done, all three loaders; fixed a live `neoforge/`-only resource leak into the Fabric/Forge jars found along the way. Server config: done, all three loaders (`ForgeConfigSpec`/hand-rolled Fabric JSON config). **Capabilities/`IItemHandler` (clothesline storage) — the last open bucket — done 2026-09-04**, closing the phase: Forge's `AttachCapabilitiesEvent<BlockEntity>`/`ICapabilityProvider`/`InvWrapper` (pre-1.20.5 shape, javap-confirmed against the 52.1.2 jar) and Fabric's `ItemStorage.SIDED.registerForBlockEntity`/`InventoryStorage.of` (javap-confirmed against the resolved `fabric-transfer-api-v1:5.4.3+c24bd99419`) both implemented and wired into their existing `Capabilities.register()` call sites. All 4 modules compile green, `:{neoforge,fabric,forge}:build -x test` green, Decision 4a `comm -12` empty, `:neoforge:runServer`/`:fabric:runServer` reach `Done`. `:forge:runServer` also reaches `Done` but then hits a **pre-existing, unrelated** `NoSuchElementException` crash in `SeededIngredientsLootTables` (confirmed via `git stash` reproduction — same crash with this session's changes removed) caused by the `golden_cubensis`/`diamour` items never being registered on Forge; not this bucket's regression, not fixed here. Not verified in-world (no GUI-automation tool to place a Clothesline and test hopper interaction). See the Phase 8 checklist and the 2026-09-04 (Capabilities session) progress-log entry for full evidence. |
| 9 | Mixins + access widening/transformers | 🟡 **in progress, updated 2026-09-04.** Config split/wiring/refmap declaration/`--mixin.config` all done; Forge+Fabric mixin parity gaps closed (`BucketItemMixin`, `ItemEntityMixin`/`ItemEntityLifespanMixin`, `LivingEntityMixin`). Two real refmap/mixin-config bugs found via actual `runClient` launches and fixed in the 2026-09-03 session (see progress log). **All three loaders now confirmed reaching the actual main menu** (`Sound engine started`) via uncapped warm-daemon `runClient` runs, 2026-09-04 — Fabric was already confirmed, NeoForge and Forge closed this session (13s/~1min to menu respectively, zero `FATAL`/`MixinApplyError`/`Exception` lines in either full log). `RecipeManager.byType`/`byName` access widener/transformer entries added to `common/potionsplus.accesswidener` and `forge/accesstransformer.cfg` (mirroring NeoForge's pre-existing AT entries), unblocking the mechanical field-access half of `core.neoforge.RecipesRegistrar`'s runtime recipe injection cross-loader. **Updated 2026-09-04 (later session): the other three couplings closed too — `RecipesRegistrar` (renamed `core.RecipesRegistrar`) is now common/, with real runtime recipe injection wired on Fabric and Forge for the first time** (new `core.{fabric,forge}.ServerLifecycleListeners`, replacing no-op stubs). Compile green on all 4 modules, `comm -12` empty, all three loaders reach `Sound engine started` clean. See the 2026-09-04 (later) progress-log entry. Remaining: production-jar mixin-discovery verification on Forge 52.x (needs an installed packaged jar), `BlockEntityType.validBlocks` Forge association (still deferred), a full AT/AW survey as Phase 11 client work grows. |
| 10 | Datagen sharing | ✅ **closed 2026-09-03** — `commonDatagen` Copy task added, `common/build.gradle` `duplicatesStrategy = INCLUDE` added; found and fixed two real gaps neither present on 26.1.2 (see phase notes: the `--existing` datagen arg not covering `common/`, and each platform module's own `processResources` needing `duplicatesStrategy = EXCLUDE` once it pulls both its own generated resources and common's copy of the same files). Exit criterion met: `commonDatagen` then `:fabric:build :forge:build :neoforge:build` all green, Fabric/Forge/NeoForge jars all carry matching blockstate/model/tag/sounds.json counts (27/27 blockstates), zero `neoforge`-tagged leaks in either non-NeoForge jar. |
| 11 | Client (renderers, particles, tooltips, colors, models, JEI ×3) | 🟡 **partial, Phase 11a steps 1-3 of 4 closed 2026-09-04 (third session)** — particles, key mappings, item-tint color, and JEI still done and smoke-verified on all three loaders. **Phase 11a step 1 (common `BlockEntityType` hub) and step 2 (packet moves) landed**: `common/core/Blocks.java` now holds all 6 `BlockEntityType` holders (4 as `Holder<BlockEntityType<?>>` pending step 4, 2 concretely typed); `ClotheslineBlockEntity`/`PotionBeaconBlockEntity` (+`ClotheslineBlockEntityBakedRenderData` + both renderers) and their `Block` classes moved `neoforge/` → `common/`; Clothesline/PotionBeacon now register as real blocks+BE types+renderers on **all three loaders**; `ClientboundAcquiredBrewingRecipeKnowledgePacket` moved to `common/network/`. **Step 3 (`DynamicIconItems` DSL) landed, re-abstracted rather than literally ported**: reading the finished `dev/26.1.2` tree's equivalent showed it can't be a literal port — its `IModelGenerator` generates against vanilla `BlockModelGenerators`/`ItemModelGenerators`, while this branch's whole 8-file DSL (`AbstractRegistererBuilder`, `RegistrationUtility`, `IModelGenerator`, `ItemBuilder`, `SimpleItemBuilder`, `GenericIconItemBuilder`, `ItemModelUtility`, `ItemOverrideUtility`) is built around NeoForge's own `BlockStateProvider`/`ItemModelProvider` datagen API (1.21.1-specific, no vanilla equivalent), so porting the DSL itself would mean porting NeoForge's datagen system — out of scope and against Decision 5 (NeoForge stays sole datagen source of truth). Instead applied the same stub-hub pattern already established for `BrewingItems`/`OreItems`/`WreathItem` (Phase 4): new `common/core/items/DynamicIconItems.java` holds the texture-location constants, the `GENERIC_ICON`/`POTION_EFFECT_ICON` `Holder<Item>` fields, and the runtime icon-lookup helpers block entities actually call (`getGenericIconItemStackCountForTexture`/`getGenericIconItemStackForTexture`, reimplemented directly against a texture→index map instead of going through the datagen-only `GenericIconItemBuilder.getItemStackForTexture`); `core.neoforge.items.DynamicIconItems` keeps the full DSL unchanged (still needed for NeoForge's item-model datagen) and now also populates the common stub at the end of `init`; new `core.{fabric,forge}.items.DynamicIconItems` register the same 2 items directly (`register.apply(name, () -> new Item(new Item.Properties()))`, no DSL — the generated models reach Fabric/Forge via `commonDatagen`, Phase 10) and populate the same stub. All 8 neoforge-only call sites of the old builder-typed `DynamicIconItems` (`AbyssalTroveBlockEntity`, `AbyssalTroveBlockEntityRenderer`, `BrewingCauldronBlockEntity`, `HerbalistsLecternBlockEntity`, `SanguineAltarBlockEntityRenderer`, `ClientEvents`, `ClientItemStacksTooltip`, `AdvancementProvider`) repointed to the common class. **The 3 named block entities (`AbyssalTroveBlockEntity`, `BrewingCauldronBlockEntity`, `HerbalistsLecternBlockEntity`) still could NOT move to `common/` this session** — confirmed each has a second, independent neoforge-only dependency beyond `DynamicIconItems` (matching the pattern the last 2 sessions each hit): `AbyssalTroveBlockEntity` and `HerbalistsLecternBlockEntity` both import `core.neoforge.RecipesRegistrar` (its runtime-injection half is explicitly gated on Phase 9's access widener, step 4, out of scope), `BrewingCauldronBlockEntity` imports `persistence.neoforge.PlayerBrewingKnowledgeNetworking` (neoforge-only packet plumbing, not a Phase-5-shaped move, not attempted). `SanguineAltarBlockEntity` untouched (already known-blocked, see prior entry). Block (cauldron water) tint and the `ItemStacksTooltip` tooltip-component factory remain blocked too — the latter's *only* remaining blocker is now NeoForge's client tooltip-component-factory extension point itself (`ClientTooltipComponentFactoriesListeners` + `ItemMixin`, no vanilla/Fabric equivalent), not `DynamicIconItems` any more. **Step 4 (`RecipesRegistrar`, gated on Phase 9's access widener) remains open, out of scope this session.** **Updated 2026-09-04 (4th session, "step 5")**: `PlayerBrewingKnowledgeNetworking`'s only remaining neoforge coupling (the packet it sent) had already moved to common in an earlier session, so it was in fact trivially portable — moved to `common/persistence/`. `BrewingCauldronBlockEntity` + `BrewingCauldronBlockEntityRenderer` then moved to `common/blockentity/` too (their only other blocker was `core.neoforge.Blocks` → swapped to the step-1 `core.Blocks` hub + `.value()`; one more hidden NeoForge-only API surfaced mid-move, `Holder#getKey()` — NeoForge patches this onto vanilla `Holder`, common doesn't have it — fixed to vanilla `Holder#unwrapKey().orElseThrow()`). **New blocker found, not an oversight**: unlike Clothesline/PotionBeacon, `block.neoforge.BrewingCauldronBlock` (the `Block` class itself) was never split off neoforge — Fabric/Forge have no cauldron `Block` or `BlockEntityType` registered at all (`grep BREWING_CAULDRON` in both loaders' `core.{fabric,forge}.blocks` returns zero), so the BE+renderer move only benefits NeoForge (whose existing registration/tint code needed zero changes); porting the `Block` class itself (+ resulting datagen/registration wiring) is a further register-hub-shaped task, not attempted. Cauldron water tint stays NeoForge-only for the same reason. **Phase 9's access widener confirmed still not landed** (`RecipeManager.byType`/`byName` only present in `neoforge/`'s own local AT, absent from the shared `common/potionsplus.accesswidener` and `forge/accesstransformer.cfg`) — step 4 and `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` remain untouched as instructed. See the 2026-09-04 (4th session) progress-log entry for full evidence. **Updated 2026-09-04 (5th session): `BrewingCauldronBlock` ported cross-loader, closing the blocker the 4th session found.** The `Block` class moved `neoforge/` → `common/block/BrewingCauldronBlock.java`; the block itself + its `BlockEntityType` binding are now registered on **Fabric and Forge** (`core.{fabric,forge}.blocks.BlockEntityBlocks` + `core.{fabric,forge}.Blocks`, same pattern as Clothesline/PotionBeacon); `BrewingCauldronBlockEntityRenderer` is now registered on all three loaders; the cauldron water tint is now shared (new `common/block/tintsource/PotionsPlusBlockColors.java`, mirroring the `PotionsPlusItemColors` pattern) and registered on Fabric (`ColorProviderRegistry.BLOCK`) and Forge (`RegisterColorHandlersEvent.Block` on the existing dist-gated `core/forge/Renderers.java` subscriber). `common/core/Blocks.java#BREWING_CAULDRON_BLOCK_ENTITY` is now concretely typed (was `Holder<BlockEntityType<?>>`, now `Holder<BlockEntityType<BrewingCauldronBlockEntity>>`). Compile green on all 4 modules, Decision 4a `comm -12` empty, real `runClient` smoke clean on all three loaders (zero new exceptions). Not verified in-world (no GUI-automation tool). `HerbalistsLecternBlockEntity`/`SanguineAltarBlockEntity`/`AbyssalTroveBlockEntity` and the tooltip-component factory remain the only open Phase 11 items, still gated on Phase 9's access widener / NeoForge's tooltip extension point. See the 2026-09-04 (5th session) progress-log entry for full evidence. **Updated 2026-09-04 (6th session): Phase 9's access widener gate is now gone (`RecipesRegistrar` is common/, see the Phase 9 row) — but `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` still can't move, for a new, distinct reason.** Both BE classes read fully portable once `RecipesRegistrar`'s import is repointed. The real blocker: their `Block` classes (`block.neoforge.AbyssalTroveBlock`/`HerbalistsLecternBlock`) are themselves still neoforge-only (same unaddressed gap as `ClotheslineBlock`/`PotionBeaconBlock` before the 3rd session, just never named for these two), and — unlike Clothesline/PotionBeacon/BrewingCauldron — Fabric/Forge have **never registered the underlying blocks at all** (`core.{fabric,forge}.blocks.BlockEntityBlocks` only has 4 entries, missing `herbalists_lectern`/`abyssal_trove`). This is a 3-part register-hub project (port both `Block` classes + register both blocks on fabric/forge for the first time + the `BlockEntityType`/renderer wiring this task's brief described), not attempted this session — real progress on `RecipesRegistrar` itself was prioritized over forcing this through at the end of the session's budget. `SanguineAltarBlockEntity` and the tooltip-component factory remain exactly as before. See the 2026-09-04 (6th session, listed as "later" in the log) progress-log entry and the Phase 11 recommendation section's addendum for the full breakdown. **Updated 2026-09-04 (7th session): the 6th session's 3-part register-hub project is done — `AbyssalTroveBlock`/`HerbalistsLecternBlock` and their BE classes/renderers are now `common/`, registered on all three loaders.** Both `Block` classes read exactly as predicted (only coupling was `core.neoforge.Blocks.X.get()` → `core.Blocks.X.value()`); moved to `common/block/`. Both BE classes + both renderers moved to `common/blockentity/` (one more hidden NeoForge-only API surfaced mid-move, same class of bug as the 4th session's `Holder#getKey()`: `HerbalistsLecternBlockEntity` called `mobEffectInstance.getEffect().getKey().location()` — NeoForge-patched-onto-vanilla-`Holder` method again, fixed to `.unwrapKey().orElseThrow().location()`). `herbalists_lectern`/`abyssal_trove` blocks + `BlockEntityType`s registered on **Fabric and Forge for the first time** (new entries in `core.{fabric,forge}.blocks.BlockEntityBlocks` and `core.{fabric,forge}.Blocks`, exact template of the other 4 already-ported blocks); both renderers registered on all three (Fabric `BlockEntityRendererRegistry`, Forge's existing dist-gated `core/forge/Renderers.java` subscriber). `common/core/Blocks.java#HERBALISTS_LECTERN_BLOCK_ENTITY`/`#ABYSSAL_TROVE_BLOCK_ENTITY` are now concretely typed (were `Holder<BlockEntityType<?>>`). Compile green on all 4 modules, Decision 4a `comm -12` empty, real `runClient` smoke clean on all three loaders (zero new exceptions — grepped each log, only the pre-existing "Missing subtitle translation" noise, which now also names `abyssal_trove_deposit`/`herbalists_lectern_appear`/`herbalists_lectern_disappear` since those sound events are reachable for the first time on Fabric/Forge). **`SanguineAltarBlockEntity` is now the only remaining neoforge-only block entity** — its real blocker (read this session, not assumed): its BE class imports `network.neoforge.ClientboundSanguineAltarConversionProgressPacket`/`ClientboundSanguineAltarConversionStatePacket`, 2 packets never moved to `common/network/` (same Phase-5-shaped gap the Phase 11a recommendation section already named). The tooltip-component factory (`ClientItemStacksTooltip`) blocker is unchanged (NeoForge's own extension point, no vanilla/Fabric equivalent). **This closes Phase 11's original exit criterion except for those two remaining items** — see the updated Exit-criterion paragraph below the checklist for the precise final state. Not verified in-world (still no GUI-automation tool in this environment to place a trove/lectern and watch it render/interact in a loaded world). See the 2026-09-04 (7th session) progress-log entry for full evidence. **Updated 2026-09-04 (8th session): `SanguineAltarBlockEntity` is done — all 6 of 6 block entities now render/register identically on all three loaders, closing Phase 11's block-entity exit criterion in full.** Its documented blocker (2 sync packets, `ClientboundSanguineAltarConversionProgressPacket`/`...StatePacket`, importing the concrete BE class/`State` enum/fields directly) turned out to be stale in exactly the way the task predicted: reading both packets and the BE class fresh found their *only* neoforge coupling was `core.neoforge.Blocks` (fixable the same way every prior BE move fixed it — swap to `core.Blocks` + `.value()`); referencing the concrete BE class directly is fine once that class is common too, same precedent as `BrewingCauldronBlockEntity`'s packet in the 4th session. `SanguineAltarBlock`, `SanguineAltarBlockEntity`, `SanguineAltarBlockEntityRenderer`, and both packets all moved `neoforge/` → `common/` (block/blockentity/network respectively); `common/core/Blocks.java#SANGUINE_ALTAR_BLOCK_ENTITY` is now concretely typed (was the last `Holder<BlockEntityType<?>>`); block + `BlockEntityType` + renderer registered on Fabric and Forge for the first time (`core.{fabric,forge}.blocks.BlockEntityBlocks`, `core.{fabric,forge}.Blocks`, Fabric `BlockEntityRendererRegistry`, Forge's existing dist-gated `core/forge/Renderers.java` subscriber); both packets registered in `core.{fabric,forge}.Packets.java` (9 of 12 payloads now shared, up from 7). **`BrewingTooltips` also done this session** (see the Phase 7 checklist item and progress-log entry) — moved to `common/item/tooltip/BrewingTooltips.java`, wired into Fabric's and Forge's `TooltipListeners`. Compile green on all 4 modules (`:common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava`), Decision 4a `comm -12` empty on fabric/forge against common. Real `:neoforge:runClient` smoke reached `Sound engine started` with zero new exceptions (only the pre-existing missing-subtitle-translation noise, now also covering the two Sanguine Altar sound events reachable for the first time). **Fabric/Forge `runClient` smokes were not run this session** (session ended before reaching them; the neoforge client JVM was killed cleanly, no orphans) — this is the one verification gap left open, noted honestly rather than claimed. The **only remaining Phase 11 gap is the `ItemStacksTooltip` tooltip-component-factory extension point**, still NeoForge-only (`ClientTooltipComponentFactoriesListeners` + `ItemMixin`, no vanilla/Fabric equivalent) — see the updated Exit-criterion paragraph below the checklist. |
| 12 | Tests (unit + game tests, three loaders) | ⬜ not started |
| 13 | Verification | ⬜ not started |

---

## VERIFIED API FACTS — 1.21.1 (2026-09-01)

All confirmed by `javap` against the real jars in the loom cache
(`forge-1.21.1-52.1.2-minecraft-merged` and the vanilla `minecraft-merged` 1.21.1). **Do not
re-derive these.** Where a fact contradicts the 26.1.2 plan, the divergence is called out.

### Forge 52.1.2 (1.21.1) registration

- `DeferredRegister.create(ResourceKey<? extends Registry<B>>, String)` ✓ (also `IForgeRegistry` and
  `ResourceLocation` overloads).
- `DeferredRegister.register(String, Supplier<? extends I>)` → **`RegistryObject<I>`**.
- `RegistryObject<T> implements Supplier<T>` **only — not `Holder<T>`**. There is **no
  `net.minecraftforge.registries.DeferredHolder` in 1.21.1** (it does not exist in the jar).
  → **the `ForgeHolder<T> implements Holder<T>` adapter is required**, exactly as on 26.1.2.
- `RegistryObject.getKey()` → `ResourceKey<T>` and `.getId()` → `ResourceLocation` are available
  **immediately at init-time**; `.getHolder()` → `Optional<Holder<T>>` fills only after
  `RegisterEvent`. Same lazy/eager split the 26.1.2 adapter relies on.
- **DIVERGENCE from 26.1.2:** flushing is `DeferredRegister.register(IEventBus)` —
  **`net.minecraftforge.eventbus.api.IEventBus`, not `BusGroup`**. 26.1.2's
  `FMLJavaModLoadingContext.get().getModBusGroup()` dance does not apply; use the classic
  `FMLJavaModLoadingContext.get().getModEventBus()` / injected `IEventBus` constructor parameter.
- Also present and useful: `createTagKey(String)`, `createOptional*` variants.

### `Holder<T>` in 1.21.1 (shapes the `ForgeHolder` adapter)

```
public interface Holder<T> extends Supplier<T>, net.minecraftforge.registries.tags.IReverseTag<T>
  default boolean containsTag(TagKey<T>)          // Forge patch
  default Stream<TagKey<T>> getTagKeys()          // Forge patch
  default T get()                                 // <- already defaulted to value() in 1.21.1
  T value(); boolean isBound();
  boolean is(ResourceLocation); is(ResourceKey<T>); is(Predicate<ResourceKey<T>>);
  boolean is(TagKey<T>); is(Holder<T>);           // <- is(Holder) is NEW vs 26.1.2
  Stream<TagKey<T>> tags();
  Either<ResourceKey<T>, T> unwrap(); Optional<ResourceKey<T>> unwrapKey();
  Kind kind(); boolean canSerializeIn(HolderOwner<T>);
  default String getRegisteredName();
  // NOTE: no getDelegate() in 1.21.1 — 26.1.2's adapter overrides it; drop that override here.
```

So the 1.21.1 `ForgeHolder` = 26.1.2's, **minus** `getDelegate()` and the explicit `Supplier`
implementation (inherited), **plus** `is(Holder<T>)`. Model `equals`/`hashCode` on the key, as before.

### Forge 52.1.2 networking

`ChannelBuilder` exposes **both** `simpleChannel()` and **`payloadChannel()`** → the 26.1.2 Forge
networking approach (`ChannelBuilder.named(id).networkProtocolVersion(1).optional()
.payloadChannel().play().serverbound()/.clientbound().add(Type, StreamCodec, BiConsumer).build()`
+ `Channel.send(payload, PacketDistributor.X)`) **ports directly**. `PacketDistributor` and
`Channel` both present. Re-verify `CustomPayloadEvent.Context`'s method set at implementation time
(26.1.2: has `enqueueWork`/`getSender`/`isServerSide`/`getConnection`; **no** `player()`, **no**
`disconnect(...)`).

### Vanilla 1.21.1 — traps and non-traps

- `BlockEntityType.Builder.of(BlockEntitySupplier<? extends T>, Block...)` → `.build(Type<?>)`.
  **Public vanilla API** — every loader uses the same call. *(26.1.2 had no `Builder` at all; that
  workaround is not needed here.)*
- `CreativeModeTab` — **CORRECTED 2026-09-02, with compile evidence from BOTH sides.** Vanilla 1.21.1
  (the fabric module's classpath) has **only** `builder(Row, int)`. The no-arg `builder()` and
  `withSearchBar()` are loader patches — **NeoForge** (javap'd `minecraft-merged-mojang-patched.jar`)
  **and Forge** (javap'd `minecraft-merged-srg-patched.jar`: literal `builder()` + `m_257815_(Row,int)`,
  Builder has `withSearchBar()`/`withSearchBar(int)`) both patch them in; vanilla has neither. So:
  **Fabric** uses `builder(CreativeModeTab.Row.TOP, 4)` with **no** `withSearchBar()` (the exact 26.1.2
  fabric form); **Forge** uses `builder(Row.TOP, 4)` + `.withSearchBar()` (safe cross-patch union). The
  plan's earlier "no tab rebuild needed" claim was verified against the *patched* jar, not vanilla; the
  two `:fabric:compileJava` errors (`method builder ... required: Row,int`; `cannot find symbol:
  withSearchBar()`) are the evidence.
- `Registry.holders()` → `Stream<Holder.Reference<T>>` **exists**. *(26.1.2 removed it and forced
  `entrySet()`.)*
- `Registry.registerForHolder(Registry<T>, ResourceKey<T>, T)` / `(…, ResourceLocation, T)` →
  `Holder.Reference<T>` — the Fabric registration path, same as 26.1.2.
- **`SimpleParticleType(boolean)` is `protected` in vanilla AND in the Forge 52.1.2 srg-patched jar**
  (**CORRECTED 2026-09-02** — the earlier "public in the Forge-patched jar" claim was NeoForge-only;
  javap on `minecraft-merged-srg-patched.jar` shows `protected SimpleParticleType(boolean)`;
  NeoForge's `minecraft-merged-mojang-patched.jar` is the one that patches it public). Same trap as
  26.1.2 → **Fabric and Forge both** use `new SimpleParticleType(false) {}` (anonymous subclass);
  NeoForge can call the ctor directly. Decide in Phase 9 whether to add an access-widener instead;
  prefer matching 26.1.2's anonymous-subclass choice for diffability.
- **`BlockEntityType.validBlocks` is `private final Set<Block>` (`f_58915_`) on Forge 52.1.2** — no
  public mutation API and no forge-patched helper (javap'd `minecraft-merged-srg-patched.jar`). On
  NeoForge the DISPENSER↔PRECISION_DISPENSER association uses the `BlockEntityTypeAddBlocksEvent`
  (`event.modify(...)`); on Fabric, `FabricBlockEntityType.addSupportedBlock(Block)`; **on Forge there
  is no portable hook → the association is deferred to Phase 9** (access-widener/mixin), mirroring the
  26.1.2 Forge tree which skips it entirely.
- `Potion` ctors: `(MobEffectInstance...)` **and** `(String, MobEffectInstance...)`. This branch uses
  the first form. `Potion.getName(Optional<Holder<Potion>>, String)` is static. The `CLAUDE.md`
  `Potion.name()` trap is a **26.1.2-only** hazard — leave 1.21.1's call sites alone.
- `ResourceLocation` (not `Identifier`), `CompoundTag`-based `load`/`saveAdditional` (not
  `ValueInput`/`ValueOutput`), 1.21.1-era `BlockStateProvider`/`ItemModelProvider` datagen.
- **`Container`/`RecipeInput` mapping collision — verified 2026-09-01, forces a real code divergence.**
  Mojang's 1.21.1 named mappings give `Container.getItem(int)`/`isEmpty()` and
  `RecipeInput.getItem(int)`/`isEmpty()` **identical names** but **different intermediary ids**
  (`Container` → `method_5438`/`method_5442`, `RecipeInput` → `method_59984`/`method_59987`). The merged
  `mappings.tiny` is clean (each has exactly one entry) — the failure is TinyRemapper 0.14.0's
  class-less conflict key: any class implementing BOTH interfaces makes one method try to map to two
  targets → `:fabric:remapJar` "Unfixable conflicts". Not a mapping defect, not fixable by
  `fabric.loom.dropNonIntermediateRootMethods=true` (both targets ARE roots; flag empirically inert —
  identical 38-conflict set with and without it), not by `ignoreConflicts=true` (a method has one
  bytecode name, so one interface silently fails to override at runtime). The plan doc's Phase 3
  divergence note records the fix (`ContainerRecipeInput` wrapper). Vanilla never hits this because
  no vanilla class implements both interfaces, and loom downloads a pre-remapped intermediary jar for
  vanilla rather than remapping it.

### Toolchain versions (from `apt-ores-worktrees/mc-1.21.1`, a known-good 1.21.1 three-loader build)

```
dev.architectury.loom          1.17-SNAPSHOT      # NOT loom-no-remap — 1.21.1 is obfuscated
architectury-plugin            3.5-SNAPSHOT
com.gradleup.shadow            8.3.6
Gradle wrapper                 9.5.0
Java toolchain                 21  (options.release = 21)
mappings                       loom.officialMojangMappings() + loom { silentMojangMappingsLicense() }
fabric_loader_version          0.17.2
fabric_api_version             0.116.7+1.21.1
neoforge_version               21.1.209           # branch currently pins 21.1.125 — bump
forge_version                  52.1.2             # pin exact; never "Latest"/"Recommended"
jei_version                    19.18.10.218       # already pinned on this branch
```

---

## Implementation history — what actually happened on 26.1.2

The 26.1.2 plan records what was *planned*. That branch's 22 commits record what actually happened,
and **11 of them are post-phase bug fixes** — several contradicting the plan they were executing.
Everything in this section is sourced from those commit messages plus
`docs/forge-runclient-handoff.md` and `docs/forge-production-mixin-handoff.md` on that branch.

```
3ae9093  Phases 0-1   registration hubs
b41b81d  Phases 2-4   event surface + networking + entrypoints
98fc18a  Phase 5      NeoForge-only systems
6469ff3  Phase 6      mixins + access widening
58767d0  Phase 7      datagen sharing
43a7f12  Phase 8      client + JEI
d75f3e0  Phase 9      game tests
────────── then eleven fix commits ──────────
f5cd94d  Forge runClient: four stacked blockers
2a5b826  Forge runClient regression introduced by Phase 9
4c3d9d3  Forge world-creation crash        (wrong diagnosis — superseded)
be402c6  Forge network double-dispatch     (the real root cause)
cef7257  Forge gametest bootstrap          (also corrected a wrong diagnosis in the plan doc)
0c1119d  IDE run-config AXFORM
00c39e0  runtime-recipe client sync + 2 bugs it surfaced
f7c5890  Fabric block-breaking + Forge tint / floating-item bugs
9ea6946  SoulMateEffect infinite recursion
83bf9a8  ForgeHolder.equals never matched the real registry Holder
2eaea41  Forge gametest registration       (needed a bespoke mixin)
243ac95  production mixin config loading
```

**The headline lesson.** Every phase's exit criterion was "it builds" or "it reaches the main menu",
and *not one of the eleven bugs was caught by those*. They were found by (a) running game tests,
(b) **creating a world**, and (c) **installing packaged jars into a real profile**. Two of them
(`243ac95`, `00c39e0`) were invisible in dev and only appeared in production. Budget a fix pass
after each of Phases 4–12 — do not defer all verification to Phase 13.

**The second lesson.** Three separate commits corrected a *wrong root-cause diagnosis* recorded in
the plan doc itself (`f5cd94d` vs the Phase 6 note, `be402c6` vs `4c3d9d3`, `cef7257` vs the Phase 9
"pre-existing blocker" claim). When this plan's phase notes are updated during implementation,
record the *evidence*, not the theory.

### A. Gotchas that apply directly — expect every one of these

- [ ] **Forge silently drops a second `@Mod` class with the same modid** (first-wins dedup). 26.1.2
      wrote a `PotionsPlusForgeClient` mirroring NeoForge's client-entrypoint split; it never ran, and
      `f5cd94d` **deleted it**. → **Phase 6 of this plan must not create one.** Do client wiring from
      the single `@Mod` class, dist-gated.
- [ ] **Forge packet handlers must call `ctx.setPacketHandled(true)`.** Without it,
      `ForgeHooks.onCustomPayload` falls through past a successful dispatch and fires
      `CustomPayloadEvent.BUS` a *second* time on the same event, re-reading a buffer whose reader
      index is already at the end → `IndexOutOfBoundsException` and an immediate client disconnect on
      the **first** custom payload of a world join. Cost two commits: `4c3d9d3` misdiagnosed it as an
      empty-payload decode bug and "fixed" it by skipping empty packets; `be402c6` found the truth
      when the next packet failed identically. **Highest-value single gotcha in this list.**
- [ ] **`ForgeHolder.equals` must compare by resource key against *any* `Holder`.** 26.1.2's version
      guarded with `getClass() == o.getClass()`, so a `ForgeHolder` could never equal the real
      `Holder.Reference` for the same entry — silently breaking every `contains()`/`equals()` check
      against holders pulled from a registry (`83bf9a8`). `Holder.Reference` declares no
      `equals`/`hashCode` of its own (registries hand out one singleton per key), so the fix must live
      entirely on the adapter side, accepting one-sided symmetry.
- [ ] **`ForgeHolder` also breaks registry *serialization*.** Because it isn't a `Holder.Reference`,
      mod potion holders failed to encode and **crashed level saves** once data actually persisted
      (`00c39e0`). 26.1.2 needed a `RegistryMixin` that unwraps the adapter, mirroring how NeoForge
      patches `DeferredHolder` internally. **Plan for this mixin from the start** — it is not optional,
      and the failure mode (world save crash) appears long after registration looks fine.
- [ ] **No Forge mixin is active in dev unless you pass `--mixin.config` explicitly.**
      `MixinPlatformAgentMinecraftForge` ignores `mods.toml`'s `[[mixins]]` for exploded-directory
      containers, so every Forge mixin was silently inert for the whole project until `00c39e0` added
      the flag to every loom run. Verify a Forge mixin actually fires the day you add the first one.
- [ ] **`loom.mods` is a single global mapping consumed by every run, not per-run.** Associating the
      `testmod` source set with the mod for the gametest run put testmod's split dirs on
      `runClient`'s and `runData`'s classpath too, producing
      `IllegalArgumentException: Invalid module name: '' is not a Java identifier` in FML mod
      discovery. This burned three commits (`d75f3e0` flagged it as an unrelated pre-existing blocker,
      `2a5b826` gated it by task name, `cef7257` found it was **one bug, not two** and replaced the
      association entirely by redirecting `compileTestmodJava` into main's classes dir). Needs
      `dependsOn(processResources)` and `jar { exclude '**/gametest/**' }`.
- [ ] **`ideaSyncTask` does not overwrite an existing run-configuration XML** — it only writes missing
      ones, reports success, and touches the mtime while leaving content alone. Editing the build
      script and re-syncing *looks like it does nothing*. Delete the stale
      `.idea/runConfigurations/*.xml` first. Recorded in `0c1119d` as "the gotcha that cost the most
      time here."
- [ ] **Put run system properties on `loom.runs`, not on the Gradle task's `jvmArgs`.** Loom generates
      IDE run configs from `RunConfigSettings` and never reads task `jvmArgs`, so `./gradlew` runs work
      while the IDE's run config fails (`0c1119d`). Same trap for any file a run depends on: generate
      it at *configuration* time, because an IDE run has only a "Make" step and won't execute a
      `dependsOn` task.
- [ ] **`DelayedEvents` is ticked from both client and server threads in singleplayer.** A plain
      `ArrayList` throws `ConcurrentModificationException` between `tick()` and `queueDelayedEvent()`.
      Confirmed on all three loaders (`be402c6`). → `CopyOnWriteArrayList`. **This bug is live on
      `mc/1.21.1` today** — see section D.
- [ ] **Fabric `PlayerBlockBreakEvents.BEFORE` treats `false` as "cancel".** 26.1.2 returned a
      gameplay method's own boolean straight through, so a hook that returns `true` only ~1% of the
      time vetoed nearly every block break in the game (`f7c5890`). Audit the return-value polarity of
      **every** fabric-api callback — several are inverted relative to the NeoForge event they replace.
- [ ] **`Minecraft.getInstance()` is null during Fabric's `onInitializeClient`.** Anything needing the
      live client instance (e.g. registering against `Minecraft`'s `BlockColors`) must be deferred to
      `ClientLifecycleEvents.CLIENT_STARTED` (`f7c5890`).
- [ ] **Block/item colour handlers have no automatic cross-loader equivalent.** NeoForge registers the
      brewing-cauldron water tint through its own colour-handler event; Fabric and Forge registered
      nothing and the water silently rendered with the fallback colour for the whole project until
      `f7c5890`. Extract the tint logic to `common` and register it explicitly on each loader.
- [ ] **Client-side block-entity state can depend on a single sync packet that may not land.** The
      cauldron's per-tick client resync was gated behind an active recipe, but crafting clears the
      active recipe the instant it finishes — leaving the client rendering an empty cauldron
      (`f7c5890`). Resync on a condition that outlives the operation.
- [ ] **`assets/minecraft/atlases/blocks.json` — a 1.18.2 port leftover that is still in this tree.**
      Its `directory` sprite sources list **every namespace's** `textures/<source>/` folder, not just
      the mod's, dragging vanilla's sub-16px particle textures into the *mipmapped* blocks atlas
      (`f5cd94d`). On 26.1.2 this crashed startup outright. See section D.
- [ ] **`SoulMateEffect` redirects damage/healing onto the entity being hurt**, because the entity
      registers its own id in the global `soulMates` set — `.hurt()` re-fires the damage pipeline and
      recurses until `StackOverflowError` (`9ea6946`). Live on 1.21.1; see section D.
- [ ] **Game tests found bugs that compiling and unit tests both missed**, including in the tests
      themselves (`963a909`: `spawnItem(Item, BlockPos)` already converts a structure-relative
      position, so an extra `helper.absolutePos()` spawned the item millions of blocks away;
      `CropBlock.randomTick` silently no-ops below raw brightness 9). Phase 12 is not optional polish.

### B. Gotchas that **invert** on 1.21.1 — do not copy the 26.1.2 fix

- [ ] **Refmaps.** 26.1.2's production bug (`243ac95`) was declaring `"refmap": "potionsplus.refmap.json"`
      that **no build step generated** — correct there, because unobfuscated Mojang mappings never need
      one. **1.21.1 is obfuscated: the refmap is required and must actually be produced.** The 26.1.2
      fix (delete the declaration) is exactly wrong here. What transfers is the *symptom*:
      **a refmap problem is invisible in dev and silently drops every mixin in production.**
- [ ] **Mixin `compatibilityLevel`.** 26.1.2 had to *downgrade* `JAVA_25` → `JAVA_21` because stock
      `org.spongepowered:mixin:0.8.7` (pinned in Forge's `version.json`, outside the mod's control)
      only knows up to `JAVA_21`. **1.21.1 already declares `JAVA_21`, so this is a non-issue** — just
      never raise it. Expect a harmless `higher than the maximum level supported (JAVA_13)` warning.
- [ ] **The dev-side Mixin fork swap.** 26.1.2 pinned Forge's dev Mixin resolution to Fabric's
      `sponge-mixin` because Architectury's `mixin-patched:0.8.7.12` fork stops at `JAVA_21`
      (`f5cd94d`). At `JAVA_21` that resolutionStrategy is unnecessary. Don't port it preemptively.
- [ ] **AXFORM / `architectury.naming.*`.** Needed only because MC 26.1+ is unobfuscated and Forge's
      runtime access-transformer service had no mappings. **1.21.1 remaps normally — not needed.**
      What *does* transfer is the delivery lesson in section A (`loom.runs`, not task `jvmArgs`).
- [ ] **Forge game-test registration.** 26.1.2 needed a bespoke `RegistryLoadTaskMixin` because
      `Registries.TEST_INSTANCE` is a dynamic per-world datapack registry that `RegisterEvent` never
      fires for, and Forge ships no equivalent of NeoForge's `RegistryDataLoader` patch (`2eaea41`).
      **1.21.1 predates the TEST_INSTANCE registry** — game tests there are annotation-scanned via the
      older `GameTestRegistry` path, so `@GameTest` + `@GameTestHolder` should just work. Verify early;
      if it does, this plan avoids the single hardest problem the 26.1.2 branch hit.

### C. Gotchas to verify against Forge 52.1.2 specifically

- [ ] **`@Mod` constructor injection.** Forge 26.1.2 injects `FMLJavaModLoadingContext` (not
      `FMLModContainer`, and `ModLoadingContext.get()` was removed) — 26.1.2 hit
      `NoSuchMethodException: PotionsPlusForge.<init>()` before getting this right (`f5cd94d`).
      **1.21.1's Forge 52.x is an older API generation**; check what its `@Mod` constructor accepts
      before writing the entrypoint.
- [ ] **Production mixin-config discovery.** Forge 64.1.0 has *no* `[[mixins]]`-in-`mods.toml` parsing
      in production, and Mixin's own platform agent rejects every container — the **only** working path
      was the `MixinConfigs` **manifest attribute** on the jar (`243ac95`). Forge 52.x may still parse
      `[[mixins]]`. **Test by installing a packaged jar, not by reading docs** — this failed silently
      in production while every dev run passed.
- [ ] **How recipes reach the client.** 26.1.2's NeoForge had a built-in `RecipeContentPayload`;
      Fabric and Forge had nothing, so custom runtime recipes never reached clients and a whole new
      `ClientboundSyncRuntimeRecipesPacket` had to be written — **batched at 64 recipes to stay under
      the 1 MiB payload cap**, with post-processing deferred to the last batch and no-op'd on
      integrated servers (`00c39e0`). Check early how this branch's injected recipes reach clients on
      each loader; if the answer is "a NeoForge built-in", budget the same work.

### D. Bugs already live in this tree — fix them before or during the split

Verified present in `mc/1.21.1` @ `b4fc36b` on 2026-09-01. All three are loader-agnostic `common`
code, so fixing them **before** Phase 1 means fixing them once instead of reviewing them three times.

- [x] `utility/DelayedEvents.java:15` — `private static final List<DelayedEvent> delayedEvents = new
      ArrayList<>();` → `CopyOnWriteArrayList`. Ticked from both threads in singleplayer.
      **Fixed 2026-09-01.**
- [x] `effect/SoulMateEffect.java:70,92` — both `for (int soulMate : soulMates)` redirect loops
      include the entity's own id (added by `onPotionAdded` at line 133). Skip self in both loops, or
      any two Soul Mate entities crash the server with `StackOverflowError` on any damage.
      **Fixed 2026-09-01.**
- [x] `assets/minecraft/atlases/blocks.json` — byte-identical to the file 26.1.2 had to delete: both
      `mob_effect/` and `particle/` `directory` sources on the mipmapped blocks atlas. On 1.21.1 the
      older atlas code may not hard-crash the way 26.1.2's `GpuDevice` mip check did, so **verify the
      symptom before assuming the fix** — but the file is wrong either way (it stitches a few hundred
      vanilla particle sprites into the blocks atlas). 26.1.2's fix: delete `blocks.json` (vanilla's
      `block/` source already covers it) and add an `items.json` at mip 0 carrying `mob_effect/` plus
      only the specific `particle/` sprites actually referenced by generated item models.
      **Fixed 2026-09-01, but NOT via 26.1.2's exact fix.** 26.1.2's `items.json` assumes MC
      1.21.4+'s split item-model atlas, which does not exist on 1.21.1 — item icons here still
      resolve through the `blocks` atlas (vanilla's own `blocks.json` bundles both `block/` and
      `item/` directory sources for exactly this reason). Created `items.json` first, verified it
      does nothing on 1.21.1, deleted it. First attempt scoped `mob_effect/` down to 22 `single`
      `potionsplus:mob_effect/*` sources — **wrong**: `potionsplus:potion_effect_icon`'s generated
      item model needs *vanilla* mob-effect icons too (a generic effect-icon item covering every
      effect, not just this mod's), and `runClient` immediately logged 38 `Missing textures`
      warnings for `minecraft:mob_effect/*`. Corrected to keep `mob_effect` as a namespace-wide
      `directory` source (that part of the original file was fine) and drop only `particle`, which
      really was unused dead weight — confirmed via `grep` that nothing references
      `<namespace>:particle/*` from an item model, and vanilla particles already have their own
      `particles.json` atlas. Verified via `runClient`: the missing-texture warnings for vanilla
      `mob_effect/*` are gone; only the pre-existing, unrelated `generic_icon` warning for
      `potionsplus:particle/sga_{a,b,c,d}` remains (those four texture files don't exist on disk at
      all — a separate, likely-dead-code gap, out of scope for this bug).

---

## Phase 0 — Build-system swap (ModDevGradle → architectury-loom)

**New vs 26.1.2.** Change *one* variable: the build system. All 294 source files stay exactly where
they are, in a single `neoforge/` module. This keeps the "did the build swap break it?" question
separable from the "did the source split break it?" question in Phase 1.

**Files:** `settings.gradle`, `gradle.properties`, `build.gradle` (root), `neoforge/build.gradle`,
`gradle/wrapper/gradle-wrapper.properties`, new `neoforge/gradle.properties`.

- [x] `gradle/wrapper/gradle-wrapper.properties`: Gradle → `9.5.0`.
- [x] `settings.gradle`: replace the MinecraftForge-only `pluginManagement` block with the four-repo
      form (fabricmc, architectury, files.minecraftforge.net, maven.neoforged.net + gradlePluginPortal).
      Keep `rootProject.name = 'potionsplus'`. **`include 'common'` and `include 'forge'` are already
      in the file but the directories do not exist** — the build is currently broken for those two
      names. Reduce to `include 'neoforge'` for this phase; re-add the others in Phases 1 and 3.
      **Additional fix not in the original plan:** the old moddev-era `settings.gradle` also had
      `plugins { id 'org.gradle.toolchains.foojay-resolver-convention' version '0.7.0' }`. This plugin
      is the root cause of a multi-hour Gson/reflection rabbit hole (see the progress log entry below)
      and had to be **removed entirely**, not carried forward.
- [x] Root `build.gradle`: drop `net.neoforged.moddev`; add
      `dev.architectury.loom` `1.17-SNAPSHOT` (apply false) + `architectury-plugin` `3.5-SNAPSHOT` +
      `com.gradleup.shadow` `8.3.6` (apply false). Add the `architectury { minecraft = … }` block,
      the `subprojects` block applying loom + architectury-plugin, `base.archivesName`,
      `loom { silentMojangMappingsLicense() }`, `minecraft` + `mappings loom.officialMojangMappings()`
      deps, Java 21 toolchain, `options.release = 21`, and the existing jar-manifest block.
      `com.gradleup.shadow` is declared but not yet applied anywhere — Phase 1 wires it once `common`
      exists.
- [x] `gradle.properties`: rewrite to the 26.1.2/apt-ores key set — `mod_id`, `mod_name`,
      `mod_version`, `mod_description`, `mod_authors`, `mod_license`, `mod_icon`, `maven_group`,
      `archives_name`, `enabled_platforms = neoforge,fabric,forge`, `minecraft_version = 1.21.1`,
      the loader versions above, and the `mod_*_version_range` interpolations. Use **26.1.2's
      `gradle.properties` as the template** and swap the version values (Decision 4). Bumped
      `neoforge_version` 21.1.125 → 21.1.209 per the toolchain table above.
- [x] **Drop Parchment** (`parchment_minecraft`/`parchment_version`, `mapping_channel`,
      `mapping_version`, `neo_form_version`). Loom uses `officialMojangMappings()`. If parameter
      names are wanted back later, layer Parchment through `loom.layered { … parchment(…) }` — but
      apt-ores 1.21.1 does not, and matching it keeps the reference build usable.
- [x] **Drop `org.gradle.configuration-cache=true`** from `gradle.properties` — loom/architectury
      are not configuration-cache clean. (26.1.2's properties file has no such line.)
- [x] `neoforge/gradle.properties` (new): `loom.platform = neoforge`.
- [x] `neoforge/build.gradle`: rewrite from `net.neoforged.moddev` to the architectury shape — crib
      `apt-ores-worktrees/mc-1.21.1/neoforge/build.gradle`: `architectury { platformSetupLoomIde();
      neoForge() }`, `neoForge "net.neoforged:neoforge:…"`, `processResources` expanding
      `META-INF/neoforge.mods.toml`, and `loom.runs` for `client`/`server`/`data`/`gametest`.
      **Deviation:** no `shadowJar`/`remapJar { inputFile.set … }` wiring yet — without a `common`
      module there's nothing to shade, and architectury-loom auto-wires a plain `remapJar` off the
      normal `jar` task on its own. The `common`/`shadowBundle` configurations block is deferred to
      Phase 1 alongside the `common(project(':common'))` deps, per the plan's own instruction not to
      add them yet.
- [x] `loom { mixin { defaultRefmapName = "potionsplus-refmap.json" } }` — **1.21.1 is obfuscated, so
      mixins need a refmap.** The existing `potionsplus.mixins.json` already declared
      `"refmap": "potionsplus.refmap.json"`; reconciled to `"potionsplus-refmap.json"` (dash) to match.
- [x] Port the JEI dependency block: `compileOnly "mezz.jei:jei-${minecraft_version}-neoforge-api:…"` /
      `runtimeOnly "mezz.jei:jei-${minecraft_version}-neoforge:…"` (plain, not `modCompileOnly` —
      JEI's NeoForge artifacts ship already in the runtime mapping namespace, no remap needed), plus
      the BlameJared + ModMaven repositories in the `subprojects` `repositories` block.
- [x] **Known casualty — record it, fix it in Phase 12:** moddev's `unitTest { enable() }` has no
      direct architectury-loom equivalent. It is what makes `src/test` JUnit work today (it wires
      NeoForge's junit fixtures and the `-Dfml.junit.argsfile` that lets `Bootstrap.bootStrap()`
      survive). Losing it breaks `AlchemyTestBase` and `PotionContentsAccessTest` — **the two things
      the backport's Phase 5 just stood up.** `:neoforge:test` is confirmed red as expected (`Bootstrap`
      failure) and stays red until Phase 12. The tests were not deleted or altered.

      > **Amended 2026-09-01 — the "no equivalent, therefore blocked" reading was too pessimistic.**
      > 26.1.2 runs the identical 12 unit tests under architectury-loom with **no** loom test
      > configuration at all: junit deps plus `useJUnitPlatform()` in `common/build.gradle`, and
      > Minecraft arrives on the test classpath from Loom itself, with `AlchemyTestBase` bootstrapping
      > registries on its own. Nothing needs to be rebuilt to replace `unitTest { enable() }`. As of
      > Phase 1 the tests live in `common/src/test` while the junit wiring is still in
      > `neoforge/build.gradle`, so the red is at least partly plain misplacement. See Phase 12.
- [x] Keep `sourceSets.main.resources { srcDir 'src/generated/resources' }` and the `testmod`
      sourceSet wiring (translated to loom's `loom.mods` form).
- [x] **New, not in the original plan — durable per-project JDK pinning.** Added
      `gradle/gradle-daemon-jvm.properties` (`toolchainVersion=21`, tracked in git). The machine's
      *global* `~/.gradle/gradle.properties` pins `org.gradle.java.home` to a JDK 25 install (for the
      MC 26.1.2 branches); a project-level `gradle.properties` `org.gradle.java.home` does **not**
      override this (daemon JVM selection happens before project properties are read — confirmed via
      `./gradlew --version`). `gradle/gradle-daemon-jvm.properties` is the mechanism that actually
      does override it per-project. Every sibling MC 1.21.1-era project should get the same file with
      `toolchainVersion=21`; MC 26+ projects should get one with `toolchainVersion=25` so neither
      family depends on the user's global default.

**Exit criterion:** `./gradlew :neoforge:build` produces a working NeoForge jar with all 294 files
still in `neoforge/`, and `./gradlew :neoforge:runClient` reaches the main menu. `:neoforge:test` is
knowingly red (tracked to Phase 12). **Nothing else has moved.**

**✅ Met 2026-09-01.** `:neoforge:build -x test` green; `:neoforge:test` red as expected
(`Bootstrap.bootStrap()` failure, matching the known-casualty note); `:neoforge:runClient` renders
every vanilla + mod atlas without a missing-texture regression and stays up without crashing. All
294 source files remain in `neoforge/`; no `common/`/`forge/` sources exist yet.

**Not required for this exit criterion, tried anyway, partially working:** `:neoforge:runGametest`
now boots the headless game-test server cleanly under the new build (mod discovery, mixins, JEI all
load correctly) but fails with `IllegalArgumentException: No test functions were given!` — the
`testmod` source set's `@GameTest` methods aren't being discovered. This is exactly the
`loom.mods`/testmod-classpath subtlety the plan's own Phase 12 section already flags as needing the
`compileTestmodJava`-redirect fix (`dependsOn(processResources)`, `jar { exclude '**/gametest/**' }`)
— not a Phase 0 regression, just not solved yet. Left for Phase 12 rather than reverse-engineering
loom's exact `RunConfigSettings` gametest wiring now.

---

## Phase 1 — Source split into `common/`

**New vs 26.1.2.** The single largest phase. 294 files, of which **118 import `net.neoforged.*`**.

- [x] Create the `common/` module: `common/build.gradle` (`architectury { common
      rootProject.enabled_platforms.split(',') }`, `loom.accessWidenerPath`, `loom.mixin
      .defaultRefmapName`, `modImplementation fabric-loader` for the `@Environment` annotations,
      `dev.architectury:architectury-injectables:1.0.13`). Added `include 'common'` to
      `settings.gradle`. **Deviation:** no `common/gradle.properties` — apt-ores' reference build
      doesn't have one either (`common` isn't itself a `loom.platform`), so there's nothing to put in it.
- [x] `neoforge/build.gradle`: added `common(project(path: ':common', configuration: 'namedElements'))
      { transitive = false }` + `shadowBundle project(path: ':common', configuration:
      'transformProductionNeoForge')`, the `configurations` extendsFrom block, and
      `processResources { from project(":common").sourceSets.main.resources }`.
      **Deviation (see exit-criterion note):** `runtimeClasspath`/`developmentNeoForge` deliberately do
      **not** `extendsFrom common` — only `compileClasspath` does.
- [x] **Moved the loader-clean files first** (verified via `git ls-files '*.java' | xargs grep -L
      'net\.neoforged'`, 162 of 279 `neoforge/src/main/java` files at Phase 0's tip). Built after each
      batch, not at the end.
- [x] Triaged the 118 `net.neoforged`-importing files, plus every "clean" file that turned out to be
      transitively coupled to one of them, into common vs. neoforge-remainder. **Bucket table below
      reflects what actually happened, not the pre-triage estimate** — see the note under the exit
      criterion for why the split ended up narrower than planned:

  | Bucket | Owned by | What Phase 1 actually did |
  |---|---|---|
  | `@EventBusSubscriber`/`@SubscribeEvent` classes | **Phase 7** | Left in `neoforge/`, unchanged. Also pulled back the ~14 mixin classes here (see note) even though most have zero `net.neoforged` imports — mixin *class* discovery itself turned out to require living in `neoforge/`, independent of the bucket table's original reasoning. |
  | Registration hubs (`Blocks`, `Items`, `Potions`, `Attributes`, `Entities`, `Particles`, `Sounds`, `LootItemConditions`, `LootItemFunctions`, `NumberProviders`(*), `DataComponents`, `MenuTypes`, `Advancements`, and all `core/blocks/*`/`core/items/*` sub-hubs) | **Phase 4** | Left in `neoforge/` — the `DeferredRegister`→`BiFunction` conversion is Phase 4's named job, not Phase 1's. **Exception:** `MobEffects`, `Recipes`, `NumberProviders`, and `Potions`' registration entry point were converted now anyway (see note) because too much unrelated common code depended on their `Holder<T>` statics to defer them. |
  | Genuinely NeoForge-API (`DataAttachments`, `PotionsPlusConfig`, `AbstractRegistererBuilder`/`IModelGenerator`/datagen-model-generator builder DSL) | **Phase 8 / Phase 2** | Left untouched in `neoforge/` — **no common code ends up depending on either `DataAttachments` or `PotionsPlusConfig` after this phase**, so there was nothing to split; both are genuinely still whole-class NeoForge citizens for now (see judgment-call note). The block/item builder+model-generator DSL (`AbstractRegistererBuilder`, `IModelGenerator`, `BlockModelUtility`, `ItemModelUtility`, `ItemOverrideUtility`, and the hub files that wire them) turned out to be far more `BlockStateProvider`-coupled than the bucket table assumed and moved back to `neoforge/` wholesale — see note. |
  | Datagen | **Phase 10** | Stayed in `neoforge/` permanently, as planned. |

- [x] **`@OnlyIn(Dist.CLIENT)` → `@Environment(EnvType.CLIENT)`**: not needed — no file that ended up
      in `common` used `@OnlyIn`.
- [x] **`net.neoforged.neoforge.common.Tags` → `grill24.potionsplus.core.ConventionalTags`**: ported
      `ConventionalTags` from the 26.1.2 sibling (rewritten `Identifier`→`ResourceLocation`), and — since
      26.1.2's version had no `Items` nested class — added one (`SEEDS`, `CROPS`, `MUSHROOMS`,
      `FOODS_RAW_MEAT`, `FOODS_RAW_FISH`, `FOODS_VEGETABLE`, `FOODS_FRUIT`, `FOODS_GOLDEN`) with tag
      paths verified via `javap -c` against the real NeoForge 21.1.209 jar's `Tags$Items.class`.
- [x] `net.neoforged.neoforge.common.util.Lazy` → `grill24.potionsplus.utility.Lazy` (new, plain
      double-checked-locking `Supplier`-backed class).
- [x] `NeoForgeExtraCodecs`: no uses actually blocked common — never touched.
- [x] Resources: `assets/`, `data/`, `pack.mcmeta`, `potionsplus.png`, `potionsplus.mixins.json` →
      `common/src/main/resources/`. `META-INF/neoforge.mods.toml` and
      `META-INF/accesstransformer.cfg` stayed in `neoforge/`.
- [x] **Repackage the `neoforge/` remainder into `.neoforge` packages (Decision 4a) — DONE
      2026-09-01, pulled forward into Phase 1 per the user's "Pull it forward, finish Phase 1"
      decision.** This step was never in the original checklist, which is the omission that
      blocked the phase: Phase 1 was scoped as "move files *into* `common/`" and said nothing about
      renaming what stayed behind. All **107** of `neoforge/`'s files that shared packages with
      `common/` were `git mv`'d into `.neoforge` sub-packages across all **14** split packages
      (`block`, `blockentity`, `core`, `core.seededrecipe`, `data`, `data.loot`, `effect`, `event`,
      `item.tooltip`, `particle`, `recipe.abyssaltroverecipe`, `utility`, `utility.registration`,
      `utility.registration.item`), `package` declarations rewritten, and every cross-module import
      fixed. `:common:compileJava :neoforge:compileJava` is **green**. See the root-cause section
      below, and Decision 4a for the invariant and its verification command.
- [x] Moved `src/test` → `common/src/test` and `src/testmod` → `common/src/testmod`. Location only —
      `neoforge/build.gradle`'s test/testmod sourceSet wiring is untouched, so both are now `NO-SOURCE`
      from `neoforge/`'s point of view (harmless; `:neoforge:test` was already known-red since Phase 0).

**Exit criterion — MET, 2026-09-01.** The repackaging below is **done** and
`:common:compileJava :neoforge:compileJava` is green. All three remaining checks passed: (a) re-run
Decision 4a's `comm -12` package-intersection check came back empty, (b) `./gradlew clean` +
full `:common:build :neoforge:build -x test` is green (after killing two abandoned dev-run JVMs that
held `clean`'s file locks), and (c) `:neoforge:runClient` reaches the main menu — mod loads, JEI
initializes (1864 ingredients), networking live, no split-package `ResolutionException`. `:neoforge:test`
stays red, as already tracked since Phase 0.
`neoforge/src/main/java` is down to 177 files (101 moved to `common/`) — more than 26.1.2's 62 because
several buckets that 26.1.2 got to finish converting (registration hubs, the model-generator DSL) are
still whole-class NeoForge-side here, exactly as Phase 4/8 are supposed to inherit them.

**`:neoforge:runClient` does NOT yet reach the main menu — this exit criterion is not met.** The
cause was re-investigated 2026-09-01 (second session); **the first session's diagnosis was wrong and
has been replaced by what follows**, because the wrong one sends Phase 9 chasing a mixin bug that does
not exist.

### Root cause: 14 split packages between `common/` and `neoforge/` (NOT a mixin problem)

`common/` and `neoforge/` both contain classes in **14 of the same packages**, holding **107 of
`neoforge/`'s 179 files**. In a NeoForge dev run FML/securejarhandler puts each mod jar in its own
JPMS module, and **JPMS forbids two modules exporting the same package**. Every symptom below is
that one fact wearing a different hat.

| files | shared package |
|---:|---|
| 29 | `grill24.potionsplus.core` |
| 14 | `grill24.potionsplus.blockentity` |
| 10 | `grill24.potionsplus.data` |
| 9 | `grill24.potionsplus.effect`, `grill24.potionsplus.particle` |
| 8 | `grill24.potionsplus.block` |
| 7 | `grill24.potionsplus.event`, `grill24.potionsplus.utility` |
| 5 | `grill24.potionsplus.utility.registration.item` |
| 3 | `grill24.potionsplus.utility.registration` |
| 2 | `grill24.potionsplus.core.seededrecipe`, `grill24.potionsplus.item.tooltip` |
| 1 | `grill24.potionsplus.data.loot`, `grill24.potionsplus.recipe.abyssaltroverecipe` |

Regenerate the list by listing each module's package directories and intersecting them (`comm -12`
over the sorted, de-duplicated `find … -name '*.java'` paths with the source root and filename
stripped).

**This is dev-runtime only.** The shipped jar is unaffected — `shadowJar` merges `common` into a
single jar with no module boundary, which is why `:neoforge:build` is green throughout.

### Three wirings tried; all three fail, for two different reasons

| `neoforge/build.gradle` wiring | Failure |
|---|---|
| Phase 1's original — only `compileClasspath.extendsFrom common`, plus `sourceSet project(':common').sourceSets.main` in `loom.mods` | `common` **never reaches the run classpath at all** → `ClassNotFoundException` |
| **Canonical** — `compileClasspath` + `runtimeClasspath` + `developmentNeoForge` all `extendsFrom common`, nothing extra in `loom.mods` (what apt-ores@1.21.1 and fishtastic both do) | `common`'s jar becomes module `generated_XXXXXXX` → `java.lang.module.ResolutionException: Modules generated_c819675 and potionsplus export package grill24.potionsplus.block to module neoforge` |
| Output-dirs instead of the jar (`sourceSets.main.runtimeClasspath += project(':common').sourceSets.main.output`) plus the `loom.mods` union | `common`'s dirs **are** on the classpath and the split-package error is gone, but still `ClassNotFoundException` — see the locator note below |

**The tree is left on the canonical wiring** (restored 2026-09-01). It is the correct target state, it
matches both reference mods, and it fails with the *informative* error rather than a misleading one.

### Facts established, so Phase 9 does not re-derive them

- **`loom.mods` contributes nothing to the run classpath.** It groups class roots for
  remapping/mod-detection only. Verified by reading the generated
  `neoforge/build/loom-cache/argFiles/runGametest`: under Phase 1's wiring its `-classpath` lists
  only `neoforge/build/{classes/java,resources}/{main,testmod}` — no `common` entry — and there is
  no `-Dfml.modFolders` anywhere in the arg file or in `.gradle/loom-cache/projects/neoforge/launch.cfg`.
- **NeoForge's userdev mod locator only folds a classpath entry into the `potionsplus` mod if that
  entry carries `META-INF/neoforge.mods.toml`.** `common`'s output dir does not, so even with its
  dirs explicitly on `runtimeClasspath` the log still reads `Found mod file "main"` (only
  `neoforge/`'s own output) and `common`'s classes stay unreachable from the mod's `ModuleClassLoader`.
  This is why the third wiring above cannot be made to work by any further build-file tweak.
- **The mixin annotation processor and the refmap are NOT involved.** `Reference map
  'potionsplus-refmap.json' … could not be read. If this is a development environment you can
  ignore this message` is benign. The actual failure is
  `MixinPreProcessorStandard.transformMemberReference` performing an ordinary classload of
  `MobEffects` and getting `ClassNotFoundException` — identical to what any non-mixin classload of a
  `common` class does. **Do not** revisit `loom.mixin.useLegacyMixinAp`; the previous session's
  experiment with it was chasing the wrong bug.
- **Mixin classes do NOT need to live in `neoforge/`.** The previous session concluded they did,
  after moving 14 of them to `common/` produced `The specified mixin 'grill24.potionsplus.mixin.BoatMixin' was not found`.
  That has the same single cause — `common` was not on the run classpath, so the mixin class itself
  was unloadable. **fishtastic (`D:\GitHub\fishtastic`) keeps its entire mixin set and
  `fishtastic.mixins.json` in `common/`** and runs fine. Once the packages are unsplit, expect the
  mixins to be shareable — which matters, because Fabric and Forge will need them.
- Both reference mods avoid split packages *structurally*: their platform code lives under
  `….neoforge.*` / `….fabric.*` and never in a package `common` also occupies. apt-ores has no
  mixins at all, so it validates the build wiring but not the mixin question; fishtastic validates
  both (it is MC 26.1.2 / loom-no-remap, so its build file differs in the remap details only).
- The `common:remapJar` disable and the `ConventionalTags`/`Lazy` ports from the first session are
  unrelated to this and remain correct.

### The fix — APPLIED in Phase 1, 2026-09-01 (was "The fix Phase 9 must apply")

Move each split package's `neoforge/` residents into a `.neoforge` sub-package — `grill24.potionsplus.block`
→ `grill24.potionsplus.block.neoforge`, and so on for all 14 — matching the convention this branch
already uses for `core.neoforge`, `core.neoforge.potion`, `event.neoforge` and `persistence.neoforge`,
and satisfying Decision 4. **This is now DONE** (107 `git mv`s, package decls + all imports rewritten,
compile green). Phases 4/7/8 will subsequently move many of these files into `common/` anyway, so some
of the renaming is transitional by design.

**How the import pass was done (so a fresh context does not re-derive it):** the `git mv` broke
same-package references and `core.*`-wildcard dependencies in three distinct ways, each fixed
differently —
1. **Same-package gaps** (moved files referencing classes still in the old/common package): added
   explicit `import` lines for each common class (e.g. moved `blockentity/*` files importing
   `grill24.potionsplus.blockentity.{InventoryBlockEntity, ISingleStackDisplayer, …}`, moved
   `effect/*` importing `IEffectTooltipDetails`, moved `utility/*` importing `ModInfo`, moved
   `utility.registration/*` importing `{IDataGenerator, ILootGenerator, IRecipeGenerator, IRegisterer}`,
   moved `core.seededrecipe/*` importing `{PpIngredient, PpMultiIngredient}`).
2. **Broken wildcard reliance** (files that imported `core.*`/`block.*`/`blockentity.*`/`utility.*`
   and referenced classes that moved into the `.neoforge` twin): added explicit `.neoforge` imports
   or a sibling `….neoforge.*` wildcard. Wildcards were only ADDED where the common and `.neoforge`
   packages share zero simple names (verified: `block`, `blockentity`, `particle`, `utility` twins
   are disjoint) — adding `core.neoforge.*` was **never** safe because both `core` and `core.neoforge`
   declare `PotionsPlus`, so any file also importing `core.*` got explicit `core.neoforge.{Blocks,
   Items, Advancements, Particles, Sounds, …}` imports instead.
3. **Fully-qualified old-location references** (e.g. `grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY`
   in `ClotheslineBlock`, `grill24.potionsplus.core.Items.` in `BlockEntityBlocks`/`LangProvider`,
   `grill24.potionsplus.core.Attributes` in `LivingEntityMixin`): string-rewritten to the `.neoforge`
   path.

**Two non-obvious traps hit and resolved:**
- **The same-package shadowing trap.** Files living in `core.neoforge` that reference the *common
  constants class* `PotionsPlus.LOGGER`/`SERVER`/`worldSeed`/`Debug` now resolve bare `PotionsPlus`
  to the **entrypoint** (`core.neoforge.PotionsPlus`, same package, which has none of those fields).
  Fix: add `import grill24.potionsplus.core.PotionsPlus;`, which per JLS 7.5.1 shadows the same-package
  entrypoint inside that file. Applied to `BlockRenderLayers`, `ClientCommands`, `CommonCommands`,
  `ServerLifecycleListeners` (the latter also needs `core.Recipes`). Verified none of those files use
  entrypoint-only members.
- **`LangProvider` vanilla-`Items` collision.** Adding `core.neoforge.Items` collided with its existing
  `import net.minecraft.world.item.Items;`. Correct fix: remove the added import, keep the fully-qualified
  `grill24.potionsplus.core.neoforge.Items.ITEMS` rewrite.

**Cascades worth knowing:** `ClotheslineBlock` missing its `HorizontalDirectionalBlock`/`ClotheslinePart`
imports broke `ClotheslineBlock.FACING` in *other* files too (`ClotheslineBehaviour`) — a broken
supertype pollutes every external access to inherited statics. Fixing the block's own imports cleared
both. Similarly, once the top ~100 same-package gaps were fixed, a second compile surfaced ~15 more
errors in files the first pass had masked (masking is why the earlier 124-line error file was
incomplete — trust the *fresh* compile, not a cached error list).

**Phase 9 exit criterion (unchanged in spirit, now concrete):** no package is occupied by both
modules, and `:neoforge:runGametest` boots and runs the gametests with `LivingEntityMixin` still
reading `MobEffects.SLIP_N_SLIDE` from `common/`.

### Verification plumbing repaired this session (kept)

- `neoforge/build.gradle`'s testmod sourceSet pointed at `neoforge/src/testmod/java`, which Phase 1
  had emptied — `:neoforge:runGametest` was silently **`NO-SOURCE` and verifying nothing**. It now
  points at `project(':common').file('src/testmod/java')`, the same shape fishtastic uses.
- The gametests had not been updated for Phase 1's `Recipes` → `RecipesRegistrar` and `Potions` →
  `PotionsRegistrar` splits — 9 compile errors in `AlchemyGameTests` and `BrewingCauldronGameTests`.
  Fixed; `:neoforge:compileTestmodJava` is green. (Those two files now import from
  `core.neoforge.*` while living in `common/src/testmod` — a layering wart that resolves itself when
  Phase 4 finishes the registration-hub conversion.)
**Two judgment calls flagged for later phases, per Decision 2's note that `DataAttachments` and (this
branch's addition) `PotionsPlusConfig` have no 26.1.2 precedent:**
- **`DataAttachments`** — still 100% NeoForge-side (`AttachmentType`, genuinely NeoForge-only API, the
  Phase 8 "genuinely NeoForge-API" bucket by design). No common file ended up needing it this phase, so
  there was no forcing function to design its common-side interface yet. Phase 8 gets a clean slate,
  not a half-finished abstraction to unpick.
- **`PotionsPlusConfig`** — same story: no common file reads a config value yet, so it's still a single
  NeoForge class with no fabric/forge siblings and no common accessor interface. When Phase 4/8
  registration-hub work starts pulling config-gated logic into `common`, expect this to need the same
  `IPotionsPlusConfig`-style split 26.1.2 presumably used (each loader has its own config class per the
  sibling repo's file layout — `fabric/.../config/fabric/PotionsPlusConfig.java` etc, no common
  variant), but nothing here forced that decision.

---

## Phase 2 — Platform abstraction layer

**New vs 26.1.2** (it inherited this from the 1.21.5 → 26.1.2 port).

- [x] `common/.../platform/Platform.java` — `@ExpectPlatform` static methods. **done 2026-09-01.** Start from **26.1.2's
      7-method set** and reconcile against what Phase 1 actually surfaced:
      `isClient`, `isDevelopmentEnvironment`, `getChorusFruitTeleportTarget`,
      `onServerPlayerHeldItemChanged`, `fireCropGrowPost`, `getPotionDrinkTimeTicks`,
      `getPotionDrinkCooldownTimeTicks`. (These 7 re-verified against the 26.1.2 tree 2026-09-01 —
      the list is exact.) **Expect this set to be larger on 1.21.1** — the event
      surface here is 36 classes, not 16, so some listeners that 26.1.2 could express as pure
      fabric-api callbacks may need a platform hook. Enumerate honestly; don't force the 7.
- [x] `common/.../platform/PacketNetwork.java` — 5 `@ExpectPlatform` methods. **done 2026-09-01.** Verified against the
      26.1.2 tree 2026-09-01, use these exact signatures (an earlier draft of this plan guessed a
      "send-to-all" that does not exist):
      `sendToPlayer(ServerPlayer, CustomPacketPayload)`,
      `sendToPlayers(ServerPlayer, CustomPacketPayload first, CustomPacketPayload[] rest)`,
      `sendToPlayersTrackingEntityAndSelf(ServerPlayer, CustomPacketPayload)`,
      `sendToServer(CustomPacketPayload)`,
      `sendToPlayersTrackingChunk(ServerLevel, ChunkPos, CustomPacketPayload)`.
- [x] `common/.../network/PacketContext.java` — the common interface every loader's context wrapper
      implements (`enqueueWork`, `player`, `disconnect`). **done 2026-09-01 — corrected from this
      plan's 4-method guess: the 26.1.2 tree's `PacketContext` has only these 3; `isServerSide`
      does not exist there (mirror discipline — the tree wins).** **Note the package: 26.1.2
      puts this under `network/`, NOT `platform/`** (an earlier draft of this plan said `platform/`;
      the tree is authoritative). Loader wrappers likewise live in `network/<loader>/`:
      `network/fabric/FabricPacketContext`, `network/forge/ForgePacketContext`,
      `network/neoforge/NeoPacketContext`. Only `Platform` and `PacketNetwork` live in `platform/`.
      Crib fishtastic's `IPacketContext` for the method shapes.
- [x] Rewrite the 12 `network/*Packet.java` handlers against `PacketContext` instead of
      `IPayloadContext` (12 direct imports today).
- [x] `neoforge/.../platform/neoforge/{PlatformImpl,PacketNetworkImpl}.java` + `NeoPacketContext` —
      the NeoForge side of every method, lifted from the code Phase 1 left behind.

**Exit criterion:** `:common:build :neoforge:build` green; `common/` has **zero** `net.neoforged`
imports (`git grep -c 'net\.neoforged' common/src/main/java` → 0); NeoForge runtime unchanged.
**MET 2026-09-01** — `:common:compileJava :neoforge:compileJava` BUILD SUCCESSFUL;
`:common:build :neoforge:build -x :common:compileTestJava` BUILD SUCCESSFUL. The full
`:common:build` stays red on `:common:compileTestJava` only — the known Phase-12 junit red (junit
wiring still lives in `neoforge/build.gradle`; see Phase 12 and the Phase 1 closure). `net.neoforged`
grep → 0 and `comm -12` → empty. "NeoForge runtime unchanged" holds by construction — every
converted call delegates to the same NeoForge API it called before (`PacketDistributor.*`,
`EventHooks.onChorusFruitTeleport`, `NeoForge.EVENT_BUS`, `CommonHooks.fireCropGrowPost`,
`PotionsPlusConfig`); a dev-run smoke check is still recommended when convenient.

---

## Phase 3 — Fabric + Forge module scaffold

*(= 26.1.2 Phase 0's Fabric/Forge half.)* Crib `apt-ores-worktrees/mc-1.21.1/{fabric,forge}/build.gradle`
**verbatim** — it is a known-good 1.21.1 three-loader build.

- [x] `settings.gradle`: re-add `include 'fabric'` and `include 'forge'`.
- [x] `fabric/gradle.properties` → `loom.platform = fabric`; `forge/gradle.properties` →
      `loom.platform = forge`.
- [x] `fabric/build.gradle`: `architectury { platformSetupLoomIde(); fabric() }`;
      `configurations` with `compileClasspath`/`runtimeClasspath`/`developmentFabric` all
      `extendsFrom common`; deps `modImplementation fabric-loader` + `fabric-api` +
      `common(project(':common'), configuration: 'namedElements')` +
      `shadowBundle project(':common', configuration: 'transformProductionFabric')`;
      `processResources` expanding `fabric.mod.json`; `shadowJar` + `remapJar { inputFile.set
      shadowJar.archiveFile }`; `loom.runs` for `datagen` and `gametest`.
- [x] `forge/build.gradle`: same shape **plus** the Forge-only workarounds:
  - [x] `forge "net.minecraftforge:forge:$minecraft_version-$forge_version"` + the
        `maven.minecraftforge.net` repository.
  - [x] `compileClasspath.extendsFrom common` and `developmentForge.extendsFrom common`,
        **but NOT `runtimeClasspath.extendsFrom common`** — the JPMS split-package
        `ResolutionException` apt-ores documents in its `forge/build.gradle` comment block.
  - [x] `META-INF/mods.toml` (**not** `neoforge.mods.toml`), expanded by `processResources`.
  - [x] **DIVERGENCE from 26.1.2 — verified 2026-09-01.** 26.1.2's `forge/build.gradle` really does
        carry all three of these (`generateEmptyMappings` at line ~213, `architectury.naming.
        sourceNamespace`/`mappingsPath` run properties at ~258, and `output.resourcesDir =
        output.classesDirs.singleFile` at ~150/153), so the temptation to copy them is real.
        **Do not.** apt-ores' 1.21.1 Forge module needs none of them: the hack existed only because
        MC 26.1+ is unobfuscated and Forge's runtime AXFORM had no mappings to work from, and 1.21.1
        remaps normally. Add only if a real failure demands it.
  - [x] **The Forge `runtimeClasspath` asymmetry is real and is the one place Decision 4a's rule is
        not enough.** Verified: 26.1.2's `forge/build.gradle` has `compileClasspath.extendsFrom
        common` and `developmentForge.extendsFrom common` **and no `runtimeClasspath`** — while its
        `fabric` and `neoforge` modules have all three. Forge is the exception; do not "fix" it to
        match the others. Note this is a *different* problem from Decision 4a (which unsplits
        packages so `common` can be its own module); on Forge the entry has to be off the runtime
        classpath entirely.
- [x] `common/build.gradle`: `sourceSets.main.resources.srcDir('src/generated/resources')` (for Phase 10).
- [x] Placeholder `fabric.mod.json` and `META-INF/mods.toml` with correct ids/entrypoints.

**Exit criterion:** `./gradlew :fabric:build :forge:build` produce jars. Empty/stubbed content is fine.
**✅ met 2026-09-01** — `potionsplus-fabric-1.6.0.jar`, `potionsplus-forge-1.6.0.jar`,
`potionsplus-neoforge-1.6.0.jar` all produced (`./gradlew :fabric:build :forge:build :neoforge:build -x test` →
`BUILD SUCCESSFUL`).

**DIVERGENCE from 26.1.2 — verified 2026-09-01 (see VERIFIED API FACTS "RecipeInput/Container mapping collision").**
The mirror's `InventoryBlockEntity` declares `implements WorldlyContainer, RecipeInput`; this tree's does
**not**. On 1.21.1 the named mappings give `Container.getItem(int)`/`isEmpty()` and
`RecipeInput.getItem(int)`/`isEmpty()` identical names but different intermediary ids
(`method_5438`/`5442` vs `method_59984`/`59987`). TinyRemapper 0.14.0's class-less conflict key treats a class
implementing both as two different targets on one method → `:fabric:remapJar` "Unfixable conflicts"
(38-conflict set, identical across all attempted workarounds). `ignoreConflicts=true` is not a valid fix —
a method has one bytecode name, so it cannot override both interface entries after remapping. Resolution:
`InventoryBlockEntity` stays off `RecipeInput`; the one call site that passed a block entity as a
`RecipeInput` (`neoforge/.../BrewingCauldronBlockEntity.java:101`, `recipe.value().matches(this, …)` — the
only such site tree-wide, verified by grep) now passes `new ContainerRecipeInput(this)`
(`common/.../recipe/ContainerRecipeInput.java`, a `Container`→`RecipeInput` delegating adapter). The
future `fabric`/`forge` brewing-cauldron ports (Phase 8) must do the same.

---

## Phase 4 — Registration hubs (Fabric + Forge)

*(= 26.1.2 Phase 1.)* **Read 26.1.2's Phase 1 in full before starting** — its registration-order
analysis and hub file list transfer almost verbatim.

- [ ] **Finish the `init(BiFunction)` conversion** started in Phase 1. `core/blocks/*` and
      `core/items/*` already take `BiFunction<String, Supplier<T>, Holder<T>>`; `Blocks`, `Items`,
      `Potions`, `MobEffects`, `Attributes`, `Entities`, `Particles`, `Recipes`, `Sounds`,
      `MenuTypes`, `LootItemConditions`, `LootItemFunctions`, `NumberProviders`, `DataComponents`,
      `Advancements` must follow. The common half holds only `Holder<T>` statics + an `init(register)`
      method; the `DeferredRegister` lives in the loader module.
- [ ] `forge/.../core/forge/util/ForgeHolder.java` — the adapter. Use **26.1.2's version as the
      template**, then apply the 1.21.1 deltas from the VERIFIED API FACTS above: drop
      `getDelegate()`, drop `implements Supplier<T>` (inherited), add `is(Holder<T>)`.
- [ ] **`equals` must compare by resource key against *any* `Holder`, not just another `ForgeHolder`.**
      26.1.2 shipped a `getClass() == o.getClass()` guard, so the adapter could never equal the real
      `Holder.Reference` for the same entry — silently breaking every `contains()`/`equals()` check
      against holders pulled from a registry, with no crash to point at it (`83bf9a8`).
      `Holder.Reference` overrides neither `equals` nor `hashCode` (registries hand out one singleton
      per key), so the fix lives entirely on this side and accepts one-sided symmetry.
- [ ] **Plan a `RegistryMixin` that unwraps `ForgeHolder` during serialization — it is not optional.**
      (26.1.2's `mixin/forge/` holds **two** registry mixins — `RegistryMixin` here, and
      `RegistryLoadTaskMixin` for the gametest problem in Phase 12. Don't conflate them.)
      Because the adapter isn't a `Holder.Reference`, mod holders fail to *encode*, which on 26.1.2
      **crashed level saves** once data actually persisted (`00c39e0`). NeoForge patches
      `DeferredHolder` internally for exactly this; Forge does not. The failure appears long after
      registration looks healthy, so write the mixin alongside the adapter rather than waiting for the
      crash.
- [ ] **Fabric hubs** `fabric/.../core/fabric/` — all via `BuiltInRegistries.X` +
      `Registry.registerForHolder`. *Corrected 2026-09-01 against the 26.1.2 tree; the earlier list
      here was a guess that both missed real hubs and invented ones that do not exist.* The actual
      26.1.2 `core.fabric` contents are:
      `BiomeModifiers`, `Blocks`, `Capabilities`, `CommandArgumentTypes`, `CreativeModeTabs`,
      `DataComponents`, `FabricRegistration`, `Items`, `LootItemFunctions`, `LootModifiers`,
      `MenuTypes`, `NumberProviders`, `Packets`, `Particles`, `Recipes`, `ServerLifecycleListeners`,
      `Sounds` (plus the two entrypoints, Phase 6). Note in particular:
  - [ ] **`FabricRegistration` is the registration-order orchestrator** — the class that encodes the
        load-bearing order in the next bullet. It has no NeoForge counterpart, so it is easy to miss
        entirely; it is the single most important file in this phase.
  - [ ] `Capabilities`, `CommandArgumentTypes`, `LootModifiers` and `BiomeModifiers` are hubs too and
        were absent from this plan's earlier list. `CommandArgumentTypes` exists on **all three**.
  - [ ] There is **no** `core/fabric/potion/` package — `MobEffects`/`Potions` are not separate Fabric
        hubs. There *is* a single-file `core/fabric/blocks/`.
- [ ] **Forge hubs** `forge/.../core/forge/`: the same set **minus `BiomeModifiers`** (Forge uses the
      datapack JSON instead — see Phase 8) and **plus `Renderers`** (Phase 11), via
      `DeferredRegister.create(Registries.X, MOD_ID)` + `ForgeHolder.of(…)`, flushed with
      `DR.register(modEventBus)` (**`IEventBus`**). Verified 26.1.2 `core.forge`: `Blocks`,
      `Capabilities`, `CommandArgumentTypes`, `CreativeModeTabs`, `DataComponents`, `Items`,
      `LootItemFunctions`, `LootModifiers`, `MenuTypes`, `NumberProviders`, `Packets`, `Particles`,
      `Recipes`, `Renderers`, `ServerLifecycleListeners`, `Sounds`.
- [ ] **Fabric registration order.** Fabric registration is *immediate*, not deferred — order is
      load-bearing. Port 26.1.2's verified order verbatim:
      `PotionBuilder.potionFactory` → Advancements/Attributes/Entities/LootItemConditions →
      MobEffects → Potions → DataComponents → Items → Blocks → BlockEntities → DISPENSER assoc →
      Sounds → Particles → Recipes → MenuTypes/LootItemFunctions/NumberProviders → CreativeModeTab.
- [ ] **Known Fabric landmine (26.1.2 Phase 6 post-mortem, will recur):** `fabric-registry-sync-v0`
      eagerly builds each block's default-state collision-shape cache *synchronously inside*
      `MappedRegistry.register()`. Any `Block` subclass whose `getShape` dereferences a
      `Holder.value()` for a block registered later (`ClotheslineBlock` is the known offender) NPEs
      during its own registration. Fix by making those lookups lazy, not by reordering.
- [ ] Block-entity valid-blocks: NeoForge's `BlockEntityTypeAddBlocksEvent` (used for the
      `PRECISION_DISPENSER` ↔ vanilla `DISPENSER` association) → Fabric
      `((FabricBlockEntityType) BlockEntityType.DISPENSER).addSupportedBlock(block)` — **the
      0.116.7 method name, verified via javap; NOT `addValidBlock`** (a later fabric-api name).
      Forge equivalent to be identified.
- [ ] `SimpleParticleType` on Fabric: `new SimpleParticleType(false) {}` (ctor is `protected` in
      vanilla — verified) unless Phase 9's access-widener covers it.
- [ ] Creative tab: **NeoForge-only** `CreativeModeTab.builder()` no-arg patch — vanilla 1.21.1 needs
      `builder(CreativeModeTab.Row.TOP, 4)` (see VERIFIED API FACTS correction). NeoForge keeps
      `BuildCreativeModeTabContentsEvent`; Fabric/Forge populate via a `displayItems` lambda
      iterating `BuiltInRegistries.ITEM.entrySet()` filtered by namespace.
- [ ] Minimal `{fabric,forge}/.../platform/*/PlatformImpl.java` with at least `isClient` /
      `isDevelopmentEnvironment` — `AbstractRegistererBuilder.modelGenerator()` calls
      `Platform.isClient()` during hub class-load. Rest stubbed until Phase 5/8.

**Exit criterion:** NeoForge build still green; Fabric + Forge compile and load a world with blocks,
items, potions and effects present.

### Phase 4 — current execution plan (written 2026-09-02)

Fabric + Forge modules currently contain **zero Java** (only `fabric.mod.json` / `mods.toml`). The
neoforge-side hub conversion (tasks 8–12, done) is the content source; 26.1.2's `core/fabric/` +
`core/forge/` are the structural template ("own statics via `FabricRegistration.register(...)` /
`DeferredRegister` + `ForgeHolder.of(...)`, then a trailing static block propagates into the common
stub"). Remaining work in order:

1. **[task 3] ✅ DONE 2026-09-02** — `forge/.../core/forge/util/ForgeHolder.java` (26.1.2's verbatim
   minus `components()`/`areComponentsBound()`/`getDelegate()`/`implements Supplier<T>`; `Identifier` →
   `ResourceLocation`; key-based `equals` fixes `83bf9a8`) + `forge/.../mixin/forge/RegistryMixin.java`
   (ports verbatim — 1.21.1 has the private-instance `Registry.safeCastToReference`) + a minimal
   `potionsplus.forge.mixins.json` + `[[mixins]]` in `mods.toml`. Forge dev-runs still won't load it
   until Phase 9's `--mixin.config` wiring — noted, not done here.
2. **[task 4] ✅ DONE 2026-09-02 — 26 fabric files, `:fabric:compileJava` green.** All hubs written in
   `fabric/.../core/fabric/` + sub-hubs (`blocks/`, `items/`) + both entrypoints, content from the
   1.21.1 neoforge hubs, structure from 26.1.2. **Deviations from the 26.1.2 order/scope, each
   recorded against evidence:**
   - **Blocks-before-Items (1.21.1 eager-holder deviation).** 1.21.1 `BlockItem`/`ItemNameBlockItem`
     take a *concrete* `Block` and deref eagerly, and `ArmorItem`'s ctor eagerly derefs its material
     holder → fabric order is potionFactory → Advancements/Attributes/LootItemConditions → MobEffects
     → Potions → percentage attributes → **ArmorMaterials.init** → **Blocks.init()** → **Items.init()**
     → DISPENSER assoc → Sounds/Particles/Recipes/MenuTypes/LootItemFunctions/NumberProviders →
     CreativeModeTabs. (26.1.2's lazy Items-before-Blocks order cannot port.)
   - **Tab = `builder(CreativeModeTab.Row.TOP, 4)`, no `withSearchBar()`** — see VERIFIED API FACTS
     correction.
   - **Scope trimmed on 1.21.1-only grounds:** 9 effects + 8 potions deferred (their NeoForge-only
     effects); **DIAMOUR/GOLDEN_CUBENSIS flowers deferred** (same effects; no common code derefs the
     null stubs — verified); the 6 BE-block classes + their BEs deferred (neoforge-only
     `SimpleBlockBuilder`); `DynamicIconItems` deferred (loader-owned; only translation-string refs in
     common). Fabric Phase 4 registers: 5 ores, 6 decoration blocks, PRECISION_DISPENSER, 6 of 8
     flowers, 11 items, 14 portable effects, 23 portable potions + ANY_POTION/ANY_OTHER_POTION.
   - **`FabricBlockEntityType.addSupportedBlock(Block)`** — fabric-api 0.116.7 method name, NOT
     `addValidBlock` (verified via javap); 1.21.1 also lacks the vanilla `BlockEntityTypeAddBlocksEvent`.
   - **`FabricRegistration` needs a cast bridge:** `registerForHolder(Registry<R>, ResourceKey<R>, R)`
     can't infer the `<R, T extends R>` covariant form → `(Holder<T>) (Holder<?>) …` under
     `@SuppressWarnings("unchecked")`, preserving subtype registration (needed for wildcard
     `BlockEntityType<?>` registries).
3. **[task 6] ✅ DONE 2026-09-02** — fabric `PlatformImpl` (all 7) + `PacketNetworkImpl` (all 5)
   written (ported from the 26.1.2 fabric mirror; `getChorusFruitTeleportTarget` passthrough — fabric
   fires no chorus-fruit event; config values hardcoded to the NeoForge defaults pending Phase 8).
   Forge `PlatformImpl` (7) + `PacketNetworkImpl` (5) already existed from Phase 3. `:fabric:compileJava`
   green with them.
4. **[task 5] ✅ DONE 2026-09-02 — 29 forge files, `:forge:compileJava` green on the first pass.**
   Hubs in `forge/.../core/forge/`: `Blocks`/`Items`/`ArmorMaterials` + `blocks/{Ore,Decoration,
   BlockEntity,Flower}` + `items/{Ore,Brewing,Wreath}` + `Particles`/`Sounds`/`Recipes`/`NumberProviders`/
   `CreativeModeTabs` + empty-parity + stub hubs. **Deviations/verifications (all javap-evidenced):**
   - **`CreativeModeTab` — CORRECTED.** Forge 52.1.2 **does** patch in both the no-arg `builder()` and
     `withSearchBar()` (javap'd `minecraft-merged-srg-patched.jar`: `builder()` literal + `m_257815_(Row,int)`;
     Builder carries `withSearchBar()`/`withSearchBar(int)`). Forge tab = `builder(CreativeModeTab.Row.TOP, 4)`
     + `.withSearchBar()`, with vanilla `displayItems` enumerating the ITEM registry (Forge has no
     `BuildCreativeModeTabContentsEvent` — NeoForge-only).
   - **`SimpleParticleType(boolean)` is `protected` on Forge 52.1.2** (the plan's "public in the
     Forge-patched jar" claim was NeoForge-only) → anonymous subclass `new SimpleParticleType(false) {}`,
     same as fabric's vanilla classpath.
   - **`BlockEntityType.validBlocks` is `private final Set<Block>` on Forge** — no public mutation API →
     DISPENSER↔PRECISION_DISPENSER association **deferred to Phase 9** (mirrors the 26.1.2 Forge tree,
     which skips it entirely).
   - **Forge's `RegistryObject` does not implement `Holder`** → a bare `DR::register` method reference
     won't type-check against the common `init(BiFunction)` (unlike NeoForge's `DeferredHolder`) → every
     hub routes through `ForgeHolder.of(DR.register(...))`, via a shared `register(DeferredRegister<T>)`
     helper in `PotionsPlusForge`.
   - **Consolidated DRs live in the entrypoint** (26.1.2 mirror shape): TRIGGERS/ATTRIBUTES/MOB_EFFECTS/
     POTIONS/LOOT_ITEM_CONDITIONS on `PotionsPlusForge`; `ArmorMaterials.ARMOR_MATERIALS` on its own hub.
     ENTITIES/BLOCK_PREDICATE_TYPES/CONSUME_EFFECTS dropped (nothing to register on 1.21.1).
   - **Flush-order note:** at RegisterEvent Forge fires BLOCK before ITEM, ARMOR_MATERIAL before ITEM,
     MOB_EFFECT before POTION — but `Blocks.init()`/`Items.init()` must still run before the
     `DR.register(bus)` calls so the block-holder *fields* are non-null when the item suppliers run.
   - Scope trims carried from fabric: DIAMOUR/GOLDEN_CUBENSIS flowers, the 6 BE-blocks (PRECISION_DISPENSER
     included), DynamicIconItems deferred.
5. **Entrypoints ✅ DONE 2026-09-02** — fabric `PotionsPlusFabric` (`ModInitializer`, 10-step order) +
   `PotionsPlusFabricClient` existed from task 4; forge `PotionsPlusForge` (`@Mod`, no-arg ctor via
   `FMLJavaModLoadingContext.get().getModEventBus()`, potionFactory static block, 9-step order, all 14
   DRs registered on the bus). Compile green.
6. **Compile gate — GREEN 2026-09-02:** `:fabric:compileJava :forge:compileJava :neoforge:compileJava`
   → BUILD SUCCESSFUL; `comm -12` package-intersection **empty** for fabric/forge/neoforge vs common
   (Decision 4a holds). **RunClient smoke — ALL THREE PASS 2026-09-02:** fabric + forge both reach the
   main menu ("Potions Plus (Fabric|Forge) initializing" + "Sound engine started", zero crash markers;
   item/block registration proven by the item-model lookups the game attempts), neoforge regression clean
   after the hub refactor + mixin split. Clean three-loader build `BUILD SUCCESSFUL in 12s`. **Phase 4
   exit criterion MET; committed `d9b2cf4`.**
6a. **Mixin split — DONE 2026-09-02 (blocker found by the fabric runClient, invisible to the compile
   gate).** The fabric smoke crashed on the very first launch: common's `potionsplus.mixins.json` listed
   16 mixins, all living in `neoforge/` — fabric/forge inherit the config (via common resources) but not
   the classes. Fix (mirrors 26.1.2, which keeps every vanilla-targeting mixin in `common/` and gives each
   loader a small own config): **10 mixins now live in `common/`** (`grill24.potionsplus.mixin`, config
   unchanged path) — AbstractProjectileDispenseBehavior, Boat, Bootstrap, ClientAdvancements,
   ClientPacketListener, Inventory, ItemAttributeModifiers, OreFeature, StateTestingPredicate, TemptGoal
   (each verified vanilla-1.21.1 via javap: injection method + `@At` target + shadowed fields all exist).
   **8 mixins stay neoforge-only** in a new `grill24.potionsplus.mixin.neoforge` package +
   `potionsplus.neoforge.mixins.json` (referenced from `neoforge.mods.toml`): ApplyBonusCount,
   **BucketItem** (hooks the neoforge-patched `FluidType.onVaporize` call site inside
   `BucketItem.emptyContents`), EnchantedCountIncreaseFunction, Entity, ItemEntity, Item, LivingEntity,
   PotionItem. **Lesson for the next phase:** the compile gate cannot catch a mixin that *compiles*
   against the neoforge-patched jar but targets a patched method/call-site at runtime ("Scanned 0
   target(s)") — every mixin slated for `common/` needs its injection targets javap-verified against
   *vanilla*. NeoForge-patched methods used by the 8: `LootItemModifiersBehaviour` (Phase 8),
   `DataAttachments` (Phase 8), `CommonCommands` (Phase 7), `RecipesRegistrar` (Phase 5),
   `BlockState.getFriction(LevelReader,BlockPos,Entity)` + `Holder.getKey()` (LivingEntity), and the
   `onVaporize` call site (BucketItem). `comm -12` re-run on common↔neoforge/fabric/forge after the
   repackage → **empty** (Decision 4a holds). All four modules compile green.
6b. **Fabric class-init ordering fix + FABRIC SMOKE **PASS** 2026-09-02.** After 6a the fabric smoke
   crashed again — `NullPointerException: Cannot invoke Holder.value() because FlowerBlocks.LUNAR_BERRY_BUSH
   is null` from `BrewingItems.init` (fabric `Items.<clinit>` → `BrewingItems.init` → eagerly derefs
   `FlowerBlocks.LUNAR_BERRY_BUSH.value()` for the `ItemNameBlockItem`). Root cause: fabric registration is
   **eager** (the entrypoint's `Blocks.init()` → `OreBlocks.init` → first `Items.registerBlockItem` triggers
   `Items.<clinit>`, which runs the whole fabric `Items` static block *mid-way through* `Blocks.<clinit>` —
   neoforge/forge hide this because their suppliers are deferred to RegisterEvent). **Fix: reorder the
   fabric `Blocks` static block so `FlowerBlocks.init` runs FIRST** (its block-item registration is the first
   reference to `Items`; by the time any other sub-hub triggers `Items.<clinit>`'s `BrewingItems` deref,
   `LUNAR_BERRY_BUSH` is already bound). `:fabric:runClient` then booted to the main menu — both markers
   ("Potions Plus (Fabric) initializing" + "Sound engine started") present, no crash markers. **Missing
   item model JSONs** (`Unable to load model: 'potionsplus:item/...' FileNotFoundException`) are expected —
   item/blockstate models land in Phase 11. **Fabric exit criterion met** (world loads with blocks, items,
   potions, effects present). Lesson carried forward: on fabric, any hub sub-block whose registration
   eagerly derefs another hub's holder must be ordered *before* the class whose `<clinit>` triggers that
   deref. Remaining: forge runClient smoke.

**Content-source mapping (neoforge hub → what fabric/forge must reproduce):** registry content for
every hub is the 1.21.1 `core/neoforge/*` (incl. `potion/MobEffectsRegistrar`, `potion/PotionsRegistrar`,
`blocks/*`, `items/*`, `RecipesRegistrar`, `NumberProvidersRegistrar`, `NeoSounds`, `Entities`,
`DataComponents`, `MenuTypes`, `LootItemFunctions`, `CreativeModeTabs`, `Features`, `PlacementModifierTypes`,
`ArmorMaterials`, `LootModifiers`, `Capabilities`, `Advancements`-TRIGGERS, `LootItemConditions`). The
26.1.2 `core/fabric`/`core/forge` give the *how* (registry access, immediate vs deferred, propagation
blocks); they are MC 26.1.2 source and **must not be copied verbatim** (Identifier, `safeCastToReference`
assumptions, `ConsumeEffects`/`BlockPredicateTypes`/`CreativeModeTab.builder(Row,int)` differ).

---

## Phase 5 — `@ExpectPlatform` impls + networking

*(= 26.1.2 Phase 2.)*

- [x] `fabric/.../platform/fabric/PlatformImpl.java` — full implementations
      (`FabricLoader.getInstance().getEnvironmentType()`, `isDevelopmentEnvironment()`, etc.).
      **Already complete** (written during Phase 4 hub work) — verified against this phase's exit
      criteria and left unchanged.
- [x] `forge/.../platform/forge/PlatformImpl.java` — same (`FMLEnvironment.dist`,
      `!FMLEnvironment.production`). **Already complete**, and confirms the field form
      `FMLEnvironment.dist` (not 26.1.2's `getDist()`). Left unchanged.
- [x] `fabric/.../platform/fabric/PacketNetworkImpl.java` — `ServerPlayNetworking.send`,
      `ClientPlayNetworking.send`, `PlayerLookup.tracking(ServerLevel, ChunkPos)` / `.tracking(Entity)`.
      **Already complete.** Left unchanged.
- [x] `forge/.../platform/forge/PacketNetworkImpl.java` — `Channel.send(payload,
      PacketDistributor.X)`. **Implemented 2026-09-02**, verbatim port of the 26.1.2 body:
      `PLAYER.with(player)`, `TRACKING_ENTITY_AND_SELF.with(player)`, `SERVER.noArg()`,
      `TRACKING_CHUNK.with(level.getChunk(chunkPos.x, chunkPos.z))`. `javap`'d
      `net.minecraftforge.network.PacketDistributor` against `forge-universal.jar` (1.21.1-52.1.2) —
      all four members present with the exact signatures 26.1.2 uses.
- [x] `fabric/.../network/fabric/FabricPacketContext` + `forge/.../network/forge/ForgePacketContext`
      implementing common `network/PacketContext`. **Implemented 2026-09-02**, verbatim ports of the
      26.1.2 classes (Forge's `CustomPayloadEvent.Context` javap-confirmed identical: `enqueueWork`,
      `isServerSide`, `getSender`, `getConnection`, no `player()`/`disconnect()`).
- [x] `fabric/.../core/fabric/Packets.java` (`registerServer()` + `registerClient()`) and
      `forge/.../core/forge/Packets.java`, mirroring `core/Packets.java`'s 12 payloads.
      **Implemented 2026-09-02, with a scope correction found while doing it:** 6 of the 12 payload
      classes (`ClientboundBlockEntityCraftRecipePacket`, `ClientboundDisplayAlert`,
      `ClientboundDisplayAlertWithItemStackName`, `ClientboundDisplayAlertWithParameter`,
      `ClientboundImpulsePlayerPacket`, `ServerboundSpawnDoubleJumpParticlesPacket`) had no remaining
      neoforge-only dependency and were `git mv`'d from `neoforge/.../network/neoforge/` into
      `common/.../network/` (package `neoforge` → un-suffixed), then registered on all three loaders.
      **The other 6 are NOT yet portable** — unlike 26.1.2 (whose finished tree has all their
      dependencies in `common/`), this branch's `SanguineAltarBlockEntity`, `core.neoforge.Blocks`,
      `JeiPotionsPlusPlugin` and `ClotheslineBehaviour` are still neoforge-only leftovers from the
      original flat module (never split — this predates Phase 1's 107-file `.neoforge` repackage,
      which moved files but didn't decouple these four from NeoForge types).
      `ClientboundSanguineAltarConversionStatePacket`/`...Progress...` need the block entity + Blocks
      hub; `Clientbound{AcquiredBrewingRecipeKnowledge,SyncKnownBrewingRecipes,SyncPairedAbyssalTrove}`
      need `JeiPotionsPlusPlugin` (blocked on JEI-on-Fabric/Forge, Phase 11);
      `ServerboundConstructClotheslinePacket` needs `ClotheslineBehaviour`, whose body is a NeoForge
      `PlayerInteractEvent.RightClickBlock` handler — exactly Phase 7's "extract the handler body into
      a common static method, thin per-loader listener calls it" mechanism. **These 6 stay registered
      only in `core/neoforge/Packets.java` for now**; each has a comment in the new
      `core/{fabric,forge}/Packets.java` naming its blocking class. Move them to `common/` and register
      on all three loaders as their dependency chains resolve in Phases 7 (Clothesline) / 8-9 (altar
      block entity) / 11 (JEI). `ClientboundSyncRuntimeRecipesPacket` (26.1.2's 12th payload) **does
      not exist in this tree at all** — see the runtime-recipe bullet below.
- [x] **Carry 26.1.2's codec-side lesson:** a payload's codec must be registered on the side that
      *sends* it **and** the side that *receives* it. Fabric's `PayloadTypeRegistry.register` throws
      `IllegalArgumentException` on duplicate → wrap client-side re-registration in try/catch for the
      integrated server. Applied to all 6 ported payloads (`core/fabric/Packets.java`'s
      `clientbound`/registerClient try/catch).
      **Divergence:** this fabric-api version (`0.116.7+1.21.1`) names the registries
      `PayloadTypeRegistry.playC2S()`/`playS2C()`, not 26.1.2's `serverboundPlay()`/`clientboundPlay()`
      — confirmed via `javap` against `fabric-networking-api-v1-4.3.0`; caught immediately by
      `:fabric:compileJava`.
- [x] Forge buffer-type narrowing: packets declaring `StreamCodec<ByteBuf, MSG>` need a `playCodec(…)`
      cast helper (26.1.2 pattern) — ported verbatim into `core/forge/Packets.java`. `javap` confirms
      `PayloadConnection.play()` returns `PayloadProtocol<RegistryFriendlyByteBuf, BASE>` on 1.21.1's
      Forge 52.1.2 too (same `net.minecraftforge.network.payload.*` class hierarchy as 26.1.2).
- [x] **Every Forge handler must call `ctx.setPacketHandled(true)` after `enqueueWork(…)`.** Wired via
      the same `handled(...)` wrapper 26.1.2 uses, applied to all 6 ported Forge handlers.
- [ ] Check how this branch's **injected runtime recipes** reach the client on each loader. **Partially
      investigated 2026-09-02, not resolved:** unlike 26.1.2, this tree has no
      `ClientboundSyncRuntimeRecipesPacket` at all — `core/neoforge/RecipesRegistrar.injectRuntimeRecipes`
      mutates `RecipeManager.byType`/`byName` directly (access-widened fields) and is called from
      `ServerLifecycleListeners` at server start, before any player joins, so vanilla's own
      `ClientboundUpdateRecipesPacket` (sent at player login from `recipeManager.getRecipes()`) may
      already carry the injected recipes for free — this would make 26.1.2's custom sync packet
      unnecessary here. **Not verified by an actual world join** (out of scope for this pass — needs
      Phase 9's access-widener work first, since `injectRuntimeRecipes` is NeoForge-only until then).
      Re-check once Phase 9 wires the shared access widener and Fabric/Forge can call
      `RecipesRegistrar`-equivalent injection: if seeded recipes are missing from JEI/the recipe book on
      a real Fabric or Forge world join, build `ClientboundSyncRuntimeRecipesPacket` then (batched at 64
      recipes / 1 MiB cap, per 26.1.2).

**Exit criterion:** all three modules build; a packet round-trips on each loader. **Verify by
creating a new world on each loader**, not by building — 26.1.2's networking bugs all appeared on
the first world join and none of them were visible at build time.
**Partially met 2026-09-02:** `:neoforge:compileJava :fabric:compileJava :forge:compileJava` and
`:neoforge:build :fabric:build :forge:build -x test` all green; `comm -12` package-intersection check
empty against `common/` for all three platform modules; `runClient` smoke on all three loaders reaches
`Sound engine started` with no new exceptions (only the pre-existing Phase-4 missing-model/subtitle
warnings). **Not yet verified: an actual world join / packet round-trip** — the 6 ported payloads have
never fired end-to-end (their sending code paths — `BrewingCauldronBlockEntity`,
`PotionBeaconBlockEntity`, `ExplodingEffect`, etc. — are still neoforge-only `@EventBusSubscriber`
classes awaiting Phase 7). Re-verify with a real world join once Phase 7 gives Fabric/Forge callers for
these packets.

---

## Phase 6 — Entrypoints

*(= 26.1.2 Phase 3.)*

- [x] `fabric/.../core/fabric/PotionsPlusFabric.java` (`ModInitializer`) +
      `PotionsPlusFabricClient.java` (`ClientModInitializer`) replicating `core/PotionsPlus.java` and
      `core/PotionsPlusClient.java`; registration is immediate, no deferred flush.
- [x] `forge/.../core/forge/PotionsPlusForge.java` (`@Mod` — `net.minecraftforge.fml.common.Mod`) —
      same wiring, then `DR.register(modEventBus)` for every `DeferredRegister`.
- [x] **Do NOT create a second `@Mod` class for the client. Re-verified 2026-09-01:** the 26.1.2
      tree's `core/forge/` contains `PotionsPlusForge` and **no** `PotionsPlusForgeClient`, while
      `core/fabric/` has both `PotionsPlusFabric` and `PotionsPlusFabricClient` and `core/neoforge/`
      has both `PotionsPlus` and `PotionsPlusClient`. Forge is the odd one out, by necessity, not
      oversight. 26.1.2 wrote a `PotionsPlusForgeClient`
      mirroring NeoForge's entrypoint split; **Forge dedups `@Mod` classes by modid, first wins, and
      silently dropped it** — the client wiring never ran, and `f5cd94d` deleted the class outright.
      Do client wiring from the single `@Mod` class, dist-gated, and see the Phase 11 timing note
      before choosing *where* in that constructor it goes.
      **Confirmed 2026-09-02: `forge/` still has no `PotionsPlusForgeClient`.** No client-only wiring
      needs dist-gating yet — this branch's Forge `Packets.register()` (unlike Fabric's server/client
      split) registers both directions in one call from the `@Mod` constructor, and renderers/screens/
      key-mappings are all deferred to Phase 7/11. Revisit dist-gating when Phase 11 adds client-only
      Forge setup.
- [x] Verify what Forge 52.x's `@Mod` constructor actually accepts before writing it (26.1.2 hit
      `NoSuchMethodException: PotionsPlusForge.<init>()` because 26.1.2's Forge injects
      `FMLJavaModLoadingContext` and had removed `ModLoadingContext.get()`; 52.x is an older
      generation and may differ again). **Confirmed 2026-09-02:** Forge 52.1.2 takes a plain no-arg
      constructor; `FMLJavaModLoadingContext.get().getModEventBus()` inside it works exactly like the
      classic (pre-26.1.2) Forge API the VERIFIED API FACTS table already predicted. No divergence.
- [x] `fabric.mod.json` + `META-INF/mods.toml` filled in properly (entrypoints, mixin configs,
      access widener, dependencies, icon).

**Exit criterion:** all three loaders reach the main menu and load a world. **Met 2026-09-02.**

**Phase 6 notes.** Every checklist item above was already satisfied going into this phase — Phases
4-5 needed working entrypoints to smoke-test the registration hubs and networking infra they built,
so `PotionsPlusFabric`/`PotionsPlusFabricClient`/`PotionsPlusForge` and both platform manifest files
were already written and correct. This phase's actual job was verifying the *stronger* exit
criterion Phase 4 didn't check — "load a world", not just "reach the main menu":

- `:neoforge:build :fabric:build :forge:build -x test` → `BUILD SUCCESSFUL`, all three jars.
- `./gradlew :{neoforge,fabric,forge}:runServer` (each with `eula.txt` pre-seeded, no prior
  world dir) — **all three** log `Done (…)! For help, type "help"` with **zero exceptions** and no
  crash markers. This is a real world creation + load: chunk generation, recipe/loot/advancement
  reload, and (neoforge) 424 injected runtime recipes + 38 saved-data seeded potion recipes loaded
  back from disk, all completed cleanly.
- `./gradlew :{neoforge,fabric,forge}:runClient` — all three reach `Sound engine started` with the
  Fabric/Forge init-log lines present (`Potions Plus (Fabric) initializing` /
  `Potions Plus (Fabric) client initializing` / `Potions Plus (Forge) initializing`), no new
  exceptions beyond the pre-existing missing-subtitle warnings already tracked as out of scope.
- **One pre-existing, non-blocking gap surfaced on both fabric and forge `runServer`:**
  `Couldn't parse element minecraft:loot_table/potionsplus:blocks/clothesline - ... Unknown registry
  key ... potionsplus:clothesline`. Expected, not a regression — `ClotheslineBlock` is one of the
  NeoForge-only block-entity blocks both platform `core/{fabric,forge}/blocks/BlockEntityBlocks.java`
  already document as deferred to Phase 8 ("deeply coupled to neoforge BEs"). The loot table (a
  `common/` resource shared by all three loaders) references a block that doesn't exist yet on those
  two loaders; it doesn't stop world load and will resolve itself once Phase 8 ports the block.
- **Dev-run hygiene note for future phases:** a `runServer`/`runClient` task killed via `timeout`
  leaves its forked game JVM running detached from the Gradle daemon (`TransformerRuntime` process),
  holding the world's `session.lock` and causing the *next* run to fail with
  `IOException: The process cannot access the file because another process has locked a portion of
  the file`. Kill it explicitly before the next run:
  `Get-CimInstance Win32_Process -Filter "Name='java.exe'" | Where-Object { $_.CommandLine -match
  'TransformerRuntime' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }`.

---

## Phase 7 — Event surface (36 subscriber classes)

*(= 26.1.2 Phase 4, but materially larger.)* On 26.1.2 the listeners were already collected into
`event/neoforge/`. Here they are **spread through gameplay code**, so this phase does two jobs:
**(a)** decouple event registration from the classes that happen to host it, and **(b)** mirror each
listener onto Fabric and Forge.

The 36 `@EventBusSubscriber` classes, grouped:

| Group | Files | Notes |
|---|---|---|
| Registration hubs | `core/{Attributes,Blocks,Capabilities,CreativeModeTabs,Packets,Screens,Renderers,BlockRenderLayers,KeyMappings}`, `core/potion/Potions` | Mostly absorbed by **Phase 4** / **Phase 11**; the annotation just goes away |
| Mob-effect behaviour | `effect/{BoneBuddy,Bouncing,Exploding,FallOfTheVoid,FlyingTime,GeodeGrace,SoulMate}Effect` | **7 gameplay classes each carrying their own `@SubscribeEvent`.** Extract the handler bodies into `common` static methods; each loader's listener class calls them |
| Explicit listeners | `event/{AdvancementListeners,ClientTooltipComponentFactoriesListeners,EnchantmentListeners,ItemListenersMod,PlayerListeners}` | Direct analogue of 26.1.2's `event/neoforge/*` — port its mapping table. **`event/ItemListenersGame` is NOT one of these** — 26.1.2 keeps it in `common/`, next to `common/event/AnimatedItemTooltipEvent` |
| Tick / lifecycle | `utility/{ClientTickHandler,ServerTickHandler,DelayedEvents,ServerPlayerUtility}`, `core/ServerLifecycleListeners` | Fabric `ServerTickEvents`/`ClientTickEvents`/`ServerLifecycleEvents`; Forge `TickEvent.*` |
| Client tooltips | `item/tooltip/{BrewingTooltips,PotionEffectTooltips}`, `blockentity/ClotheslineBlockEntityRenderer` | Fabric `ItemTooltipCallback`; Forge `ItemTooltipEvent` |
| Commands / input | `core/{CommonCommands,ClientCommands,KeyMappingsListener,ClientEvents}` | Fabric `CommandRegistrationCallback`; Forge `RegisterCommandsEvent` |
| Datagen | `data/DataGen` | **Stays NeoForge-only** (Decision 5) |

- [ ] Extract every `@SubscribeEvent` body into a loader-agnostic `common` method; leave the NeoForge
      annotation on a thin `neoforge/.../event/neoforge/*` listener that delegates. Mirror 26.1.2's
      package layout exactly.
- [ ] **Know the fan-in shape before you start — it is asymmetric.** On 26.1.2 NeoForge keeps **16**
      separate listener classes in `event/neoforge/` (`AdvancementListeners`, `ClientGameListeners`,
      `ClientTooltipComponentFactoriesListeners`, `EffectListeners`, `EnchantmentListeners`,
      `EntityListeners`, `ItemFrameListeners`, `ItemListenersMod`, `NeoAnimatedItemTooltipEvent`,
      `NeoAttributeEvents`, `NeoCommandEvents`, `NeoDelayedEvents`, `NeoItemListeners`,
      `NeoServerTickEvents`, `PlayerListeners`, `ServerPlayerHeldItemChangedEvent`), while Fabric and
      Forge each collapse to just **two**: `{Fabric,Forge}EventListeners` +
      `{Fabric,Forge}ClientEventListeners`. Do not build 16 classes per loader.
- [ ] **`ItemListenersGame` stays in `common/`** on 26.1.2 (`common/event/ItemListenersGame.java`),
      alongside `AnimatedItemTooltipEvent`. The group table below lists it under "Explicit listeners →
      analogue of `event/neoforge/*`"; that is wrong — only `ItemListenersMod` and `NeoItemListeners`
      are NeoForge-side.
- [ ] **Forge needs gameplay mixins too — not just `BUS.addListener`.** This plan's earlier text
      anticipated mixins only for Fabric. 26.1.2's `mixin/forge/` contains four gameplay mixins —
      `BucketItemMixin`, `EnchantmentHelperMixin`, `ItemEntityMixin`, `LivingEntityMixin` — the same
      set Fabric needs, minus `ItemEntityLifespanMixin`/`PlayerAdvancementsMixin` and plus the two
      registry mixins from Phases 4 and 12. Budget Forge mixin work in this phase, not just Phase 9.
- [ ] Fabric listeners: fabric-api callbacks where they exist, `mixin/fabric/` where they don't.
      26.1.2's exact `mixin/fabric/` set, verified: `BucketItemMixin`, `EnchantmentHelperMixin`,
      `ItemEntityLifespanMixin`, `ItemEntityMixin`, `LivingEntityMixin`, `PlayerAdvancementsMixin`
      (six — the earlier prose here undercounted by omitting the bucket and item-lifespan cases).
- [ ] **Audit the return-value polarity of every fabric-api callback.** They are not uniformly aligned
      with the NeoForge event they replace. 26.1.2 returned a gameplay method's own boolean straight
      out of `PlayerBlockBreakEvents.BEFORE`, where `false` means *cancel* — so a hook that returns
      `true` only ~1% of the time **vetoed nearly every block break in the game** (`f7c5890`), and it
      shipped through four phases unnoticed. Check each one against its javadoc, not against intuition.
- [ ] Forge listeners: static `BUS.addListener(…)`. **`@SubscribeEvent` on Forge 1.21.1 is
      `net.minecraftforge.eventbus.api.SubscribeEvent`** (26.1.2 moved it to
      `…eventbus.api.listener.SubscribeEvent` — do not copy that import).
- [ ] **The two custom `Event` subclasses are load-bearing — the earlier "dead code" note here was
      wrong.** *Corrected 2026-09-01 by grepping the 26.1.2 tree.* Both were re-homed, not skipped:
  - [ ] **`event/AnimatedItemTooltipEvent`** lives in **`common/`** on 26.1.2 and is referenced by
        roughly twenty files — most of `effect/*`, the `IEffectTooltipDetails` /
        `IEnchantmentBonusTooltipDetails` / `ITickingAreaTooltipDetails` interfaces,
        `item/WeightDataComponent`, and both `item/tooltip/*` classes. Each loader dispatches it from
        its own listener: `fabric/event/fabric/FabricClientEventListeners`,
        `forge/event/forge/ForgeClientEventListeners`,
        `neoforge/event/neoforge/NeoAnimatedItemTooltipEvent`. Budget for it.
  - [ ] **`event/ServerPlayerHeldItemChangedEvent`** is the NeoForge-side implementation behind
        `Platform.onServerPlayerHeldItemChanged` — it sits in `neoforge/event/neoforge/` and is
        referenced by `event/neoforge/EntityListeners` plus **all three** `PlatformImpl` classes. It
        is not an orphan; it is one end of a Phase 2 platform hook.

**Exit criterion:** every gameplay behaviour that fires from an event on NeoForge also fires on
Fabric and Forge. `common/` still has zero `net.neoforged` imports.

**Phase 7 progress notes (2026-09-02) — mob-effect behaviour group done, five groups remain.**

- [x] **Mob-effect behaviour (7 classes, matches the plan table exactly — `MetalDetectingEffect` and
      `TeleportationEffect` were miscounted into this bucket in earlier prose; neither ever had
      `@SubscribeEvent` and both moved to `common/` as pure relocations with zero listener work).**
      `BoneBuddyEffect`, `BouncingEffect`, `ExplodingEffect`, `FallOfTheVoidEffect`,
      `FlyingTimeEffect`, `GeodeGraceEffect`, `SoulMateEffect` all moved `neoforge/effect/neoforge/`
      → `common/effect/`, `@SubscribeEvent` bodies extracted to plain static methods matching
      26.1.2's signatures exactly (`onPotionAdded`, `onPotionExpired`, `onLivingFall`,
      `onLivingEntityDamage`, `onEntityHurt`, `onEntityHeal`, `onEntityDeath`, …). Construction moved
      from the neoforge-only `MobEffectsRegistrar` into common `MobEffects.init()`, so **Fabric and
      Forge now register these 7 effects for the first time** (previously silently absent from those
      two loaders — a real feature gap, not just a refactor).
  - [x] `event/neoforge/EffectListeners.java` (new) — direct 26.1.2 mirror, `@SubscribeEvent` +
        `@EventBusSubscriber` on `MobEffectEvent.{Added,Expired,Remove}`, `LivingFallEvent`,
        `LivingDamageEvent.Pre`, `LivingHealEvent`, `LivingDeathEvent`.
  - [x] `event/forge/EffectListeners.java` (new) — same events, but Forge 52.1.2 keeps the classic
        event shape (`LivingDamageEvent` has a single mutable `amount`, no `.Pre`/`.Post` split) and
        this module's established explicit-registration style (`MinecraftForge.EVENT_BUS.addListener`
        calls from `PotionsPlusForge`'s constructor), not `@SubscribeEvent` auto-discovery — matches
        how `Packets.register()` etc. are already wired here. Verified via javap against the Forge
        52.1.2 merged jar: `LivingDamageEvent`/`LivingFallEvent`/`LivingDeathEvent` are all
        `@Cancelable`, `MobEffectEvent.{Added,Expired,Remove}` exist with the expected accessors.
  - [x] `mixin/fabric/LivingEntityMixin.java` (new) + `event/fabric/EffectListeners.java` (new,
        `ServerLivingEntityEvents.AFTER_DEATH` for the death-only pair). **Diverges from 26.1.2's
        fabric mixin in one load-bearing way:** 1.21.1's `LivingEntity` predates the
        `onEffectsRemoved(Collection)` batching refactor 26.1.2 redirects — javap against the vanilla
        merged jar turned up only a singular `onEffectRemoved(MobEffectInstance)`, called from enough
        different places that a single injection point can't distinguish natural expiry from explicit
        removal the way NeoForge's patched events do. Fix: detect expiry by **diffing
        `getActiveEffectsMap()` across `tickEffects()`** (`@Inject` at `HEAD` snapshots, `@Inject` at
        `RETURN` reports whatever key vanished) instead of redirecting a call that may not be there;
        explicit removal still redirects `removeEffectNoUpdate`/`removeAllEffects` directly, which
        does have stable 1.21.1 names (confirmed via javap). New `potionsplus.fabric.mixins.json`
        registered in `fabric.mod.json`.
  - [x] Regression-verified: `:{neoforge,fabric,forge}:build -x test` green; `:{neoforge,fabric,
        forge}:runServer` all reach `Done (...)!` with zero exceptions and the fabric mixin applying
        cleanly (no "Mixin apply failed" in the log); Decision 4a `comm -12` still empty on all three.
- [x] **Registration hubs — confirmed no-op 2026-09-02.** Audited all 8 neoforge-side
      `@EventBusSubscriber` hub classes (`core/neoforge/{Blocks,Capabilities,CreativeModeTabs,
      Packets,Screens,Renderers,BlockRenderLayers,KeyMappings}`; the `core/potion/{Potions,
      MobEffects}` hits in the earlier grep were stale Javadoc prose mentioning "Phase 7", not
      actual annotations — false positives). None needed the annotation deleted: they already live
      correctly package-scoped in `neoforge/` (Decision 4a), and each is a real NeoForge lifecycle
      hook (`RegisterColorHandlersEvent`, `RegisterCapabilitiesEvent`,
      `BuildCreativeModeTabContentsEvent`, `RegisterPayloadHandlersEvent`,
      `RegisterMenuScreensEvent`, `EntityRenderersEvent.RegisterRenderers`, `FMLClientSetupEvent`,
      `RegisterKeyMappingsEvent`), not a vestigial annotation left over from the split. `Blocks`,
      `Capabilities`, `CreativeModeTabs`, `Packets` already have working Fabric + Forge equivalents
      (`core/{fabric,forge}/*`) from Phases 4/5. `Screens`, `Renderers`, `BlockRenderLayers`,
      `KeyMappings` are client-registration hubs correctly deferred to **Phase 11** — no fabric/forge
      files for them exist yet, and none should before that phase.
- [~] **Explicit listeners — partially done 2026-09-02, rest genuinely blocked on other buckets/phases
      (evidence below, not a guess).** Of the six items in this bucket, only two were free of
      cross-bucket dependencies:
  - [x] **`EnchantmentListeners`** (`GetEnchantmentLevelEvent` → item-attribute enchantment bonus).
        Forge 52.1.2 has no such event (confirmed via javap: only `EnchantmentLevelSetEvent` exists,
        a different hook) and neither does vanilla/Fabric, so both loaders mixin into
        `EnchantmentHelper.getItemEnchantmentLevel` instead: new
        `{fabric,forge}/mixin/{fabric,forge}/EnchantmentHelperMixin.java`. **Diverges from 26.1.2's
        mixin in two ways, both forced by this MC version:** (1) 1.21.1's overload takes `ItemStack`
        directly, not 26.1.2's `ItemInstance` abstraction; (2) the bonus-calculation body is
        **inlined** in each mixin rather than calling NeoForge's existing
        `LootItemModifiersBehaviour.getEnchantmentLevelFromItemAttributes` — that class has zero
        NeoForge imports (pure vanilla logic) but still physically lives under `neoforge/behaviour/`,
        because **the entire `behaviour` package is unsplit Phase 8 territory on this branch**
        (`MossBehaviour`, `ClotheslineBehaviour`, both loot-modifier classes are neoforge-only today —
        unlike 26.1.2, where `behaviour` was already common). Moving just the one class would leave
        `common/behaviour` and `neoforge/behaviour` co-existing, violating Decision 4a's zero-package-
        intersection rule. Also had to swap `Holder.getKey()`/`ItemStack.getAttributeModifiers()` for
        `Holder.unwrapKey()`/`stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ...)` — both of
        the former are **NeoForge-only extension methods** (`IHolderExtension`/`IItemStackExtension`,
        confirmed via javap on the AT-patched NeoForge jar: `Holder<T> extends IHolderExtension<T>`),
        invisible on Fabric/Forge/vanilla despite compiling silently in the existing neoforge code.
  - [x] **`ItemListenersMod`** (potion `MAX_STACK_SIZE` → 16). NeoForge keeps
        `ModifyDefaultComponentsEvent` (unchanged). Neither Fabric nor Forge has that event, and
        1.21.1 predates `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS` (confirmed absent via a jar
        listing — that's a 1.21.5+ API, so 26.1.2's Fabric/Forge approach doesn't port). New
        `{fabric,forge}/mixin/{fabric,forge}/ItemMixin.java`: `@Inject` at `RETURN` of
        `Item(Item.Properties)`, `@Mutable @Shadow @Final DataComponentMap components`, reassign via
        `DataComponentMap.builder().addAll(this.components).set(MAX_STACK_SIZE, 16).build()` when
        `this instanceof PotionItem`. Verified safe against Forge's lazy `builtComponents` cache
        (`Item.components()` calls `ForgeHooks.gatherItemComponents(this, components)` on first
        access, well after construction — confirmed via javap on the Forge 52.1.2 merged jar).
  - [x] Both mixins registered in `potionsplus.{fabric,forge}.mixins.json`. Verified:
        `:{neoforge,fabric,forge}:build -x test` green (including `remapJar`/refmap generation);
        Decision 4a `comm -12` empty on all three; `:{neoforge,fabric,forge}:runServer` all reach
        `Done (...)!` with zero exceptions and no "Mixin apply failed".
  - [ ] **`AdvancementListeners` — blocked.** Needs `RecipesRegistrar.ALL_SEEDED_POTION_RECIPES_ANALYSIS`,
        which is neoforge-only (`neoforge/core/neoforge/RecipesRegistrar.java`) pending Phase 5's
        remaining runtime-recipe work (Phase 5 status: "6/12 payloads ported... runtime-recipe client
        sync unresolved"). The advancement-id set itself is trivial to port (`Utility.ppId("root")`
        etc., already in `common/`), but the payload it drops depends on data that doesn't exist on
        Fabric/Forge yet.
  - [ ] **`PlayerListeners` — still blocked overall, but one of its blockers is now cleared
        (2026-09-03).** `onItemPickedUp` still reaches `RecipesRegistrar` (Phase 5's runtime-recipe
        remainder). `onTick` (passive item potion effects)'s `ServerTickHandler.ticksInGame` blocker is
        now **stale** — the Tick/lifecycle bucket already moved `ServerTickHandler` to
        `common/.../utility/ServerTickHandler.java` (confirmed by re-reading the file: no loader
        package suffix). **`MossBehaviour` ported to `common/behaviour/` today**, refactored from a
        `PlayerInteractEvent.RightClickBlock`-shaped API to plain vanilla parameters (`Level, BlockPos,
        ItemStack, Player, InteractionHand`) returning a `boolean` (handled/should-cancel); NeoForge's
        `PlayerListeners.on(RightClickBlock)` now calls it and sets `event.setCanceled(true)` itself.
        **One deliberate behaviour narrowing**, called out because it's a real (if minor) parity loss:
        the old check was NeoForge's `ItemStack.canPerformAction(ItemAbilities.SHEARS_HARVEST)`, a
        NeoForge-only capability extension point letting *other mods'* tools register as
        shear-equivalent; vanilla has no such hook, so the common version checks
        `itemStack.getItem() instanceof ShearsItem` instead — the same test vanilla's own
        `BeehiveBlock` uses. Loses only third-party-mod shear-tool compat, not vanilla shears.
        `ClotheslineBehaviour` stays neoforge-only and genuinely blocked — unlike Moss it isn't a pure
        vanilla-logic class: it imports `block.neoforge.ClotheslineBlock`,
        `blockentity.neoforge.ClotheslineBlockEntity`, `core.neoforge.Blocks`, and
        `network.neoforge.ServerboundConstructClotheslinePacket`, none of which exist cross-loader yet
        (same dependency chain Phase 5 already flagged for `ServerboundConstructClotheslinePacket`).
        Porting it needs the Clothesline block/block-entity themselves split first — real Phase 8/11
        work, not a Phase-7-shaped refactor. **Decision 4a note:** splitting `MossBehaviour` out left
        `LootItemModifiersBehaviour`/`AddMobEffectsLootModifier`/`WormrootLootModifier`/
        `ClotheslineBehaviour` alone in `neoforge/.../behaviour/`, which re-created a
        `common`/`neoforge` package intersection on `grill24.potionsplus.behaviour` — fixed by moving
        those four into `grill24.potionsplus.behaviour.neoforge` (package-only move, no logic changes)
        and repointing their seven call sites. Re-verified: `comm -12` empty on all three platform
        modules; `:{neoforge,fabric,forge}:build -x test` green; `:neoforge:runServer` reaches
        `Done (...)!` with zero exceptions.
  - [ ] **`ClientTooltipComponentFactoriesListeners` — blocked.** Forge has the same
        `RegisterClientTooltipComponentFactoriesEvent` NeoForge does (confirmed via jar listing) and
        Fabric's `ClientTooltipComponentCallback.EVENT` is the documented 26.1.2 equivalent, so the
        *registration* is trivial — but the actual `ClientTooltipComponent` implementation,
        `ClientItemStacksTooltip`, lives at `neoforge/utility/neoforge/ClientItemStacksTooltip.java`
        and hard-depends on `core.neoforge.items.DynamicIconItems.GENERIC_ICON`, which has **no
        Fabric or Forge equivalent at all** (`DynamicIconItems` was apparently missed by Phase 4/5's
        item-hub porting; it only exists in `neoforge/`). Porting this listener means porting
        `DynamicIconItems` first, which is registration-hub/Phase-4 scope, not Phase 7.
  - [ ] **`ItemListenersGame` → `common/` — blocked.** 26.1.2's version is a pure animation-math
        helper class with zero loader imports, which is exactly why it lives in `common/` there. On
        this branch the equivalent helper methods (`animateComponentText`/
        `animateComponentTextStartTime`) call `ClientTickHandler.total()`, and `ClientTickHandler` is
        still neoforge-only (`neoforge/utility/neoforge/ClientTickHandler.java`) pending the
        **Tick / lifecycle** bucket. Moving `ItemListenersGame` to `common/` today would mean `common/`
        importing a neoforge-only class - not possible (separate module classpaths, not just a style
        rule). The `@SubscribeEvent`-carrying half of the current `neoforge/event/neoforge/
        ItemListenersGame.java` (tooltip animation dispatch, `LivingEntityUseItemEvent` duration
        shortening) additionally reaches into `BrewingTooltips`/`PotionEffectTooltips`
        (**Client tooltips** bucket, also not done) via `AnimatedItemTooltipBusEvent` — left entirely
        untouched.
  - [ ] **`AnimatedItemTooltipEvent` — left as-is, divergence noted, not fixed.** 26.1.2's version is
        `abstract` with nested `Add`/`Modify` subclasses and is called **directly** by each loader's
        tooltip listener (no event bus at all — see `NeoItemListeners`/`FabricClientEventListeners`/
        `ForgeClientEventListeners` there). This branch's `common/event/AnimatedItemTooltipEvent.java`
        is a plain concrete data holder, and NeoForge still wraps it in a NeoForge-only
        `AnimatedItemTooltipBusEvent` posted to `NeoForge.EVENT_BUS`. Redesigning this to match
        26.1.2 means touching `WeightDataComponent`, `BrewingTooltips`, `PotionEffectTooltips` — all
        **Client tooltips** bucket, not this one — so left alone. Whoever does that bucket should
        consider adopting 26.1.2's direct-call design instead of inventing a NeoForge-bus-shaped one
        for Fabric/Forge, since NeoForge's own reference class for the bus-wrapper approach
        (`NeoAnimatedItemTooltipEvent`) turned out to be **dead code even on 26.1.2** (confirmed via
        grep — zero call sites; NeoForge's `NeoItemListeners` calls `AnimatedItemTooltipEvent.Add`
        directly, same as Fabric/Forge). Do not port `NeoAnimatedItemTooltipEvent` when tackling that
        bucket; 26.1.2 itself doesn't use it.
  - [x] **`ServerPlayerHeldItemChangedEvent` — confirmed no-op, already correctly scoped.** Verified
        it is exactly what the plan predicted: one end of a Phase 2 `Platform` hook, neoforge-only by
        design (`Platform.onServerPlayerHeldItemChanged`), not an orphan. Nothing to port here — this
        bullet was already satisfied before this session.
  - **Net effect:** this bucket surfaced three real, evidence-backed prerequisites for other work:
    (1) Phase 5's remaining runtime-recipe/`RecipesRegistrar` work blocks `AdvancementListeners` and
    part of `PlayerListeners`; (2) the **Tick / lifecycle** bucket (below) blocks the rest of
    `PlayerListeners` and `ItemListenersGame`'s move to `common/`; (3) Phase 8's `behaviour` package
    split blocks the moss/clothesline half of `PlayerListeners` (loot-modifier classes were already
    known Phase 8 scope, but `MossBehaviour`/`ClotheslineBehaviour` living there too is a **new**
    finding — worth flagging to whoever scopes Phase 8, since those two classes are general gameplay
    behaviour, not loot modifiers). None of these are Phase-7-bucket work; they're prerequisites this
    bucket exposed.
- [~] **Tick / lifecycle — partially done 2026-09-02.** `ClientTickHandler`, `ServerTickHandler`,
      `TickHandler`, `DelayedEvents` moved `neoforge/utility/neoforge/` → `common/utility/` unchanged
      (pure vanilla state, `Minecraft.getInstance()` guarded by dist as before). Thin dispatchers
      added per loader:
  - [x] NeoForge: `event/neoforge/{ClientGameListeners,NeoServerTickEvents,NeoDelayedEvents}.java`
        (new), exact 26.1.2 filenames/shapes — `@EventBusSubscriber` delegating to the moved common
        statics. Old `utility/neoforge/{ClientTickHandler,ServerTickHandler,TickHandler,
        DelayedEvents}.java` deleted; 12 neoforge files (blockentity renderers, `RUtil`, `Blocks`,
        `ItemListenersGame`, `PlayerListeners`) repointed to the `common` imports.
  - [x] Fabric/Forge: `event/{fabric,forge}/TickListeners.java` (new) — this branch's per-concern
        style (unlike 26.1.2's collapsed `FabricEventListeners`/`ForgeEventListeners`, which don't
        exist here). Two API corrections needed, both verified via javap/dependency inspection before
        writing code, not guessed:
    - **Forge 52.1.2's `TickEvent` predates the `.Post.BUS`-static-field pattern** 26.1.2's Forge
      tree uses (javap on `forge-1.21.1-52.1.2-universal-srg.jar` confirms `TickEvent$ServerTickEvent
      $Post`/`ClientTickEvent$Post`/`RenderTickEvent$Post` are plain subclasses with no `BUS` field) —
      registered instead as classic `MinecraftForge.EVENT_BUS.addListener((TickEvent.X.Post event) ->
      …)` lambdas, matching this module's existing `EffectListeners` style. `registerServer()` is
      called unconditionally from `PotionsPlusForge`'s constructor; `registerClient()` is called from
      the same constructor via a `bus.addListener((FMLClientSetupEvent event) -> …)` listener — that
      event only ever posts client-side, so no separate dist-gated `@Mod.EventBusSubscriber` class was
      needed (unlike heavier client wiring that touches client classes eagerly at registration time).
    - **fabric-api 0.116.7+1.21.1 resolves `fabric-rendering-v1:5.1.0`** (confirmed via `./gradlew
      :fabric:dependencies`), which has no `LevelRenderEvents` (a later fabric-api addition) — used
      `WorldRenderEvents.START` + `WorldRenderContext.tickCounter().getGameTimeDeltaPartialTick(true)`
      instead, the 1.21.1-era equivalent hook.
    - `Minecraft.getDeltaTracker()` (guessed from 26.1.2) doesn't exist on 1.21.1 either — vanilla's
      accessor is `Minecraft.getTimer()` (javap-verified against `minecraft-merged-mojang.jar`);
      moot after switching to `WorldRenderContext.tickCounter()` for the fabric side, but the forge
      side's `RenderTickEvent.Post.getTimer()` (verified via javap) is the same shape.
  - [x] Wired into all three entrypoints: `PotionsPlusFabric.onInitialize` (server tick),
        `PotionsPlusFabricClient.onInitializeClient` (client tick/render), `PotionsPlusForge`'s
        constructor (both).
  - [x] Verified: `:{neoforge,fabric,forge}:build -x test` green; Decision 4a `comm -12` empty on all
        three; `:{neoforge,fabric,forge}:runServer` all reach `Done (...)!` with no exceptions and no
        "Mixin apply failed" (the pre-existing `potionsplus:blocks/clothesline` loot-table parse
        warning is unrelated - a real but out-of-scope bug, same on all three loaders).
  - [ ] **`core/ServerLifecycleListeners` — still blocked, not attempted.** Confirmed via the
        existing fabric/forge stub class-doc comments (already correctly worded, left as-is): its
        `onServerStarted`/`onServerAboutToStart` bodies call `RecipesRegistrar.injectRuntimeRecipes`/
        `SANGUINE_ALTAR_ANALYSIS.compute`/etc., all neoforge-only pending Phase 5's runtime-recipe
        remainder (same blocker already surfacing in the Explicit-listeners bucket for
        `AdvancementListeners`/`PlayerListeners`).
  - [ ] **`ServerPlayerUtility` — deliberately not split further.** It's a single self-contained
        `onTossItemEvent` → `Platform.onServerPlayerHeldItemChanged` listener, already correctly
        scoped to `neoforge/`, already working, and low-value to touch now: matching 26.1.2's split
        (an empty `common/utility/ServerPlayerUtility` marker + the real logic living in a
        common `EntityListeners`-equivalent) would require that not-yet-existing common listener
        class, which is Explicit-listeners-bucket scope. Also confirmed (again) that the event it
        posts is dead code on every loader today - `ServerPlayerHeldItemChangedEvent` has zero
        subscribers on NeoForge here, matching 26.1.2's own `PlatformImpl` comment ("NeoForge posts a
        custom event here with zero subscribers") - so there is no missing Fabric/Forge behaviour to
        chase, only a structural split that isn't worth doing before its dependency exists.
- [~] **Client tooltips — partially done 2026-09-02.** Adopted the direct-call
      `AnimatedItemTooltipEvent` redesign the Explicit-listeners bucket flagged (see 351a5c2's plan
      note): confirmed via grep that 26.1.2's `AnimatedItemTooltipEvent` is `abstract` with nested
      `Add`/`Modify` classes, called **directly** by each loader's tooltip listener — no bus event at
      all. This branch's `common/event/AnimatedItemTooltipEvent.java` now matches that shape exactly
      (verbatim byte-diff against 26.1.2 apart from `ResourceLocation` vs `Identifier`, the known
      1.21.1-vs-26.1.2 rename). `neoforge/event/neoforge/AnimatedItemTooltipBusEvent.java` deleted —
      it had exactly one poster (`ItemListenersGame`) and no other subscribers, so it added nothing
      over a direct call, matching the prior fork's finding that its 26.1.2 analogue
      (`NeoAnimatedItemTooltipEvent`) was dead code there too.
  - [x] **Enabling move: `RUtil` → `common/utility/RUtil.java`.** Audited first — zero
        `net.neoforged`/`net.minecraftforge` imports (confirmed via grep) despite living in
        `neoforge/` — a pure-vanilla rendering-math class that was never actually loader-coupled, just
        mis-filed pre-split. Matches 26.1.2's placement exactly. 10 call sites repointed
        (blockentity renderers, `ItemListenersGame`, `EmitterParticle`).
  - [x] **`PotionEffectTooltips` → `common/item/tooltip/PotionEffectTooltips.java`.** Had zero
        `RecipesRegistrar`/neoforge dependency (unlike `BrewingTooltips` below) — a clean move once
        the bus-event redesign let it drop `@EventBusSubscriber`/`@SubscribeEvent` and take
        `AnimatedItemTooltipEvent.Add` directly, matching 26.1.2's `common/item/tooltip/
        PotionEffectTooltips.java` exactly (verified: same file there, same package).
  - [x] **`ItemListenersGame`'s animation math → `common/event/ItemListenersGame.java`.** This was
        the blocker the Explicit-listeners bucket recorded ("`ClientTickHandler` is still
        neoforge-only") — resolved by the Tick/lifecycle bucket (719691c) moving `ClientTickHandler`
        to `common/` first. The pure animation-math methods (`animateComponentText*`,
        `durationUpgradeTextAnimationDurationTicks`) now live in `common/`, byte-identical to
        26.1.2's `common/event/ItemListenersGame.java` apart from the already-common `RUtil`/
        `ClientTickHandler` imports. The NeoForge-only half (the `ItemTooltipEvent`/
        `LivingEntityUseItemEvent.Tick` subscriber methods) was renamed
        `neoforge/event/neoforge/NeoItemListeners.java` to match 26.1.2's naming (mirror discipline)
        and now direct-calls `BrewingTooltips`/`PotionEffectTooltips` instead of posting bus events.
  - [x] **Fabric + Forge tooltip listeners added**, matching this tree's per-bucket-file convention
        (`event/{fabric,forge}/TooltipListeners.java`, alongside the Tick/lifecycle bucket's
        `TickListeners.java`), wired from `PotionsPlusFabricClient.onInitializeClient` /
        `PotionsPlusForge`'s `FMLClientSetupEvent` listener. Only `PotionEffectTooltips` is called on
        these two loaders — `BrewingTooltips` remains genuinely blocked (below). API notes, both
        javap-verified before writing code:
    - **Fabric**: `fabric-item-api-v1` 11.2.0's `ItemTooltipCallback` has no `Player` parameter
      (unlike NeoForge's/Forge's `ItemTooltipEvent`) — used `Minecraft.getInstance().player`, safe
      because tooltips only render while a player exists.
    - **Forge**: `net.minecraftforge.event.entity.player.ItemTooltipEvent` has the same shape as
      NeoForge's (`getEntity()`→`Player`, `getItemStack()`, `getToolTip()`, `getFlags()`) and
      `LivingEntityUseItemEvent.Tick` also matches — both confirmed via javap on the Forge 52.1.2
      universal-srg jar. Registered via `MinecraftForge.EVENT_BUS.addListener(...)` from the
      `FMLClientSetupEvent` listener (client-only event, matching this module's dist-gating
      convention for client registration), not the mod bus — Forge event classes live on the game
      bus (`MinecraftForge.EVENT_BUS`), same as this module's `EffectListeners`.
  - [x] Verified: `:{neoforge,fabric,forge}:build -x test` green; Decision 4a `comm -12` empty on all
        three; `common/` has zero `net.neoforged`/`net.minecraftforge` imports; `:{neoforge,fabric,
        forge}:runServer` all reach `Done (...)!` with zero exceptions and no "Mixin apply failed"
        (the pre-existing `potionsplus:blocks/clothesline` loot-table parse warning is the same
        already-documented unrelated bug seen in the Tick/lifecycle bucket).
  - [x] **`BrewingTooltips` → `common/item/tooltip/BrewingTooltips.java`, done 2026-09-04 (8th
        session).** Its former blockers — `RecipesRegistrar` and
        `AbyssalTroveBlockEntity.ABYSSAL_TROVE_INGREDIENTS` — are both `common/` now (Phase 9's
        access widener gate closed, and the block-entity register-hub project landed in the 7th
        session). Read every import fresh before moving: all clean (`Recipes`, `Potions`,
        `PotionUpgradeIngredients`, `PpIngredient`, `SeededIngredientsLootTables`,
        `AnimatedItemTooltipEvent`, `SavedData`, `BrewingCauldronRecipe`, `alchemy.*`, `Utility`,
        `TooltipPriorities` all already `common/`). Moved verbatim (package line only). Wired into
        Fabric's and Forge's `TooltipListeners.onToolTipEvent`/`getTooltipMessages` (both already
        existed as Phase 7 stubs that explicitly skipped it) — one `BrewingTooltips.onBrewingTooltip(addEvent)`
        call added to each, right before the existing `PotionEffectTooltips` call, matching
        `NeoItemListeners`'s call order exactly.
  - [ ] **`ClotheslineBlockEntityRenderer` — deferred to Phase 11, not this bucket.** It's a real
        `BlockEntityRenderer<ClotheslineBlockEntity>` implementation (client rendering, not a tooltip)
        that needs `core/neoforge/Renderers`'s BE-renderer registration hub — already confirmed
        deferred to Phase 11 by the Registration-hubs bucket above (no fabric/forge `Renderers`
        equivalent exists yet, and none should before that phase). Its one `@SubscribeEvent
        onRender(RenderLevelStageEvent)` static hook (clears a per-frame render-dedup `Set<BlockPos>`)
        is real Phase-7-shaped listener work, but porting only the hook without the renderer
        registration it supports would be dead code on Fabric/Forge. Note for whoever does Phase 11:
        26.1.2's equivalent class has **no such hook** because its 1.21.5+-era renderer pipeline
        (`ClotheslineRenderState`) redesigned away the per-frame tracking this MC version's older
        `BlockEntityRenderer` API still needs — 26.1.2's file is not a usable reference for this one
        piece, unlike the rest of the class.
- [~] **Commands / input — partially done 2026-09-02, and one bucket item reclassified as
      mislabeled.** Of the four items in the plan's bucket table:
  - [x] **`CommonCommands`** — moved to `common/command/PpCommands.java` (matches 26.1.2's package
        and class name exactly, confirmed by reading that tree's file). Ported every subcommand
        except `potionHand`, which needs `Potions.FLYING_TIME_POTIONS` — on 26.1.2 that's common-side
        (`common/core/potion/Potions.java`), but on this branch the equivalent
        (`PotionsRegistrar.FLYING_TIME_POTIONS`) is still neoforge-only pending Phase 5's
        runtime-recipe remainder, the same blocker already recorded against `AdvancementListeners`
        in the Explicit-listeners bucket. Kept `potionHand` registered from NeoForge's
        `event/neoforge/NeoCommandEvents.java` via a **second** `dispatcher.register(...)` call under
        the same `"potionsplus"` literal — Brigadier's `CommandNode.addChild` merges children of two
        separately-registered nodes with the same name (confirmed against Brigadier's source: a
        matching existing child has the new node's children folded into it rather than replacing it),
        so this doesn't require touching `PpCommands` itself or forking the tree. All three loaders'
        `runServer` reached `Done (...)!` with no registration exceptions, which is consistent with
        (but doesn't by itself prove) the merge — no further verification attempted within this
        bucket's scope.
    - Collapsed a latent double-field bug found by diffing against 26.1.2 rather than reproducing
      it: 26.1.2 has **three** separate `expiryTime` fields (`core.CommonCommands.expiryTime` default
      `-1`, read by the loader mixins; `command.PpCommands.expiryTime` default `6000`, set by the
      command; `core.ModState.expiryTime` default `6000`, unclear consumer) that don't talk to each
      other — the `quickItemExpiry` command there sets a field the mixins never read, so the command
      is silently a no-op on 26.1.2. This branch's original `core.neoforge.CommonCommands.expiryTime`
      (default `6000`) was both the command's field and the one NeoForge's `ItemEntityMixin` reads —
      already correctly wired. Kept that single-field design in `PpCommands.expiryTime` (still default
      `6000`, unchanged live behaviour) instead of importing 26.1.2's split; repointed
      `neoforge/mixin/neoforge/ItemEntityMixin.java`'s one reference.
  - [ ] **`ClientCommands` — blocked, not attempted.** Its only subcommand body
        (`JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes`) needs the JEI
        integration classes, which are entirely neoforge-only today (`neoforge/client/integration/jei/*`,
        no `fabric`/`forge` equivalents exist) — Decision 3 "JEI on all three" is **Phase 11** scope.
        The `reveal`/`dumpResource` debug-command shell itself has no loader dependency and could be
        split out, but doing so now would ship a `reveal` command whose entire purpose (toggling JEI's
        hidden-recipe filter) is a no-op on Fabric/Forge until Phase 11 lands — deferred as a unit
        rather than half-ported.
  - [ ] **`KeyMappingsListener` — blocked, not attempted.** Its only body
        (`KeyMappings.ACTIVATE_ABILITY.get().consumeClick()`) reads a keybinding that the
        Registration-hubs bucket already confirmed is correctly deferred to **Phase 11** (`KeyMappings`
        itself has no fabric/forge equivalent yet, by design — client-registration hub work). Nothing
        to port until that keybinding exists on the other two loaders.
  - [x] **`ClientEvents` — reclassified, not this bucket's work.** Read the file: both its methods
        (`FMLClientSetupEvent` → item-property overrides for `DynamicIconItems`;
        `RegisterParticleProvidersEvent` → particle-provider registration) are pure **Phase 11**
        client-registration-hub work with zero command/input logic — the plan's grouping table placed
        it here by mistake. It also depends on `DynamicIconItems`, the same fabric/forge item-hub gap
        the Explicit-listeners bucket flagged for `ClientTooltipComponentFactoriesListeners`. Left
        entirely untouched; whoever does Phase 11 should treat this as that phase's file, not port it
        as a leftover Phase 7 item.
  - [x] Verified: `:{neoforge,fabric,forge}:build -x test` green; Decision 4a `comm -12` empty on all
        three; `common/` has zero `net.neoforged`/`net.minecraftforge` imports;
        `:{neoforge,fabric,forge}:runServer` all reach `Done (...)!` with zero exceptions.

**Phase 7 summary (2026-09-02) — all six buckets touched, none fully closed; 🟡 partial, matching the
Status table.** Net tally across the whole phase:

- **Fully done:** mob-effect behaviour (7 classes, full parity on all three loaders).
- **Confirmed no-op / correctly scoped already:** registration hubs (8 classes — 4 already had
  Fabric/Forge parity from Phases 4/5, 4 correctly deferred to Phase 11).
- **Partially ported, real items remain blocked (not silently dropped — every one below has a
  plan-doc entry with grep/javap evidence):**
  - Explicit listeners: 2/6 done (`EnchantmentListeners`, `ItemListenersMod`); 4 blocked
    (`AdvancementListeners`, `PlayerListeners`, `ClientTooltipComponentFactoriesListeners`,
    `ItemListenersGame`'s move — the last two are now actually resolved, see Tick/lifecycle and
    Client-tooltips buckets below, which shipped after this entry was first written).
  - Tick/lifecycle: 4 core classes done; 2 blocked (`ServerLifecycleListeners`, `ServerPlayerUtility`
    — deliberately not split further).
  - Client tooltips: `AnimatedItemTooltipEvent` redesigned + `PotionEffectTooltips`/`ItemListenersGame`
    done; 2 blocked (`BrewingTooltips`, `ClotheslineBlockEntityRenderer` — the latter reclassified to
    Phase 11).
  - Commands/input: `CommonCommands`→`PpCommands` done; 2 blocked (`ClientCommands`,
    `KeyMappingsListener`); `ClientEvents` reclassified to Phase 11.
- **Blockers this phase surfaced, owned by other phases (not Phase 7 work):**
  1. **Phase 5** — the runtime-recipe/`RecipesRegistrar`/`PotionsRegistrar` remainder blocks
     `AdvancementListeners`, part of `PlayerListeners`, `BrewingTooltips`, and `CommonCommands`'s
     `potionHand` subcommand. The single biggest recurring blocker in this phase.
  2. **Phase 8** — the still-unsplit `neoforge/behaviour` package (containing general gameplay
     classes `MossBehaviour`/`ClotheslineBehaviour`, not just loot modifiers — a new finding from this
     phase) blocks the moss/clothesline half of `PlayerListeners`. **Update 2026-09-03:** half-cleared
     — `MossBehaviour` ported to `common/behaviour/` (see the `PlayerListeners` bullet above);
     `ClotheslineBehaviour` remains blocked on its own dependency chain (Clothesline block/block-entity
     not split cross-loader), not on the package-split issue anymore.
  3. **Phase 11** — `DynamicIconItems` (missing Fabric/Forge equivalent, a Phase 4/5 item-hub gap),
     `KeyMappings`/`Renderers`/`Screens`/`BlockRenderLayers` (client-registration hubs, correctly
     deferred), and Decision 3's JEI-on-all-three all block `ClientTooltipComponentFactoriesListeners`,
     `ClotheslineBlockEntityRenderer`, `KeyMappingsListener`, `ClientCommands`, and `ClientEvents`
     (reclassified into Phase 11 outright).
- **Do not mark Phase 7 ✅ closed.** Every remaining `[ ]` item above is real, evidenced work, not
  polish — but none of it can proceed further without Phases 5, 8, or 11 landing first. The
  loader-agnostic-shell-first strategy this phase used throughout (port what's free of cross-bucket
  dependencies now, leave a plan-doc trail for the rest) means Phase 7 is now **fan-out complete but
  fan-in blocked** — closing it is a matter of finishing those other phases and returning, not
  further Phase-7-shaped investigation.

---

## Phase 8 — NeoForge-only systems (full parity)

*(= 26.1.2 Phase 5, **plus `DataAttachments`**.)* Decision 2 is full parity — reimplement, don't stub.

- [x] **Global loot modifiers — done 2026-09-03** (Wormroot + AddMobEffects, the only two GLMs this
      tree has; there is no third — the plan's "3×" was wrong, `core/neoforge/LootModifiers.java` only
      ever held these two `DeferredHolder`s).
  - [x] **Shared core extracted to `common/behaviour/`:** `WormrootLootBehaviour.apply(List<ItemStack>,
        RandomSource, Block brokenBlock, List<Block> targetBlocks)` and
        `AddMobEffectsLootBehaviour.apply(List<ItemStack>, RandomSource, List<Holder<MobEffect>>
        eligibleEffects)` — pure vanilla logic (no loader imports), lifted verbatim from the neoforge
        `doApply` bodies. NeoForge's own `WormrootLootModifier`/`AddMobEffectsLootModifier` were
        refactored to delegate to these too, so the loot logic now has exactly one implementation
        instead of drifting across three.
  - [x] **Forge: near-verbatim `net.minecraftforge.common.loot.{IGlobalLootModifier,LootModifier}`
        port**, confirmed correct via `javap` on `forge-1.21.1-52.1.2-universal-srg.jar` before writing
        code: `LootModifier.doApply(ObjectArrayList<ItemStack>, LootContext)` — **no extra `LootTable`
        parameter**, unlike 26.1.2's later-MC-version signature `doApply(LootTable, ObjectArrayList,
        LootContext)`. 52.1.2 matches NeoForge's shape exactly, so the two wrapper classes
        (`behaviour/forge/{Wormroot,AddMobEffects}LootModifier.java`) are simpler ports than 26.1.2's.
        `core/forge/LootModifiers.java` uses Forge's own `DeferredRegister`/`RegistryObject` (not the
        common `ForgeHolder` abstraction — this registry holds `MapCodec`s, not game objects, so no
        `Holder` wrapping is needed) against `ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS`,
        registered from `PotionsPlusForge`'s constructor (`LootModifiers.LOOT_MODIFIERS.register(bus)`,
        alongside the other `DeferredRegister`s) — replacing the Phase-7-era `LootModifiers.register()`
        no-op stub and its now-dead call site.
        `AddMobEffectsLootModifier`'s blacklist codec can't reuse NeoForge's
        `NeoForgeExtraCodecs.setOf(...)` (Forge has no equivalent) — used 26.1.2's fix instead:
        `ResourceKey.codec(...).listOf().xmap(HashSet::new, ArrayList::new)`.
        **Hand-written data, not a ported datagen provider** (Phase 10's `commonDatagen` share hasn't
        landed yet, so nothing is copied to `common/` automatically): copied NeoForge's already-generated
        `data/potionsplus/loot_modifiers/{wormroot,add_mob_effects_to_tools_and_armor}_loot_modifier.json`
        verbatim into `forge/src/main/resources/`, plus the Forge-namespace enable/order index
        `data/forge/loot_modifiers/global_loot_modifiers.json` NeoForge has no equivalent of (mirrors
        `data/neoforge/loot_modifiers/global_loot_modifiers.json`'s two entries exactly). Revisit once
        Phase 10 makes `common/` the shared source for the per-modifier JSON (the enable-list file stays
        loader-namespaced either way).
  - [x] **Fabric: no `MODIFY_DROPS` hook exists at this branch's pinned `fabric-loot-api-v3` version —
        verified by `javap`, not assumed from the plan's original text.** `fabric_api_version =
        0.116.7+1.21.1` resolves `fabric-loot-api-v3:1.0.3+3f89f5a519`, whose `LootTableEvents` only
        declares `REPLACE`/`MODIFY`/`ALL_LOADED` — `MODIFY` fires at datapack-load time over a
        `LootTable.Builder` (structural edits only), not post-generation over the drop list. The
        plan's original "closer to `doApply` than `MODIFY`" text assumed a newer fabric-api than this
        toolchain is pinned to (Decision 4's toolchain table pins `0.116.7+1.21.1` from apt-ores as
        known-good; not bumped without a stronger reason than one event's absence).
        **Used a mixin instead: `mixin/fabric/LootTableMixin.java` injects at `@At("RETURN")` of
        `LootTable.getRandomItems(LootParams, RandomSource)`**, the same post-generation,
        whole-drop-list entry point NeoForge/Forge patch internally to run every `IGlobalLootModifier`
        — so this reaches every loot table exactly like the other two loaders' GLM system, not a
        narrower approximation of it. `LootParams` (not just `LootContext`) already exposes
        `hasParam`/`getParamOrNull`, so `LootContextParams.BLOCK_STATE` reads straight off the mixin's
        method parameters with no context-construction workaround needed. No explicit registration
        call — mixins self-apply via `potionsplus.fabric.mixins.json` (added `LootTableMixin` to its
        list) — so `core/fabric/LootModifiers.java`'s Phase-7-era no-op stub (which had assumed
        `MODIFY_DROPS` and wouldn't have compiled) was deleted outright, along with its dead
        `LootModifiers.register()` call site in `PotionsPlusFabric`.
  - [x] Verified: `:{neoforge,fabric,forge}:build -x test` green (including Fabric's refmap generation
        for the new mixin); Decision 4a `comm -12` empty on all three; `:{neoforge,fabric,
        forge}:runServer` all reach `Done (...)!`/`Done (...)!` with zero exceptions and no "Mixin
        apply failed" (only the pre-existing, already-documented `potionsplus:blocks/clothesline`
        loot-table parse warning, unrelated — Clothesline is still neoforge-only, tracked above).
        **Not yet verified: an actual drop roll on Fabric/Forge** (break a Wormroot block or armor
        piece in a real world) — `runServer`/`runClient` smokes prove the mixin/GLM registered and
        didn't crash, not that the modified loot is correct. Whoever does Phase 13 (or anyone jumping
        into a `runClient` session before then) should break a `minecraft:rooted_dirt` block and check
        for `potionsplus:wormroot` in the drops on all three loaders.
- [x] **Capabilities / `IItemHandler`** (`core/Capabilities`, clothesline storage) — **done
      2026-09-04, closing Phase 8.** `ClotheslineBlock`/`ClotheslineBlockEntity` and the block +
      `BlockEntityType` registration on all three loaders were already landed by an earlier Phase 11a
      session (see that phase's row), so this bucket was purely the capability wiring.
  - [x] **Forge: confirmed via `javap` against `forge-1.21.1-52.1.2-universal-srg.jar`
        (`modules-2/files-2.1/net.minecraftforge/forge/1.21.1-52.1.2/.../forge-1.21.1-52.1.2-universal-srg.jar`
        in the gradle cache) that **1.21.1 Forge 52.1.2 still uses the pre-1.20.5 capability-provider
        shape**, exactly as flagged — do not assume 26.1.2's `RegisterCapabilitiesEvent`/
        `BlockCapability` API, which does not exist in this jar at all (no
        `net.minecraftforge.capabilities` package; the *same-named*
        `net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent` class here is the old
        per-mod `<T> void register(Class<T>)` declaration event, an unrelated API from the same era).
        The real hook, confirmed by javap, is `net.minecraftforge.event.AttachCapabilitiesEvent<T>`
        (`getObject()`/`addCapability(ResourceLocation, ICapabilityProvider)`), fired once per game
        object; javap on the forge-patched `minecraft-merged-srg-patched.jar`'s `BlockEntity.class`
        confirms it already `extends CapabilityProvider<BlockEntity>` (Forge patches this in), so no
        extra provider plumbing is needed on the BE side. `ICapabilityProvider.getCapability(Capability<T>,
        Direction)` returns `LazyOptional<T>` (javap-confirmed) rather than a plain nullable value like
        NeoForge's lookup. `ForgeCapabilities.ITEM_HANDLER` (javap-confirmed present) is the
        well-known shared capability instance every item-handler consumer already queries, used
        instead of declaring a new named capability. `net.minecraftforge.items.wrapper.InvWrapper`
        lives at the *same* package path as NeoForge's, with an identical `InvWrapper(Container)`
        constructor (javap-confirmed) — so the wrapping logic is a near-verbatim port of
        `core.neoforge.Capabilities`. Implemented in `forge/.../core/forge/Capabilities.java`,
        following this module's existing `CommandListeners`/`TickListeners`/`EffectListeners`
        explicit-registration convention (a plain lambda against `MinecraftForge.EVENT_BUS`, not an
        `@Mod.EventBusSubscriber` class) — `register()` is called from `PotionsPlusForge`'s
        constructor (call site already existed as a no-op stub call from an earlier session).
        **One real bug caught only by an actual `:forge:runServer` smoke, not by compilation:**
        `IEventBus.addListener((AttachCapabilitiesEvent<BlockEntity> e) -> …)` compiles fine but
        throws `IllegalArgumentException: Cannot register a generic event listener with addListener,
        use addGenericListener` at mod-construction time, because `AttachCapabilitiesEvent<T> extends
        GenericEvent<T>`, which `IEventBus.addListener` rejects outright. Fixed by javap-verifying
        `IEventBus`'s actual overload set against `eventbus-6.2.32.jar` (the version 52.1.2 pulls in)
        and switching to `MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, …)`.
  - [x] **Fabric: confirmed via `javap` that this branch's pinned `fabric_api_version =
        0.116.7+1.21.1` resolves `fabric-transfer-api-v1:5.4.3+c24bd99419`** (read straight off
        `fabric-api-0.116.7+1.21.1.pom`'s own dependency list in the gradle cache, not assumed) —
        confirms the plan's 1.21.1-era name is right and 26.1.2's is stale: this jar has no
        `ContainerStorage` class at all; `ItemStorage.SIDED` is a
        `BlockApiLookup<Storage<ItemVariant>, Direction>` constant. `BlockApiLookup` itself lives in
        the separate `fabric-api-lookup-api-v1:1.6.71+b559734419` module (also read off the same pom);
        javap on that jar confirms the exact overload used:
        `registerForBlockEntity(BiFunction<? super T, C, A>, BlockEntityType<T>)`. javap on
        `InventoryStorage` confirms `static InventoryStorage of(Container, Direction)` — matching the
        plan's "not `ContainerStorage.of`" note. Implemented in
        `fabric/.../core/fabric/Capabilities.java`; `register()`'s call site from
        `PotionsPlusFabric.onInitialize()` already existed as a no-op stub call from an earlier
        session, so no entrypoint change was needed on Fabric.
  - [x] Verified: `:common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava`
        and `:neoforge:build :fabric:build :forge:build -x test` both green; Decision 4a `comm -12`
        empty on both fabric and forge against common. `:neoforge:runServer` and `:fabric:runServer`
        smokes both reach `Done (...)!` with zero new exceptions (Fabric's pre-existing
        `golden_cubensis`/`diamour` item-registration-gap noise, unrelated to this bucket, is unchanged
        from before this session — those two items are simply never registered on Fabric/Forge yet, a
        gap this task didn't touch). `:forge:runServer` also reaches `Done (...)!` clean, but then
        **crashes ~2s later** with `java.util.NoSuchElementException: No value present` inside
        `SeededIngredientsLootTables.getItemsInTags` → `RecipesRegistrar.injectRuntimeRecipes` →
        `ServerLifecycleListeners.onServerStarted` — **confirmed pre-existing and unrelated to this
        bucket**, not a regression: `git stash`-ing both `Capabilities.java` changes and re-running
        `:forge:runServer` reproduces the identical crash at the identical stack trace, so this is the
        same Forge-only `golden_cubensis`/`diamour` item-registration gap Fabric already tolerates
        (Fabric's `RecipesRegistrar` path happens not to hit the missing-item case; Forge's does) —
        real, but out of scope for the Capabilities bucket and not touched here. Whoever picks up the
        `golden_cubensis`/`diamour` OreItems gap (not yet tracked as its own checklist item anywhere in
        this doc — worth adding when someone next touches `core.{fabric,forge}.items.OreItems` or
        `RecipesRegistrar`) should start from this stack trace. **Not verified in-world:** actually
        placing a Clothesline, hanging items on it, and confirming a hopper/other IItemHandler consumer
        can insert/extract through the new capability on Fabric/Forge (no GUI-automation tool in this
        environment; `runServer` proves registration + zero crash at startup/attach time, not runtime
        query behavior).
- [x] **`core/DataAttachments` — deleted, not abstracted. Done 2026-09-03.** Per Decision 2's
      carve-out and the 26.1.2 precedent (no attachment abstraction there — it has no attachments):
  - [x] Added a transient (not persisted to NBT — the original attachment builder had no
        `.serialize(...)` either, so this preserves session-only semantics), `Map<UUID,
        LastPotionUsePlayerData>` field + `getLastPotionUseTime(Player)`/`setLastPotionUseTime(Player,
        long)` helpers to `common/.../persistence/SavedData.java`, mirroring the existing
        `playerDataMap`/`getData(Player)` pattern for `PlayerBrewingKnowledge`.
  - [x] `neoforge/.../mixin/neoforge/PotionItemMixin.java` now calls
        `SavedData.instance.getLastPotionUseTime(player)` / `.setLastPotionUseTime(player, …)` instead
        of `player.getData(DataAttachments.LAST_POTION_USE_PLAYER_DATA)`; `finishUsingItem` gates on
        `entityLiving instanceof Player` (the attachment API allowed any `LivingEntity`, but
        `SavedData` is keyed by player UUID and the cooldown check in `use` only ever reads it for a
        `Player` anyway).
  - [x] `neoforge/.../mixin/neoforge/EntityMixin.java` no longer `extends AttachmentHolder` — that
        was only there to give `Entity` the `getData`/`setData` NeoForge attachment API that
        `PotionItemMixin` consumed; nothing else in the file used it. Dropped along with the
        now-unused `DataAttachments` import.
  - [x] Removed `DataAttachments.ATTACHMENT_TYPES.register(bus)` from `core/neoforge/PotionsPlus.java`
        and deleted `neoforge/.../core/neoforge/DataAttachments.java`.
  - [x] Net effect: **`DataAttachments` stops being a parity problem** — there is no NeoForge-only
        system left to reimplement on Fabric and Forge, so Decision 2's "plus `DataAttachments`"
        carve-out disappears and this phase matches 26.1.2 Phase 5 exactly.
        Verified: `:neoforge:compileJava :common:compileJava` and
        `:neoforge:build :fabric:build :forge:build -x test` both green.
- [x] **Server config — done 2026-09-03**, feeding `Platform.getPotionDrinkTimeTicks` /
      `getPotionDrinkCooldownTimeTicks` on all three loaders.
  - [x] **Forge:** `forge/.../config/PotionsPlusConfig.java`, a near-verbatim
        `net.minecraftforge.common.ForgeConfigSpec` port of the NeoForge `ModConfigSpec` version
        (confirmed both classes have an identical `Builder.translation(...).defineInRange(...)`
        surface — no signature differences to work around, unlike some other Forge/NeoForge pairs
        this phase hit). Registered via `ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,
        PotionsPlusConfig.CONFIG_SPEC)` in `PotionsPlusForge`'s constructor — confirmed via `javap` on
        `fmlcore-1.21.1-52.1.2.jar` that 52.1.2's `ModLoadingContext` still exposes
        `registerConfig(ModConfig.Type, IConfigSpec<?>)` directly (unlike 26.1.2, which reaches it
        through a newer `FMLJavaModLoadingContext.getModBusGroup()`-shaped constructor this branch's
        Forge doesn't have) and that `net.minecraftforge.common.ForgeConfigSpec` exists in the
        52.1.2 universal jar. Forge's `PlatformImpl` reads through the same
        try/`IllegalStateException`-fallback pattern NeoForge's already used, for the same reason
        (recipe datagen can run before the server config loads).
  - [x] **Fabric:** `fabric/.../config/PotionsPlusConfig.java`, a near-verbatim hand-rolled Gson JSON
        config under `FabricLoader.getInstance().getConfigDir().resolve("potionsplus.json")`, ported
        from 26.1.2's Fabric config (same shape: load-or-create-with-defaults at class-load, `save()`
        writes pretty-printed JSON). No try/catch needed in `PlatformImpl` — a plain instance field,
        not a lazy spec value, so there's no "read before config loaded" failure mode to guard against.
  - [x] Both loader classes live under the same `grill24.potionsplus.config` package (this branch's
        neoforge config already uses that package unqualified, unlike 26.1.2's
        `config.{fabric,forge}` split) — no `common`/`neoforge` intersection risk since `config/`
        was never a `neoforge/`-only package to begin with.
  - [x] Verified: `:{neoforge,fabric,forge}:build -x test` green; Decision 4a `comm -12` empty on both
        fabric and forge; `:{neoforge,fabric,forge}:runServer` all reach `Done (...)!` with zero
        PotionsPlus exceptions and clean shutdown on `stop` (only the pre-existing, already-documented
        `potionsplus:blocks/clothesline` loot-table parse warning on Forge, unrelated). **Forge's log
        is direct proof the config wiring is live, not just compiling:** it generated
        `world/serverconfig/potionsplus-server.toml` and logged `Incorrect key potionDrinkTimeTicks
        was corrected from null to its default, 16` / `potionUseCooldownTimeTicks ... 0` — the
        `ForgeConfigSpec` registered and ran its correction pass. Not yet verified: actually editing
        a config value and confirming `Platform.getPotionDrinkTimeTicks()` picks it up at runtime on
        all three — deferred to whoever does Phase 13, same caveat as the other Phase 8 buckets above.
- [x] **Biome modifiers — done 2026-09-03**, all three: `add_lunar_berry_bush_patch`,
      `remove_berry_bush_patch`, and a third this branch has that 26.1.2 doesn't —
      `add_dense_diamond_ore`. The plan's "verified 2026-09-01" note only mentioned the first two;
      the ore modifier needed the same treatment.
  - [x] **Forge reads the same datapack JSON, confirmed by class listing** (`javap -l` on
        `forge-1.21.1-52.1.2-universal-srg.jar` shows `net.minecraftforge.common.world.
        ForgeBiomeModifiers$\{Add,Remove\}FeaturesBiomeModifier`, matching NeoForge's `add_features`/
        `remove_features` types 1:1) — hand-authored
        `forge/src/main/resources/data/potionsplus/forge/biome_modifier/{add_lunar_berry_bush_patch,
        remove_berry_bush_patch,add_dense_diamond_ore}.json`, the NeoForge files with `"type":
        "neoforge:…"` → `"forge:…"` and the namespace directory `neoforge/` → `forge/`. No code
        required, exactly as 26.1.2 predicted.
  - [x] **Fabric has a code-only API**, ported near-verbatim from 26.1.2's
        `fabric/.../core/fabric/BiomeModifiers.java`: `BiomeModifications.create(...)` +
        `BiomeSelectors.tag(ConventionalTags.Biomes.IS_TREE_CONIFEROUS)` + `ModificationPhase.
        {REMOVALS,ADDITIONS}` with `ctx.getGenerationSettings().{remove,add}Feature(...)`. Verified via
        `javap` against the resolved `fabric-biome-api-v1:13.0.31` jar (not assumed from 26.1.2, which
        is on a different fabric-api generation) — `BiomeSelectors`/`BiomeModifications`/
        `BiomeModificationContext.GenerationSettingsContext` all match the reference shape exactly,
        plus `BiomeSelectors.foundInOverworld()` for the ore modifier (no `#c:is_overworld` tag needed).
        **One necessary divergence from 26.1.2:** the placed-feature keys
        (`potionsplus:patch_lunar_berry_bush`, `potionsplus:ore_dense_diamond_small`) are declared
        inline via `ResourceKey.create(Registries.PLACED_FEATURE, Utility.ppId(...))` rather than
        imported from `worldgen.Placements`, because that class is still neoforge-only on this branch
        (26.1.2's `Placements` is already common). Revisit once worldgen is split.
  - [x] **Fixed the exact leak the plan warned about, found while doing this work, not preemptively:**
        `data/potionsplus/neoforge/biome_modifier/*.json` was sitting in `common/src/main/resources/`
        and shipping inside the Fabric and Forge jars too (confirmed via `unzip -l` on the built jars).
        **First fix attempt was wrong and is worth recording:** adding a `CopySpec.exclude(...)` to
        each loader's `processResources` did nothing — the final jar's resources come from
        `shadowJar`'s `shadowBundle` configuration (`:common`'s prebuilt `transformProduction{Fabric,
        Forge}` artifact), a completely separate merge path `processResources`'s `from` block never
        touches. The real fix: moved the three JSON files from `common/src/main/resources/` to
        `neoforge/src/main/resources/` (they were hand-authored, not datagen'd, so a plain `git mv`
        was safe) — since they never enter the shared `common` jar, they can't leak into it. Re-verified
        via `unzip -l` on all three rebuilt jars: present only in the neoforge jar.
  - [x] **Worldgen JSON these modifiers reference (`configured_feature`/`placed_feature` for
        `lunar_berry_bush`/`ore_dense_diamond_small`) also didn't exist outside the neoforge jar** —
        confirmed via `find`, only in `neoforge/src/generated/resources/` (Phase 10's `commonDatagen`
        share hasn't landed). Hand-copied (not moved — NeoForge's `runData` regenerates its own) into
        `fabric/src/main/resources/` and `forge/src/main/resources/` individually, **not** `common/`:
        a first attempt putting them in `common/src/main/resources/` hit the exact same
        `:neoforge:processResources` "duplicate entry" failure as the biome_modifier leak's mirror
        image — `common`'s copy collides with `neoforge/src/generated/resources`'s datagen'd copy of
        the same path. Per-loader placement (matching the loot-modifier JSON precedent from earlier
        this phase) sidesteps it entirely at the cost of two duplicated copies instead of one shared
        one; revisit once Phase 10 makes `commonDatagen` the single source instead of neoforge's own
        `src/generated/resources`.
  - [x] Verified: `:{neoforge,fabric,forge}:build -x test` green; Decision 4a `comm -12` empty on all
        three; `:{neoforge,fabric,forge}:runServer` all reach `Done (...)!` with zero exceptions.
        **Fabric explicitly logs the modifiers firing** — `Applied 63 biome modifications to 53 of 64
        new biomes` — proof-of-life beyond "didn't crash" that Forge's silent datapack-JSON path
        doesn't give for free (Forge would only log on a parse failure, same as the pre-existing
        unrelated clothesline loot-table warning both loaders already show). **Not yet verified: actual
        worldgen output** (generate a new world on each loader and confirm lunar berry bushes /
        dense diamond ore actually appear) — same caveat as the loot modifiers above, deferred to
        whoever does a real `runClient` session or Phase 13.

**Exit criterion:** `:common:test :neoforge:build :fabric:build :forge:build` green; each system
demonstrably fires on all three loaders.

---

## Phase 9 — Mixins + access widening / transformers

*(= 26.1.2 Phase 6, **harder** — 1.21.1 is obfuscated.)* 18 mixin classes today (16 common + 2 client).

> **Do this first — it is the blocker Phase 1 handed over, and it is not a mixin bug.**
> Dev runs on this branch die before the main menu because `common/` and `neoforge/` share **14
> packages** (107 of `neoforge/`'s 179 files) and JPMS forbids two modules exporting the same
> package. Move each split package's `neoforge/` residents into a `.neoforge` sub-package
> (`grill24.potionsplus.block` → `grill24.potionsplus.block.neoforge`, etc.), matching the
> `core.neoforge` / `event.neoforge` / `persistence.neoforge` convention already in the tree.
> **The target layout is not a matter of taste — copy 26.1.2's**, whose `neoforge/` is exactly:
> `core.neoforge` (21), `event.neoforge` (16), `data.neoforge` (12), `mixin.neoforge` (3),
> `platform.neoforge` (2), `behaviour.neoforge` (2), `network.neoforge` (1), `data.loot.neoforge` (1),
> `core.neoforge.blocks` (1), `config.neoforge` (1) — 60 files, zero packages shared with `common/`.
> Expect 1.21.1's remainder to be larger until Phases 4/7/8/10 land, but the *names* should match.
> Gate the work on Decision 4a's `comm -12` check returning empty, not on a green build.
> **Read Phase 1's "Root cause" section in full before touching anything here** — it records the
> three build wirings already tried and rejected, why `loom.mods` and the refmap/mixin-AP are red
> herrings, and why mixin classes can in fact live in `common/` (fishtastic proves it). Treat
> "`:neoforge:runGametest` boots and `LivingEntityMixin` still reads `MobEffects.SLIP_N_SLIDE` from
> `common/`" as this phase's first exit criterion, before any Fabric/Forge mixin work.

- [x] Split `potionsplus.mixins.json` into `common` + `potionsplus.{fabric,forge,neoforge}.mixins.json`
      by target. **`compatibilityLevel` stays `JAVA_21`** (26.1.2's `JAVA_25`-not-recognised crash on
      Forge's bundled Sponge Mixin does not apply here). **Already done before this session** (Phase
      4's mixin-split fix) — 10 vanilla-only mixins in `common`, 4 fabric-only, 6 forge-only (now, see
      below), 8 neoforge-only.
- [x] **Wire the common config into all three loaders.** Verified: `fabric.mod.json`'s `"mixins"`
      array, `forge/META-INF/mods.toml`'s `[[mixins]]`, and `neoforge.mods.toml`'s `[[mixins]]` each
      list `potionsplus.mixins.json` in addition to their own platform config. Already correct.
- [x] **Refmaps — three real bugs found via actual `runClient` launches and fixed, 2026-09-03.**
      `./gradlew build` was never enough to catch any of these — every one is invisible to a green
      build, exactly the failure mode this checklist item warned about in the abstract. Found by
      actually running `:forge:runClient` (per explicit instruction: build-only verification is not
      sufficient for mixin/refmap work) and reading the crash.
      1. **Refmap generation was silently disabled.** `loom.mixin.defaultRefmapName` was already set
         project-wide and `common`'s config already declared `"refmap"`, but **newer
         `dev.architectury.loom` (1.17-SNAPSHOT) disables the legacy mixin annotation processor by
         default** ("The mixin annotation is no longer enabled by default..." — printed on every
         `./gradlew` invocation and easy to read as noise). With the AP off, `find . -iname
         '*refmap*'` across the whole tree returned **zero files** despite a fully green build. Fixed
         by adding `useLegacyMixinAp = true` to the root `loom.mixin` block.
      2. **`common` and each platform module generated a refmap under the same filename**
         (`potionsplus-refmap.json`, from the same `defaultRefmapName` default), and shadowJar merging
         let one clobber the other in the final jar depending on task ordering. Fixed by giving each
         module its own name (`potionsplus-{common,fabric,forge,neoforge}-refmap.json`, set per-module
         in each `build.gradle`'s own `loom.mixin` block, declared explicitly in each `*.mixins.json`).
      3. **The real fix, and the one that actually mattered: `common`'s shared mixin config must NOT
         declare a `"refmap"` key at all.** Architectury's own transformer says so explicitly at
         launch: `[Architectury Transformer] Mixin Config [potionsplus.mixins.json] contains
         'refmap', please remove it so it works in development environment!` — a shared/cross-loader
         mixin config gets its refmap resolved dynamically by Architectury's own `RemapperChain`
         *unless* a `"refmap"` path is hardcoded, in which case Architectury uses that file literally
         in every environment, dev included, even though the AP only ever produces ONE namespace's
         data (Fabric intermediary) for it. With `"refmap": "potionsplus-common-refmap.json"` still in
         `potionsplus.mixins.json`, `:forge:runClient` crashed applying `BootstrapMixin`:
         `@Inject annotation on bootStrap specifies a target class 'net/minecraft/class_2966', which
         is not supported` — `class_2966` being `CauldronInteraction`'s Fabric-intermediary id, wrong
         in every way for a Forge dev environment. Removing the `"refmap"` key from
         `common/src/main/resources/potionsplus.mixins.json` (keeping `defaultRefmapName` so the AP
         still has somewhere to write) fixed it outright — `:forge:runClient` now clears the
         mixin-apply phase cleanly and reaches `Datafixer Bootstrap` with zero errors. Bugs 1 and 2
         were real and are still fixed (verified via jar inspection - both refmaps coexist correctly,
         and `transformProductionForge`'s SRG-remapped copy of `common`'s refmap is correct), but
         **bug 3 was the one actually causing the crash** - 1 and 2 alone did not fix it. Do not
         re-add a `"refmap"` key to any shared/`common` mixin config; platform-local configs
         (`potionsplus.{fabric,forge,neoforge}.mixins.json`) keep theirs, since those aren't shared
         cross-loader and the ambiguity doesn't apply to them.
      This turned out to also validate the earlier "surfaced a second, related latent bug" note below
      bullet below.
- [x] **Pass `--mixin.config` explicitly on every Forge loom run.** Added to `forge/build.gradle`
      (`loom.runs.configureEach { run.programArgs '--mixin.config', 'potionsplus.forge.mixins.json' }`),
      copied from 26.1.2's identical fix verbatim. Not yet verified by an actual `:forge:runClient`
      launch (this session was compile/build/jar-level only) — do that before calling this phase closed.
- [ ] **Production mixin discovery is a separate problem from dev discovery.** Forge 64.1.0 had no
      `[[mixins]]` parsing in production at all, and the only working path was the **`MixinConfigs`
      manifest attribute** on the jar. Forge 52.x may still parse `[[mixins]]` — **determine this by
      installing a packaged jar and checking behaviour**, not from documentation. **Not done this
      session** — needs a real Forge install, out of reach of a build-only verification pass. On
      26.1.2 this failed silently in production while every dev run passed, and it shipped.
- [x] `compatibilityLevel` stays `JAVA_21`, which stock `org.spongepowered:mixin:0.8.x` accepts.
      Expect a harmless `higher than the maximum level supported (JAVA_13)` warning. Do **not** port
      26.1.2's `sponge-mixin` resolutionStrategy swap — that existed solely to get `JAVA_25` accepted.
      Already correct in all four mixin configs.
- [x] **Access widener vs access transformer.** `common/src/main/resources/potionsplus.accesswidener`
      already existed (7 entries: `Block.popExperience`, `HolderSetCodec.homogenousListCodec`, 4
      `TrackingEmitter` fields) and `neoforge/META-INF/accesstransformer.cfg` already existed
      (pre-dates the split, broader — neoforge's own historical AT needs, superset of the AW).
      **Authored the missing mirror**: `forge/src/main/resources/META-INF/accesstransformer.cfg`,
      same 7 members in AT syntax. Nothing in `forge/` actually touches these members yet (Phase 11
      client work is what will — `TrackingEmitter` is particle-rendering-only), so this compiles clean
      but is currently unexercised; that's expected; keep it mirrored as `common`'s AW grows. Confirmed
      NeoForge 21.1 needs its AT kept (relied on for its own broader pre-existing member set;
      `neoforge.mods.toml`'s `[[accessTransformers]]` stays commented out, using the default
      `META-INF/accesstransformer.cfg` fallback path, same as 26.1.2).
- [x] **`RecipeManager.byType`/`byName` access widener/transformer entries added, 2026-09-04.**
      `core.neoforge.RecipesRegistrar#injectRuntimeRecipes` mutates `recipeManager.byType`
      (`private Multimap<RecipeType<?>, RecipeHolder<?>> byType`) and `.byName`
      (`private Map<ResourceLocation, RecipeHolder<?>> byName`) directly as field writes, not
      reflection — confirmed via `javap -p -s` on `minecraft-merged-mojang-patched.jar`
      (fabric-loom neoforge cache, 21.1.209): neither field is `final`, so `accessible field` (no
      `mutable`) is sufficient in AW terms. **`neoforge/META-INF/accesstransformer.cfg` already had
      both entries pre-dating this session** (`public net.minecraft.world.item.crafting.RecipeManager
      byType`/`byName`) — that's why NeoForge's own `injectRuntimeRecipes` already compiles and runs
      today. Added the missing mirrors: `common/src/main/resources/potionsplus.accesswidener`
      (`accessible field net/minecraft/world/item/crafting/RecipeManager byType
      Lcom/google/common/collect/Multimap;` / `byName Ljava/util/Map;`) and
      `forge/src/main/resources/META-INF/accesstransformer.cfg` (same two members, AT syntax,
      mirroring neoforge's pre-existing entries verbatim). Verified: `./gradlew :common:compileJava
      :neoforge:compileJava :fabric:compileJava :forge:compileJava` → `BUILD SUCCESSFUL`.
      **What this does and doesn't unblock**: the reference tree (26.1.2) solves this differently —
      `common/mixin/RecipeManagerMixin.java` injects into `RecipeManager.prepare(...)`, which returns
      an immutable `RecipeMap` on that (newer) MC version. **1.21.1's `RecipeManager` has no
      `RecipeMap`/`prepare()` of that shape at all** (confirmed via the same javap dump — 1.21.1 uses
      `apply(Map, ResourceManager, ProfilerFiller)` returning `void` and mutates `byType`/`byName`
      fields in place), so the reference tree's mixin approach cannot be mirrored line-for-line here;
      the AW/AT field-widening approach already established by NeoForge's own pre-existing AT is the
      correct 1.21.1-shaped equivalent, and is now mirrored to Fabric/Forge. **This does NOT by itself
      unblock porting `RecipesRegistrar` to `common/`** — read in full, the class has three more
      neoforge-only couplings unrelated to `byType`/`byName`: (1) `RECIPE_TYPES`/`RECIPE_SERIALIZERS`
      are `net.neoforged.neoforge.registries.DeferredRegister`, needing the same
      platform-abstraction shape as Phase 4/5's register hubs; (2)
      `core.seededrecipe.neoforge.SeededPotionRecipes` (173 lines) and
      `core.seededrecipe.neoforge.SanguineAltarRecipes` (which itself imports
      `recipe.abyssaltroverecipe.neoforge.SanguineAltarRecipeBuilder`) are both neoforge-only classes
      the injection functions call into, not just the field-write step; (3)
      `core.neoforge.ServerLifecycleListeners.postProcessRecipes` is neoforge-namespaced. Each is its
      own Phase-4/5-shaped sub-project (register-hub abstraction + a builder DSL port), consistent
      with the fan-out already documented in Phase 11's blocker note below. **Update 2026-09-04
      (later session): all three couplings closed, `RecipesRegistrar` moved to `common/`** — see the
      new progress-log row below for the full writeup. **Runtime verification**: not separately smoke-tested in isolation (no new
      NeoForge/Forge `runClient` smokes below confirm no AW/AT regression from adding the entries.
- [x] **Audit each of the 18 mixins for NeoForge-only injection targets — and audit Fabric/Forge for
      the mixins they were each still missing entirely.** 26.1.2's one NeoForge-only finding
      (`BucketItemMixin` → `FluidType.onVaporize`) holds here too, confirmed by javap (below). But the
      bigger finding: **Fabric and Forge had never received `BucketItemMixin`, `ItemEntityMixin`
      (Forge)/`ItemEntityLifespanMixin` (Fabric), or `LivingEntityMixin` (Forge) at all** — these three
      gameplay features (salt-on-bucket-empty, `/pp` item-expiry override, Slip'n'Slide friction +
      Wreath death protection + sprint-speed attribute) were silently NeoForge-only since Phase 4's
      mixin split, with no plan-doc flag (they weren't part of Phase 7's event-surface audit because
      they're mixins, not events). Ported all four this session, javap-verifying every injection point
      against the actual jars this project compiles against rather than assuming 26.1.2 parity:
      - `BucketItem.emptyContents` **is not the same method across loaders on 1.21.1.** Forge/NeoForge
        both patch in a 5-arg overload (`Player, Level, BlockPos, BlockHitResult, ItemStack`) absent
        from plain vanilla (confirmed via javap on all three of Forge's, NeoForge's, and the plain
        merged jar's `BucketItem.class` — the 5-arg overload's real name survives even in Forge's
        *SRG*-mapped jar, proof it's a patch addition, not a renamed vanilla member). Fabric's
        `BucketItemMixin` therefore targets the vanilla 4-arg overload instead (same shape as 26.1.2's
        Fabric mixin, updated for 1.21.1's `Player`-not-`LivingEntity` vanilla signature). Forge's
        mirrors NeoForge's 5-arg target exactly (confirmed present) but — like 26.1.2's Forge tree —
        hooks the plain vanilla ultra-warm-dimension evaporation branch (`Level.playSound(Player,
        BlockPos,SoundEvent,SoundSource,F,F)`, confirmed present inside the 5-arg method on **both**
        Forge and NeoForge's jars) rather than `FluidType.onVaporize`, since Forge's own capability
        attachment isn't wired here either.
      - `Holder<T>.getKey()` **does not exist on vanilla/Forge's `Holder`** (only NeoForge patches it
        in — already documented in this file's "VERIFIED API FACTS", and independently corroborated by
        `ForgeHolder.java`'s own `getKey()` having no `@Override`). Forge's ported `LivingEntityMixin`
        had to use `attribute.unwrapKey()` instead of the NeoForge original's `attribute.getKey()`.
        Caught immediately by `:forge:compileJava` — exactly the kind of error the mirror-then-compile
        workflow is supposed to catch.
      - **Enabling the refmap AP (previous bullet) surfaced a second latent bug**: with the AP
        actually validating obfuscation mappings, `neoforge/mixin/neoforge/BucketItemMixin.java`'s
        `@Inject` failed compilation outright — `error: Unable to locate obfuscation mapping for
        @Inject target emptyContents` — because both the enclosing 5-arg `emptyContents` and the
        `FluidType.onVaporize` `@At` target are NeoForge-patch additions with no entry in the vanilla
        obfuscation table the AP resolves against. This had been silently uncompiled-against until the
        AP was actually on. Fixed with `remap = false` on that one `@Inject` (patch-added members keep
        their literal name in production, so skipping the refmap lookup is correct, not a workaround).
        Forge's and Fabric's new `BucketItemMixin`s never hit this because their targets are real
        (Forge: patch-added-but-mapped; Fabric: plain vanilla).
      - `ItemEntity.lifespan` **is a mutable public field on both Forge and NeoForge** (not vanilla —
        vanilla hardcodes the `6000`-tick despawn literal). Forge's `ItemEntityMixin` ported verbatim
        from NeoForge's (field `@Redirect`). Fabric has neither the field nor the patch, so its new
        `ItemEntityLifespanMixin` uses `@ModifyConstant` on the vanilla `6000` literal instead — same
        shape as 26.1.2's Fabric `ItemEntityLifespanMixin`, confirming the vanilla-literal approach is
        the right one here too, not a 1.21.1-specific guess.
      - **`event/{fabric,forge}/PlayerListeners`'-equivalent "on item pickup" mixin (26.1.2's
        `fabric/mixin/fabric/ItemEntityMixin`) is explicitly NOT part of this — it's a different
        feature (brewing-knowledge pickup alerts) blocked on Phase 5's runtime-recipe remainder per
        Phase 7's own notes, not a mixin/refmap concern.** Left untouched; don't conflate the two
        "ItemEntityMixin" names across branches.
      - All three loaders' mixin configs updated (`BucketItemMixin` added to fabric+forge;
        `ItemEntityLifespanMixin` added to fabric; `ItemEntityMixin`+`LivingEntityMixin` added to
        forge) and verified via `./gradlew build -x test -x compileTestJava` → `BUILD SUCCESSFUL`,
        all three loader jars produced, refmaps embedded (see above).
- [x] `ItemEntityMixin` / `LivingEntityMixin` Forge equivalents: confirmed via javap against the actual
      Forge-named jar this project compiles against — `BlockState.getFriction(LevelReader,BlockPos,
      Entity)` and the `ItemEntity.lifespan` field both match NeoForge's exactly (same descriptors);
      see the bullet above for the one real divergence found (`Holder.getKey()`).
- [x] **Descriptor precision.** 26.1.2 lost real time to a mixin targeting `Player.onItemPickup(Entity)`
      when the actual method is `LivingEntity.onItemPickup(ItemEntity)`. Obfuscated 1.21.1 makes this
      class of error harder to spot, not easier — `defaultRequire: 1` is already set; keep it. With
      the refmap AP now genuinely on (see above), a wrong descriptor is a **compile-time** error, not
      just a runtime one — real protection against exactly this class of bug going forward.

**Done this session (2026-09-03, second pass — actual `runClient` launches, 30s smoke each):**
- `:forge:runClient` — crashed with the refmap bug above on the first attempt; after the fix, launches
  clean through the mixin-apply phase and past `Datafixer Bootstrap` (well beyond where the crash was)
  with zero `FATAL`/`ERROR`/exception lines in 30s. `--mixin.config potionsplus.forge.mixins.json` is
  confirmed present in the launch args and `Remapping refMap potionsplus-forge-refmap.json` confirms
  it's being read.
- `:fabric:runClient` — **fully booted to the main menu** on the second (warm-daemon) 30s smoke:
  `Setting user: Player409` → all vanilla+`potionsplus` texture atlases stitched → `Sound engine
  started`, zero `[main/FATAL]`/`MixinApplyError`/`BUILD FAILED` lines anywhere in the 521-line log.
  (Some unrelated `WARN`-level "missing model for variant" lines for a handful of flower/ore
  blockstates — pre-existing content/datagen gaps, not a mixin or Phase 9 issue; out of scope here.)
- `:neoforge:runClient` — two 30s smokes, zero errors both times, though neither got past
  `generateDLIConfig`/Architectury transformer boot within 30s (that task doesn't cache and eats most
  of the window) — inconclusive on reaching mixin-apply, but no regression signal either. Worth one
  more warm-daemon attempt like fabric's second run before treating this as fully confirmed.
- Leftover forked client JVMs from the killed (`timeout 30`) runs were confirmed cleaned up afterward
  (`tasklist` showed only the two Gradle daemons remaining) — see the memory note about killing
  architectury dev-run JVMs before any future `clean`.

**Done this session (2026-09-04): NeoForge and Forge both confirmed reaching the main menu.** Warm
gradle daemon (`./gradlew --status` showed one `IDLE` 9.5.0 daemon before either run), each launched
via `./gradlew :<loader>:runClient` uncapped in the background rather than a timed `timeout` kill:
- `:neoforge:runClient` — `[14:03:30] [Render thread/INFO] [minecraft/SoundEngine]: Sound engine
  started`, 13s after the `runClient` task started (warm daemon + cached assets). Full 130-line log
  checked for `[main/FATAL]`/`MixinApplyError`/`Exception in thread`/`ERROR` — zero hits besides the
  same pre-existing benign "Missing subtitle translation" lines for `potionsplus:*` sound events
  (content gap, not a Phase 9 issue). **This closes the one item this phase's previous session left
  open.**
- `:forge:runClient` — `[14:04:35] [Render thread/INFO] [minecraft/SoundEngine]: Sound engine
  started`. Full 119-line log checked the same way — zero `FATAL`/`MixinApplyError`/`Exception`/`ERROR`
  lines at all (not even the subtitle-translation ones this run). Confirms the previous session's
  "well past mixin-apply, killed before menu" result now goes all the way to menu.
- Both client dev-run JVMs identified precisely via `wmic process get ProcessId,CommandLine` (matched
  on `-Darchitectury.main.class=...` / `--mixin.config potionsplus.forge.mixins.json`) and killed with
  `taskkill /F /PID <pid> /T` immediately after confirming the log; `tasklist` re-checked afterward
  each time — only the Gradle daemon (`java.exe`, ~1.8-2.0 GB) remained, no leaked forked JVMs.

**Still open before closing this phase:**
- Production mixin-config discovery on Forge 52.x (needs an installed packaged jar).
- `BlockEntityType.validBlocks` Forge association — still deferred (see "VERIFIED API FACTS", no
  portable Forge hook found as of Phase 3).
- A full AT/AW survey beyond the 7 members `common` currently needs — expect this list to grow as
  Phase 11 (client, particles) lands and Forge/Fabric start actually touching `TrackingEmitter`.

**Exit criterion:** `./gradlew build` green, and **built jars** (not just dev runs) load on all three
loaders with every mixin applying.

---

## Phase 10 — Datagen sharing

*(= 26.1.2 Phase 7.)* NeoForge datagen stays the source of truth (Decision 5).

- [ ] Root `build.gradle` `commonDatagen` Copy task: `neoforge/src/generated/resources/**` →
      `common/src/generated/resources/`, excluding `.cache/**`, `data/neoforge/**`, `**/neoforge/**`,
      with a `doFirst { delete … }` so removed content doesn't leave orphans. **Copy 26.1.2's task
      verbatim** — the exclusion list is the interesting part.
- [ ] **`common/build.gradle` needs `processResources { duplicatesStrategy = DuplicatesStrategy
      .INCLUDE }`** alongside the `srcDir('src/generated/resources')` it already has, so a
      hand-authored file in `src/main/resources` can override a generated one at the same path.
      26.1.2 has both; 1.21.1 currently has only the `srcDir`. (26.1.2 also keeps a
      `forge/src/generated/resources` dir of its own — the Copy task feeds `common/`, it does not
      make the platform modules generation-free.)
- [ ] Keep the NeoForge-only providers (`GlobalLootModifierProvider`, `DatapackBuiltinEntriesProvider`,
      `SoundDefinitionsProvider`, NeoForge `Block`/`ItemTagsProvider`) in `neoforge/`.
- [ ] **1.21.1 model datagen is the old system** — `BlockStateProvider` + `ItemModelProvider` +
      `ExistingFileHelper` (9 + 6 imports). None of 26.1.2's `BlockModelGenerators`/item-model-
      definition rework applies. `ExistingFileHelper` is NeoForge-only, which is a further reason
      datagen stays NeoForge-side.
- [ ] Verify no `data/neoforge/` or `"type": "neoforge:…"` JSON reaches the Fabric or Forge jars.
      Unzip the built jars and grep — 26.1.2 found a real leak this way.

**Exit criterion:** `./gradlew commonDatagen` then `build`; Fabric and Forge jars contain the shared
models/blockstates/tags/`sounds.json`.

---

## Phase 11 — Client (renderers, particles, tooltips, colors, models, JEI ×3)

*(= 26.1.2 Phase 8.)*

- [x] **Fabric** (`PotionsPlusFabricClient.onInitializeClient`): sprite + emitter particles, item
      color handler, key mapping (`KeyBindingHelper.registerKeyBinding`) done. **1.21.1-era API names
      confirmed by javap against the actual fabric-api jars in the gradle cache** (not just the plan's
      prediction): `net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry` (fabric-particles-v1
      4.0.2, matches the plan) and `net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry`
      (fabric-rendering-v1 5.1.0). **BE renderers, entity renderers/model layers, tooltip component
      factories, and screens NOT done — see the blocker note below the checklist.** (No entity
      renderer/model-layer work needed at all: this 1.21.1 tree has no `Grungler`-equivalent entity —
      that's a 26.1.2-only addition.)
- [x] **Forge** `forge/.../core/forge/Renderers.java`: `RegisterParticleProvidersEvent`,
      `RegisterColorHandlersEvent.Item`, `RegisterKeyMappingsEvent` done, using
      `@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)` +
      `net.minecraftforge.eventbus.api.SubscribeEvent` (**not** the reference tree's
      `net.minecraftforge.eventbus.api.listener.SubscribeEvent` — javap-confirmed this Forge version,
      52.1.2/eventbus 6.x, predates that package move, and `Mod.EventBusSubscriber.Bus` here has only
      `MOD`/`FORGE`, no `BOTH`). `EntityRenderersEvent.{RegisterRenderers,RegisterLayerDefinitions}`
      and `RegisterClientTooltipComponentFactoriesEvent` NOT done — see the blocker note.
- [x] **Forge timing.** Confirmed and followed: `Renderers` registers via FML's own dist-gated
      `@Mod.EventBusSubscriber` scanning (not nested inside `PotionsPlusForge`'s existing
      `FMLClientSetupEvent` listener, which fires too late for these three events).
- [x] **JEI on all three**, with one real blocker found and fixed. `client/integration/jei/*` moved to
      `common/` (decoupled from the still-neoforge-only `BrewingCauldronBlockEntity.CONTAINER_SIZE` by
      inlining the constant as `6` with a comment — the only coupling either recipe category had).
      Coordinates confirmed working at `jei_version = 19.18.10.218`: `mezz.jei:jei-1.21.1-common-api`
      (already wired pre-Phase-11) + `-fabric-api`/`-forge-api` (compileOnly, this phase) and
      `jei-1.21.1-{fabric,forge,neoforge}` (runtime, this phase for fabric/forge). **Forge artifact
      verified to resolve**: `:forge:dependencies --configuration runtimeClasspath` shows
      `mezz.jei:jei-1.21.1-forge:19.18.10.218` present — the plan's flagged uncertainty is resolved,
      it exists and resolves. **Real blocker hit and fixed**: plain `runtimeOnly` for the Fabric JEI
      jar crashed `:fabric:runClient` at `Knot.init` with `AccessWidenerFormatException: Namespace
      (intermediary) does not match current runtime namespace (named)` — the JEI fabric jar is a real
      Fabric mod shipping its own intermediary-namespace AW, and this architectury-loom setup runs
      dev-time in the **named** namespace, so the dependency needs Loom's mod-aware `modRuntimeOnly`
      (which remaps the jar, AW included) instead of plain `runtimeOnly`. Fixed in `fabric/build.gradle`;
      re-ran `:fabric:runClient` clean afterward. Fabric plugin registration wired via
      `fabric.mod.json`'s `jei_mod_plugin` entrypoint + `suggests.jei` (mirrors the reference tree
      exactly); Forge/NeoForge need no manifest entry (`@JeiPlugin` classpath scanning).
- [x] **`Minecraft.getInstance()` is null during Fabric's `onInitializeClient`.** No new code in this
      phase needed the live client instance directly in `onInitializeClient` (particle/color/key-mapping
      registration doesn't touch it), so nothing to defer to `CLIENT_STARTED` here — noted for whoever
      picks up the BE-renderer/tint work later, since `BrewingCauldronWaterTintSource`-equivalent block
      tint registration on Fabric will need this.
- [x] **Block/item colour handlers.** Item color (potion tint, rainbow-cycles for "any potion") is
      DONE and shared: extracted NeoForge's existing inline `registerItemColors` lambda (already
      present in `core/neoforge/Blocks.java` — turns out this exact silent-failure risk had **already
      been avoided on NeoForge itself** pre-Phase-11, just not shared) into
      `common/.../item/tintsource/PotionsPlusItemColors.anyPotionItemColor`, called identically from
      all three loaders' `ItemColor` registration. **Block (cauldron water) tint — DONE as of the
      2026-09-04 (5th session) `BrewingCauldronBlock` port**: `BrewingCauldronBlockEntity` and the
      `Block` class it attaches to are now `common/`, so the same computation is shared via new
      `common/block/tintsource/PotionsPlusBlockColors.cauldronWaterColor` (extracted from NeoForge's
      previously-unshared `core/neoforge/Blocks.java#registerBlockColors` lambda, same pattern as the
      item-tint extraction) and registered on all three loaders. **1.21.1 note**: confirmed this tree
      predates the `ItemTintSource`/`BlockTintSource` codec system the 26.1.2 reference tree uses —
      1.21.1 registers through the classic `ItemColor`/`BlockColor` functional interfaces via
      `RegisterColorHandlersEvent.{Item,Block}` (NeoForge/Forge) and `ColorProviderRegistry.{ITEM,BLOCK}`
      (Fabric), matching the plan's own note, not the reference tree's newer API.
- [ ] **Verify `assets/minecraft/atlases/blocks.json` early** (section D). Not touched this phase — no
      atlas/mip crash was observed on any of the three `runClient` smokes (all three reached "Sound
      engine started" and built the blocks atlas without a `GpuDevice` mip-check crash), so this risk
      did not materialize here. Not independently re-verified beyond that.
- [ ] **Client BE state can hinge on one sync packet that may not arrive.** Not touched — no code in
      this phase's scope reaches the cauldron resync path.
- [ ] **REI / EMI (future note):** not touched this phase.

**BLOCKER discovered this phase (not in the original plan): BE renderers, the cauldron block tint, and
the `ItemStacksTooltip` client tooltip-component factory cannot move to `common` or be ported to
Fabric/Forge yet.** All three depend on classes that were never split out of `neoforge/` in an earlier
phase: the six concrete BlockEntity logic classes (`BrewingCauldronBlockEntity`,
`HerbalistsLecternBlockEntity`, `SanguineAltarBlockEntity`, `AbyssalTroveBlockEntity`,
`ClotheslineBlockEntity`, `PotionBeaconBlockEntity` — all still under
`neoforge/.../blockentity/neoforge/`) and `core/neoforge/items/DynamicIconItems`. Fabric and Forge have
**no block entities behind these six blocks at all** right now (only the `Block`/`BlockEntityType`
registration exists, via the common `core.blocks.BlockEntityBlocks` hub) — so there is nothing yet for
a renderer, a color-tint lookup, or `ClientItemStacksTooltip` (which renders `DynamicIconItems`'
"unknown ingredient" icon) to target on those two loaders. This is a real prerequisite gap, not a
Phase-11 oversight: porting it is BE-logic-class-porting work (register-hub + platform-abstraction
shaped, like Phases 4/5), not client work, and is out of this phase's scope per the task brief. Until
that port happens, NeoForge keeps its existing (working, unshared) BE rendering/tint/tooltip and
Fabric/Forge simply render these six blocks with no BE-driven visuals — not a regression on those two
loaders (they never had it), but it does mean Phase 11's original exit criterion ("all three clients
render every BE... correctly") cannot be met until that porting work lands.

**2026-09-04 correction — the blocker is deeper than "port 6 BE classes + `DynamicIconItems`".** A
session dedicated to unblocking this read every one of the 6 concrete BE classes plus their direct
neoforge-side dependencies (not just the plan's summary of them) and found the fan-out is Phase-4/5/8
sized, not a quick follow-on. Evidence, class by class:

- **`core.blocks.BlockEntityBlocks` (the common hub the previous note said "already exists") only
  holds `Holder<Block>` fields** (`grep` confirms: `BREWING_CAULDRON`, `HERBALISTS_LECTERN`,
  `SANGUINE_ALTAR`, `ABYSSAL_TROVE`, `PRECISION_DISPENSER`, `CLOTHESLINE`, `POTION_BEACON` — all
  `Holder<Block>`). **There is no common (or fabric/forge) `BlockEntityType` hub at all** — every one
  of the 6 `BlockEntityType.Builder.of(XBlockEntity::new, ...).build(null)` calls, and the
  `DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES` that flushes them, live only in
  `neoforge/.../core/neoforge/Blocks.java` (confirmed: `grep BLOCK_ENTITY fabric/.../core/fabric/Blocks.java
  forge/.../core/forge/Blocks.java` → zero matches in both). So even after moving the 6 BE classes
  themselves to `common/`, Fabric and Forge still have no `BlockEntityType` to construct them with —
  this hub has to be built from scratch on both loaders (register-hub work, same shape as Phase 4).
- **`core.neoforge.items.DynamicIconItems`** (needed by `AbyssalTroveBlockEntity`,
  `BrewingCauldronBlockEntity`, `HerbalistsLecternBlockEntity`) sits on top of an **8-file,
  fully neoforge-only item-registration DSL** that was never split either:
  `utility/registration/item/neoforge/{ItemBuilder,SimpleItemBuilder,GenericIconItemBuilder,
  ItemModelUtility,ItemOverrideUtility}.java` +
  `utility/registration/neoforge/{RegistrationUtility,AbstractRegistererBuilder,IModelGenerator}.java`.
  Porting `DynamicIconItems` means porting or re-abstracting this whole DSL first — its own register-hub
  project, comparable in size to the Phase 4 item hubs.
- **`core.neoforge.RecipesRegistrar`** (needed by `AbyssalTroveBlockEntity`, `HerbalistsLecternBlockEntity`,
  indirectly `BrewingCauldronBlockEntity` via `Recipes`) owns both a `DeferredRegister`-based recipe-type
  hub *and* runtime recipe injection — the injection half is already flagged in the Phase 5 progress log
  as blocked on Phase 9's access-widened `RecipeManager.byType`/`byName` fields, which Phase 9 has not
  landed yet (still 🟡, "Capabilities/AT survey beyond the small set needed today" left open).
- **`block.neoforge.ClotheslineBlock` and `block.neoforge.PotionBeaconBlock`** — the `Block` classes
  themselves (not just their BEs) are still neoforge-only; both were never split off in Phase 1/4. Their
  only NeoForge-specific coupling is `core.neoforge.Blocks.X_BLOCK_ENTITY.get()/.value()` lookups (same
  fix as the BE classes once the BlockEntityType hub exists above), so they are portable *after* that
  hub exists, but are one more file each to move + re-verify (`getTicker`, `newBlockEntity`,
  `getShape`/`useItemOn` all reference the neoforge BE-type holder directly).
- **`persistence.neoforge.PlayerBrewingKnowledgeNetworking`** (needed by `BrewingCauldronBlockEntity`)
  is a thin wrapper (2 methods) but its only neoforge coupling is sending a still-neoforge-only packet,
  `network.neoforge.ClientboundAcquiredBrewingRecipeKnowledgePacket` — itself trivially portable (already
  uses the common `PacketContext`/`PacketNetwork` abstraction from Phase 2, exactly like the 6 packets
  Phase 5 already moved) but not yet done.
- **`network.neoforge.ClientboundSanguineAltarConversionProgressPacket` /
  `ClientboundSanguineAltarConversionStatePacket`** (needed by `SanguineAltarBlockEntity`) — same
  situation, 2 more Phase-5-shaped packet moves not yet done (these are 2 of the 6 payloads Phase 5's
  progress log already named as remaining neoforge-only, for the same reason: `core.neoforge.Blocks`
  coupling, now understood to really be the missing-`BlockEntityType`-hub issue above, not a
  packet-specific blocker).
- Only `PotionBeaconBlockEntity` and `ClotheslineBlockEntity` are *relatively* close — their sole direct
  blockers are the missing `BlockEntityType` hub and their own still-neoforge `Block` class (both listed
  above); neither touches `DynamicIconItems` or `RecipesRegistrar`. **`HerbalistsLecternSounds` had zero
  remaining coupling once read in full (only used already-common `core.Sounds`/`utility.Utility`) and was
  moved to `common/blockentity/HerbalistsLecternSounds.java` this session** — `:common:compileJava
  :neoforge:compileJava :fabric:compileJava :forge:compileJava` green afterward, Decision 4a `comm -12`
  re-run empty on all three platform modules. This is the only code change made this session; the rest of
  the blocker was left alone per the task's own guidance not to force a deep dependency through under
  time pressure.

**Recommendation for whoever picks this up next** (updated 2026-09-04, Phase 11a steps 1-3 now
**closed**, see the 2026-09-04-c progress-log entry): step 3 turned out to be a re-abstraction (a
stub hub matching the `BrewingItems`/`OreItems` pattern), not a literal port — NeoForge's item-model
datagen DSL itself stays neoforge-only (Decision 5), only the runtime `Holder`s + icon-lookup helpers
moved to `common/core/items/DynamicIconItems.java`. ~~(1) build the common `BlockEntityType` hub +
fabric/forge registration~~ **done**. ~~(2) move the 3 remaining packets~~ **partially done** (as
before — `ClientboundAcquiredBrewingRecipeKnowledgePacket` moved; the 2 Sanguine Altar packets still
need either `SanguineAltarBlockEntity` ported or a new common interface). ~~(3) decide whether to port
or re-abstract the `DynamicIconItems` item-builder DSL~~ **done** — re-abstracted; `DynamicIconItems`
is no longer a blocker for any of the 3 named block entities. **What step 3 revealed**: each of the 3
block entities has its own *second* neoforge-only dependency, unrelated to `DynamicIconItems` —
`AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` both import `core.neoforge.RecipesRegistrar`
(that's step 4, already known), but `BrewingCauldronBlockEntity` imports
`persistence.neoforge.PlayerBrewingKnowledgeNetworking` (calls
`tryAddKnownRecipeServer(...)`, neoforge-only packet-send plumbing) — **newly identified this
session, not part of the original 4-step order**, needs its own scoping before
`BrewingCauldronBlockEntity` can move regardless of step 4. (4) revisit `RecipesRegistrar`'s
runtime-injection half only after Phase 9's access widener lands — this now fully gates
`AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity`. A 5th item is now needed too: resolve
`PlayerBrewingKnowledgeNetworking` for `BrewingCauldronBlockEntity`. Only after (4)+(5) does the rest
of the original Phase 11 client work (remaining 4 BE renderers, block tint, tooltip factory) described
in the task brief become unblocked — and separately, the tooltip-component factory
(`ClientItemStacksTooltip`) has its own remaining blocker even once its BE dependents move: NeoForge's
client tooltip-component-factory extension point (`ClientTooltipComponentFactoriesListeners` +
`ItemMixin`) has no vanilla/Fabric-API equivalent and needs its own design, not scoped yet.

**Update 2026-09-04 (later session): step 4 (`RecipesRegistrar`) is done — `RecipesRegistrar` moved to
`common/` — but this does NOT unblock `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` moving,
for a reason distinct from anything above.** Their `Block` classes (`block.neoforge.AbyssalTroveBlock`,
`block.neoforge.HerbalistsLecternBlock`) are themselves still neoforge-only (same class of issue as the
`ClotheslineBlock`/`PotionBeaconBlock` entry above, not previously called out for these two), **and**
unlike Clothesline/PotionBeacon, Fabric and Forge have never registered the underlying
`herbalists_lectern`/`abyssal_trove` *blocks* at all — `core.{fabric,forge}.blocks.BlockEntityBlocks`
only carry `PRECISION_DISPENSER`/`CLOTHESLINE`/`POTION_BEACON`/`BREWING_CAULDRON`. So moving these two
block entities now needs: (a) port `AbyssalTroveBlock`/`HerbalistsLecternBlock` to `common/` (both read
clean on inspection — no neoforge-only API beyond `core.neoforge.Blocks` holder lookups, same
`.get()`→`.value()` fix as every other ported Block), (b) register both blocks on fabric/forge for the
first time (new `BlockEntityBlocks` entries, same `registerBlock.apply(...)` + `Items
.registerBlockItemWithAutoModel` pattern already used for the other 4), (c) then the `BlockEntityType`
registration + renderer registration this task's brief described. None of (a)-(c) attempted this
session — real, verified progress on the `RecipesRegistrar` unblock was prioritized within this
session's budget over forcing a 3-part block-registration project through at the end of it. See the
2026-09-04 (later) progress-log entry for the `RecipesRegistrar` writeup and verification.

**Exit criterion:** **not fully met** — see blocker above. What ships: all three clients render every
particle correctly (10/10 particle classes ported + registered on all three, smoke-verified); item
tint (potion rainbow) is correct and shared on all three; key mappings work on all three; JEI shows the
brewing-cauldron and clothesline categories' recipe types on all three (categories register and JEI's
own atlas builds cleanly on every loader in the smoke logs) — not manually verified in-game via the JEI
GUI itself (would need a world join, out of this phase's smoke-test budget). **Updated 2026-09-04
(Phase 11a steps 1-2)**: Clothesline and PotionBeacon BE rendering is now real and registered on all
three loaders (not just NeoForge) — boot-to-menu-plus-JEI-atlas smoke verified on all three, but actual
in-world block placement/render-firing was not verified (no GUI-automation tool available in this
environment to join a world and place a block). **Updated 2026-09-04 (Phase 11a step 3)**:
`DynamicIconItems` is common and registers real items with real icon-lookup helpers on all three
loaders (boot-to-menu smoke verified on all three, zero new exceptions, only the identical
pre-existing `generic_icon` missing-particle-texture warning present on all three including NeoForge).
The 4 remaining BE renderers, the block (cauldron water) tint, and the tooltip component factory
remain neoforge-only — now gated on Phase 9 (`RecipesRegistrar`) plus the newly-found
`PlayerBrewingKnowledgeNetworking` dependency plus (for the tooltip factory specifically) NeoForge's
tooltip-component-factory extension point, not on `DynamicIconItems` any more. **Updated 2026-09-04
(4th session, "step 5")**: the `PlayerBrewingKnowledgeNetworking` dependency is resolved —
`BrewingCauldronBlockEntity` and `BrewingCauldronBlockEntityRenderer` are now `common/`. This does
**not** move the needle on Fabric/Forge rendering though: `block.neoforge.BrewingCauldronBlock` (the
`Block` class the BE/renderer actually attach to) was never split off neoforge, so neither loader has
a cauldron `Block` or `BlockEntityType` registered at all — confirmed via `grep BREWING_CAULDRON`
across both loaders' `core.{fabric,forge}.blocks`, zero hits. Only NeoForge benefits (its existing,
unchanged registration/tint code now resolves the moved classes automatically) — real `:neoforge:runClient`
smoke confirmed zero new exceptions, boot-to-menu reached, but as with every prior entry, no
GUI-automation tool exists in this environment to actually place a cauldron and watch it render/tint
in a loaded world, so that remains unverified rather than assumed. `AbyssalTroveBlockEntity`/
`HerbalistsLecternBlockEntity` (step 4) still fully blocked — Phase 9's access widener confirmed not
landed cross-loader this session (`RecipeManager.byType`/`byName` present only in `neoforge/`'s own
local `accesstransformer.cfg`, absent from `common/potionsplus.accesswidener` and
`forge/accesstransformer.cfg`). **Updated 2026-09-04 (5th session) — the `BrewingCauldronBlock` gap
from the previous entry is now closed.** The `Block` class moved to `common/block/BrewingCauldronBlock.java`
and is registered as a real block + `BlockEntityType` on **all three loaders**
(`core.{fabric,forge}.blocks.BlockEntityBlocks` + `core.{fabric,forge}.Blocks`, mirroring the
Clothesline/PotionBeacon pattern exactly); `BrewingCauldronBlockEntityRenderer` is registered on all
three (Fabric: `BlockEntityRendererRegistry`; Forge: `EntityRenderersEvent.RegisterRenderers` on the
existing dist-gated `core/forge/Renderers.java`); the cauldron water tint is now shared (new
`common/block/tintsource/PotionsPlusBlockColors.cauldronWaterColor`) and registered on Fabric
(`ColorProviderRegistry.BLOCK`) and Forge (`RegisterColorHandlersEvent.Block`, same subscriber class —
avoids the `FMLClientSetupEvent`-too-late timing trap). All API signatures (`RegisterColorHandlersEvent
.Block#register`, `BlockColor#getColor`, `ColorProviderRegistry.BLOCK`) verified by `javap` against the
actual jars before writing the code. `:common:compileJava :neoforge:compileJava :fabric:compileJava
:forge:compileJava` → BUILD SUCCESSFUL; Decision 4a `comm -12` empty on all three; real `runClient`
smoke on all three loaders reaches `Sound engine started` with zero new exceptions (grepped each log
for `cauldron`/`brewing_cauldron` — nothing, consistent with silent successful registration). **Not
verified**: actually placing a cauldron in a loaded world and watching it render/tint/tick — still no
GUI-automation tool in this environment, same caveat as every prior entry. `AbyssalTroveBlockEntity`/
`HerbalistsLecternBlockEntity`/`SanguineAltarBlockEntity` and the tooltip-component factory are now the
only remaining Phase 11 gaps, both still gated on prerequisites outside this task's scope (Phase 9's
access widener; NeoForge's own tooltip-component-factory extension point, no vanilla/Fabric
equivalent).

**Update 2026-09-04 (7th session): `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` are done —
5 of 6 block entities (all but `SanguineAltarBlockEntity`) now render/tint/register identically on all
three loaders.** `AbyssalTroveBlock`/`HerbalistsLecternBlock` (the `Block` classes) moved to
`common/block/`; both BE classes + both renderers moved to `common/blockentity/`; both underlying
blocks + `BlockEntityType`s registered on Fabric/Forge for the first time
(`core.{fabric,forge}.blocks.BlockEntityBlocks`, `core.{fabric,forge}.Blocks`); both renderers
registered on all three (`BlockEntityRendererRegistry` on Fabric, the existing dist-gated
`core/forge/Renderers.java` subscriber on Forge). `common/core/Blocks.java` now has 5 of 6
`BlockEntityType` holders concretely typed — only `SANGUINE_ALTAR_BLOCK_ENTITY` stays
`Holder<BlockEntityType<?>>`. Compile green on all 4 modules; Decision 4a `comm -12` empty; real
`runClient` smoke clean on all three loaders (zero new exceptions beyond the pre-existing missing-
subtitle-translation noise). **`SanguineAltarBlockEntity` is now the *only* remaining neoforge-only
block entity** — confirmed blocker: it imports 2 still-neoforge-only packets
(`network.neoforge.ClientboundSanguineAltarConversionProgressPacket`/
`ClientboundSanguineAltarConversionStatePacket`), a Phase-5-shaped packet move, not attempted this
session (out of this task's scope, which was specifically Abyssal Trove + Herbalist's Lectern). **The
tooltip-component factory (`ClientItemStacksTooltip`) blocker is unchanged**: NeoForge's own
client-tooltip-component-factory extension point (`ClientTooltipComponentFactoriesListeners` +
`ItemMixin`) still has no vanilla/Fabric-API equivalent, and still needs its own design — not attempted.
**Phase 11's original exit criterion ("all three clients render every BE correctly") is now met for 5
of 6 block entities**; the two remaining named gaps (`SanguineAltarBlockEntity`, the tooltip-component
factory) are the full, precise remainder — not a re-estimate, an exhaustive list. `BrewingTooltips` (the ingredient-tooltip logic distinct from `ClientItemStacksTooltip`), flagged here
as newly-unblocked-but-not-wired, was moved to `common/item/tooltip/BrewingTooltips.java` and wired
into Fabric's and Forge's `TooltipListeners` in the 2026-09-04 8th session — see that session's
progress-log entry. See the 2026-09-04 (7th session) progress-log entry for the original block-entity
evidence.

**Updated 2026-09-04 (8th session): `SanguineAltarBlockEntity` is done, closing Phase 11's
block-entity exit criterion for all 6 of 6.** Its blocker was read fresh rather than trusted from the
prior note: both sync packets (`ClientboundSanguineAltarConversionProgressPacket`/`...StatePacket`)
and the BE class itself had exactly one neoforge coupling each — `core.neoforge.Blocks` — the same
shape every prior BE unblock in this chain resolved. Referencing the concrete `SanguineAltarBlockEntity`
class (its `State` enum, its public fields) directly from the packets is fine once that class is
`common/` too, confirmed against the finished `dev/26.1.2` reference tree, which does exactly this
(`common/network/ClientboundSanguineAltarConversion{Progress,State}Packet.java` there import the BE
class directly, no abstraction layer). `SanguineAltarBlock`, `SanguineAltarBlockEntity`,
`SanguineAltarBlockEntityRenderer`, and both packets moved `neoforge/` → `common/`;
`common/core/Blocks.java#SANGUINE_ALTAR_BLOCK_ENTITY` is now concretely typed (was the last
`Holder<BlockEntityType<?>>` of the six); block + `BlockEntityType` + renderer registered on Fabric and
Forge for the first time, exact template of the other 5; both packets registered in
`core.{fabric,forge}.Packets.java` (9 of 12 payloads now shared). `BrewingTooltips` also moved to
`common/item/tooltip/` and wired into Fabric's/Forge's `TooltipListeners` this session (see the Phase 7
checklist and progress-log entry) — unrelated to the block-entity work but the task's second half.
**Exit criterion now met**: all three clients render/register all 6 block entities identically (blocks,
`BlockEntityType`s, renderers all shared and registered on every loader). Compile green on all 4
modules, Decision 4a `comm -12` empty. Real `:neoforge:runClient` smoke reached `Sound engine started`
with zero new exceptions beyond the pre-existing missing-subtitle-translation noise. **Fabric and Forge
`runClient` smokes closed out the same session** (immediately following, by the coordinator rather than
the subagent that flagged the gap): both `:fabric:runClient` and `:forge:runClient` reached `Sound engine
started` cleanly — Fabric at zero new exceptions/errors, Forge with only a pre-existing harmless
`log4j.xml` URI-parsing `WARN` (config quirk, not a mod issue) — confirming the SanguineAltar +
BrewingTooltips changes boot clean on all three loaders, not just NeoForge. Both client JVM trees killed
cleanly afterward, zero orphans confirmed via `wmic`. The
**sole remaining Phase 11 item is the `ItemStacksTooltip` tooltip-component-factory extension point**
(`ClientTooltipComponentFactoriesListeners` + `ItemMixin`), still genuinely NeoForge-only — no
vanilla/Fabric-API equivalent exists, and it needs its own design, not a port.

---

## Phase 12 — Tests (unit + game tests, three loaders)

*(= 26.1.2 Phase 9, **plus** repairing what Phase 0 knowingly broke.)*

- [ ] **Restore JUnit — and the answer is already known, so do this first (30 minutes, not a phase).**
      *Looked up 2026-09-01.* 26.1.2 needs **no loom test configuration whatsoever**. Its
      `common/build.gradle` contains only:

      ```gradle
      dependencies {
          testImplementation 'org.junit.jupiter:junit-jupiter:5.9.3'
          testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
      }
      tasks.withType(Test).configureEach { useJUnitPlatform() }
      ```

      with the comment *"Minecraft itself is on the test classpath via Loom, so tests that need
      registries (potions, mob effects) bootstrap it themselves — see `AlchemyTestBase`."* There is no
      moddev-`unitTest`-equivalent to replace, because none is needed.

      **The likely whole cause of the current red is misplacement, not a missing loom feature:**
      Phase 1 moved the tests to `common/src/test`, but the junit dependencies and
      `useJUnitPlatform()` are still in **`neoforge/build.gradle`**, and `common/build.gradle` has
      **no test wiring at all**. Move that block into `common/build.gradle` and run `:common:test`
      before assuming anything deeper is wrong. *(Not yet attempted — flagged, not verified.)*
- [ ] The two trees' unit-test files match 1:1 (12 files, `AlchemyTestBase` … `PotionDataTest`,
      including `PotionContentsAccessTest`), so no test authoring is needed here — only wiring.
- [ ] `PotionContentsAccessTest` must run and pass. It is the guard that keeps the alchemy layer
      from unravelling (`CLAUDE.md`; backport plan line 319) and it is worth more than the rest of
      this phase combined.
- [ ] NeoForge game tests: keep the existing `testmod` sourceSet, translated to loom's
      `loom.mods`/`gametest` run form.
- [ ] Fabric: `fabric-gametest-api-v1`; `fabric/src/testmod` with its **own** `fabric.mod.json`
      declaring a separate `potionsplus_testmod` mod with a `fabric-gametest` entrypoint.
- [ ] **`loom.mods` is global, not per-run.** Do not associate the `testmod` source set with the mod
      to make gametests work — it puts testmod's split dirs on `runClient`'s and `runData`'s classpath
      too and crashes FML mod discovery with `Invalid module name: '' is not a Java identifier`.
      26.1.2 spent three commits on this before landing the right shape: **redirect
      `compileTestmodJava` into main's classes dir** for gametest invocations, with an explicit
      `dependsOn(processResources)` (two tasks now write there) and `jar { exclude '**/gametest/**' }`
      so merged classes can't reach the distributable jar.
- [ ] Forge: `net.minecraftforge.gametest` — verify what 52.1.2 actually ships. **26.1.2's Forge
      registered 0 of its 31 tests** and ultimately needed a bespoke `RegistryLoadTaskMixin`, because
      `Registries.TEST_INSTANCE` is a dynamic per-world datapack registry `RegisterEvent` never fires
      for, and Forge ships no equivalent of NeoForge's `RegistryDataLoader` patch. **1.21.1 predates
      TEST_INSTANCE** — tests are annotation-scanned through the older `GameTestRegistry` path, so
      `@GameTest` + `@GameTestHolder` should just work. **Verify this in the first hour of this
      phase**: if it holds, this plan sidesteps the single hardest problem the 26.1.2 branch hit.
- [ ] Watch for bugs *in the tests*. 26.1.2's `963a909` found `spawnItem(Item, BlockPos)` already
      converts a structure-relative position (an extra `helper.absolutePos()` spawned the item
      millions of blocks away) and that `CropBlock.randomTick` silently no-ops below raw brightness 9.
- [ ] Port the platform test harness (`TestPlayers.makeMockCreativePlayerInLevel`) per loader.
      26.1.2's verified testmod inventory, including its **package convention — `<loader>` is an
      infix here (`grill24.potionsplus.<loader>.gametest`), not the `core.<loader>` suffix used in
      main source**; either way no package is shared, per Decision 4a:
      `common/src/testmod` → `gametest/{AlchemyGameTests, BrewingCauldronGameTests, EffectGameTests,
      RecipeSyncGameTests}`; `fabric/src/testmod` → `fabric/gametest/PotionsPlusFabricGameTests` +
      its own `fabric.mod.json`; `forge/src/testmod` → `forge/gametest/{ForgeGameTestRegistration,
      ForgeTestPlayers, PotionsPlusForgeGameTests}`; `neoforge/src/testmod` →
      `neoforge/gametest/{NeoForgeGameTestRegistration, NeoForgeTestPlayers}`.
      **1.21.1 has only two of the four shared files** (`AlchemyGameTests`,
      `BrewingCauldronGameTests`) — `EffectGameTests` and `RecipeSyncGameTests` are 26.1.2 additions,
      and `RecipeSyncGameTests` covers the runtime-recipe sync packet from Phase 5.
- [ ] Target: the same 34 shared alchemy + brewing-cauldron tests passing on NeoForge and Fabric.

**Exit criterion:** `:common:test` green; NeoForge and Fabric game-test runs pass; Forge status
recorded honestly whatever it turns out to be.

---

## Phase 13 — Verification

- [ ] `./gradlew build` — all four modules compile, `common` JUnit green.
- [ ] `./gradlew :neoforge:runClient` — regression check against pre-refactor behaviour.
- [ ] `./gradlew :fabric:runClient` and `:forge:runClient` — load a world; verify blocks, items,
      potions, effects, BEs, particles, entities, the clothesline inventory, loot modifiers and any
      surviving worldgen.
- [ ] Game-test runs on every loader that supports them.
- [ ] `./gradlew :neoforge:runData && ./gradlew commonDatagen` — confirm propagation into the
      Fabric/Forge jars.
- [ ] **Production-jar smoke test, not just dev runs.** 26.1.2 shipped a production-only mixin-config
      bug (commit `243ac95`) that every dev run missed. Install the three built jars into real
      profiles — the `deploy-dev-builds` skill covers this — and launch each.
- [ ] Manual smoke: brew a potion, drink it (config drink-time respected), trigger a loot-modifier
      drop, exercise the clothesline, confirm `LastPotionUsePlayerData` survives a respawn on all three.

---

## Risks / verify-at-implementation

- **The build-system swap is the riskiest single step** and it is first. `loom-no-remap` (26.1.2) and
  `dev.architectury.loom` (here) behave differently in exactly the places that hurt: mappings,
  refmaps, and the `remapJar` step. Phase 0 deliberately changes *only* the build system so a failure
  there is unambiguous.
- **Unit tests go red in Phase 0 by design** and stay red until Phase 12. Don't "fix" it by deleting
  tests. *Downgraded 2026-09-01:* this was recorded as "`unitTest.enable()` has no loom equivalent",
  implying something had to be rebuilt. 26.1.2 shows otherwise — junit deps + `useJUnitPlatform()` in
  `common/build.gradle` is the whole of it. Likely a wiring-location bug, not a missing capability.
- **Refmaps fail silently in production and work in dev.** Every mixin-touching phase must verify a
  *built jar*, not a dev run.
- **AW vs AT split** — `common` wants an access widener, Forge only reads access transformers. Two
  files must be kept in sync by hand; drift shows up as a `NoSuchMethodError` on one loader only.
- ~~**`DataAttachments` has no off-NeoForge equivalent**~~ — **retired 2026-09-01.** 26.1.2 has no
  `AttachmentType` usage at all; it uses vanilla `SavedData`, which 1.21.1 already has in `common/`.
  Phase 8 deletes `DataAttachments` rather than abstracting it, so this stopped being a risk.
- **Forge capability API on 1.21.1 is the *pre*-1.20.5 shape**, not the one 26.1.2 ported. Do not
  copy 26.1.2's `Capabilities` code without checking it against the 52.1.2 jar.
- **JEI's Forge 1.21.1 artifact** is assumed from JEI's publishing convention, not verified against a
  jar. If it doesn't resolve, Decision 3 degrades to the 26.1.2 outcome (NeoForge + Fabric only) and
  Forge waits for REI/EMI.
- **Fabric immediate registration** — order is load-bearing, and `fabric-registry-sync-v0` builds
  collision-shape caches inside `MappedRegistry.register()`. Expect NPEs in block constructors that
  reference other blocks' holders.
- **Backport Phase 6 must land first.** Starting Phase 0 on an unverified backport means debugging two
  unrelated classes of breakage at once.
- **"It builds" and "it reaches the main menu" catch almost nothing.** On 26.1.2 those were every
  phase's exit criterion and they missed all eleven post-phase bugs. The three things that actually
  found bugs were **running game tests, creating a new world, and installing packaged jars**. Where a
  phase below says "verify", it means one of those — not a green build.
- **Production ≠ dev, on Forge especially.** Two 26.1.2 bugs (`243ac95`, `00c39e0`) were invisible in
  every dev run and only appeared in packaged installs; one of them shipped. Mixin discovery in
  particular takes a completely different path in production on Forge.
- **Beware confident root causes.** Three 26.1.2 commits exist solely to correct a wrong diagnosis
  recorded in its own plan document. When updating this doc mid-implementation, record the evidence
  (log line, decompiled method, crash report path), not the theory.

---

## Progress log

| Date | Phase | Notes |
|---|---|---|
| 2026-09-01 | — | Plan written. Surveyed `mc/1.21.1` @ `b4fc36b` (294 `.java` files, 118 with `net.neoforged` imports, 36 `@EventBusSubscriber` classes, 18 mixins, no `common/`, ModDevGradle). Verified the Forge 52.1.2 + vanilla 1.21.1 API facts above via `javap` against the loom-cached jars. Decisions 1–6 confirmed with user. Backport status confirmed: Phases 1–5 landed, Phase 6 outstanding. |
| 2026-09-01 | — | Added the **Implementation history** section after reviewing all 22 commits on `dev/26.1.2/multi-loader-expansion` plus its two handoff docs. Folded ~25 gotchas into the phases, split into apply-directly / inverts-on-1.21.1 / verify-against-52.1.2. **Corrected one direct contradiction:** Phase 6 previously prescribed a second `@Mod` client class, which Forge silently dedups away — 26.1.2 deleted theirs. Confirmed three 26.1.2 bugs are already live in this tree (`DelayedEvents` `ArrayList`, `SoulMateEffect` self-redirect, `atlases/blocks.json`) and added pre-flight phase **P1** for them. |
| 2026-09-01 | P0, P1 | **P0 verified.** `:neoforge:build`, `:neoforge:test` (unit), `:neoforge:runData`, `:neoforge:runGametest` (33/33 required tests) all green. Bumped `mod_version` 1.5.8 → 1.6.0. **P1 fixed** all three: `DelayedEvents` → `CopyOnWriteArrayList`; `SoulMateEffect`'s two redirect loops now skip the entity's own id; `atlases/blocks.json` rewritten as 22 scoped `single` sources instead of the two namespace-leaking `directory` sources (26.1.2's `items.json` fix does not apply — that assumes MC 1.21.4+'s split item atlas, which 1.21.1 does not have; tried it, confirmed it does nothing here, reverted). **Bonus find during game-test verification:** `data/potionsplus/loot_table/blocks/lunar_berry_bush.json` used JSON boolean literals (`"blooming": false`) for a `block_state_property` condition instead of the required string form (`"blooming": "false"`) — silently failed to parse on every server start (`LootDataType` ERROR in the `runGametest` log), meaning the bush never dropped loot. Fixed; re-ran game tests to confirm the parse error is gone and all 33 still pass. Starting Phase 0 next. |
| 2026-09-01 | 0 | **Phase 0 done, after a real multi-hour blocker worth recording in full.**
`:neoforge:build`/`:neoforge:help` failed at project-configuration time with `IllegalAccessException:
Can not set final java.util.Map field ...VersionsManifest.latest` (Gson 2.9.1, deep inside
Architectury Loom 1.17.491's Mojang-manifest parsing), on **every** attempt, before any real work
started. `apt-ores-worktrees/mc-1.21.1` — the reference build, same Loom/Gson/neoforge-version
combo — never hit it. Things tried and **ruled out** (each verified, not assumed): JDK 16/17/21/25,
including via a genuinely-confirmed-effective override (`./gradlew --version` showing the actual
daemon JVM); `--add-opens`; forcing Gson 2.14.0 via a root `buildscript{}` classpath block; matching
apt-ores' exact `plugins{}` set (its `net.darkhax.curseforgegradle` does resolve Gson to 2.14.0 per
`buildEnvironment`, but adding it to our own `plugins{}` block changed nothing — Loom evidently
shades its own Gson, immune to classpath-level version forcing); clearing a stale
`ACQUIRED_PREVIOUS_OWNER_DISOWNED` loom-cache lock (real, but a red herring — the crash reproduced
identically with or without it); deleting all project-local `.gradle`/`build` dirs; even renaming
away apt-ores' own project-local loom cache (it still built cleanly, real work and all — refuting the
entire "warm cache" theory outright); stripping `neoforge/build.gradle` down to near-byte-identical
with apt-ores'; adding an empty stub `common/` module. **Actual root cause**, found by diffing
`settings.gradle` line-by-line against apt-ores': the old moddev-era `settings.gradle` carried
`plugins { id 'org.gradle.toolchains.foojay-resolver-convention' version '0.7.0' }` forward
unnecessarily — apt-ores' `settings.gradle` has no such plugin. Removing it fixed the build outright;
the very next run did real first-time work (renaming, AT, remapping) and succeeded. Separately,
while chasing JDK theories, found and fixed a real, independent, durable-value bug: the user's
*global* `~/.gradle/gradle.properties` pins `org.gradle.java.home` to a JDK 25 install (for the MC
26.1.2 branches), and a *project-level* `gradle.properties` `org.gradle.java.home` does **not**
override that (confirmed: daemon JVM selection happens before project properties are read). Added
`gradle/gradle-daemon-jvm.properties` (`toolchainVersion=21`, tracked in git) as the correct,
per-project mechanism — every sibling MC 1.21.1-family project should get one, and MC 26+ projects
should get their own with `toolchainVersion=25`, so neither family depends on whichever JDK happens
to be the machine's global default. **Lesson for future phases:** when carrying files forward from
an old build system "unchanged", verify line-by-line against the reference build rather than
assuming — one stray plugin line cost most of a session. Also found and fixed a regression in this
same session's own P1 atlas fix: scoping `mob_effect/` in `blocks.json` to only `potionsplus:`
sources broke `potion_effect_icon` (a generic item that needs *every* vanilla effect's icon too) —
`runClient` caught it immediately via `Missing textures` warnings; `mob_effect/` needed to stay a
namespace-wide `directory` source, only `particle/` was actually dead weight. |
| 2026-09-01 | 1 | **Phase 1 repackaging done — split-package fix pulled forward and applied.** The launch
blocker (14 split packages, 107 files) is gone: all 107 files were `git mv`'d into `.neoforge`
sub-packages, `package` decls + every cross-module import rewritten, and
`:common:compileJava :neoforge:compileJava` is **BUILD SUCCESSFUL**. Full method in the Phase 1
section ("The fix — APPLIED in Phase 1"). Compile-iterate history for the record: first pass fixed
the ~28 same-package import gaps; a **fresh** recompile surfaced ~15 more that the earlier 124-line
error list had masked (trust fresh compiles, not cached error files); three sub-passes of rewrites
later it went green. **Two traps documented in the Phase 1 section:** the `core.neoforge`
same-package `PotionsPlus` shadowing (fixed with `import grill24.potionsplus.core.PotionsPlus;`
in 4 files) and the `LangProvider` vanilla-`Items` collision. |
| 2026-09-01 | 1 | **Phase 1 CLOSED — all three exit-criterion verifications done, work committed.** **(1)
Decision 4a:** `comm -12` re-run on the `common/`↔`neoforge/` package dirs came back **empty** (0
shared packages). **(2) Clean full build:** killed two abandoned architectury dev-run JVMs that held
`clean`'s file locks (`cwd` set to `neoforge/build/gametest`), then `./gradlew clean
:common:build :neoforge:build -x test` → **BUILD SUCCESSFUL**. `:neoforge:test` stays known-red
until Phase 12 (junit wiring + test location, tracked since Phase 0). **(3) `:neoforge:runClient`:**
reached the main menu — mod loading complete (no `ResolutionException` / ClassNotFound / mixin
errors), Sound engine started, JEI initialized (1864 ingredients), PotionsPlus networking live. Only
cosmetic `Missing subtitle translation` warnings (pre-existing i18n gap, not launch-relevant). **Post
commit:** the Phase 1 changeset (107 `.neoforge` renames + cross-module import fixes + this doc) is
committed together on branch `dev/1.21.1/multi-loader-expansion`. Mirror discipline still in force
for Phase 2+: diff against the actual 26.1.2 tree before concluding anything. |
| 2026-09-01 | 2 | **Phase 2 done — platform abstraction layer + networking wired, build verified.** `common/.../platform/{Platform,PacketNetwork}.java` (7 + 5 `@ExpectPlatform` methods, mirrored from 26.1.2 incl. the `CustomPacketPayload[] rest` form), `common/.../network/PacketContext.java` (3 methods — **corrected plan prose**: the 26.1.2 tree has no `isServerSide`), `neoforge/.../platform/neoforge/{PlatformImpl,PacketNetworkImpl}` + `network/neoforge/NeoPacketContext`. All 12 `network/*Packet` handlers rewritten against `PacketContext`; registrations in `core/neoforge/Packets.java` wrapped `(pkt, ctx) -> Handler.handleDataOnMain(pkt, new NeoPacketContext(ctx))`. Because Decision 4a must stay empty once `common/.../network/PacketContext` exists, the 12 neoforge packets were `git mv`'d into `.network.neoforge` (they stay loader-side until Phases 4/5/11 resolve their neoforge-only deps) and 10 referencers re-imported. Exercised the abstraction: 9 senders now call `PacketNetwork.sendTo*`; 4 call sites refactored onto `Platform` (`TeleportationEffect` chorus-fruit target, `PotionItemMixin` drink time/cooldown, `InventoryMixin` + `ServerPlayerUtility` held-item-changed). **Two 1.21.1 divergences from 26.1.2** recorded in `PlatformImpl`/`PacketNetworkImpl` comments: no `onItemConsumptionTeleport` on 21.1.209 → chorus-fruit hook; no `ClientPacketDistributor` → static `PacketDistributor.sendToServer`. **First build caught one real error** (`PlayerListeners.onPlayerJoin` sent two packets through 1-arg `sendToPlayer` → now `sendToPlayers(player, first, new CustomPacketPayload[]{second})`, the 26.1.2 form) — fixed, recompile green. Exit criteria: `:common:compileJava :neoforge:compileJava` BUILD SUCCESSFUL; `:common:build :neoforge:build -x :common:compileTestJava` BUILD SUCCESSFUL (`:common:build` itself stays red on the known Phase-12 junit red); `git grep -c 'net\.neoforged' common/src/main/java` → 0; `comm -12` package intersection → empty. **Not committed** — changeset on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-02 | 4 | **Phase 4 neoforge-side groundwork DONE — session plan written below.** Finished the last of the neoforge side of the hub conversion: rewrote `neoforge/.../core/neoforge/PotionsPlus.java` (single `@Mod` ctor drives every `DeferredRegister.register(bus)` + `common.init(reg::register)` call; `potionFactory` lazy-lambda in a static block; duplicate `TRIGGERS` field removed), `core/neoforge/Particles.java` (mirrors 26.1.2 raw-cast propagation `(Holder) (Object)` for the `DeferredHolder<ParticleType<?>,SimpleParticleType>` → `Holder<SimpleParticleType>` mismatch), `common/.../item/WormrootItem.java` emptied (26.1.2 mirror — `onEntityItemUpdate` is a NeoForge extension method), and consumer import swaps for `core.Particles` / `core.Sounds` / `core.Attributes` / `core.Advancements` / `core.potion.Potions` / `core.neoforge.items.DynamicIconItems` + `.get()`→`.value()` on `Holder`s + `.getKey()`→`.unwrapKey()` in the two biome loot conditions. **`./gradlew :common:compileJava :neoforge:compileJava :neoforge:compileTestmodJava` all BUILD SUCCESSFUL.** Not committed. |
| 2026-09-02 | 4 | **ForgeHolder + RegistryMixin plan verified against 1.21.1 jars.** `javap` on the loom-cached `forge-1.21.1-52.1.2-minecraft-merged` confirms: 1.21.1 `Holder<T>` extends `Supplier<T>` + Forge's `IReverseTag<T>`; abstract methods are `value, isBound, is(ResourceLocation), is(ResourceKey), is(Predicate), is(TagKey), is(Holder), tags, unwrap, unwrapKey, kind, canSerializeIn`; **NO `components()`/`areComponentsBound()`/`getDelegate()`** (26.1.2's adapter drops those three overrides). `Registry.safeCastToReference` **IS present but is a private INSTANCE method** (my first grep missed it) — `holderByNameCodec → referenceHolderWithLifecycle → flatComapMap(encode = safeCastToReference → key().location())`, so the 26.1.2 `RegistryMixin` (interface mixin, non-static handler at HEAD, unwrap via `ForgeHolder.resolveReference()`) ports **verbatim**. `Registry.registerForHolder(Registry, ResourceKey|ResourceLocation, T)` → `Holder.Reference<T>` confirmed. `RegistryObject.getKey()/getId()/getHolder()` confirmed. |
| 2026-09-01 | 3 | **Phase 3 done — Fabric + Forge scaffold green, after a real mapping blocker worth recording in full.** `:fabric:remapJar` threw TinyRemapper "Unfixable conflicts" (38-entry set) on the FIRST full three-loader build. Root cause (decompiled loom 1.17.491 + tiny-remapper 0.14.0, verified empirically): Mojang's 1.21.1 named mappings give `Container.getItem/isEmpty` and `RecipeInput.getItem/isEmpty` identical names but different intermediary ids (`method_5438/5442` vs `method_59984/59987`); TinyRemapper 0.14.0's class-less conflict key flags any class implementing both interfaces; `InventoryBlockEntity implements WorldlyContainer, RecipeInput` is exactly that class. The merged `mappings.tiny` is clean — NOT a mapping defect. Ruled out: `fabric.loom.dropNonIntermediateRootMethods=true` (both targets are roots; forced cache rebuild → identical 38-conflict set, flag proven read via `project.findProperty` but inert), `nameSyntheticMembers=false` (worse: 39 conflicts), `ignoreConflicts=true` (would emit a method that fails to override one interface at runtime). **Fix applied:** removed `RecipeInput` from `InventoryBlockEntity` (import, interface, `@Override` on `size()`), added `common/.../recipe/ContainerRecipeInput.java` (a `Container`→`RecipeInput` delegating record, mirroring the mirror's `MultiRecipeInput` location idiom), and the single tree-wide site that passed a block entity as a `RecipeInput` (`neoforge/.../BrewingCauldronBlockEntity.java:101`, `matches(this, …)` — the only such site, proven by grep) now passes `new ContainerRecipeInput(this)`. Full exit criterion `./gradlew :fabric:build :forge:build :neoforge:build -x test` → **BUILD SUCCESSFUL in 15s**; `potionsplus-{fabric,forge,neoforge}-1.6.0.jar` all produced. **Not committed** — changeset on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-02 | 4 | **Phase 4 registration hubs done — Fabric + Forge written, all three loader modules compile green, `comm -12` empty. Remaining: runClient smoke on fabric + forge (the real exit criterion).** Fabric side: 26.1.2-mirror sub-hubs (`blocks/{OreBlocks,DecorationBlocks,BlockEntityBlocks,FlowerBlocks}.java`, `items/{OreItems,BrewingItems,WreathItem}.java`) + flat hubs (`Particles,Sounds,Recipes,NumberProviders,CreativeModeTabs`) + `MenuTypes/LootItemFunctions/DataComponents/Entities` parity stubs; already existed, verified verbatim against 26.1.2. Forge side (**29 files**): same hub set with the correct Forge idioms — every `DeferredRegister` routes through `ForgeHolder.of(DR.register(...))` because `RegistryObject` does **not** implement `Holder<T>` (a bare `::register` method reference won't type-check; neoforge's `DeferredHolder` does, which is why neoforge keeps bare refs); `Particles`/`Sounds` use `new SimpleParticleType(false) {}` / `SoundEvent.createVariableRangeEvent`; `CreativeModeTabs` uses `builder(Row.TOP, 4).withSearchBar()` (javap-verified Forge patches BOTH in — the plan's "no-arg builder exists in vanilla" claim was NeoForge-only, corrected below); `Blocks.init()`/`Items.init()` run before the `DR.register(bus)` calls so block-holder fields are non-null when ITEM suppliers flush (eager 1.21.1 `BlockItem`/`ArmorItem` derefs); `PotionBuilder.potionFactory` wired in a static block; five consolidated DRs (`TRIGGERS/ATTRIBUTES/MOB_EFFECTS/POTIONS/LOOT_ITEM_CONDITIONS`) live in `PotionsPlusForge`, ENTITIES/BLOCK_PREDICATE_TYPES/CONSUME_EFFECTS dropped (nothing on 1.21.1). **Deferred to Phase 9 (mirrors 26.1.2 Forge):** DISPENSER↔PRECISION_DISPENSER association — Forge 52.1.2 `BlockEntityType.validBlocks` is `private final Set<Block>` (`f_58915_`), no public mutation API (neoforge uses `BlockEntityTypeAddBlocksEvent`, fabric uses `FabricBlockEntityType.addSupportedBlock`). Also wrote `platform/fabric/{PlatformImpl,PacketNetworkImpl}` (all 7 + 5 `@ExpectPlatform` surfaces; chorus-fruit passthrough; potion drink time/cooldown hardcoded to NeoForge defaults pending Phase 8 config). `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → **BUILD SUCCESSFUL**; Decision 4a `comm -12` on all three loader↔common package dirs → **empty**. **Not committed** — changeset on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-02 | 4 | **Mixin split + FABRIC SMOKE PASS — the fabric exit criterion is met.** The fabric runClient crashed twice before booting, both invisible to the compile gate: **(1) mixin ClassNotFound** — common's `potionsplus.mixins.json` (inherited by all three loaders via common resources) listed 16 mixins that all lived in `neoforge/`. Split (step 6a): **10 vanilla-only mixins → `common/`** (config unchanged path, each javap-verified against vanilla 1.21.1), **8 neoforge-dependent → new `grill24.potionsplus.mixin.neoforge` package + `potionsplus.neoforge.mixins.json`** (referenced from `neoforge.mods.toml`); `comm -12` empty after repackage. **(2) class-init ordering NPE** — `FlowerBlocks.LUNAR_BERRY_BUSH` null when `BrewingItems.init` derefed it for the `ItemNameBlockItem`: fabric registration is **eager**, so `Blocks.<clinit>` → first `Items.registerBlockItem` triggers `Items.<clinit>` mid-way → `BrewingItems` derefs `FlowerBlocks` before its init ran (neoforge/forge hide it — their suppliers are deferred). **Fix: `FlowerBlocks.init` runs FIRST in the fabric `Blocks` static block** (step 6b). After both fixes `:fabric:runClient` booted to main menu — "Potions Plus (Fabric) initializing" + "Sound engine started" both present, no crash markers. Missing item-model JSONs (`FileNotFoundException: potionsplus:models/item/...`) are expected — models land in Phase 11. **Forge runClient smoke next; then neoforge regression + commit.** |
| 2026-09-02 | 4 | **Phase 4 CLOSED — all exit criteria met, committed `d9b2cf4` (153 files, +3150/−523).** Forge runClient smoke **PASS** (main menu reached; 26 item-model lookups prove blocks/items registered — the same signal that proved fabric), neoforge regression smoke **PASS** after the hub refactor + mixin split (Sound engine started, zero crash/mixin markers, JEI initialized), clean three-loader build **BUILD SUCCESSFUL in 12s** (`./gradlew clean :common:build :neoforge:build :fabric:build :forge:build -x test -x :common:compileTestJava`). Standing memory rule applied: killed the three dev-run client JVMs before the clean (abandoned-JVM file locks), and cleaned after the content-removal phase (stale-build registry crash). Full changeset: fabric hubs (26 files) + forge hubs (29 files) + common hub stubs + neoforge hub conversion + the mixin split (6a) + fabric ordering fix (6b). **Noted for Phase 9:** the forge dev-run `--mixin.config` wiring is still outstanding (RegistryMixin + the forge config exist but won't load in dev until then); forge's DISPENSER↔PRECISION_DISPENSER association is also deferred to Phase 9 (private `validBlocks`). |
| 2026-09-02 | 5 | **Phase 5 partially done — networking infra complete on all three loaders, 6/12 payloads ported.** `PlatformImpl`/`fabric` `PacketNetworkImpl` were already done (Phase 4); wrote the rest: `forge/.../platform/forge/PacketNetworkImpl` (`Channel.send` + `PacketDistributor.{PLAYER,TRACKING_ENTITY_AND_SELF,SERVER,TRACKING_CHUNK}`, javap-verified against `forge-universal.jar`), `network/fabric/FabricPacketContext` + `network/forge/ForgePacketContext` (both verbatim ports of 26.1.2 — Forge's `CustomPayloadEvent.Context` shape javap-confirmed identical), and full `core/fabric/Packets.java` + `core/forge/Packets.java` (Forge's `ChannelBuilder…payloadChannel().play().serverbound()/.clientbound().add(...).build()` chain javap-confirmed to resolve the same way as 26.1.2's MC-26.1 Forge fork; the `handled(...)` `setPacketHandled(true)` wrapper and `playCodec(...)` narrowing cast both ported verbatim). **Scope-narrowing discovery:** unlike the finished 26.1.2 tree (zero split packages, everything portable), 6 of `core/neoforge/Packets.java`'s 12 payload classes still depend on neoforge-only helpers that were never split out of the original flat module — `SanguineAltarBlockEntity` + `core.neoforge.Blocks` (2 packets), `JeiPotionsPlusPlugin` (3 packets), `ClotheslineBehaviour` (1 packet, a raw NeoForge `PlayerInteractEvent.RightClickBlock` handler — textbook Phase 7 work). Moved the other 6 (`ClientboundBlockEntityCraftRecipePacket`, `ClientboundDisplayAlert{,WithItemStackName,WithParameter}`, `ClientboundImpulsePlayerPacket`, `ServerboundSpawnDoubleJumpParticlesPacket`) to `common/network/` via `git mv` + package fix, fixed 7 call-site imports across `neoforge/` (`PotionBeaconBlock`, 3× blockentity, `ExplodingEffect`, `PlayerListeners`, `core/neoforge/Packets`), and registered all 6 on all three loaders. The other 6 stay neoforge-only with a comment naming the blocker; `ClientboundSyncRuntimeRecipesPacket` (26.1.2's 12th payload) doesn't exist in this tree at all — investigated whether it's needed (vanilla's own recipe-sync packet may already cover it, since `RecipesRegistrar.injectRuntimeRecipes` runs at server start before any player joins) but **did not verify with a real world join** — deferred, needs Phase 9's access widener first since the injection path is NeoForge-only until then. **Verified:** `comm -12` empty on all three platform modules; `:neoforge:compileJava :fabric:compileJava :forge:compileJava` and `:neoforge:build :fabric:build :forge:build -x test` both green; fresh `runClient` smoke on **all three loaders** reaches `Sound engine started` with zero new exceptions (Forge's Channel build included — confirms the javap read was right at runtime, not just at compile time). **Not verified: an actual packet round-trip / world join** — none of the 6 ported payloads has a sending call site wired yet (those live in still-neoforge-only `@EventBusSubscriber` classes awaiting Phase 7). **Committed `8a71344`** (together with the two crash fixes below). |
| 2026-09-02 | 5 | **Fix pass: real client-server crash found by playing, not by building** — exactly the headline lesson from the 26.1.2 implementation history (budget a fix pass after each phase; game tests/world creation/real play find what builds don't). User ran a dedicated neoforge server + client, joined, and crashed after a few minutes: `NullPointerException: Cannot invoke Holder.getKey() because attribute is null` in a mixin-injected `LivingEntity.setSprinting`, entity-ticking the local player (`crash-2026-09-02_19.06.17-client.txt`). **Root cause:** `mixin/neoforge/LivingEntityMixin.SPRINT_SPEED_ATTRIBUTES` was a `static final` list built **once**, at class-init, from `Attributes.SPRINTING_SPEED` — but that mixin's `<clinit>` runs as part of `LivingEntity`'s own class-load (very early, well before `PotionsPlus`'s constructor assigns `Attributes.SPRINTING_SPEED` during mod construction), so the list permanently captured `null`, and every sprint toggle after that NPE'd. **Not a Phase 5 regression** — this bug was already latent in the file (present since before this phase, untouched by the packet work) and only surfaced now because it needed an actual `setSprinting()` call in a live world to trigger — exactly the class of bug the plan's "test by playing" lesson warns about. **The 26.1.2 tree already hit and fixed this identical bug** (`common/mixin/LivingEntityMixin.java` carries a code comment explaining it) — ported its fix verbatim: read `Attributes.SPRINTING_SPEED` lazily inside `setSprinting()` instead of caching it, null-guard, drop the now-dead `SPRINT_SPEED_ATTRIBUTES` list and its now-unused imports. `:neoforge:compileJava` green. **Confirmed fixed 2026-09-02** — user re-ran server + client, played past the point that crashed before; no recurrence. |
| 2026-09-02 | 1 | **Fix pass: second real crash found by playing — placing a brewing cauldron.** `java.lang.RuntimeException: IllegalAccessException: class grill24.potionsplus.blockentity.InventoryBlockEntity (in module generated_6bcfdde) cannot access a member of class grill24.potionsplus.blockentity.neoforge.BrewingCauldronBlockEntity (in module potionsplus) with modifiers "protected"` (`crash-2026-09-02_19.13.14-client.txt`), on `BlockItem.place → BlockEntity.saveWithFullMetadata → InventoryBlockEntity.writePacketNbt`. **Root cause: a genuine regression from Phase 1's package split**, not Phase 5. `InventoryBlockEntity.writePacketNbt`/`readPacketNbt` (`common/.../blockentity/InventoryBlockEntity.java`) reflect over `getClass().getDeclaredFields()` — the concrete runtime subclass — and call `field.get(this)`/`field.set(this, …)` on every field annotated `@BlockEntitySerializableData`, with no `setAccessible(true)`. Plain Java reflective access to a `protected` field is only legal when the *caller* is in the same package as the declaring class, or is itself a subclass reading through its own instance — neither holds here: `InventoryBlockEntity` is the **superclass** reflecting into a **subclass**'s field, and before Phase 1 both classes lived in the same package (`grill24.potionsplus.blockentity`), which satisfied the same-package rule by accident. Phase 1 moved `BrewingCauldronBlockEntity` into `blockentity.neoforge`, breaking that accidental same-package cover — invisible to compilation (reflection isn't type-checked) and only surfaced the first time a cauldron with a `@BlockEntitySerializableData` field (`storedExperience`) was saved. Grepped the whole tree: `storedExperience` is the **only** `@BlockEntitySerializableData` field that exists. **Fix:** widened it `protected` → `public` in `BrewingCauldronBlockEntity` — sidesteps the package/module check entirely (public reflective access needs no same-package/subclass relationship), rather than chasing `setAccessible`/module-opens plumbing that would have to be redone for every loader. **Not caught by any of Phase 4/5's `runClient` smokes** because none of them placed a block with a `@BlockEntitySerializableData` field — reinforces the plan's standing lesson that boot-to-menu smokes and green builds don't exercise gameplay code paths; only real interaction does. `:neoforge:compileJava`/`:neoforge:build` green. **Confirmed fixed 2026-09-02** — user re-placed a brewing cauldron on a live server; no recurrence. |
| 2026-09-03 | 10 | **Phase 10 closed — datagen sharing wired, two real gaps found and fixed (neither predicted by the plan or present on 26.1.2).** Added the root `commonDatagen` Copy task (verbatim from the 26.1.2 form: copies `neoforge/src/generated/resources` → `common/src/generated/resources`, excluding `.cache/**`, `data/neoforge/**`, `**/neoforge/**`, with a `doFirst` clean) and `common/build.gradle`'s `processResources { duplicatesStrategy = DuplicatesStrategy.INCLUDE }`. **Gap 1 — `:neoforge:runData` crashed outright** (`IllegalArgumentException: Texture potionsplus:item/wreath does not exist in any known resource pack`) the first time it ran since Phase 1's split moved that texture into `common/src/main/resources`: the `data` run's `--existing` arg in `neoforge/build.gradle` only ever pointed at `neoforge/src/main/resources`, and `ExistingFileHelper` does not walk the `common` configuration on the runtime classpath — it only sees paths explicitly passed. Fixed by passing a second `--existing` for `project(':common').file('src/main/resources/')` (repeatable arg, both roots merge). **This is 1.21.1-specific** — 26.1.2 never hits it because it's on the newer model-datagen system, not `ExistingFileHelper`. **Gap 2 — after the Copy ran, all three platform builds failed** with `Entry ... is a duplicate but no duplicate handling strategy has been set`: each platform module's own `processResources` already does `from project(":common").sourceSets.main.resources`, and now that common's copy of the shared datagen output exists, that duplicates the same files each platform module also generates into its own `src/generated/resources` srcDir. Fixed by adding `duplicatesStrategy = DuplicatesStrategy.EXCLUDE` to `neoforge/`, `fabric/`, and `forge/`'s own `processResources` blocks (26.1.2 already carries this on all three — the plan's diff just hadn't been re-checked against it for this exact task). **Also confirmed and worked around a known-real Gradle/architectury-loom hang**, matching the user's prior field experience: `:neoforge:runData`'s underlying MC datagen process completes and logs `[exited with code 0]`, but the forked JVM never actually terminates (a non-daemon thread survives), so Gradle's `Exec`-backed task hangs indefinitely waiting on process exit and never reaches the dependent `commonDatagen` Copy task. Worked around by running `timeout 90 ./gradlew :neoforge:runData` (GNU coreutils `timeout`, which kills the whole process tree cleanly — confirmed no orphaned JVMs afterward) to let the generation finish and files land on disk, then a separate `./gradlew commonDatagen -x :neoforge:runData` to run just the Copy against the already-generated files. **Exit criterion met:** `commonDatagen` (2s, BUILD SUCCESSFUL) then `:fabric:build :forge:build :neoforge:build -x test` (12s, BUILD SUCCESSFUL); unzipped Fabric/NeoForge jars both carry 27/27 matching blockstates plus shared models/tags/sounds.json; grepped both non-NeoForge jars for `neoforge` — zero matches. Not committed — changeset (root `build.gradle`, `common/build.gradle`, `neoforge/build.gradle`, `fabric/build.gradle`, `forge/build.gradle`, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-03 | 8 | **Server config ported to all three loaders — Phase 8's second-to-last bucket closed.** `forge/.../config/PotionsPlusConfig.java`: near-verbatim `ForgeConfigSpec` port of the existing `neoforge/.../config/PotionsPlusConfig.java` `ModConfigSpec` version (`javap` on `fmlcore-1.21.1-52.1.2.jar` confirmed `ModLoadingContext.get().registerConfig(ModConfig.Type, IConfigSpec<?>)` is still the right 52.1.2 call, not 26.1.2's newer `FMLJavaModLoadingContext.getModBusGroup()`-shaped constructor), registered in `PotionsPlusForge`'s constructor, read through the same try/`IllegalStateException`-fallback `PlatformImpl` pattern NeoForge already used. `fabric/.../config/PotionsPlusConfig.java`: hand-rolled Gson JSON config under `FabricLoader.getInstance().getConfigDir()`, ported near-verbatim from the 26.1.2 worktree's Fabric config (load-or-create-with-defaults at class-load, `save()` writes pretty JSON) — no config API exists in fabric-api. Both new classes share `neoforge`'s unqualified `grill24.potionsplus.config` package (this branch never put `config/` under `neoforge/`-only, unlike 26.1.2's `config.{fabric,forge}` split), so no Decision 4a intersection risk. **Verified beyond compile:** `:{neoforge,fabric,forge}:build -x test` green; `comm -12` empty on fabric/forge; `runServer` smoke on all three reaches `Done (...)!` and shuts down clean on `stop` with zero PotionsPlus exceptions — **Forge's log is direct runtime proof, not just a compile pass:** it generated `world/serverconfig/potionsplus-server.toml` and logged `Incorrect key potionDrinkTimeTicks was corrected from null to its default, 16` / `potionUseCooldownTimeTicks ... 0`, meaning `ForgeConfigSpec` actually registered and ran its correction pass. Leaves **Capabilities/`IItemHandler`** as the only open Phase 8 bucket, genuinely blocked on porting `ClotheslineBlock`/`ClotheslineBlockEntity` to `common/` first (real Phase 8/11 scope, not a quick follow-on — see the bucket's own notes). Not committed — changeset on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-03 | 11 | **Phase 11 partial — particles/keymappings/item-tint/JEI done and smoke-verified on all three loaders; BE renderers/block-tint/tooltip-factory genuinely blocked on an earlier-phase gap.** Moved 8 particle classes + `ParticleConfigurations` (`particle/neoforge/*` → `common/particle/`, stripped `@OnlyIn`/`Dist` — confirmed against the reference tree, which carries these with zero NeoForge coupling) and all 3 JEI classes (`client/integration/jei/*` → `common/`, decoupling `BrewingCauldronRecipeCategory`/`ClotheslineRecipeCategory` from `BrewingCauldronBlockEntity.CONTAINER_SIZE` by inlining the value `6` as a local constant with a reconcile-later comment — the only neoforge coupling either class had). Wrote `common/core/KeyMappings.java` + `common/event/KeyMappingsListener.java` (mutable-holder idiom matching `core.Particles`/`core.Blocks`, since `KeyMapping`'s Forge/NeoForge ctor overload needs a platform `KeyConflictContext` type with no vanilla equivalent) and `common/item/tintsource/PotionsPlusItemColors.java` (extracted from NeoForge's pre-existing, already-correct `core/neoforge/Blocks.java#registerItemColors` inline lambda — this exact silent-failure risk had already been dodged on NeoForge itself before this phase, it just wasn't shared). Registered particles + item color + key mapping on **Fabric** (`PotionsPlusFabricClient.onInitializeClient`, using javap-confirmed 1.21.1-era `net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry` and `...rendering.v1.ColorProviderRegistry`) and **Forge** (new `core/forge/Renderers.java`, `@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)` — javap on the actual 52.1.2/eventbus-6.x jars showed `SubscribeEvent` lives at `net.minecraftforge.eventbus.api` (not reference tree's newer `.api.listener`) and `Bus` has only `MOD`/`FORGE`, no `BOTH`; corrected the plan's assumed shape rather than guessing). **JEI wired on all three module build.gradles** (`jei-1.21.1-{fabric,forge,neoforge}-api` compileOnly, `-{fabric,forge,neoforge}` runtime) and **Forge artifact resolution verified for real** (`:forge:dependencies --configuration runtimeClasspath` shows `mezz.jei:jei-1.21.1-forge:19.18.10.218` resolved — the plan's flagged uncertainty is now closed, not just assumed). **One real blocker hit and fixed**: `:fabric:runClient` crashed at `Knot.init` with `AccessWidenerFormatException: Namespace (intermediary) does not match current runtime namespace (named)` — JEI's fabric jar is a real Fabric mod carrying its own intermediary-namespace AW, and plain `runtimeOnly` skips Loom's mod-aware remap step (this architectury-loom setup runs dev-time in the **named** namespace); switched to `modRuntimeOnly` and it cleared. Also wired `fabric.mod.json`'s `jei_mod_plugin` entrypoint + `suggests.jei` (Forge/NeoForge need no manifest entry — `@JeiPlugin` classpath scanning). **Real blocker discovered, not worked around**: BE renderers (all 6), the cauldron block-tint, and the `ItemStacksTooltip` client tooltip-component factory cannot be ported — `BrewingCauldronBlockEntity` et al. (still `blockentity.neoforge.*`) and `core.neoforge.items.DynamicIconItems` were never split out of neoforge in an earlier phase, so Fabric/Forge have no BlockEntity behind these six blocks at all (only the `Block`/`BlockEntityType` registration exists, via `core.blocks.BlockEntityBlocks`) and nothing for a renderer/tint/tooltip-factory to target. Confirmed this is a real prerequisite gap by reading the actual files (not inferred): grepped for `blockentity`/BE-logic classes under `common/`, `fabric/`, `forge/` and found only marker interfaces (`InventoryBlockEntity`, `ICraftingBlockEntity`, etc.), zero concrete BE classes outside `neoforge/`. Left clearly commented in `forge/core/forge/Renderers.java` and `fabric/core/fabric/PotionsPlusFabricClient.java` for whoever does that porting work. **Verified beyond compile**: `:common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` all green together; Decision 4a `comm -12` empty on all three platform modules; **real `runClient` smoke on all three loaders** (not just boot-to-menu — watched for JEI-specific signals too) — all three reach `Sound engine started` with zero exceptions, and all three build `jei:textures/atlas/gui.png-atlas` (proof JEI's own plugin/atlas init ran without crashing, including the Fabric `jei_mod_plugin` entrypoint and the Forge `@JeiPlugin` classpath scan). **Not verified**: JEI's actual in-game GUI (recipe categories opening, ingredients populating) — would need a world join, out of this pass's smoke budget; the atlas-build signal is compile+init proof, not a full behavioral check. Not committed — changeset (particle/JEI moves, `common/core/KeyMappings.java`, `common/event/KeyMappingsListener.java`, `common/item/tintsource/PotionsPlusItemColors.java`, `forge/core/forge/Renderers.java`, `fabric/core/fabric/PotionsPlusFabricClient.java`, `fabric/event/fabric/TickListeners.java`, `forge/event/forge/TickListeners.java`, `neoforge/core/neoforge/{Blocks,ClientEvents,KeyMappings,KeyMappingsListener}.java`, `fabric/build.gradle`, `forge/build.gradle`, `fabric/src/main/resources/fabric.mod.json`, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11 | **Session dedicated to unblocking Phase 11's BE-renderer/tint/tooltip gap — re-scoped it much larger after reading every file involved, landed one small safe port, did not attempt the rest.** Per Decision 4/4a, opened and diffed all 6 concrete BE classes (`AbyssalTrove`, `BrewingCauldron`, `Clothesline`, `HerbalistsLectern`, `PotionBeacon`, `SanguineAltar`) plus `ClotheslineBlockEntityBakedRenderData` and `HerbalistsLecternSounds` against the finished `dev/26.1.2/multi-loader-expansion` tree (all 6 + `ClotheslineBlockEntityBakedRenderData` already sit unsuffixed in that tree's `common/blockentity/`, confirming these are meant to be shared code, not neoforge-specific). Read every neoforge-side import each of the 8 files pulls in (not just grepped for `net.neoforged`) and traced each to its own file to check depth, rather than trusting the 2026-09-03 note's "just DynamicIconItems" summary. **Finding: the fan-out is much bigger than previously recorded** — full evidence now in the Phase 11 section's "2026-09-04 correction" block above. Summary: `core.blocks.BlockEntityBlocks` (common) has zero `BlockEntityType` fields (only `Block`), so the entire `BlockEntityType` registration hub (`DeferredRegister<BlockEntityType<?>>` + all 6 `.build(null)` calls) is neoforge-only and unbuilt on fabric/forge — confirmed by `grep BLOCK_ENTITY` returning zero hits in both `fabric/.../core/fabric/Blocks.java` and `forge/.../core/forge/Blocks.java`; `core.neoforge.items.DynamicIconItems` sits on an 8-file neoforge-only item-builder DSL (`utility/registration/{item/}neoforge/*`, confirmed via `find`); `core.neoforge.RecipesRegistrar`'s runtime-injection half is already-known-blocked on Phase 9's access widener (not yet landed); `block.neoforge.ClotheslineBlock`/`PotionBeaconBlock` are themselves still unsplit `Block` classes; 3 packets (`ClientboundAcquiredBrewingRecipeKnowledgePacket`, 2× `ClientboundSanguineAltarConversion*Packet`) are Phase-5-shaped moves not yet done. **What was actually landed**: `HerbalistsLecternSounds` (`neoforge/.../blockentity/neoforge/HerbalistsLecternSounds.java` → `common/.../blockentity/HerbalistsLecternSounds.java`) — read in full first and confirmed its only dependencies (`core.Sounds`, `utility.Utility.createSoundInstance`/`playSoundStopOther`) are already common; `git mv` + package fix + import fix in its 4 referencing neoforge files (`HerbalistsLecternBlock`, `PotionBeaconBlock`, `HerbalistsLecternBlockEntity`, `PotionBeaconBlockEntity`). **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → BUILD SUCCESSFUL; Decision 4a `comm -12` re-run empty on all three platform modules against `common/`. **Deliberately not attempted this session** (per the task's own instruction not to force a deep dependency through under time pressure): the `BlockEntityType` hub, the `DynamicIconItems` DSL port, the 2 remaining Block classes, the 3 remaining packets, the 6 BE-class moves themselves, the 6 renderer moves, the cauldron tint, and the tooltip-component factory — none of these were touched, so none of Phase 11's original exit-criterion gaps closed. `runClient` smokes on the three loaders were not re-run this session since no client-visible code changed beyond the one file move (compile-green + package-intersection-empty is sufficient evidence for a non-behavioral rename). **Recommendation recorded in the Phase 11 section**: split this into a Phase 11a with an explicit 4-step order (BlockEntityType hub → 3 packets → DynamicIconItems DSL → RecipesRegistrar-after-Phase-9), since each step unblocks a different subset of the 6 BE classes independently. Not committed — changeset (`common/blockentity/HerbalistsLecternSounds.java` new location, 4 import fixes, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11a | **Phase 11a steps 1-2 (of the 4-step order recorded in the previous entry) landed: common `BlockEntityType` hub + fabric/forge registration, and the one packet move that turned out to be genuinely mechanical.** **Step 1**: added `common/core/Blocks.java` (new file, mirrors the finished `dev/26.1.2` tree's `core.Blocks` exactly in shape — diffed against it first) with all 6 `BlockEntityType` holder fields; 4 (`BREWING_CAULDRON`/`HERBALISTS_LECTERN`/`SANGUINE_ALTAR`/`ABYSSAL_TROVE`) stay `Holder<BlockEntityType<?>>` since their BE classes are still neoforge-only, 2 (`CLOTHESLINE`/`POTION_BEACON`) are concretely typed since their BE classes moved to common this session (below). Wired the neoforge-side assignment into the existing `core.neoforge.Blocks` (mirrors the reference tree's `(Holder)(Object)` unchecked-cast pattern for `DeferredHolder`→`Holder`), and built the same registration from scratch on **fabric** (`core.fabric.Blocks` — `FabricBlockEntityTypeBuilder` + the existing `FabricRegistration` helper, immediate registration) and **forge** (`core.forge.Blocks` — `ForgeHolder`-wrapped `DeferredRegister<BlockEntityType<?>>`, `Blocks.BLOCK_ENTITIES.register(bus)` added to `PotionsPlusForge`'s existing `DeferredRegister` flush list). Moved `ClotheslineBlockEntity`, `PotionBeaconBlockEntity`, `ClotheslineBlockEntityBakedRenderData`, both renderers, and the `ClotheslineBlock`/`PotionBeaconBlock` Block classes (7 files total) from `neoforge/.../{block,blockentity}/neoforge/` to `common/.../{block,blockentity}/` (unsuffixed, matching the reference tree, which already carries all 7 unsuffixed) — package decls + every cross-module import fixed, plus the 5 files elsewhere that referenced the old `.neoforge.` paths (`ClotheslineBehaviour`, `Capabilities`, `ClotheslineBlockModelGenerator`, `PlayerBrewingKnowledgeNetworking`, `core.neoforge.Blocks` itself). **Also had to register the underlying Clothesline/PotionBeacon *blocks* on fabric/forge** (`core.{fabric,forge}.blocks.BlockEntityBlocks` — previously only `PRECISION_DISPENSER`), since a `BlockEntityType` needs a real `Block` to bind to and neither loader had one; used the plain `registerBlock.apply(name, supplier)` path (same as `DecorationBlocks`) rather than the neoforge-only `RegistrationUtility`/`SimpleBlockBuilder` DSL that step 3 is scoped around — Decision 5 keeps NeoForge as datagen source of truth via `commonDatagen`, so this needs no model/recipe/loot generation on fabric/forge. Registered the 2 newly-common renderers on all three loaders: NeoForge's existing `core/neoforge/Renderers.java` needed no edit (its wildcard `blockentity.*`/`blockentity.neoforge.*` imports resolve the moved classes automatically), Fabric got `BlockEntityRendererRegistry.register(...)` in `PotionsPlusFabricClient.onInitializeClient` (javap-confirmed `net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry` — same package fishtastic uses on this MC version), Forge got a new `registerBlockEntityRenderers` method in `core/forge/Renderers.java` using `net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers` (javap-confirmed on the actual 52.1.2 jar: `registerBlockEntityRenderer(BlockEntityType<? extends T>, BlockEntityRendererProvider<T>)`, same shape as NeoForge). **Step 2**: moved `ClientboundAcquiredBrewingRecipeKnowledgePacket` (`network.neoforge` → `common/network/`) — its only dependencies (`JeiPotionsPlusPlugin`, `PotionsPlus`, `Sounds`, `SavedData`) were already common (the fabric `Packets.java` doc comment blocking it on `JeiPotionsPlusPlugin` was stale — that class moved to common back in the 2026-09-03 session), registered on fabric's and forge's `Packets.java` (mirroring the existing 6-payload pattern exactly), fixed its 1 call site (`persistence.neoforge.PlayerBrewingKnowledgeNetworking`'s import). **Two real hidden dependencies found and NOT forced through** (recorded here per the task's own instruction, not worked around): (1) `ItemStack#getCraftingRemainingItem()`/`hasCraftingRemainingItem()`, called by `ClotheslineBlockEntity.craft()`, is NeoForge-only sugar — `ItemStack implements ...IItemStackExtension` on NeoForge, confirmed by extracting and reading the actual `ItemStack.java` from the neoforge-merged sources jar (vanilla `ItemStack` has no such method, only `Item` does) — not caught by any prior session because the class had never left neoforge before; fixed by going through `Item#getCraftingRemainingItem()` directly and wrapping in a `new ItemStack(...)`, which is vanilla-safe on every loader. (2) The 2 `ClientboundSanguineAltarConversion{State,Progress}Packet`s, assumed Phase-5-shaped in the previous session's recommendation, are **not** actually unblocked by the step-1 hub: reading them in full shows `ClientboundSanguineAltarConversionStatePacket` directly imports and references the concrete `SanguineAltarBlockEntity` class — its `State` enum (`SanguineAltarBlockEntity.State.values()[packet.state]`) and public fields (`blockEntity.state =`, `.chainedIngredientToDisplay`) — not just its `BlockEntityType` holder; `SanguineAltarBlockEntity` itself is explicitly out of scope this session (per the task brief), and Phase 2's common-interface pattern the other 6 already-moved packets use (e.g. `ClientboundBlockEntityCraftRecipePacket` pattern-matching on a common `ICraftingBlockEntity` interface, no `BlockEntityType` needed at all) doesn't cover concrete-class field access — designing a new common interface for this was judged out of scope and not attempted. Left both packets in `neoforge/`, corrected the stale fabric/forge `Packets.java` doc comments to say so precisely instead of re-asserting the old "Phase 7/JEI" blocker reasoning that no longer applies now that JEI is common. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → BUILD SUCCESSFUL (hit and fixed 2 additional loader-specific compile errors along the way: `Holder<BlockEntityType<T>>` has no `.get()` on this MC version — only `.value()` — fixed at every call site touched this session; Forge 52.1.2's `BlockEntityType` constructor is unpatched vanilla `(BlockEntitySupplier, Set<Block>, Type<?>)`, no NeoForge-style varargs convenience constructor, so forge's `registerBlockEntity` helper goes through `BlockEntityType.Builder.of(...).build(null)` instead of `new BlockEntityType<>(...)`, matching what neoforge's own `core.neoforge.Blocks` already does — confirms the VERIFIED-API-FACTS note in the task brief). Decision 4a `comm -12` re-run empty on fabric/forge/neoforge vs `common/`. **Real `runClient` smoke on all three loaders** (not just compile): killed and relaunched all three (`:neoforge:runClient`, `:fabric:runClient`, `:forge:runClient` in parallel, polled each log for `Sound engine started`/`Exception`/`BUILD FAILED`) — all three reach `Sound engine started` and build the `jei:textures/atlas/gui.png-atlas` (same JEI-init proof the 2026-09-03 session used) with zero exceptions in any of the three logs; NeoForge additionally logs the pre-existing "already has a render layer" warning for `potion_beacon` (harmless, unrelated to this session's renderer registration — it's an `ItemBlockRenderTypes` warning from an existing pre-registration call). Cleanly killed all 3 architectury client JVMs afterward (verified via `Get-CimInstance Win32_Process` that none remained) rather than leaving them running. **Not verified**: actually placing a Clothesline or PotionBeacon block in a loaded world and watching it render — this environment has no GUI-automation tool available to drive the Minecraft window (mouse/keyboard/world-join), so only the boot-to-menu-plus-atlas-build smoke was possible; per the task brief's own caveat, a green boot does not prove the renderer fires, so this is flagged as unverified, not assumed working. **Deliberately not attempted this session**: steps 3 (`DynamicIconItems` DSL port) and 4 (`RecipesRegistrar` runtime injection) — out of scope per the task brief, and step 4 is still blocked on Phase 9's access widener regardless. Not committed — changeset (`common/core/Blocks.java` new file; 7 `git mv`s into `common/{block,blockentity}/`; `common/network/ClientboundAcquiredBrewingRecipeKnowledgePacket.java` new location; edits to `{fabric,forge,neoforge}/.../core/{fabric,forge,neoforge}/{Blocks,Packets}.java`, `fabric/.../PotionsPlusFabricClient.java`, `forge/.../{Renderers,PotionsPlusForge}.java`, `{fabric,forge}/.../blocks/BlockEntityBlocks.java`, 5 neoforge files with stale `.neoforge.` imports, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11a | **Phase 11a step 5 (`PlayerBrewingKnowledgeNetworking`) landed — `BrewingCauldronBlockEntity` + its renderer now common; NeoForge unaffected, Fabric/Forge still blocked one layer further down by the still-neoforge-only `BrewingCauldronBlock`. Phase 9's access widener confirmed still not landed cross-loader, so step 4 (`RecipesRegistrar`) stays untouched as instructed.** Read `persistence.neoforge.PlayerBrewingKnowledgeNetworking` in full rather than trusting the prior session's "sends a still-neoforge-only packet" note — that note was stale: `ClientboundAcquiredBrewingRecipeKnowledgePacket` already moved to `common/network/` in the step-2 session, so the class's only imports left were `PlayerBrewingKnowledge` (common), the packet (now common), and `platform.PacketNetwork` (common) — zero neoforge coupling remained. `git mv` to `common/persistence/PlayerBrewingKnowledgeNetworking.java`, package fix, no logic change. Then read all of `BrewingCauldronBlockEntity` (`neoforge/.../blockentity/neoforge/`, 421 lines) end-to-end per the task brief's explicit warning that 2 prior sessions each found a second hidden blocker after clearing the first: found exactly one more, `import grill24.potionsplus.core.neoforge.Blocks` (used only for `Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get()` in the constructor) — every other import (`Recipes`, `Advancements`, `Particles`, `DynamicIconItems`, `PpIngredient`, `ClientboundBlockEntityCraftRecipePacket`, `SavedData`, `ContainerRecipeInput`, `BrewingCauldronRecipe(Builder)`, `alchemy.*`, `Utility`, the 4 `blockentity` marker interfaces) was already common, confirmed file-by-file with `find`/`ls` against `common/src/main/java`. Swapped to `core.Blocks` (the step-1 hub) and `.value()` (matches the reference tree's `common/blockentity/BrewingCauldronBlockEntity.java`, diffed side-by-side before and after). `git mv` to `common/blockentity/BrewingCauldronBlockEntity.java`; fixed the 4 stale `blockentity.neoforge.BrewingCauldronBlockEntity` imports found by repo-wide grep (`BrewingCauldronGameTests`, `AlchemyGameTests`, `neoforge/block/neoforge/BrewingCauldronBlock`, `core.neoforge.Blocks`). Hit one more hidden NeoForge-only API mid-move, not caught by the import-list read because it's a *method call* not an import: `common:compileJava` failed on `effect.getEffect().getKey()` — `Holder<MobEffect>` has no `getKey()` in vanilla; NeoForge patches a convenience `getKey()` onto `Holder` that the neoforge module's compile view silently accepted. Fixed to vanilla `effect.getEffect().unwrapKey().orElseThrow()`, matching the reference tree's common version exactly (diffed to confirm). Also moved `BrewingCauldronBlockEntityRenderer` (`blockentity.neoforge/` → `common/blockentity/`, stripped `@OnlyIn(Dist.CLIENT)`/the unused `core.neoforge.Items` import — matches how `ClotheslineBlockEntityRenderer` already lives unsuffixed, unannotated in `common/`) since it only referenced already-common types once the BE class moved. **Did NOT extend registration to Fabric/Forge, and this is a real newly-found blocker, not an oversight**: `grep BREWING_CAULDRON` across `fabric/.../core/fabric/blocks/` and `forge/.../core/forge/blocks/` returns zero hits — unlike Clothesline/PotionBeacon (whose `Block` classes were ported to `common/` in step 1), `block.neoforge.BrewingCauldronBlock` itself was never split off neoforge, so Fabric and Forge have no cauldron `Block` registered at all, let alone a `BlockEntityType` bound to one — the common `Blocks.BREWING_CAULDRON_BLOCK_ENTITY` field exists (Phase 11a step 1) but is populated only by NeoForge's own `core.neoforge.Blocks` static block, `.value()` on Fabric/Forge would NPE if ever called. Porting `BrewingCauldronBlock` (`CauldronBlock` subclass, `useItemOn`/`useWithoutItem`/`getTicker`/`getAnalogOutputSignal`/`setPlacedBy`, plus whatever blockstate/model/loot-table datagen wiring Fabric/Forge would then need) is its own register-hub-shaped task, same class of work as Phase 11a step 1 was for Clothesline/PotionBeacon — not attempted this session, left for a follow-up. Consequently the cauldron water tint (`RegisterColorHandlersEvent.Block`/`ColorProviderRegistry.BLOCK`) also stays NeoForge-only for the same reason (registering it on Fabric/Forge now would be a no-op stub, same reasoning `core/forge/Renderers.java`'s javadoc already gives for the other 4 BE renderers) — NeoForge's existing `registerBlockColors` in `core/neoforge/Blocks.java` needed no changes (already resolves the moved class via its unqualified reference). **Part 2 (Phase 9 access widener status) — checked, confirmed still not landed, not attempted.** Read the Phase 9 status-table row and its own section: still 🟡 "in progress", explicitly lists "Capabilities/AT survey beyond the small set needed today" as still open. Verified directly rather than trusting the doc alone: `common/src/main/resources/potionsplus.accesswidener` and `forge/src/main/resources/META-INF/accesstransformer.cfg` both carry only the small original set (`Block#popExperience`, `HolderSetCodec#homogenousListCodec`, `TrackingEmitter` fields) — no `RecipeManager.byType`/`byName` entry in either; `neoforge/src/main/resources/META-INF/accesstransformer.cfg` *does* have `public net.minecraft.world.item.crafting.RecipeManager byType`/`byName`, but that's NeoForge's own local AT (used today only by `core.neoforge.RecipesRegistrar.injectRuntimeRecipes`, confirmed via `grep`), not shared cross-loader. Confirmed `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` both still `import grill24.potionsplus.core.neoforge.RecipesRegistrar` (unchanged) — per the task brief's own instruction, did not attempt this AW work (out of Phase 11's scope) and did not touch either block entity. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → BUILD SUCCESSFUL. Decision 4a `comm -12` re-run empty on fabric/forge vs `common/`. **Real `:neoforge:runClient` smoke** (the only loader with a client-visible change, since the renderer isn't registered on fabric/forge): launched, polled the log — reached `Sound engine started` + built `jei:textures/atlas/gui.png-atlas` with zero new exceptions, only the identical pre-existing warnings already documented in prior entries (missing subtitle translations, `generic_icon` particle-texture warning); confirmed via `Get-Process`/`Stop-Process` that the JVM was still alive and idle (log quiet, no crash) before killing it cleanly. Fabric/forge were not re-run beyond compile since the renderer move added dead (unregistered) code to their classpath with zero behavioral effect there, matching the task brief's own "persistence/network class with no client-visible effect → compile-green + package-check-empty is sufficient" standard. **Not verified**: an actual cauldron placed and brewing in a loaded NeoForge world (renderer firing, water tint applying) — no GUI-automation tool available in this environment, same caveat every prior Phase 11a entry has recorded. Not committed — changeset (`common/persistence/PlayerBrewingKnowledgeNetworking.java`, `common/blockentity/{BrewingCauldronBlockEntity,BrewingCauldronBlockEntityRenderer}.java` new locations; import fixes in `BrewingCauldronGameTests`, `AlchemyGameTests`, `neoforge/block/neoforge/BrewingCauldronBlock`, `neoforge/core/neoforge/Blocks`; this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11a | **Phase 11a step 3 (`DynamicIconItems` DSL) landed via re-abstraction, not a literal port; the 3 named block entities stayed neoforge-only after tracing a second hidden dependency each.** First re-derived the file list from scratch per the task brief's own warning (2 prior sessions each under-scoped this): `grep`/`find` in `neoforge/src/main/java` for `DynamicIconItems`/`RegistrationUtility`/`AbstractRegistererBuilder`/`ItemModelUtility`/`ItemOverrideUtility`/`IModelGenerator` confirmed exactly 8 neoforge-only files (`core.neoforge.items.DynamicIconItems`; `utility.registration.item.neoforge.{ItemModelUtility,ItemOverrideUtility,GenericIconItemBuilder,ItemBuilder,SimpleItemBuilder}`; `utility.registration.neoforge.{RegistrationUtility,AbstractRegistererBuilder,IModelGenerator}` — 9 counting `IModelGenerator`, task brief's "8" was close). Read `dev/26.1.2`'s equivalent (`common/.../core/items/DynamicIconItems.java` + its full `utility/registration/` tree, ~20 files, all common) and found it is **not** a literal-portable shape: its `IModelGenerator.generate(BlockModelGenerators, ItemModelGenerators)` runs against vanilla model-datagen classes (26.1.2 is on a newer MC where item-model datagen was vanilla-ized), whereas this branch's `IModelGenerator.generate(BlockStateProvider)` (confirmed via `Read`, `neoforge/.../utility/registration/neoforge/IModelGenerator.java`) is built on NeoForge's own `net.neoforged.neoforge.client.model.generators.BlockStateProvider`/`ItemModelProvider` — a 1.21.1-era NeoForge-only datagen API with no vanilla or Fabric equivalent. Porting the DSL itself would mean porting NeoForge's datagen system, which Decision 5 explicitly rules out (NeoForge stays sole datagen source of truth, shared via `commonDatagen`, Phase 10). **Re-abstraction applied instead**, matching the stub-hub pattern already established for `BrewingItems`/`OreItems`/`WreathItem` in Phase 4 (each loader duplicates registration, common only holds the resulting `Holder`s + shared logic): new `common/core/items/DynamicIconItems.java` holds the 25 texture-location `ResourceLocation` constants (copied verbatim from the neoforge original, order-sensitive — the task brief's own comment in the new file calls this out since the index is baked into NeoForge's generated override predicates), the `GENERIC_ICON`/`POTION_EFFECT_ICON` `Holder<Item>` fields, and a `GENERIC_ICON_TEXTURES`-derived index map with `getGenericIconIndex`/`getGenericIconItemStackCountForTexture`/`getGenericIconItemStackForTexture` — a direct reimplementation of what `GenericIconItemBuilder.getItemStackForTexture`/`ItemOverrideUtility.DynamicItemOverrideModelData.getItemStackCountForTexture` did before, now decoupled from the datagen-only builder chain. `core.neoforge.items.DynamicIconItems` is otherwise untouched (still drives the full `RegistrationUtility`/`AbstractRegistererBuilder` DSL for item-model datagen) except appending 2 lines at the end of `init` populating the common stub (`grill24.potionsplus.core.items.DynamicIconItems.{POTION_EFFECT_ICON,GENERIC_ICON} = ...getHolder()`), mirroring `BrewingItems`' "Populate common stubs" comment exactly. New `core.fabric.items.DynamicIconItems`/`core.forge.items.DynamicIconItems` register the same 2 `Item`s directly (`register.apply(name, () -> new Item(new Item.Properties()))`, no DSL — same reasoning `core.{fabric,forge}.items.BrewingItems` already uses: the generated models reach these jars via `commonDatagen`, so no per-loader model generation is needed) and populate the same stub; wired into both `Items.java` static blocks alongside the other 3 `init` calls. All 8 call sites of the old builder-typed `DynamicIconItems` — `AbyssalTroveBlockEntity`, `AbyssalTroveBlockEntityRenderer`, `BrewingCauldronBlockEntity`, `HerbalistsLecternBlockEntity`, `SanguineAltarBlockEntityRenderer`, `core.neoforge.ClientEvents`, `utility.neoforge.ClientItemStacksTooltip`, `data.neoforge.AdvancementProvider` — repointed via `sed` (import `core.neoforge.items.DynamicIconItems` → `core.items.DynamicIconItems`; `.GENERIC_ICON.getItemStackForTexture(` → `.getGenericIconItemStackForTexture(`; `.{POTION_EFFECT_ICON,GENERIC_ICON}.getValue()` → `.value()`), then grepped to confirm zero remaining references to the old import outside comments/the neoforge `init` call itself. **Checked whether this unblocks the 3 named block entities and found it does not, for a second independent reason each** (same pattern the last 2 sessions both hit — confirmed by reading every neoforge-tagged import in each file, not just grepping for `DynamicIconItems`): `AbyssalTroveBlockEntity` and `HerbalistsLecternBlockEntity` both import `core.neoforge.RecipesRegistrar` (used for live recipe-analysis lookups, e.g. `RecipesRegistrar.ALL_SEEDED_POTION_RECIPES_ANALYSIS.isIngredientUsed(...)`) — this is Phase 11a step 4, explicitly gated on Phase 9's access widener and out of scope this session; `BrewingCauldronBlockEntity` imports `persistence.neoforge.PlayerBrewingKnowledgeNetworking` (calls `PlayerBrewingKnowledgeNetworking.tryAddKnownRecipeServer(...)`, neoforge-only packet-send plumbing, not a Phase-5-shaped move) — neither dependency was forced through; all 3 classes stay in `neoforge/` untouched beyond their `DynamicIconItems` call-site fix. `SanguineAltarBlockEntity` itself still untouched (already known-blocked from the prior session's entry). Cauldron water tint stays blocked (needs `BrewingCauldronBlockEntity` in common). `ItemStacksTooltip`'s tooltip-component factory: its `DynamicIconItems` dependency is now gone, but tracing its remaining chain (`event.neoforge.ClientTooltipComponentFactoriesListeners`, `mixin.neoforge.ItemMixin`) shows it hooks into NeoForge's own client tooltip-component-factory extension point, which has no vanilla or Fabric-API equivalent — corrected the stale blocker comment in `fabric/.../PotionsPlusFabricClient.java` to say so precisely instead of re-pointing at `DynamicIconItems`. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → BUILD SUCCESSFUL (one loader-specific fix needed: `register.apply(name, Item::new)` doesn't compile on fabric/forge — `Item`'s only constructor takes `Item.Properties`, no no-arg overload — fixed to `register.apply(name, () -> new Item(new Item.Properties()))`, matching every other plain-registration call site in `BrewingItems`/`OreItems`). Decision 4a `comm -12` re-run empty on fabric/forge/neoforge vs `common/`. **Real `runClient` smoke on all three loaders**: killed stale logs, relaunched `:neoforge:runClient`/`:fabric:runClient`/`:forge:runClient` in parallel, polled each `run/logs/latest.log` for `Sound engine started`/`Exception`/`BUILD FAILED` — all three reach `Sound engine started` with zero exceptions. Grepped all three logs for `generic_icon`/`potion_effect_icon`/`dynamic_icon`: all three (including NeoForge, the untouched datagen source) log the identical pre-existing `Missing textures in model potionsplus:generic_icon#inventory` warning (the `sga_a`/`sga_b`/`sga_c`/`sga_d` particle textures aren't in the item atlas) — present on NeoForge too, so confirmed pre-existing and not a regression from this session's change, not chased further per the task brief's own instruction. Identified and killed the 3 client JVMs cleanly via `Get-CimInstance Win32_Process` filtered on `-Darchitectury.main.class=...` (left the 3 long-lived `-Xmx64m` gradle daemons running, untouched) and re-ran the same filter afterward to confirm zero remained. **Not verified**: placing a Clothesline/PotionBeacon/other block in a loaded world and watching item icons render in-hand or in a GUI — no GUI-automation tool available in this environment, so only the boot-to-menu smoke was possible, matching the prior session's same caveat. **Step 4 (`RecipesRegistrar`, gated on Phase 9's access widener) remains open, out of scope this session** — it is now the sole remaining blocker for `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity`; `BrewingCauldronBlockEntity` additionally needs its `PlayerBrewingKnowledgeNetworking` dependency resolved (not part of the original 4-step order, newly identified this session, not yet scoped into a step). Not committed — changeset (`common/core/items/DynamicIconItems.java` new file; `fabric/.../core/fabric/items/DynamicIconItems.java` new file; `forge/.../core/forge/items/DynamicIconItems.java` new file; edits to `neoforge/.../core/neoforge/items/DynamicIconItems.java`, `{fabric,forge}/.../core/{fabric,forge}/Items.java`, the 8 call-site files listed above, `fabric/.../PotionsPlusFabricClient.java` comment, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11a | **Phase 11a "port `BrewingCauldronBlock` cross-loader" task landed — the `Block` class itself, its `BlockEntityType` binding, its BE renderer, and the cauldron water tint are now registered on all three loaders. This closes the specific blocker the previous entry flagged.** Read `neoforge/.../block/neoforge/BrewingCauldronBlock.java` (a `CauldronBlock` subclass, 90 lines) and diffed it against the finished `dev/26.1.2` tree's `common/block/BrewingCauldronBlock.java` per Decision 4 — the reference version is for a newer MC (`useItemOn` returns `InteractionResult` not `ItemInteractionResult`, `getAnalogOutputSignal` takes an extra `Direction` param, `hasAnalogOutputSignal`/`getAnalogOutputSignal` are `protected` not `public`, uses `org.jspecify.annotations.Nullable`, drops the explicit `onRemove` override) — confirmed these are all newer-MC `CauldronBlock`/`Block` signature changes, not something to blindly copy, so the actual port kept this branch's 1.21.1 `CauldronBlock` API (matching what already compiled in the neoforge original) and only fixed the neoforge-only surface: `import core.neoforge.Blocks` → `import core.Blocks`, `BREWING_CAULDRON_BLOCK_ENTITY.get()` → `.value()` (3 call sites: `getTicker`, `useItemOn`, `getAnalogOutputSignal`), `javax.annotation.Nullable` on the `setPlacedBy` placer param → `org.jetbrains.annotations.Nullable` (matches `ClotheslineBlock`'s already-common `setPlacedBy`). `git mv` to `common/block/BrewingCauldronBlock.java`; deleted the neoforge original; fixed its 2 remaining referencing files (`neoforge/.../core/neoforge/blocks/BlockEntityBlocks.java` already had a wildcard `block.*` import so needed no change, `neoforge/.../utility/registration/block/BlockDropSelfLoot.java` needed its import repointed). Retyped `common/core/Blocks.java#BREWING_CAULDRON_BLOCK_ENTITY` from `Holder<BlockEntityType<?>>` to `Holder<BlockEntityType<BrewingCauldronBlockEntity>>` (matches the reference tree's `core.Blocks` exactly, diffed side-by-side) — NeoForge's existing `(Holder)(Object)` unchecked-cast assignment needed no change since the class is already `@SuppressWarnings("unchecked")`. Registered the block itself on **Fabric and Forge** (`core.{fabric,forge}.blocks.BlockEntityBlocks` — added a `BREWING_CAULDRON` field + `registerBlock.apply("brewing_cauldron", () -> new BrewingCauldronBlock(...))` using the exact same properties as neoforge's `SimpleBlockBuilder` version, plain `registerBlock.apply`/`Items.registerBlockItemWithAutoModel` path per Decision 5, same pattern Clothesline/PotionBeacon already used in Phase 11a step 1 — populated the common stub at the end of `init`), then the `BlockEntityType` binding on both (`core.{fabric,forge}.Blocks` — Fabric via `FabricBlockEntityTypeBuilder`/`FabricRegistration`, Forge via `ForgeHolder`-wrapped `DeferredRegister<BlockEntityType<?>>` + `BlockEntityType.Builder.of(...).build(null)`, identical to the existing `CLOTHESLINE_BLOCK_ENTITY`/`POTION_BEACON_BLOCK_ENTITY` blocks immediately above each) and assigned into the newly-typed `core.Blocks.BREWING_CAULDRON_BLOCK_ENTITY`. Registered `BrewingCauldronBlockEntityRenderer` on Fabric (`BlockEntityRendererRegistry.register` in `PotionsPlusFabricClient.onInitializeClient`, next to Clothesline/PotionBeacon) and Forge (`EntityRenderersEvent.RegisterRenderers` handler in `core/forge/Renderers.java`, same dist-gated `@Mod.EventBusSubscriber` class already used for the other 2). **Cauldron water tint**: 1.21.1 predates the codec-driven `BlockTintSource` system the reference tree's `BrewingCauldronWaterTintSource` uses (confirmed by reading that reference file — it's a `BlockTintSource` interface implementation registered via `BlockColors.register(List<BlockTintSource>, Block...)`, a 26.1.2-only vanilla API), so instead extracted the shared computation as a static helper mirroring the already-established `PotionsPlusItemColors` pattern from Phase 11: new `common/block/tintsource/PotionsPlusBlockColors.java#cauldronWaterColor(BlockState, BlockAndTintGetter, BlockPos, int)`, lifted verbatim from NeoForge's existing inline `registerBlockColors` lambda in `core/neoforge/Blocks.java` (unchanged, already correct, just not shared — same situation Phase 11's item-tint extraction found). Registered on Fabric (`ColorProviderRegistry.BLOCK.register(PotionsPlusBlockColors::cauldronWaterColor, ...)`, next to the existing item-color registration in `PotionsPlusFabricClient`) and Forge (new `registerBlockColorHandlers(RegisterColorHandlersEvent.Block event)` method on the same dist-gated `core/forge/Renderers.java` subscriber class used for particles/item-color/keymappings/BE-renderers — not a separate `@Mod` constructor listener, since this class's whole reason to exist is dodging the `FMLClientSetupEvent`-fires-too-late problem the task brief describes, and it already proves that pattern works for `RegisterColorHandlersEvent.Item`). **API signatures verified by `javap` against the actual jars in the gradle cache before writing the registration calls, not assumed**: `net.minecraftforge.client.event.RegisterColorHandlersEvent$Block.register(BlockColor, Block...)` (against the merged-mojang 52.1.2 jar), `net.minecraft.client.color.block.BlockColor.getColor(BlockState, BlockAndTintGetter, BlockPos, int)`, `BlockAndTintGetter` → `BlockGetter#getBlockEntity(BlockPos, BlockEntityType<T>)` (confirms the shared helper's signature compiles), and `ColorProviderRegistry.BLOCK` on the pinned fabric-rendering-v1 5.1.0 jar (`ColorProviderRegistry<Block, BlockColor>` — matches). **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → BUILD SUCCESSFUL, no fixes needed beyond the initial write (all API assumptions were javap-checked first). Decision 4a `comm -12` re-run empty on fabric/forge/neoforge vs `common/`. **Real `runClient` smoke on all three loaders**: launched `:neoforge:runClient`, `:fabric:runClient`, `:forge:runClient` sequentially (killing each JVM tree via `Get-CimInstance Win32_Process`/`Stop-Process` before starting the next), polled each log for `Sound engine started` and grepped for `Exception`/`ERROR`/`error` (excluding the known pre-existing `Missing subtitle translation`/`Missing sound for event`/`ShaderInstance` noise) — all three reach `Sound engine started` with **zero** exceptions or errors, and grepping all three logs for `cauldron`/`brewing_cauldron` returns nothing (no registration-time crash or warning spam, consistent with a silent successful registration). Confirmed no leftover architectury JVMs remained after killing each (`Get-CimInstance` filtered on `fabric.dli.env=client` returned empty). **Not verified, same caveat as every prior Phase 11a entry**: actually placing a brewing cauldron in a loaded world and watching it render/tint/tick — no GUI-automation tool exists in this environment to join a world and interact with it, so this remains unverified rather than assumed working. **What's still open in Phase 11 after this**: `HerbalistsLecternBlockEntity`/`SanguineAltarBlockEntity`/`AbyssalTroveBlockEntity` (steps 4/5-equivalent, gated on Phase 9's access widener + `SanguineAltarBlockEntity`'s own scoping, untouched, not attempted this session — out of this task's scope) and the `ItemStacksTooltip` client tooltip-component factory (still blocked on NeoForge's own tooltip-component-factory extension point, no vanilla/Fabric equivalent, not attempted). Not committed — changeset (`common/block/BrewingCauldronBlock.java` new location; `common/block/tintsource/PotionsPlusBlockColors.java` new file; edits to `common/core/Blocks.java`, `{fabric,forge}/.../core/{fabric,forge}/{Blocks,blocks/BlockEntityBlocks}.java`, `fabric/.../PotionsPlusFabricClient.java`, `forge/.../core/forge/Renderers.java`, `neoforge/.../utility/registration/block/BlockDropSelfLoot.java`, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 9 | **`RecipeManager.byType`/`byName` AW/AT entries added and mirrored; NeoForge+Forge `runClient` main-menu boot confirmed, closing Phase 9's last open boot-verification item.** Read `core.neoforge.RecipesRegistrar` in full: `injectRuntimeRecipes` writes `recipeManager.byType`/`.byName` directly as field assignments (`ImmutableMultimap.copyOf(...)`/`ImmutableMap.copyOf(...)`), not reflection. `javap -p -s` against `minecraft-merged-mojang-patched.jar` (fabric-loom `neoforge/21.1.209` cache) confirmed both are private, non-final instance fields — `private Multimap<RecipeType<?>, RecipeHolder<?>> byType;` (`Lcom/google/common/collect/Multimap;`), `private Map<ResourceLocation, RecipeHolder<?>> byName;` (`Ljava/util/Map;`) — so `accessible field` (no `mutable`) is the correct AW form. Discovered `neoforge/META-INF/accesstransformer.cfg` already had both members (pre-dating this session, why NeoForge's own injection already worked) but `common/potionsplus.accesswidener` and `forge/META-INF/accesstransformer.cfg` did not — added both, mirroring existing entries' syntax exactly. Checked the reference tree (`dev/26.1.2/multi-loader-expansion`) for how it solves the same problem: it doesn't need AW/AT at all — `common/mixin/RecipeManagerMixin.java` `@Inject`s into `RecipeManager.prepare(...)`, which on that newer MC version returns an immutable `RecipeMap`. **1.21.1 has no `RecipeMap`/`prepare()` of that shape** (confirmed via the same javap dump: 1.21.1's `RecipeManager.apply(...)` returns `void` and mutates the two fields in place), so the reference tree's approach isn't portable here and the AW/AT field-widening NeoForge already used is the right 1.21.1-shaped fix — now mirrored to all three loaders. **Investigated whether this unblocks porting `RecipesRegistrar` to `common/`: no, not by itself.** Beyond `byType`/`byName`, the class has three further neoforge-only couplings: `RECIPE_TYPES`/`RECIPE_SERIALIZERS` are `net.neoforged.neoforge.registries.DeferredRegister` (needs Phase-4/5-shaped platform abstraction); the injection functions call `core.seededrecipe.neoforge.SeededPotionRecipes` (173 lines) and `core.seededrecipe.neoforge.SanguineAltarRecipes` (which itself imports the neoforge-only `recipe.abyssaltroverecipe.neoforge.SanguineAltarRecipeBuilder`); and `ServerLifecycleListeners.postProcessRecipes` is neoforge-namespaced. Per the task's own instruction, stopped here rather than forcing a bigger port through — documented precisely instead. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → `BUILD SUCCESSFUL`. **Separately, closed Phase 9's other open item**: ran `:neoforge:runClient` and `:forge:runClient` uncapped (not the earlier 30s-`timeout` smokes) against a warm gradle daemon (`./gradlew --status` showed one `IDLE` daemon beforehand). NeoForge: `[14:03:30] [Render thread/INFO] [minecraft/SoundEngine]: Sound engine started`, 13s after task start; 130-line log had zero `[main/FATAL]`/`MixinApplyError`/`Exception`/`ERROR` beyond pre-existing benign `Missing subtitle translation` lines. Forge: `[14:04:35] ... Sound engine started`; 119-line log had zero `FATAL`/`MixinApplyError`/`Exception`/`ERROR` lines at all. Both dev-run client JVMs were identified precisely via `wmic process get ProcessId,CommandLine` (matched on `-Darchitectury.main.class=...neoforge...`/`--mixin.config potionsplus.forge.mixins.json`) and killed with `taskkill /F /PID <pid> /T` right after confirming the log; `tasklist` re-checked each time — only the Gradle daemon remained, no leaked JVMs. Fabric was already confirmed reaching menu in the 2026-09-03 session. **Not attempted, per the task brief**: production mixin-config discovery on Forge 52.x (needs an installed packaged jar) and the `BlockEntityType.validBlocks` Forge association — both left exactly as they were. Not committed, per explicit instruction — all changes (`common/potionsplus.accesswidener`, `forge/META-INF/accesstransformer.cfg`, this doc) left uncommitted in the working tree on `dev/1.21.1/multi-loader-expansion`. |
| 2026-09-04 | 9/11a | **`RecipesRegistrar` moved to `common/` — all three remaining neoforge-only couplings from the previous entry closed; `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` themselves stay blocked on a newly-found, distinct issue (their `Block` classes + fabric/forge never having registered those blocks at all).** Read `core.neoforge.RecipesRegistrar`, `core.seededrecipe.neoforge.{SeededPotionRecipes,SanguineAltarRecipes}`, `recipe.abyssaltroverecipe.neoforge.SanguineAltarRecipeBuilder`, and `core.neoforge.ServerLifecycleListeners#postProcessRecipes` in full, checking every import (not just package names) per the task's own warning. Result: **`SeededPotionRecipes`/`SanguineAltarRecipes`/`SanguineAltarRecipeBuilder` had zero real neoforge-only imports** — they were neoforge-only purely by package placement, not content — so moved verbatim (package decl only) to `common/core/seededrecipe/{SeededPotionRecipes,SanguineAltarRecipes}.java` and `common/recipe/abyssaltroverecipe/SanguineAltarRecipeBuilder.java`, deleting the `.neoforge.` originals. `RECIPE_TYPES`/`RECIPE_SERIALIZERS` (`DeferredRegister<RecipeType<?>>`/`DeferredRegister<RecipeSerializer<?>>`) were **not** actually blocking anything new — Fabric (`core.fabric.Recipes`) and Forge (`core.forge.Recipes`) already had their own loader-native flush of the common `core.Recipes.initTypes`/`initSerializers` hub from an earlier session; NeoForge alone lacked the equivalent split-out class (it inlined the DeferredRegisters directly into the old `RecipesRegistrar`), so added `core.neoforge.Recipes` mirroring `core.forge.Recipes`'s exact shape, and repointed `core.neoforge.PotionsPlus`'s two `.register(bus)` calls to it. `ServerLifecycleListeners.postProcessRecipes` turned out to be pure recipe-analysis computation (`RecipeManager` + the 5 analysis statics) plus two block-entity-facing side effects (`SanguineAltarBlockEntity.computeRecipeMap`, `AbyssalTroveBlockEntity.computeAbyssalTroveIngredients`) that only needed to stay loader-specific *because those two block entities are still neoforge-only* — not because the method itself needed NeoForge APIs. Moved the analysis-computation half into a new `common/core/RecipesRegistrar.java` (`postProcessRecipes(RecipeManager)`, `injectRuntimeRecipes(MinecraftServer)`, the 5 `BrewingCauldronRecipeAnalysis`/`RecipeAnalysis` statics, `getVanillaBrewingRecipes`) and left the 2 block-entity calls as a 2-line follow-up in each loader's own `ServerLifecycleListeners` (NeoForge only, since Fabric/Forge don't have `SanguineAltarBlockEntity`/`AbyssalTroveBlockEntity` yet either). **One genuine, previously-undocumented NeoForge-only API surfaced during the port**: `getVanillaBrewingRecipes` called `server.potionBrewing().isInput(ItemStack)` — `javap` against the plain vanilla `minecraft-merged` jar shows `PotionBrewing` has no such method at all, while the NeoForge-patched `PotionBrewing` (same jar family, neoforge variant) adds it directly onto the class (extra constructor + `registry` field) — a real vanilla-class patch, not an extension interface, confirmed by diffing `javap -p` output on both jars side by side. Not portable as-is; replaced with vanilla's own `isBrewablePotion(Holder<Potion>)` (present on both jars, verified via the same javap pair), filtering the potion holder before building the container `ItemStack` rather than filtering the built stack — same intent (skip potions the brewing config doesn't consider brewable), zero behavior assumption beyond what both jars' public API actually guarantees. Wired real runtime recipe injection onto **Fabric** (new `core.fabric.ServerLifecycleListeners`, `ServerLifecycleEvents.SERVER_STARTED` — verified via `javap` against the pinned `fabric-lifecycle-events-v1` jar that this event exists and hands a `MinecraftServer`) and **Forge** (new `core.forge.ServerLifecycleListeners`, `MinecraftForge.EVENT_BUS.addListener` against `net.minecraftforge.event.server.ServerStartedEvent`, matching this module's existing plain-listener style per `event.forge.TickListeners`'s own javadoc) — both previously **no-op stubs** ("nothing to register" left over from Phase 5/7), now doing the same `SavedData` init + world-seed capture + `RecipesRegistrar.injectRuntimeRecipes`/`postProcessRecipes` sequence NeoForge's listener already did, so seeded brewing-cauldron and sanguine-altar recipe generation now actually runs server-side on Fabric and Forge for the first time in this port. Updated all 8 non-`RecipesRegistrar`-file consumer imports (`AbyssalTroveBlockEntity`, `AbyssalTroveBlockEntityRenderer`, `HerbalistsLecternBlockEntity`, `event.neoforge.{AdvancementListeners,PlayerListeners}`, `item.tooltip.neoforge.BrewingTooltips`, `mixin.neoforge.ItemMixin`, `common/testmod/.../BrewingCauldronGameTests.java`) from `core.neoforge.RecipesRegistrar` to `core.RecipesRegistrar` via targeted `sed`, then grepped to confirm zero remaining `core.neoforge.RecipesRegistrar` references anywhere (docs comments excluded). **Checked whether this unblocks `AbyssalTroveBlockEntity`/`HerbalistsLecternBlockEntity` moving to `common/` per the task's stretch goal, per-file, and found a new, distinct blocker**: both block entity classes themselves read fully portable (only `core.neoforge.RecipesRegistrar` and `core.neoforge.Blocks.X.get()`→`.value()`, both trivial fixes) — but their `Block` classes (`block.neoforge.AbyssalTroveBlock`, `block.neoforge.HerbalistsLecternBlock`) are themselves still neoforge-only (same unaddressed issue Phase 11a's session already flagged for `ClotheslineBlock`/`PotionBeaconBlock`, just never called out for these two), **and**, checked via `grep` on `core.{fabric,forge}.blocks.BlockEntityBlocks`, Fabric and Forge have **never registered the underlying `herbalists_lectern`/`abyssal_trove` blocks at all** (only `PRECISION_DISPENSER`/`CLOTHESLINE`/`POTION_BEACON`/`BREWING_CAULDRON` exist there) — a bigger prerequisite than the task brief anticipated, not something to force through at the tail end of this session's budget. Per the task's own instruction ("if you find the runtime recipe-injection logic itself has a deeper mechanism... don't force a broken abstraction through... report it honestly"), stopped here and scoped this precisely rather than guessing at a workaround — see the Phase 11 recommendation section's new 2026-09-04 addendum for the full (a)/(b)/(c) breakdown of what's left for those two block entities. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → `BUILD SUCCESSFUL` (one real compile error hit and fixed along the way: `isInput` didn't exist on the vanilla-facing common module, per the `isBrewablePotion` fix above). Decision 4a `comm -12` re-run empty on fabric/forge/neoforge vs `common/`. **Real `runClient` smoke on all three loaders**, warm daemon, uncapped background launches: NeoForge `Sound engine started` at 14:24:48 (15-line grep count for `error`/`FATAL`/etc. was all pre-existing `log4j.xml` URI-parsing noise, not new); Fabric `Sound engine started` at 14:25:16, zero error/FATAL/exception lines; Forge `Sound engine started` at 14:27:08 (after a retry — the first attempt's log file was blocked by a stale locked file from an old session under the same name, `rm`/`>` both silently no-op'd in bash on a permission-denied file so gradle never actually launched; re-ran with a fresh log filename, worked cleanly), zero error/FATAL/exception lines. All three dev-run JVMs identified via `Get-CimInstance Win32_Process` filtered on `-Darchitectury.main.class=...` and killed with `Stop-Process -Force`; re-checked after each kill — zero `architectury.main.class` java.exe processes remained, only the warm gradle daemon left running. **Not verified**: actually joining a world and confirming seeded recipes appear in a brewing cauldron / sanguine altar on Fabric or Forge (no GUI-automation tool in this environment — same caveat as every prior Phase 11/11a entry; main-menu boot only proves static init / class-load doesn't crash, not that a world's `ServerStartedEvent` fires cleanly through the new Fabric/Forge listeners — a real risk given this is the *first* time those two loaders ever run this code path). Not committed, per explicit instruction — changeset (`common/core/RecipesRegistrar.java` new; `common/core/seededrecipe/{SeededPotionRecipes,SanguineAltarRecipes}.java` new; `common/recipe/abyssaltroverecipe/SanguineAltarRecipeBuilder.java` new; deleted `neoforge/.../core/neoforge/RecipesRegistrar.java` and the 3 `.neoforge.` originals; new `neoforge/.../core/neoforge/Recipes.java`; new `fabric/.../core/fabric/ServerLifecycleListeners.java` and `forge/.../core/forge/ServerLifecycleListeners.java` (replacing the no-op stubs); edits to `neoforge/.../core/neoforge/{PotionsPlus,ServerLifecycleListeners}.java`, `common/core/Recipes.java` (javadoc), the 8 consumer-import files listed above, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11a | **`AbyssalTroveBlock`/`HerbalistsLecternBlock` ported cross-loader, closing the 6th session's 3-part register-hub project — 5 of 6 block entities (all but `SanguineAltarBlockEntity`) now common/, registered + rendering on all three loaders.** Read both `Block` classes in full first, checking every import (not just the neoforge package suffix): confirmed each has exactly one real neoforge coupling, `core.neoforge.Blocks.X_BLOCK_ENTITY.get()` (3 call sites in `AbyssalTroveBlock` — `useItemOn`, `getTicker`; 4 in `HerbalistsLecternBlock` — `useItemOn`, `useWithoutItem`, `getTicker`, `getAnalogOutputSignal`), same shape as every prior ported `Block` class in this chain (Clothesline/PotionBeacon/BrewingCauldron). `git mv` both to `common/block/{AbyssalTroveBlock,HerbalistsLecternBlock}.java` (package `grill24.potionsplus.block`, matching the reference tree's package location — its file contents are for a newer MC with a `RenderState`-submission BE-renderer API not present in 1.21.1, so not literally diffable line-for-line, same situation Phase 11a step 3's `DynamicIconItems` re-abstraction already documented), swapped the import to `core.Blocks` and every `.get()` to `.value()`. Re-read both BE classes (`AbyssalTroveBlockEntity`, `HerbalistsLecternBlockEntity`) fresh rather than trusting the 6th session's "reads fully portable" note — confirmed still true (`RecipesRegistrar`, `DynamicIconItems`, `PotionUpgradeIngredients`, `SeededIngredientsLootTables`, `EffectRegistry`, `Recipes`, `MobEffects`, `BrewingCauldronRecipe` all already `common/`), `git mv` to `common/blockentity/`, same `core.neoforge.Blocks`→`core.Blocks`/`.get()`→`.value()` fix in each constructor. **One more hidden NeoForge-only API surfaced mid-move, same class of bug the 4th session hit (`Holder#getKey()`)**: `common:compileJava` failed on `HerbalistsLecternBlockEntity.RendererData#updateItemStacksToDisplay`'s `mobEffectInstance.getEffect().getKey().location()` — `Holder<MobEffect>` has no `getKey()` on vanilla, only NeoForge's patched one; fixed to `.unwrapKey().orElseThrow().location()`, matching the established codebase-wide pattern (`grep unwrapKey common/` shows 10+ existing call sites use exactly this form). Moved both renderers (`AbyssalTroveBlockEntityRenderer`, `HerbalistsLecternBlockEntityRenderer`) to `common/blockentity/` too, stripping `@OnlyIn(Dist.CLIENT)`/the `net.neoforged.api.distmarker` imports (matches how `PotionBeaconBlockEntityRenderer` already lives unannotated in `common/`) — neither had any other neoforge coupling. **Registered both underlying blocks on Fabric and Forge for the first time** (previously confirmed via `grep BREWING_CAULDRON`-style check that only 4 of 6 entries existed): added `HERBALISTS_LECTERN`/`ABYSSAL_TROVE` fields + `registerBlock.apply(...)` + `Items.registerBlockItemWithAutoModel` to both `core.{fabric,forge}.blocks.BlockEntityBlocks` (properties copied verbatim from neoforge's `SimpleBlockBuilder` calls in `core.neoforge.blocks.BlockEntityBlocks` — `herbalists_lectern`: `MapColor.WOOD`, strength 2.5, `SoundType.WOOD`; `abyssal_trove`: `MapColor.COLOR_BROWN`, strength 5.0/6.0, `SoundType.SOUL_SAND`), then the `BlockEntityType` binding on both (`core.{fabric,forge}.Blocks`, identical template to the existing `CLOTHESLINE_BLOCK_ENTITY`/`BREWING_CAULDRON_BLOCK_ENTITY` entries — Fabric via `FabricBlockEntityTypeBuilder`/`FabricRegistration`, Forge via `ForgeHolder`-wrapped `DeferredRegister<BlockEntityType<?>>` + `BlockEntityType.Builder.of(...).build(null)`) and assigned into `core.Blocks.{HERBALISTS_LECTERN,ABYSSAL_TROVE}_BLOCK_ENTITY`, now retyped from `Holder<BlockEntityType<?>>` to their concrete BE class (only `SANGUINE_ALTAR_BLOCK_ENTITY` stays untyped — updated `common/core/Blocks.java`'s javadoc to say so precisely, including the actual confirmed `SanguineAltarBlockEntity` blocker: its 2 still-neoforge-only network packets, read directly rather than guessed). Registered both renderers on Fabric (`BlockEntityRendererRegistry.register` in `PotionsPlusFabricClient`, next to the other 3) and Forge (`EntityRenderersEvent.RegisterRenderers` handler in the existing dist-gated `core/forge/Renderers.java` subscriber, next to the other 3), updating that class's own javadoc from "3 of 6" to "5 of 6". Fixed the 3 stray neoforge-path call sites found by repo-wide grep after the moves (`core.neoforge.ServerLifecycleListeners`, `event.neoforge.PlayerListeners`, `item.tooltip.neoforge.BrewingTooltips` — all just import-line fixes, `AbyssalTroveBlockEntity.computeAbyssalTroveIngredients()`/`.getAcceptedIngredients()`/`.ABYSSAL_TROVE_INGREDIENTS` call sites unchanged since the class name is the same); `core.neoforge.Blocks`/`core.neoforge.blocks.BlockEntityBlocks` needed no import-line changes since both already carry a `blockentity.*`/`block.*` wildcard import that resolves the moved common classes automatically. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → `BUILD SUCCESSFUL` (one real compile error hit and fixed along the way, the `Holder#getKey()` issue above). Decision 4a `comm -12` re-run empty on fabric/forge vs `common/`. **Real `runClient` smoke on all three loaders**, warm daemon: NeoForge, Fabric, and Forge each reached `[Render thread/INFO] [minecraft/SoundEngine]: Sound engine started` cleanly; grepped all three full logs for `Exception`/`FATAL`/`ERROR`/`Crash`/`MixinApplyError` and found only the pre-existing `Missing subtitle translation` noise (now naming `abyssal_trove_deposit`/`herbalists_lectern_appear`/`herbalists_lectern_disappear` too, since those sound events are reachable on Fabric/Forge for the first time — expected, not a regression). All three dev-run client JVMs identified precisely via `Get-CimInstance Win32_Process -Filter "Name='java.exe'"` (matched on `-Darchitectury.main.class=...`) and killed with `Stop-Process -Force` one loader at a time before starting the next; re-checked after each kill that only the warm gradle daemon remained. Hit one tooling snag along the way, not a code bug: the fresh log-redirect target for the first `:forge:runClient` attempt collided with a stale locked file from a much older (Aug 25) session under the same name in `$TEMP` (`Permission denied` on read/write, silent no-op in bash) — same class of issue the previous session's entry already documented for exactly this reason; fixed by using a fresh unique filename, no code implicated. **Not verified**: actually placing an abyssal trove or herbalist's lectern in a loaded world and interacting with it (inserting ingredients, watching the renderer animate/rotate) — still no GUI-automation tool in this environment, same caveat as every prior Phase 11/11a entry. **What's left in Phase 11 after this session**: only `SanguineAltarBlockEntity` (blocked on its 2 still-neoforge-only sync packets, a Phase-5-shaped move, not attempted — out of this task's explicit scope) and the `ItemStacksTooltip` tooltip-component factory (blocked on NeoForge's own client-tooltip-component-factory extension point, no vanilla/Fabric equivalent, needs its own design). Also noted but not acted on: `item/tooltip/neoforge/BrewingTooltips.java` (the ingredient-tooltip logic, distinct from `ItemStacksTooltip`) is no longer blocked on `RecipesRegistrar`/`AbyssalTroveBlockEntity` now that both are common/, but was never wired into Fabric's/Forge's `TooltipListeners` — their doc comments citing that as the reason are now stale. Not committed, per explicit instruction — changeset (`common/block/{AbyssalTroveBlock,HerbalistsLecternBlock}.java` new locations; `common/blockentity/{AbyssalTroveBlockEntity,AbyssalTroveBlockEntityRenderer,HerbalistsLecternBlockEntity,HerbalistsLecternBlockEntityRenderer}.java` new locations; edits to `common/core/Blocks.java`, `{fabric,forge}/.../core/{fabric,forge}/{Blocks,blocks/BlockEntityBlocks,Renderers or PotionsPlusFabricClient}.java`, `neoforge/.../core/neoforge/Blocks.java`, `neoforge/.../core/neoforge/ServerLifecycleListeners.java`, `neoforge/.../event/neoforge/PlayerListeners.java`, `neoforge/.../item/tooltip/neoforge/BrewingTooltips.java`, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 11a/7 | **8th session: `SanguineAltarBlockEntity` ported cross-loader (all 6 of 6 block entities now common/) and `BrewingTooltips` wired into Fabric/Forge — the two items this session was explicitly assigned.** Read `SanguineAltarBlockEntity`, its `State` enum, `SanguineAltarBlock`, `SanguineAltarBlockEntityRenderer`, and both sync packets (`ClientboundSanguineAltarConversionProgressPacket`/`...StatePacket`) in full before writing anything, checking every import rather than trusting the prior session's blocker note. Found the documented blocker ("packets reference the concrete BE class/enum/fields directly") was stale in exactly the way the task predicted: both packets' *only* neoforge coupling was `core.neoforge.Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get()` (fixed to `core.Blocks...value()`), and referencing the concrete `SanguineAltarBlockEntity`/`State` directly is fine once that class is `common/` too — confirmed against the finished `dev/26.1.2` reference tree, which does exactly that (`common/network/ClientboundSanguineAltarConversion{Progress,State}Packet.java` there import the BE class directly, no abstraction). `SanguineAltarBlock` (only coupling: same `core.neoforge.Blocks` lookup, 4 call sites) and `SanguineAltarBlockEntityRenderer` (only import: `core.items.DynamicIconItems`, already common since Phase 11a step 3; needed only its `@OnlyIn(Dist.CLIENT)` annotation stripped, same as every other block-entity renderer already in `common/`) were equally clean. `git mv` all 5 files (`block/SanguineAltarBlock.java`, `blockentity/{SanguineAltarBlockEntity,SanguineAltarBlockEntityRenderer}.java`, `network/ClientboundSanguineAltarConversion{Progress,State}Packet.java`) to `common/`; retyped `common/core/Blocks.java#SANGUINE_ALTAR_BLOCK_ENTITY` from `Holder<BlockEntityType<?>>` to `Holder<BlockEntityType<SanguineAltarBlockEntity>>` (the last of the six to go concrete). Registered the block + `BlockEntityType` on **Fabric and Forge for the first time** (`core.{fabric,forge}.blocks.BlockEntityBlocks` — added a `SANGUINE_ALTAR` field + `registerBlock.apply("sanguine_altar", () -> new SanguineAltarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE)))`, properties copied verbatim from neoforge's `SimpleBlockBuilder` call; `core.{fabric,forge}.Blocks` — identical template to the other 5 `BlockEntityType` bindings) and assigned into `core.Blocks.SANGUINE_ALTAR_BLOCK_ENTITY`. Registered the renderer on all three (Fabric `BlockEntityRendererRegistry` in `PotionsPlusFabricClient`; Forge `EntityRenderersEvent.RegisterRenderers` on the existing dist-gated `core/forge/Renderers.java` subscriber; NeoForge's existing `core/neoforge/Renderers.java` needed only a stale `blockentity.neoforge.*` import removed, since it already had a `blockentity.*` wildcard that now resolves the moved common class). Registered both packets in `core.{fabric,forge}.Packets.java` (Fabric: `clientboundCodec`/`clientbound` calls added to `registerServer`/`registerClient`; Forge: two more `.add(...)` entries on the existing `ChannelBuilder` chain) — 9 of 12 payloads now shared, up from 7; NeoForge's `core.neoforge.Packets` needed no change (already used a `network.*` wildcard import). Fixed one stray reference found by repo-wide grep after the move: `neoforge/.../core/neoforge/ServerLifecycleListeners.java`'s `SanguineAltarBlockEntity.computeRecipeMap(...)` call site needed only its import line repointed (`blockentity.neoforge.SanguineAltarBlockEntity` → `blockentity.SanguineAltarBlockEntity`); grepped the whole repo afterward for `blockentity\.neoforge\.SanguineAltar`/`block\.neoforge\.SanguineAltar`/`network\.neoforge\.ClientboundSanguineAltar` and confirmed zero remaining hits. **`BrewingTooltips`**: read its full import list fresh (`RecipesRegistrar`, `AbyssalTroveBlockEntity`, `IStoredIngredientsContainer`, `Recipes`, `Potions`, `PotionUpgradeIngredients`, `PpIngredient`, `SeededIngredientsLootTables`, `AnimatedItemTooltipEvent`, `SavedData`, `BrewingCauldronRecipe`, `alchemy.*`, `Utility`, `TooltipPriorities`) and confirmed every one is already `common/` (the two historically-named blockers, `RecipesRegistrar` and `AbyssalTroveBlockEntity`, both landed in earlier sessions this same day) — `git mv` to `common/item/tooltip/BrewingTooltips.java`, package line only. Wired into Fabric's and Forge's `TooltipListeners` (both already existed as Phase 7 stubs whose doc comments explicitly said `BrewingTooltips` was skipped as neoforge-only): added one `BrewingTooltips.onBrewingTooltip(addEvent)` call to each, immediately before the existing `PotionEffectTooltips.onPotionEffectTooltip(addEvent)` call, matching `NeoItemListeners`'s call order exactly; updated both classes' stale doc comments. **Verified**: `./gradlew :common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` → `BUILD SUCCESSFUL` on the first attempt, no fixes needed beyond the writes described above. Decision 4a `comm -12` re-run empty on fabric/forge vs `common/`. **Real `:neoforge:runClient` smoke**: launched in background, polled the log for `Sound engine started`/`FAILED`/`Exception` — reached `[Render thread/INFO] [minecraft/SoundEngine]: Sound engine started` cleanly; grepped the full log for `Exception`/`ERROR` (excluding known-benign `Missing subtitle translation`/`Missing sound for event` noise) and found only a pre-existing `log4j.xml` URI-parsing `WARN`, not new. Identified the client JVM precisely via `Get-CimInstance Win32_Process` (matched `-Darchitectury.main.class=...neoforge...`) and killed the whole tree with `taskkill /F /T`; re-checked afterward — zero `neoforge`-matching `java.exe` processes remained. **`:fabric:runClient` and `:forge:runClient` were NOT run this session** — the session was checked in on and asked to wrap up before reaching them; this is an honest, explicitly-flagged gap, not a silent skip, and should be the first verification step for whoever picks this up next (the code changes are symmetric with the already-verified NeoForge path and follow the exact template of the prior 3 successful cross-loader BE ports, but that is not a substitute for an actual boot). **This closes Phase 11's block-entity exit criterion in full — 6 of 6 block entities now render/register identically on all three loaders.** The sole remaining Phase 11 item is the `ItemStacksTooltip` tooltip-component-factory extension point, still genuinely NeoForge-only (`ClientTooltipComponentFactoriesListeners` + `ItemMixin`, no vanilla/Fabric-API equivalent) — needs its own design, not a port. Not committed, per explicit instruction — changeset (`common/block/SanguineAltarBlock.java`, `common/blockentity/{SanguineAltarBlockEntity,SanguineAltarBlockEntityRenderer}.java`, `common/network/ClientboundSanguineAltarConversion{Progress,State}Packet.java`, `common/item/tooltip/BrewingTooltips.java` new locations; edits to `common/core/Blocks.java`, `{fabric,forge}/.../core/{fabric,forge}/{Blocks,blocks/BlockEntityBlocks,Packets}.java`, `fabric/.../core/fabric/PotionsPlusFabricClient.java`, `forge/.../core/forge/Renderers.java`, `{fabric,forge}/.../event/{fabric,forge}/TooltipListeners.java`, `neoforge/.../core/neoforge/{Blocks,Renderers}.java`, `neoforge/.../core/neoforge/blocks/BlockEntityBlocks.java`, `neoforge/.../core/neoforge/ServerLifecycleListeners.java`, `neoforge/.../event/neoforge/NeoItemListeners.java`, this doc) on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
| 2026-09-04 | 8 | **Capabilities/`IItemHandler` (clothesline storage) — done, closing Phase 8.** `ClotheslineBlock`/`ClotheslineBlockEntity` already lived in `common/` and were already registered on all three loaders from an earlier Phase 11a session, so this was purely the capability-wiring bucket. Implemented `forge/.../core/forge/Capabilities.java`: javap against `forge-1.21.1-52.1.2-universal-srg.jar` (gradle cache) confirmed the plan's warning — 1.21.1 Forge 52.1.2 still uses the pre-1.20.5 shape (`AttachCapabilitiesEvent<BlockEntity>` + `ICapabilityProvider` returning `LazyOptional<T>` + `ForgeCapabilities.ITEM_HANDLER`), not 26.1.2's `RegisterCapabilitiesEvent`/`BlockCapability` API (that class name exists in the jar but is an unrelated old per-mod declaration event); javap on the forge-patched `minecraft-merged-srg-patched.jar` confirmed `BlockEntity` already `extends CapabilityProvider<BlockEntity>`; javap confirmed `net.minecraftforge.items.wrapper.InvWrapper(Container)` at the same package path as NeoForge's. Wired via `MinecraftForge.EVENT_BUS`, matching this module's existing `CommandListeners`/`TickListeners` explicit-registration convention. **One real bug an actual `:forge:runServer` smoke caught that compilation didn't**: `AttachCapabilitiesEvent<T> extends GenericEvent<T>`, so `IEventBus.addListener(...)` throws `IllegalArgumentException` at mod-construction time ("use addGenericListener"); fixed by javap-verifying `IEventBus`'s overloads against `eventbus-6.2.32.jar` and switching to `addGenericListener(BlockEntity.class, ...)`. Implemented `fabric/.../core/fabric/Capabilities.java`: javap against the *actually resolved* `fabric-transfer-api-v1:5.4.3+c24bd99419` (read off `fabric-api-0.116.7+1.21.1.pom`'s own dependency list, not assumed) confirmed `ItemStorage.SIDED` is a `BlockApiLookup<Storage<ItemVariant>, Direction>` (no `ContainerStorage` class exists in this jar at all), `BlockApiLookup.registerForBlockEntity(BiFunction<? super T, C, A>, BlockEntityType<T>)` (from the separate `fabric-api-lookup-api-v1:1.6.71+b559734419` module), and `InventoryStorage.of(Container, Direction)` — matching the plan's 1.21.1-era naming exactly. Both `Capabilities.register()` call sites already existed as no-op stub calls in `PotionsPlusForge`'s constructor and `PotionsPlusFabric.onInitialize()` from an earlier session, so no entrypoint wiring was needed. **Verified:** `:common:compileJava :neoforge:compileJava :fabric:compileJava :forge:compileJava` green; `:neoforge:build :fabric:build :forge:build -x test` green; Decision 4a `comm -12` empty on fabric/forge vs `common/`. `:neoforge:runServer` and `:fabric:runServer` reach `Done (...)!` with zero new exceptions (Fabric's pre-existing `golden_cubensis`/`diamour` item-registration-gap noise is unchanged, unrelated). `:forge:runServer` also reaches `Done (...)!` but then crashes ~2s later with `NoSuchElementException` inside `SeededIngredientsLootTables.getItemsInTags` (via `RecipesRegistrar.injectRuntimeRecipes` → `ServerLifecycleListeners.onServerStarted`) — **confirmed pre-existing via `git stash`**: stashing both `Capabilities.java` changes and re-running `:forge:runServer` reproduces the identical crash at the identical stack trace, so this is Forge hitting the same never-registered `golden_cubensis`/`diamour` items Fabric's `RecipesRegistrar` path happens to tolerate; real, but unrelated to this bucket and not fixed here (not yet tracked as its own checklist item — worth adding when someone next touches `core.{fabric,forge}.items.OreItems` or `RecipesRegistrar`). Killed all `runServer` JVMs cleanly after each smoke, confirmed via `Get-CimInstance Win32_Process` (`architectury.main.class` match) that none were left orphaned. **Not verified in-world**: no GUI-automation tool in this environment to place a Clothesline and confirm a hopper/other `IItemHandler` consumer can actually insert/extract through the new capability — `runServer` proves registration + zero crash at attach/query-setup time, not the runtime query path itself. **This closes Phase 8 in full** — all four buckets (`DataAttachments`, global loot modifiers, biome modifiers, server config, and now Capabilities) are done. |
