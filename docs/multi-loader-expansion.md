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
| 4 | Registration hubs (Fabric + Forge) | ⬜ not started |
| 5 | `@ExpectPlatform` impls + networking | ⬜ not started |
| 6 | Entrypoints | ⬜ not started |
| 7 | Event surface (36 subscriber classes) | ⬜ not started |
| 8 | NeoForge-only systems (full parity) | ⬜ not started |
| 9 | Mixins + access widening/transformers | ⬜ not started |
| 10 | Datagen sharing | ⬜ not started |
| 11 | Client (renderers, particles, tooltips, colors, models, JEI ×3) | ⬜ not started |
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
- `CreativeModeTab.builder()` (no-arg) **and** `builder(Row, int)` both exist in vanilla.
  *(26.1.2's Fabric/Forge tab rebuild is not needed.)* `withSearchBar()` is still a NeoForge patch —
  verify before relying on it off-NeoForge.
- `Registry.holders()` → `Stream<Holder.Reference<T>>` **exists**. *(26.1.2 removed it and forced
  `entrySet()`.)*
- `Registry.registerForHolder(Registry<T>, ResourceKey<T>, T)` / `(…, ResourceLocation, T)` →
  `Holder.Reference<T>` — the Fabric registration path, same as 26.1.2.
- **`SimpleParticleType(boolean)` is `protected` in vanilla** but **`public` in the Forge-patched
  jar.** Same trap as 26.1.2 → Fabric needs `new SimpleParticleType(false) {}` (anonymous subclass)
  or an access-widener entry. Decide in Phase 9 which; prefer matching 26.1.2's anonymous-subclass
  choice for diffability.
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
      `((FabricBlockEntityType) BlockEntityType.DISPENSER).addValidBlock(block)`; Forge equivalent
      to be identified.
- [ ] `SimpleParticleType` on Fabric: `new SimpleParticleType(false) {}` (ctor is `protected` in
      vanilla — verified) unless Phase 9's access-widener covers it.
- [ ] Creative tab: vanilla `CreativeModeTab.builder()` works on all three in 1.21.1. NeoForge keeps
      `BuildCreativeModeTabContentsEvent`; Fabric/Forge populate via a `displayItems` lambda
      iterating `BuiltInRegistries.ITEM.holders()` filtered by namespace.
- [ ] Minimal `{fabric,forge}/.../platform/*/PlatformImpl.java` with at least `isClient` /
      `isDevelopmentEnvironment` — `AbstractRegistererBuilder.modelGenerator()` calls
      `Platform.isClient()` during hub class-load. Rest stubbed until Phase 5/8.

**Exit criterion:** NeoForge build still green; Fabric + Forge compile and load a world with blocks,
items, potions and effects present.

---

## Phase 5 — `@ExpectPlatform` impls + networking

*(= 26.1.2 Phase 2.)*

- [ ] `fabric/.../platform/fabric/PlatformImpl.java` — full implementations
      (`FabricLoader.getInstance().getEnvironmentType()`, `isDevelopmentEnvironment()`, etc.).
- [ ] `forge/.../platform/forge/PlatformImpl.java` — same (`FMLEnvironment.dist`,
      `!FMLEnvironment.production`). **Note 1.21.1 uses the field form `FMLEnvironment.dist`, not
      26.1.2's `FMLEnvironment.getDist()`** — verify.
- [ ] `fabric/.../platform/fabric/PacketNetworkImpl.java` — `ServerPlayNetworking.send`,
      `ClientPlayNetworking.send`, `PlayerLookup.tracking(ServerLevel, ChunkPos)` / `.tracking(Entity)`.
- [ ] `forge/.../platform/forge/PacketNetworkImpl.java` — `Channel.send(payload,
      PacketDistributor.X)`. `payloadChannel()` confirmed present on Forge 52.1.2.
- [ ] `fabric/.../network/fabric/FabricPacketContext` + `forge/.../network/forge/ForgePacketContext`
      implementing common `network/PacketContext`. **Package confirmed against the 26.1.2 tree —
      these live under `network/<loader>/`, not `platform/<loader>/`** (see Phase 2).
- [ ] `fabric/.../core/fabric/Packets.java` (`registerServer()` + `registerClient()`) and
      `forge/.../core/forge/Packets.java`, mirroring `core/Packets.java`'s 12 payloads. Both
      confirmed present in the 26.1.2 tree, alongside `core/neoforge/Packets.java`.
- [ ] **Carry 26.1.2's codec-side lesson:** a payload's codec must be registered on the side that
      *sends* it **and** the side that *receives* it. Fabric's `PayloadTypeRegistry.register` throws
      `IllegalArgumentException` on duplicate → wrap client-side re-registration in try/catch for the
      integrated server. Register the serverbound codec client-side too, or
      `ServerboundConstructClotheslinePacket` breaks on a dedicated client.
- [ ] Forge buffer-type narrowing: `NetworkProtocol.PLAY` is `NetworkProtocol<RegistryFriendlyByteBuf>`;
      packets declaring `StreamCodec<ByteBuf, MSG>` need a `playCodec(…)` cast helper (26.1.2 pattern).
- [ ] **Every Forge handler must call `ctx.setPacketHandled(true)` after `enqueueWork(…)`.** This is
      the single highest-value gotcha in this plan. Without it, Forge dispatches the payload
      *correctly*, then falls through and fires `CustomPayloadEvent.BUS` a second time on the same
      event, re-reading a buffer already consumed → `IndexOutOfBoundsException` and an immediate
      client disconnect on the **first** custom payload of a world join. 26.1.2 burned two commits
      here, the first misdiagnosing it as an empty-payload decode bug.
- [ ] Check how this branch's **injected runtime recipes** reach the client on each loader. On 26.1.2
      only NeoForge had a built-in path, and Fabric/Forge needed a whole new
      `ClientboundSyncRuntimeRecipesPacket` — batched at 64 recipes to stay under the **1 MiB payload
      cap**, post-processing deferred to the final batch, no-op on integrated servers.

**Exit criterion:** all three modules build; a packet round-trips on each loader. **Verify by
creating a new world on each loader**, not by building — 26.1.2's networking bugs all appeared on
the first world join and none of them were visible at build time.

---

## Phase 6 — Entrypoints

*(= 26.1.2 Phase 3.)*

- [ ] `fabric/.../core/fabric/PotionsPlusFabric.java` (`ModInitializer`) +
      `PotionsPlusFabricClient.java` (`ClientModInitializer`) replicating `core/PotionsPlus.java` and
      `core/PotionsPlusClient.java`; registration is immediate, no deferred flush.
- [ ] `forge/.../core/forge/PotionsPlusForge.java` (`@Mod` — `net.minecraftforge.fml.common.Mod`) —
      same wiring, then `DR.register(modEventBus)` for every `DeferredRegister`.
- [ ] **Do NOT create a second `@Mod` class for the client. Re-verified 2026-09-01:** the 26.1.2
      tree's `core/forge/` contains `PotionsPlusForge` and **no** `PotionsPlusForgeClient`, while
      `core/fabric/` has both `PotionsPlusFabric` and `PotionsPlusFabricClient` and `core/neoforge/`
      has both `PotionsPlus` and `PotionsPlusClient`. Forge is the odd one out, by necessity, not
      oversight. 26.1.2 wrote a `PotionsPlusForgeClient`
      mirroring NeoForge's entrypoint split; **Forge dedups `@Mod` classes by modid, first wins, and
      silently dropped it** — the client wiring never ran, and `f5cd94d` deleted the class outright.
      Do client wiring from the single `@Mod` class, dist-gated, and see the Phase 11 timing note
      before choosing *where* in that constructor it goes.
- [ ] Verify what Forge 52.x's `@Mod` constructor actually accepts before writing it (26.1.2 hit
      `NoSuchMethodException: PotionsPlusForge.<init>()` because 26.1.2's Forge injects
      `FMLJavaModLoadingContext` and had removed `ModLoadingContext.get()`; 52.x is an older
      generation and may differ again).
- [ ] `fabric.mod.json` + `META-INF/mods.toml` filled in properly (entrypoints, mixin configs,
      access widener, dependencies, icon).

**Exit criterion:** all three loaders reach the main menu and load a world.

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

---

## Phase 8 — NeoForge-only systems (full parity)

*(= 26.1.2 Phase 5, **plus `DataAttachments`**.)* Decision 2 is full parity — reimplement, don't stub.

- [ ] **Global loot modifiers** (`core/LootModifiers`, `loot/*`, 3× `IGlobalLootModifier`):
  - [ ] Forge: Forge's own `net.minecraftforge.common.loot.{IGlobalLootModifier,LootModifier}` —
        near-verbatim port. **Forge additionally requires a hand-written
        `data/forge/loot_modifiers/global_loot_modifiers.json` enable/order index** that NeoForge has
        no equivalent of.
  - [ ] Fabric: `fabric-loot-api-v3` `LootTableEvents.MODIFY_DROPS` (closer to `doApply` than
        `MODIFY`, which only edits table structure at load time).
- [ ] **Capabilities / `IItemHandler`** (`core/Capabilities`, clothesline storage):
  - [ ] Forge: `AttachCapabilitiesEvent.BlockEntities` + `ICapabilityProvider` + `InvWrapper`.
        **1.21.1 Forge still uses the pre-1.20.5 capability-provider shape** — do not assume 26.1.2's
        API; verify against the 52.1.2 jar.
  - [ ] Fabric: `fabric-transfer-api-v1` `ItemStorage.SIDED.registerForBlockEntity` +
        `InventoryStorage.of(...)` (1.21.1-era name; 26.1.2 used `ContainerStorage.of`).
- [ ] **`core/DataAttachments` — delete it; do not abstract it.** *Rewritten 2026-09-01 after
      checking the 26.1.2 tree.* The previous draft of this bullet designed a common
      `PlayerDataStore` interface with three per-loader implementations. **That is not what 26.1.2
      did.** `grep -rl "AttachmentType\|DataAttachments"` over the entire 26.1.2 tree returns
      **nothing** — it carries no attachment abstraction because it has no attachments. Per-player
      state there lives in `common/.../persistence/SavedData.java`, a plain
      `net.minecraft.world.level.saveddata.SavedData` subclass keyed by player UUID
      (`getData(Player)` / `getData(UUID)`), which is pure vanilla and needs no platform hook at all.
  - [ ] **1.21.1 already has that exact class** — `common/.../persistence/SavedData.java`, already in
        `common/` since Phase 1, already vanilla-based. Nothing to build.
  - [ ] The whole surface to migrate is **one** attachment: `LAST_POTION_USE_PLAYER_DATA`
        (`AttachmentType<LastPotionUsePlayerData>` on `NeoForgeRegistries.ATTACHMENT_TYPES`).
        Move `LastPotionUsePlayerData` onto `SavedData` the way `PlayerBrewingKnowledge` already is,
        then delete `neoforge/.../core/DataAttachments.java`.
  - [ ] Call sites to update (`grep -rl DataAttachments`): `core/neoforge/PotionsPlus.java`,
        `mixin/EntityMixin.java`, `mixin/PotionItemMixin.java`.
  - [ ] Net effect: **`DataAttachments` stops being a parity problem** — there is no NeoForge-only
        system left to reimplement on Fabric and Forge, so Decision 2's "plus `DataAttachments`"
        carve-out disappears and this phase matches 26.1.2 Phase 5 exactly.
- [ ] **Server config** (`config/`, `ModConfigSpec`, feeding `Platform.getPotionDrinkTimeTicks` /
      `getPotionDrinkCooldownTimeTicks`):
  - [ ] Forge: `net.minecraftforge.common.ForgeConfigSpec` + `ModLoadingContext.get()
        .registerConfig(…)` in the `@Mod` constructor.
  - [ ] Fabric: no config API in fabric-api — hand-roll a small JSON config under
        `FabricLoader.getInstance().getConfigDir()`, matching 26.1.2's approach.
- [ ] **Biome modifiers have equivalents on both other loaders — verified 2026-09-01.** The note
      below that NeoForge's `BiomeModifier` registry "has no Fabric or Forge equivalent" is wrong in
      both directions:
  - [ ] **Forge reads the same datapack JSON.** 26.1.2 ships hand-authored
        `forge/src/main/resources/data/potionsplus/forge/biome_modifier/{add_lunar_berry_bush_patch,
        remove_berry_bush_patch}.json` — the NeoForge files with the namespace directory renamed
        `neoforge/` → `forge/`. No code required.
  - [ ] **Fabric has a code-only API.** `fabric/.../core/fabric/BiomeModifiers.java` uses
        `BiomeModifications.create(...)` + `BiomeSelectors.tag(...)` + `ModificationPhase.REMOVALS`
        with `ctx.getGenerationSettings().removeFeature(...)`. There is no Fabric JSON equivalent,
        which is exactly why Phase 10's `commonDatagen` excludes `**/neoforge/**`.
- [ ] **Worldgen / biome modifiers** — if any survive backport Phase 1.8 (`core/Features`,
      `core/PlacementModifierTypes`, `core/blocks/OreBlocks` are still present at tip; confirm what
      Phase 1.8 actually left). NeoForge's `BiomeModifier` datapack registry has no Fabric or Forge
      equivalent → Fabric `BiomeModifications` API, Forge hand-written biome-modifier equivalent.
      **`data/potionsplus/neoforge/biome_modifier/*.json` must never reach the Fabric/Forge jars** —
      26.1.2 shipped exactly that leak once (see its Phase 7).

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

- [ ] Split `potionsplus.mixins.json` into `common` + `potionsplus.{fabric,forge,neoforge}.mixins.json`
      by target. **`compatibilityLevel` stays `JAVA_21`** (26.1.2's `JAVA_25`-not-recognised crash on
      Forge's bundled Sponge Mixin does not apply here).
- [ ] **Wire the common config into all three loaders.** 26.1.2 got this wrong twice — its Phase 6
      claimed the fix had landed and its Phase 9 found it still broken. Verify explicitly that
      `fabric.mod.json`'s `"mixins"` array **and** `META-INF/mods.toml`'s `[[mixins]]` **and**
      `neoforge.mods.toml` each list `potionsplus.mixins.json`, not just their platform config.
- [ ] **Refmaps.** New vs 26.1.2 entirely, and the one place its fix **inverts**: `243ac95` deleted the
      `"refmap"` declaration because unobfuscated 26.1.2 never generates one. **1.21.1 is obfuscated —
      the refmap is required and must actually be produced.** Set `loom { mixin { defaultRefmapName =
      "potionsplus-refmap.json" } }` in **every** module, keep the declaration in each config, and
      confirm each built jar really contains the file. What transfers from 26.1.2 is the symptom:
      **a refmap mismatch is invisible in dev and silently drops every mixin in production.**
- [ ] **Pass `--mixin.config` explicitly on every Forge loom run.**
      `MixinPlatformAgentMinecraftForge` ignores `mods.toml`'s `[[mixins]]` for exploded-directory
      containers, so on 26.1.2 **no Forge mixin was active in dev for the entire project** until
      `00c39e0` found it. Verify a Forge mixin actually fires the day you add the first one — don't
      infer it from a green build.
- [ ] **Production mixin discovery is a separate problem from dev discovery.** Forge 64.1.0 had no
      `[[mixins]]` parsing in production at all, and the only working path was the **`MixinConfigs`
      manifest attribute** on the jar. Forge 52.x may still parse `[[mixins]]` — **determine this by
      installing a packaged jar and checking behaviour**, not from documentation. On 26.1.2 this failed
      silently in production while every dev run passed, and it shipped.
- [ ] `compatibilityLevel` stays `JAVA_21`, which stock `org.spongepowered:mixin:0.8.x` accepts.
      Expect a harmless `higher than the maximum level supported (JAVA_13)` warning. Do **not** port
      26.1.2's `sponge-mixin` resolutionStrategy swap — that existed solely to get `JAVA_25` accepted.
- [ ] **Access widener vs access transformer.** Today this branch has
      `META-INF/accesstransformer.cfg` (NeoForge/Forge). Architectury wants an **access widener** in
      `common` (`loom.accessWidenerPath`), which loom bakes into the shared remapped Minecraft
      dependency at compile time for every platform, and which Fabric re-applies at runtime via the
      `"accessWidener"` key in `fabric.mod.json`. Plan: author
      `common/src/main/resources/potionsplus.accesswidener` as the source of truth, and keep a
      **mirrored** `forge/src/main/resources/META-INF/accesstransformer.cfg` (Forge does not read
      AWs). Confirm whether NeoForge 21.1 reads the AW or needs its AT kept too.
- [ ] Audit each of the 18 mixins for NeoForge-only injection targets. 26.1.2 found exactly one
      (`BucketItemMixin` injecting into `FluidType.onVaporize`) — `BucketItemMixin` exists here too.
      **Check it first.**
- [ ] `ItemEntityMixin` / `LivingEntityMixin` Forge equivalents: verify Forge's `ItemEntity.lifespan`
      and `BlockState.getFriction(…)` patches match NeoForge's on 1.21.1 before porting verbatim.
- [ ] **Descriptor precision.** 26.1.2 lost real time to a mixin targeting `Player.onItemPickup(Entity)`
      when the actual method is `LivingEntity.onItemPickup(ItemEntity)`. Obfuscated 1.21.1 makes this
      class of error harder to spot, not easier — `defaultRequire: 1` is already set; keep it.

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

- [ ] **Fabric** (`PotionsPlusFabricClient.onInitializeClient`): BE renderers, entity renderers,
      model layers, sprite + emitter particles, item/block color handlers, tooltip component
      factories, key mappings (`KeyBindingHelper.registerKeyBinding`), screens
      (`MenuScreens.register`).
      **1.21.1-era API names differ from 26.1.2's** — `ParticleFactoryRegistry` not
      `ParticleProviderRegistry`, `EntityModelLayerRegistry` not `ModelLayerRegistry`,
      `ColorProviderRegistry` for tints, and `BlockEntityRendererRegistry`/`EntityRendererRegistry`
      are still the live fabric-api entry points on 1.21.1 (not yet deprecated in favour of vanilla
      statics). Verify each against the fabric-api version in use.
- [ ] **Forge** `forge/.../core/forge/Renderers.java` etc.: `EntityRenderersEvent.RegisterRenderers`,
      `RegisterLayerDefinitions`, `RegisterParticleProvidersEvent`,
      `RegisterColorHandlersEvent.{Block,Item}`, `RegisterClientTooltipComponentFactoriesEvent`,
      `RegisterKeyMappingsEvent`.
- [ ] **Forge timing (26.1.2 caught this late):** several of those events fire during
      `Minecraft.<init>`, **before** `FMLClientSetupEvent`. A listener registered from
      `FMLClientSetupEvent` never runs. Register them from the `@Mod` constructor on the mod bus,
      guarded by dist, or via `@Mod.EventBusSubscriber(value = Dist.CLIENT)`.
- [ ] **JEI on all three.** `client/integration/jei/*` moves to `common/` (as 26.1.2 did). Coordinates:
      `mezz.jei:jei-1.21.1-common-api`, `-fabric-api` / `-forge-api` / `-neoforge-api` (compileOnly)
      and `jei-1.21.1-{fabric,forge,neoforge}` (runtimeOnly). **Verify the Forge artifact actually
      resolves before committing to it** — its existence is the reason Decision 3 is satisfiable here,
      and it is the one claim in this plan that comes from JEI's published-artifact convention rather
      than a jar we inspected.
- [ ] **Block/item colour handlers are silently missing unless registered per loader.** NeoForge
      registers the brewing-cauldron water tint through its own colour-handler event; on 26.1.2
      Fabric and Forge registered nothing and the water rendered with the fallback colour **for the
      whole project** until `f7c5890`. Extract the tint into a shared `common` class and register it
      explicitly on all three. No crash, no log line — you only catch this by looking at the cauldron.
- [ ] **`Minecraft.getInstance()` is null during Fabric's `onInitializeClient`.** Anything needing the
      live client instance must be deferred to `ClientLifecycleEvents.CLIENT_STARTED`.
- [ ] **Verify `assets/minecraft/atlases/blocks.json` early** (section D). Its `directory` sprite
      sources list *every* namespace's texture folder, dragging vanilla particle sprites into the
      mipmapped blocks atlas. On 26.1.2 that hard-crashed startup on `GpuDevice`'s mip check.
- [ ] **Client BE state can hinge on one sync packet that may not arrive.** The cauldron's per-tick
      resync was gated behind an active recipe, but crafting clears the recipe the instant it
      finishes, leaving clients rendering an empty cauldron (`f7c5890`). Gate resyncs on a condition
      that outlives the operation.
- [ ] **REI / EMI (future note):** keep the recipe-viewer integration behind one interface of
      categories + recipe suppliers so viewers stay pluggable.

**Exit criterion:** all three clients render every BE, entity, particle, tooltip and tint correctly;
JEI shows the brewing-cauldron and clothesline categories on all three.

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
| 2026-09-01 | 3 | **Phase 3 done — Fabric + Forge scaffold green, after a real mapping blocker worth recording in full.** `:fabric:remapJar` threw TinyRemapper "Unfixable conflicts" (38-entry set) on the FIRST full three-loader build. Root cause (decompiled loom 1.17.491 + tiny-remapper 0.14.0, verified empirically): Mojang's 1.21.1 named mappings give `Container.getItem/isEmpty` and `RecipeInput.getItem/isEmpty` identical names but different intermediary ids (`method_5438/5442` vs `method_59984/59987`); TinyRemapper 0.14.0's class-less conflict key flags any class implementing both interfaces; `InventoryBlockEntity implements WorldlyContainer, RecipeInput` is exactly that class. The merged `mappings.tiny` is clean — NOT a mapping defect. Ruled out: `fabric.loom.dropNonIntermediateRootMethods=true` (both targets are roots; forced cache rebuild → identical 38-conflict set, flag proven read via `project.findProperty` but inert), `nameSyntheticMembers=false` (worse: 39 conflicts), `ignoreConflicts=true` (would emit a method that fails to override one interface at runtime). **Fix applied:** removed `RecipeInput` from `InventoryBlockEntity` (import, interface, `@Override` on `size()`), added `common/.../recipe/ContainerRecipeInput.java` (a `Container`→`RecipeInput` delegating record, mirroring the mirror's `MultiRecipeInput` location idiom), and the single tree-wide site that passed a block entity as a `RecipeInput` (`neoforge/.../BrewingCauldronBlockEntity.java:101`, `matches(this, …)` — the only such site, proven by grep) now passes `new ContainerRecipeInput(this)`. Full exit criterion `./gradlew :fabric:build :forge:build :neoforge:build -x test` → **BUILD SUCCESSFUL in 15s**; `potionsplus-{fabric,forge,neoforge}-1.6.0.jar` all produced. **Not committed** — changeset on `dev/1.21.1/multi-loader-expansion`, ready for review/commit. |
