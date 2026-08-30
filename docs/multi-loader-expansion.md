# Potions Plus — Multi-loader expansion (NeoForge + Fabric + Forge)

> Living plan + progress tracker. Tick boxes as phases complete. Update the **Status** table and **Progress log** at the bottom each session so we can pick up where we left off.

## Context

`potions-plus` currently ships **NeoForge-only**. The goal is to expand to **three loaders** — keep the existing NeoForge module, and add **Fabric** and **regular MinecraftForge** — using the Architectury plugin, with **full feature parity** on all loaders, **JEI** integration everywhere, and **NeoForge datagen** retained as the source of truth.

The codebase is already most of the way there:

- It is on an Architectury layout (`common/` + `neoforge/` subprojects) with `architectury-plugin` + `loom-no-remap` + `architectury-injectables` (`@ExpectPlatform`) already wired. This **is** the "Architectury API dependency in the mod" — every sibling mod (fishtastic, gelatin-ui, apt-ores) uses `dev.architectury:architectury-injectables:1.0.13` only; **none** use the full `dev.architectury:architectury-fabric/neoforge` runtime library. We keep it that way.
- `gradle.properties` already declares `fabric_loader_version` / `fabric_api_version`; `settings.gradle` has a `// include 'fabric'` stub; `enabled_platforms = neoforge`.
- The platform surface is deliberately thin: only `common/.../platform/Platform.java` (7 `@ExpectPlatform` methods) and `PacketNetwork.java` (5 methods).
- **Registration is already cross-platform-shaped**: every common `core/*.init(...)` signature is `BiFunction<String, Supplier<T>, Holder<T>>` (verified across `Advancements`, `Attributes`, `BlockPredicateTypes`, `ConsumeEffects`, `Entities`, `LootItemConditions`, `MobEffects`, `Potions`, and every `core/blocks/*` + `core/items/*`). The NeoForge module satisfies them with `DeferredRegister::register` (returns `DeferredHolder`, a `Holder`). Fabric's `Registry.registerForHolder` (returns `Holder.Reference`) and Forge's `DeferredRegister::register` (returns Forge `DeferredHolder`, also a `Holder`) both slot in with **zero common-code changes**.

### Decisions (confirmed with user)
1. **Loaders**: NeoForge (existing) + **Fabric** + **Forge** (real `net.minecraftforge:forge`, like apt-ores).
2. **Parity**: **full** — the four NeoForge-only systems are reimplemented on both new loaders, not stubbed.
3. **Recipe viewer**: **JEI** on all three now; REI/EMI recorded as a future add (see the note at the end of Phase 8).
4. **Datagen**: keep **NeoForge** as source of truth; share generated resources into `common`.
5. **Registration approach**: keep potions-plus's existing `init(register)` pattern (already `Holder`-generic) rather than migrating to fishtastic's `IRegistrationApi` singleton — lower churn, working NeoForge build preserved.

### Canonical references to mirror
- `D:\GitHub\fishtastic\` — canonical 2-loader content mod (registration, networking `IPacketContext`, mixin layout, `fabric.mod.json`).
- `D:\GitHub\apt-ores-worktrees\mc-26.1\` — **the** 3-loader reference (has a real Forge module) and its `docs/PORTING.md` / `docs/DEVELOPMENT.md` documenting every Forge workaround + API divergence.
- `D:\GitHub\modding-guide\` topic files `01`, `02`, `06`, `07`, `08`.

---

## Status

| Phase | Title | Status |
|---|---|---|
| 0 | Build scaffold (three modules) | ✅ complete |
| 1 | Registration hubs (Fabric + Forge) | ✅ complete |
| 2 | `@ExpectPlatform` impls + networking | ✅ complete |
| 3 | Entrypoints | ✅ complete |
| 4 | Event surface (~16 classes, ~30 events) | ✅ complete |
| 5 | NeoForge-only systems (full parity) | ⬜ not started |
| 6 | Mixins + access widening | ⬜ not started |
| 7 | Datagen sharing | ⬜ not started |
| 8 | Client (renderers, particles, tooltips, colors, models, JEI) | ⬜ not started |
| 9 | Game tests | ⬜ not started |
| 10 | Verification | ⬜ not started |

---

## Phase 0 — Build scaffold (make three modules compile)

**Files:** `settings.gradle`, `gradle.properties`, `build.gradle` (root), new `fabric/build.gradle`, `fabric/gradle.properties`, `forge/build.gradle`, `forge/gradle.properties`.

- [x] `settings.gradle`: add `include 'fabric'` and `include 'forge'`.
- [x] `gradle.properties`: set `enabled_platforms = neoforge,fabric,forge`.
- [x] `gradle.properties`: add `forge_version = 64.1.0` (pin exact — apt-ores §PORTING warns "Latest"/"Recommended" can ship corrupted jars) and `mod_forge_version_range = [${forge_version},)`.
- [x] `gradle.properties`: reconcile `fabric_api_version` → `0.155.2+26.1.2` (the version fishtastic and apt-ores both resolve for 26.1.2).
- [x] `fabric/gradle.properties` → `loom.platform = fabric`; `forge/gradle.properties` → `loom.platform = forge`.
- [x] `fabric/build.gradle`: clone `neoforge/build.gradle` but `architectury { platformSetupLoomIde(); fabric() }`; deps `fabric-loader` + `fabric-api` + `common(project(':common')) { transitive=false }` + `shadowBundle project(':common', configuration:'transformProductionFabric')`; keep `jar { archiveClassifier='raw' }` + `shadowJar` primary-artifact pattern; `processResources` expanding `fabric.mod.json`; add `loom.runs` for `datagen` (`-Dfabric-api.datagen`, `-Dfabric-api.datagen.modid=potionsplus`) and `gametest`.
- [x] `forge/build.gradle`: clone `neoforge/build.gradle` **plus** the Forge-only workarounds from `apt-ores-worktrees/mc-26.1/forge/build.gradle`:
  - [x] `forge "net.minecraftforge:forge:$minecraft_version-$forge_version"` (note the `$mc-$forge` coordinate form).
  - [x] `configurations`: `compileClasspath.extendsFrom common` and `developmentForge.extendsFrom common`, **but NOT `runtimeClasspath.extendsFrom common`** (JPMS split-package crash otherwise).
  - [x] `sourceSets.main.output.resourcesDir = output.classesDirs.singleFile` (Forge `SecureModuleFinder` can't union split dirs).
  - [x] `generateEmptyMappings` task (writes `v1\tofficial\tnamed\n` `.tiny`) + `-Darchitectury.naming.sourceNamespace=official -Darchitectury.naming.mappingsPath=<file>` on every `run*` JavaExec (Forge runtime AXFORM).
  - [x] `META-INF/mods.toml` (Forge metadata name — **not** `neoforge.mods.toml`), expanded by `processResources`.
- [x] `common/build.gradle`: add `sourceSets.main.resources.srcDir('src/generated/resources')` (for Phase 9 shared datagen output).

**Exit criterion:** `./gradlew :fabric:build :forge:build` produce jars (content stubbed/empty is fine this phase). — **met 2026-08-29.**

> **Env bumps made for Phase 0 (required to build the Forge module):** loom-no-remap `1.14-SNAPSHOT` → `1.17.491` (the 1.14 line NPEs in `McpExecutor` during Forge Minecraft patching) and Gradle `9.2.1` → `9.5.0` (loom 1.17's `extendsFrom(Provider[])` needs the newer Gradle API — both matching apt-ores mc-26.1). The neoforge module's `programArgs.addAll(...)` was switched to the varargs form (`programArgs ...`) for loom 1.17.

---

## Phase 1 — Registration hubs (Fabric + Forge)

**Pattern:** for each `neoforge/src/main/java/grill24/potionsplus/core/neoforge/*.java` registry hub, write a mirror in `fabric/.../core/fabric/` (vanilla `Registry.registerForHolder`) and `forge/.../core/forge/` (Forge `DeferredRegister`), calling the **same** common `init(...)` calls.

### VERIFIED API FACTS (2026-08-29 — all confirmed from source; do not re-derive)

**Common `init(...)` signatures are `BiFunction<String, Supplier<T>, Holder<T>>`** (DecorationBlocks, BlockEntityBlocks, FlowerBlocks, HatItems, OreItems, BrewingItems, DynamicIconItems, MobEffects, Potions, Entities, BlockPredicateTypes, ConsumeEffects, LootItemConditions, Advancements, Attributes). The return value is stored into static `Holder<T>` fields.

- **NeoForge** satisfies it via `DeferredRegister::register` (its `DeferredHolder` **is** a `Holder`).
- **Fabric** satisfies it via `Registry.registerForHolder(Registry<R>, ResourceKey<R>, T)` / `(Registry<R>, Identifier, T)` → returns `Holder.Reference<T>` (vanilla `Registry.java:126-132`). Verify `Builder`/`ItemBuilder` already `.setId(ResourceKey.create(...))` — **CONFIRMED they do** (plan checkbox done).
- **Forge CANNOT use `DR::register` directly** — Forge 26.1.2 `DeferredRegister.register(String, Supplier)` returns `net.minecraftforge.registries.RegistryObject<T>`, which implements **only `Supplier<T>`, NOT `Holder<T>`**. **→ write a `ForgeHolder<T> implements Holder<T>, Supplier<T>` adapter** (full sketch below).

**What is dereferenced at init-time vs deferred:** only `unwrapKey()` is called synchronously during hub class-load (e.g. `RegistrationUtility.registerBlockItem` → `block.unwrapKey().orElseThrow()`, and `.name()` paths). `value()` is always deferred inside itemFactory/modelGenerator/recipeGenerator/lootGenerator lambdas. Proven: NeoForge `DeferredHolder.value()` throws NPE when unbound (`NeoForge/src/.../registries/DeferredHolder.java:101-108`) yet the mod loads fine. So the Forge adapter only needs `unwrapKey()` present *immediately*; `value()` may resolve lazily.

**Forge 26.1.2 DeferredRegister API** (`net.minecraftforge.registries`, verified in Forge sources jar):
- `create(ResourceKey<? extends Registry<B>>, String)` ✓; `register(String, Supplier<? extends I>)` → `RegistryObject<I>`; `key(String path)` → `ResourceKey<T>` (useful for the common recipe-KEY stubs).
- `register(BusGroup)` — **NOT** `IEventBus`. Get the mod's `BusGroup` via `FMLJavaModLoadingContext.get().getModBusGroup()` or injected `FMLModContainer#getModBusGroup()` (both verified via javap).
- `RegistryObject.getHolder()` → `Optional<Holder<T>>` — present **only after** the RegisterEvent; empty at init-time.
- No `DeferredRegister.DataComponents` on Forge (NeoForge-only). Data components → plain `DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID)`; element type `DataComponentType<?>`.
- Vanilla registry keys `Registries.BLOCK/ITEM/BLOCK_ENTITY_TYPE/MOB_EFFECT/POTION/ATTRIBUTE/TRIGGER_TYPE/ENTITY_TYPE/...` all work as the `ResourceKey` arg (apt-ores verified). Forge also has `ForgeRegistries.X`/`ForgeRegistries.Keys.X` wrappers — **not needed**; use vanilla `Registries.X` uniformly.
- `@Mod` is `net.minecraftforge.fml.common.Mod`, no `dist` attribute.
- **RESOLVED (2026-08-29):** Forge's `BlockEntityType` ctor is `public BlockEntityType(BlockEntitySupplier<? extends T>, Set<Block>)` (verified via `javap` of `minecraft-merged-patched.jar`). It does **not** patch in NeoForge's `(BlockEntitySupplier, Block...)` varargs form, and there is no `Builder` — so Forge BE registration uses `new BlockEntityType<>(factory, Set.of(block))`, with the `block` captured inside the deferred supplier (blocks flush before block entities).
- **RESOLVED (2026-08-29):** vanilla `SimpleParticleType(boolean)` ctor is `protected` on Fabric (Forge/NeoForge patch it public via AT) → Fabric uses `new SimpleParticleType(false) {}` (anonymous subclass).
- **RESOLVED (2026-08-29):** `Registry` has **no `holders()`** method in 26.1.2 — enumerate registered entries via `Registry.entrySet()` (or `registryKeySet()`). The creative-tab `displayItems` iterates `BuiltInRegistries.ITEM.entrySet()` filtered by namespace.
- **CONFIRMED (2026-08-29):** Fabric's DISPENSER-association method is `FabricBlockEntityType.addValidBlock(Block)` (the plan's original name; the `addSupportedBlock` variant seen in an older `fabric-object-builder-api-v1` source jar in the cache is NOT the resolved version).

**Fabric API** (verified in fabric-api source): `FabricBlockEntityTypeBuilder.create(Factory<BlockEntity>, Block...).build()` → `BlockEntityType<T>`; `((FabricBlockEntityType) BlockEntityType.DISPENSER).addValidBlock(block)` is an interface-injected default method (drop-in replacement for NeoForge `BlockEntityTypeAddBlocksEvent`).

**CreativeModeTab (Fabric/Forge):** vanilla has **no no-arg `builder()` and no `withSearchBar()`** (NeoForge patches both in). Use `CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)` + `.title(...).icon(() -> new ItemStack(BrewingItems.LUNAR_BERRIES.value())).displayItems((params, output) -> { iterate BuiltInRegistries.ITEM.holders(), accept() those whose unwrapKey().location().getNamespace().equals(MOD_ID); }).build()`. The `displayItems` lambda runs when the creative screen builds, so every potionsplus item is already registered. This replaces NeoForge's `BuildCreativeModeTabContentsEvent` (not available on Fabric). No in-tab search bar (acceptable Phase 1; global search still finds items).

### ForgeHolder adapter (new file `forge/.../core/forge/util/ForgeHolder.java`)

Model EXACTLY on NeoForge `DeferredHolder` semantics, delegating to a `RegistryObject<T>`:

```java
public final class ForgeHolder<T> implements Holder<T>, Supplier<T> {
    private final RegistryObject<T> delegate;
    private ForgeHolder(RegistryObject<T> delegate) { this.delegate = delegate; }
    public static <T> ForgeHolder<T> of(RegistryObject<T> delegate) { return new ForgeHolder<>(delegate); }
    @Override public T get()      { return delegate.get(); }          // value()
    @Override public T value()    { return delegate.get(); }          // NPE if absent, like DeferredHolder
    @Override public ResourceKey<T> getKey() { return delegate.getKey(); }
    @Override public boolean isBound() { return delegate.getHolder().map(Holder::isBound).orElse(false); }
    @Override public boolean is(Identifier id) { return id.equals(delegate.getId()); }
    @Override public boolean is(ResourceKey<T> key) { return key.equals(getKey()); }
    @Override public boolean is(Predicate<ResourceKey<T>> p) { return getKey() != null && p.test(getKey()); }
    @Override public boolean is(TagKey<T> tag) { return delegate.getHolder().map(h -> h.is(tag)).orElse(false); }
    @Override public Stream<TagKey<T>> tags() { return delegate.getHolder().map(Holder::tags).orElse(Stream.empty()); }
    @Override public Either<ResourceKey<T>, T> unwrap() { return Either.left(getKey()); }
    @Override public Optional<ResourceKey<T>> unwrapKey() { return Optional.ofNullable(getKey()); }
    @Override public Kind kind() { return Kind.REFERENCE; }
    @Override public boolean canSerializeIn(HolderOwner<T> o) { return delegate.getHolder().map(h -> h.canSerializeIn(o)).orElse(false); }
    @Override public Holder<T> getDelegate() { return delegate.getHolder().map(Holder::getDelegate).orElse(this); }
    // equals/hashCode by key (mirror DeferredHolder.java:176-189). Suppress unchecked casts.
}
```
`delegate.getKey()` and `delegate.getId()` are available immediately at init-time; `delegate.getHolder()` fills after the RegisterEvent. Every hub then passes `(name, sup) -> ForgeHolder.of(DR.register(name, sup))` to the common `init(...)`.

**Common `Supplier`-typed stubs** (`core.Recipes.*`, `core.DataComponents.WEIGHT`): on Forge the `RegistryObject` itself IS a `Supplier` — assign directly (`core.Recipes.BREWING_CAULDRON_RECIPE = BREWING_CAULDRON_RECIPE;`). On Fabric `Holder.Reference` is NOT a Supplier — assign `() -> holder.value()`. The `core.Recipes.*_KEY` stubs (`ResourceKey<RecipeType<?>>`): set from the registry key directly — Forge `RECIPE_TYPES.key("brewing_cauldron_recipe")`, Fabric `ResourceKey.create(Registries.RECIPE_TYPE, ppId("brewing_cauldron_recipe"))` (avoids `ResourceKey` invariance casts).

### Registration order (matters only on Fabric — registration is IMMEDIATE, not deferred)

NeoForge order is safe because DRs flush later; Fabric must order so no `.value()`/null-deref happens during another registration. Hard dependencies:
1. **`PotionBuilder.potionFactory`** set FIRST (before `core.Potions` class loads — its static fields trigger registration via potionFactory). Fabric: `(name, effectSupplier) -> Registry.registerForHolder(BuiltInRegistries.POTION, ResourceKey.create(Registries.POTION, ppId(name)), new Potion(name, effectSupplier.get()))` — ctor's first arg is the translation suffix and MUST equal `name` (the `Potion.name()` trap).
2. **`MobEffects.init` BEFORE `Potions.init`** (Potions' static fields hold `MobEffects.*` holders; `OreFlowerBlock(MobEffects.X, ...)` factory is evaluated immediately on Fabric).
3. **Items before Blocks before BEs** (BE factories use `BlockEntityBlocks.X.value()` immediately); **then the DISPENSER association** `((FabricBlockEntityType) BlockEntityType.DISPENSER).addValidBlock(BlockEntityBlocks.PRECISION_DISPENSER.value())`.
4. **Creative tab last** (icon references `BrewingItems.LUNAR_BERRIES`).
Recommended Fabric `onInitialize` order: potionFactory → Advancements/Attributes/Entities/BlockPredicateTypes/ConsumeEffects (immediate) → MobEffects → Potions → DataComponents → Items(Hat/Ore/Brewing/DynamicIcon) → Blocks(Decoration/BlockEntity/Flower) → BEs → DISPENSER assoc → Sounds → Particles → Recipes → MenuTypes/CommandArgumentTypes/LootItemFunctions/NumberProviders → CreativeModeTab. (DataComponents before Items if any item uses `core.DataComponents.WEIGHT`.)

### Hub files to write

- **Fabric** `fabric/.../core/fabric/`: `PotionsPlusFabric` (wire all of the above into `onInitialize()`), `Blocks`, `Items`, `Particles`, `Recipes`, `DataComponents`, `MenuTypes`, `CommandArgumentTypes`, `LootItemFunctions`, `NumberProviders`, `Sounds`, `CreativeModeTabs`, `blocks/FlowerBlocks` (use `grill24.potionsplus.core.ConventionalTags.Blocks.ORES_*` — same `c:` tags, semantically identical to NeoForge's `Tags.Blocks.ORES_*`). All via `BuiltInRegistries.X` + `Registry.registerForHolder`. Block items / common stubs populated the same way as the NeoForge hubs.
- **Forge** `forge/.../core/forge/`: same set, via `DeferredRegister.create(Registries.X, MOD_ID)` + `ForgeHolder.of(...)` adapters; `PotionsPlusForge` constructor gets `BusGroup` via `FMLJavaModLoadingContext.get().getModBusGroup()`, runs the same `init(...)` calls in the same order, then `DR.register(bus)` for every DR.
- `PercentageAttribute` → `new RangedAttribute(Translations.DESCRIPTION_..., 0.0, 0.0, 1.0)` through `Attributes.registerPlatformAttribute(...)` on both (common `Attributes.init` already uses `RangedAttribute` for the enchantment-bonus attrs).
- **Partial Phase 2 prerequisite (do it now):** minimal `fabric/.../platform/fabric/PlatformImpl.java` and `forge/.../platform/forge/PlatformImpl.java` — at least `isClient` (Fabric `FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT`; Forge `FMLEnvironment.getDist() == Dist.CLIENT`) and `isDevelopmentEnvironment` (`FabricLoader.isDevelopmentEnvironment()` / `!FMLEnvironment.isProduction()`), because `AbstractRegistererBuilder.modelGenerator()` calls `Platform.isClient()` during hub class-load. The other 5 `@ExpectPlatform` methods: stub `getChorusFruitTeleportTarget` → `new Vec3(x,y,z)`, `onServerPlayerHeldItemChanged` → no-op, `fireCropGrowPost` → no-op, and the two drink-time getters → return the same defaults `PotionsPlusConfig` uses (check `neoforge/.../config/neoforge/PotionsPlusConfig.java` for the default values; full impl lands in Phase 5).
- `Sounds` (both loaders): register the 13 sound events via `Registries.SOUND_EVENT`; keep `NeoSounds`' `SoundDefinitionsProvider` datagen NeoForge-only (Phase 7 propagates the JSON).
- `LootModifiers`/`Capabilities` are Phase 5 — do not stub.

**Exit criterion:** NeoForge build still green; Fabric + Forge compile and load a world with blocks/items/potions/effects present.

---

## Phase 2 — `@ExpectPlatform` impls + networking

**Files:** new `fabric/.../platform/fabric/{PlatformImpl,PacketNetworkImpl}.java`, `forge/.../platform/forge/{PlatformImpl,PacketNetworkImpl}.java`, plus per-platform `PacketContext` wrappers.

- [x] `fabric/.../platform/fabric/PlatformImpl.java` (7 methods): `isClient`/`isDevelopmentEnvironment` implemented; the 5 event/config methods (`getChorusFruitTeleportTarget`, `onServerPlayerHeldItemChanged`, `fireCropGrowPost`, two drink-time getters) remain **stubs** returning defaults, with full impl landing in Phase 5 (already the case since Phase 1).
- [x] `forge/.../platform/forge/PlatformImpl.java` (7 methods): same split — `isClient`/`isDevelopmentEnvironment` done, 5 event/config methods stubbed for Phase 5.
- [x] `fabric/.../platform/fabric/PacketNetworkImpl.java` (5 methods): `ServerPlayNetworking.send`, `ClientPlayNetworking.send`, `PlayerLookup.tracking(ServerLevel, ChunkPos)` / `PlayerLookup.tracking(Entity)`.
- [x] `forge/.../platform/forge/PacketNetworkImpl.java` (5 methods): `Channel.send(payload, PacketDistributor.X.with(...))` — see verified API below.
- [x] `FabricPacketContext` (wraps `ServerPlayNetworking.Context`/`ClientPlayNetworking.Context` via two factory methods) and `ForgePacketContext` (wraps `CustomPayloadEvent.Context`), both implementing common `PacketContext`.
- [x] Packet registration hub mirroring `neoforge/.../core/neoforge/Packets.java`: Fabric `core/fabric/Packets.java` (`registerServer()` + `registerClient()`) and Forge `core/forge/Packets.java` (`register()`, builds the channel in the `@Mod` constructor on both dists).

### VERIFIED API FACTS (2026-08-29 — decompiled Forge `net.minecraftforge.network` sources + Fabric networking-api-v1 6.3.1 via javap)

**Forge 26.1.2 does NOT use NeoForge's `RegisterPayloadHandlersEvent`/`PayloadRegistrar`, nor the old pre-1.20.5 `SimpleChannel`.** Its modern API is `ChannelBuilder` + `Channel` + `PacketDistributor`:

- `ChannelBuilder.named(Identifier).networkProtocolVersion(1).optional().payloadChannel()` → `PayloadConnection<CustomPacketPayload>`; then `.play()` → `PayloadProtocol<RegistryFriendlyByteBuf, BASE>`; `.serverbound()`/`.clientbound()` (from `BaseProtocol`, alias for `.flow(PacketFlow.SERVERBOUND/CLIENTBOUND)`) → `PayloadFlow`; `.add(Type<MSG>, StreamCodec<RegistryFriendlyByteBuf, MSG>, BiConsumer<MSG, CustomPayloadEvent.Context>)`; final `.build()` → `Channel<CustomPacketPayload>`.
- **Buffer-type trap:** `NetworkProtocol.PLAY` is `NetworkProtocol<RegistryFriendlyByteBuf>`, so `.add(...)` wants `StreamCodec<RegistryFriendlyByteBuf, MSG>`. Several common packets declare `StreamCodec<ByteBuf, MSG>` — safe to narrow-cast (a `ByteBuf` codec encodes/decodes a `RegistryFriendlyByteBuf` at runtime), done via a `playCodec(...)` helper in `forge/core/forge/Packets.java`.
- **Send targets** (`Channel.send(payload, PacketDistributor.X)`): `PLAYER.with(ServerPlayer)`, `TRACKING_ENTITY_AND_SELF.with(Entity)`, `TRACKING_CHUNK.with(LevelChunk)` (`level.getChunk(chunkPos.x(), chunkPos.z())` — note `ChunkPos` is a `record`, fields are private, use `x()`/`z()`), `SERVER.noArg()` (C2S), `ALL.noArg()`.
- **`CustomPayloadEvent.Context`** has `enqueueWork(Runnable)` → `CompletableFuture<Void>`, `getSender()` → `@Nullable ServerPlayer` (server-side only), `isServerSide()`/`isClientSide()`, `getConnection()`. **No** `player()` (client) and **no** `disconnect(...)` — the Forge wrapper resolves the client player via `Minecraft.getInstance().player` and disconnects via `context.getConnection().disconnect(reason)`.

**Fabric networking-api-v1 6.3.1+554860db4c** (resolved by fabric-api 0.155.2+26.1.2):

- `PayloadTypeRegistry.serverboundPlay()`/`clientboundPlay()` → `PayloadTypeRegistry<RegistryFriendlyByteBuf>`; `.register(Type<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>)` **throws `IllegalArgumentException` on duplicate** (so client-side codec registration wraps in try/catch to tolerate integrated-server double-registration).
- `ServerPlayNetworking.send(ServerPlayer, payload)`, `ClientPlayNetworking.send(payload)`, `PlayerLookup.tracking(ServerLevel, ChunkPos)` / `.tracking(Entity)`.
- `ServerPlayNetworking.registerGlobalReceiver(Type<T>, (pkt, ctx) -> ...)`; `ClientPlayNetworking.registerGlobalReceiver(...)`. Contexts: `ServerPlayNetworking.Context` (`player()`, `server()`, `responseSender()`), `ClientPlayNetworking.Context` (`player()`, `client()`, `responseSender()`). `enqueueWork` = `context.server().execute(...)` / `context.client().execute(...)` (fire-and-forget → returned future is pre-completed).
- **Codec side coverage:** `CustomPacketPayload.Type<T>` is `record Type(Identifier id)` — no embedded codec, so the codec must come from `PayloadTypeRegistry`. A packet's codec must be registered on the side that *sends* it and the side that *receives* it. Fabric `registerServer()` = serverbound codec+handler + all clientbound codecs; `registerClient()` = serverbound codec (try/catch) + clientbound codec (try/catch) + clientbound handlers. (fishtastic's registrar omits the client-side serverbound codec, which would break `ClientPlayNetworking.send` on a dedicated client — potions-plus registers it, so `ClotheslineBehaviour`'s C2S send works everywhere.)

---

## Phase 3 — Entrypoints

**Files:** new `fabric/.../core/fabric/PotionsPlusFabric.java` (`implements ModInitializer`) + `PotionsPlusFabricClient.java` (`implements ClientModInitializer`); `forge/.../core/forge/PotionsPlusForge.java` (`@Mod`) + `PotionsPlusForgeClient.java`.

- [x] Fabric `ModInitializer` + `ClientModInitializer` replicate the `neoforge/.../core/neoforge/PotionsPlus.java` wiring (all `core.*.init(...)` calls + registration-hub flushes); registration is immediate (no deferred flush). (Already landed during Phase 1/2 — `PotionsPlusFabric`/`PotionsPlusFabricClient` cover every hub except `LootModifiers` (Phase 5, per Phase 1 note).)
- [x] Forge `@Mod` constructor replicates the same wiring and flushes its `DeferredRegister`s on its mod event bus (already landed during Phase 1/2 — `PotionsPlusForge`). No code currently calls `RegistryObject`/`DeferredHolder.get()` at construction time (verified: only `unwrapKey()`/deferred lambdas, per the Phase 1 "VERIFIED API FACTS" note), so no `FMLCommonSetupEvent.enqueueWork(...)` deferral is needed yet — kept in mind for Phase 5+ when config/capabilities land.
- [x] Added `forge/.../core/forge/PotionsPlusForgeClient.java` (the entrypoint that was still missing) — a second `@Mod` class mirroring NeoForge's `PotionsPlusClient` split, but dist-safe: it registers an `FMLClientSetupEvent` listener (via `FMLClientSetupEvent.getBus(container.getModBusGroup()).addListener(...)`) instead of eagerly touching client-only classes in the constructor, since Forge's Forge/NeoForge unified jar would otherwise construct it on a dedicated server too. Body is a placeholder log line — real client wiring lands in Phase 8.

---

## Phase 4 — Event surface (~16 classes, ~30 events)

Map each `neoforge/.../event/neoforge/*.java` listener to Fabric + Forge equivalents.

| NeoForge listener | Fabric equivalent | Forge equivalent |
|---|---|---|
| `AdvancementListeners` (`AdvancementEarnEvent`) | `PlayerAdvancementCallback` | Forge `AdvancementEvent.AdvancementEarnEvent` |
| `EffectListeners` (`MobEffectEvent.*`, `LivingFall/Damage/Heal/Death`) | mixins into `MobEffectInstance`/`LivingEntity` | Forge `net.minecraftforge` effect/living events |
| `EnchantmentListeners` (`GetEnchantmentLevelEvent`) | mixin into `EnchantmentHelper` | Forge enchantment event |
| `EntityListeners` (`BlockDropsEvent`, `LivingDeathEvent`, `ItemTossEvent`, `EntityAttributeCreationEvent`) | `PlayerBlockBreakEvents`, `ServerEntityEvents`, `DefaultAttributeRegistry` | Forge equivalents |
| `ItemListenersMod` (`ModifyDefaultComponentsEvent` — potion stack=16) | mixin into item default components | Forge `ModifyDefaultComponentsEvent` |
| `NeoAttributeEvents` (`EntityAttributeModificationEvent`) | `DefaultAttributeRegistry.register` | Forge `EntityAttributeModificationEvent` |
| `NeoDelayedEvents` / `NeoServerTickEvents` / `ClientGameListeners` (tick) | `ServerTickEvents`, `ClientTickEvents` | Forge `TickEvent.ServerTickEvent`/`ClientTickEvent` |
| `NeoItemListeners` (`ItemTooltipEvent`) | `ItemTooltipCallback` | Forge `ItemTooltipEvent` |
| `NeoCommandEvents` (`RegisterCommandsEvent`) | `CommandRegistrationCallback.EVENT` | Forge `RegisterCommandsEvent` |
| `PlayerListeners` (`ItemEntityPickupEvent`, `PlayerInteractEvent.RightClickBlock`, `EntityJoinLevelEvent`) | `UseBlockCallback`, `ServerEntityEvents.ENTITY_LOAD` | Forge equivalents |
| `ClientTooltipComponentFactoriesListeners` | `ClientTooltipComponentCallback.EVENT` | Forge `RegisterClientTooltipComponentFactoriesEvent` |

- [x] Implement Fabric mappings for each listener class (fabric-api callbacks + `mixin/fabric/` for effects/enchantment/advancement/pickup).
- [x] Implement Forge mappings for each listener class (static `BUS.addListener(...)`, plus `mixin/forge/` for enchantment level).
- [x] The two custom `Event` subclasses (`ServerPlayerHeldItemChangedEvent`, `NeoAnimatedItemTooltipEvent`) are **dead code** (zero subscribers) — skipped, not re-homed; `ItemFrameListeners` (dead stub) also skipped.
- [x] **Forge caveat**: Forge listeners written against Forge's actual API (`net.minecraftforge.*`, static `BUS`, predicate-cancel, `BlockEvent.BreakEvent` via `Result.DENY`), not NeoForge's.

---

## Phase 5 — NeoForge-only systems (full parity)

- [ ] **Global loot modifiers** (`WormrootLootModifier`, `AddMobEffectsLootModifier` + `LootModifiers` hub):
  - [ ] Forge: Forge's own `IGlobalLootModifier` + `GlobalLootModifierSerializer` (`net.minecraftforge.common.loot`) — port near-verbatim.
  - [ ] Fabric: Fabric Loot API (`fabric-loot-api-v3`) `LootTableEvents.MODIFY`; move shared logic (already partly in `common/behaviour/LootItemModifiersBehaviour.java`) into common.
- [ ] **Capabilities / `IItemHandler`** (clothesline storage, `Capabilities.java`):
  - [ ] Forge: `net.minecraftforge.common.capabilities` `Capability<ItemHandler>` + `CapabilityManager` + `ICapabilityProvider` (not NeoForge's `BlockCapability`/`RegisterCapabilitiesEvent`).
  - [ ] Fabric: `fabric-transfer-api-v1` `Storage<ItemVariant>` via `StorageUtil`/`Lookup`; prefer a common `ClotheslineInventory` interface both impls expose.
- [ ] **Server config** (`PotionsPlusConfig` / `ModConfigSpec`, feeding `PlatformImpl.getPotionDrinkTimeTicks/getPotionDrinkCooldownTimeTicks`):
  - [ ] Forge: `net.minecraftforge.common.ForgeConfigSpec`.
  - [ ] Fabric: hand-roll JSON like apt-ores (`config/potionsplus.json` via `FabricLoader.getInstance().getConfigDir()`); keep the `@ExpectPlatform` getters as the single access point.
- [ ] **Biome modifiers** (`add_lunar_berry_bush_patch`, `remove_berry_bush_patch`):
  - [ ] Forge: Forge biome modifiers (JSON `forge:add_features`/`forge:remove_features`).
  - [ ] Fabric: `fabric-biome-api-v1` `BiomeModifications.addFeature/removeFeature` in code (no JSON equivalent).

---

## Phase 6 — Mixins + access widening

- [ ] Add `potionsplus.fabric.mixins.json` + `potionsplus.forge.mixins.json`; wire into `fabric.mod.json` `"mixins"` and Forge `mods.toml` `[[mixins]]`.
- [ ] Fix the one common leak — `common/.../mixin/BucketItemMixin.java` injects at `Lnet/neoforged/neoforge/fluids/FluidType;onVaporize(...)` (NeoForge-only target). Split: NeoForge injection → `mixin/neoforge/`; add `mixin/fabric/` + `mixin/forge/` variants; keep vanilla parts in common.
- [ ] Resolve orphan `CauldronDispatcherAccessor` (on disk but not in `potionsplus.mixins.json`) — add or delete.
- [ ] Forge `mixin/forge/` equivalents of `NeoItemEntityMixin`/`NeoLivingEntityMixin` (Forge's `ItemEntity.lifespan` field differs — verify against the Forge jar).
- [ ] Access widener: `common/.../potionsplus.accesswidener` works on Fabric (`"accessWidener"` in `fabric.mod.json`) and NeoForge as-is; add Forge `META-INF/accesstransformer.cfg` (gelatin-ui ships both a `.accesswidener` and an `accesstransformer.cfg`).

---

## Phase 7 — Datagen sharing (keep NeoForge)

- [ ] Add a Gradle `Copy` task: `neoforge/src/generated/resources/**` → `common/src/generated/resources/` (mirror guide `06-datagen.md:351-367`), so Fabric + Forge pick up models/blockstates/tags/`sounds.json` via `common`'s `src/generated/resources` srcDir.
- [ ] Keep NeoForge-only providers (`GlobalLootModifierProvider`, `DatapackBuiltinEntriesProvider`, `SoundDefinitionsProvider`, NeoForge `Block/ItemTagsProvider`) where they are; hand-write the Fabric/Forge worldgen equivalents (Phase 5).
- [ ] Verify no `data/neoforge/` or `neoforge:` tagged JSON leaks into the Fabric/Forge jars.

---

## Phase 8 — Client (renderers, particles, tooltips, colors, models, JEI)

- [ ] Fabric (`PotionsPlusFabricClient`): `BlockEntityRendererRegistry`, `EntityRendererRegistry`, `ParticleProviderRegistry`, `ClientTooltipComponentCallback.EVENT`, `ColorProviderRegistry.BLOCK/ITEM`, `BlockRenderLayerMap`/render-type lookup, `MenuScreens.register`, `ModelLoadingPlugin` (reference: apt-ores `AptOresModelLoadingPlugin`).
- [ ] Forge (`PotionsPlusForgeClient`): `EntityRenderersEvent.RegisterRenderers`, `RegisterLayerDefinitions`, `RegisterColorHandlersEvent`, `RegisterClientTooltipComponentFactoriesEvent`, `RegisterMenuScreensEvent`, `ModelEvent.*` (note Forge **removed** `ModelEvent.RegisterAdditional`).
- [ ] **JEI**: move shared plugin logic (`neoforge/.../client/integration/jei/*`) into common; keep thin per-loader plugins — Fabric `jei_fabric` (mirror fishtastic's `jei_mod_plugin`), Forge `jei_forge`, NeoForge `jei_neoforge`. Add JEI dep per loader.
- [ ] **REI / EMI (future note)**: the recipe-viewer integration is a single interface of categories + recipe suppliers. After JEI ships, add REI (`me.shedaniel:RoughlyEnoughItems-*`) and EMI (`dev.emi:emi-*`) behind the same abstraction so viewers are pluggable. Recorded as a follow-up, not this milestone.

---

## Phase 9 — Game tests

- [ ] Fabric: `fabric-gametest-api-v1`; `fabric/src/testmod` including the common testmod source (as `neoforge` does via `sourceSets.testmod.java.srcDir(project(':common').file('src/testmod/java'))`), a `fabric.mod.json` gametest entrypoint, and a `gametest` loom run.
- [ ] Forge: `net.minecraftforge.gametest` `@GameTest`/`@GameTestGenerator` + `GameTestHooks`, same testmod source-sharing pattern.
- [ ] Port the two platform harness classes (`NeoForgeGameTestRegistration`, `NeoForgeTestPlayers`) to Fabric/Forge; keep `AlchemyGameTests`/`BrewingCauldronGameTests` in common untouched.

---

## Phase 10 — Verification

- [ ] `./gradlew build` — all three modules compile + test (`common` JUnit tests stay green, incl. `PotionContentsAccessTest`).
- [ ] `./gradlew :neoforge:runClient` — regression: NeoForge unchanged.
- [ ] `./gradlew :fabric:runClient` and `./gradlew :forge:runClient` — load a world; verify blocks/items/potions/effects/BEs/particles/entities render and behave, the clothesline inventory works, and loot modifiers + biome modifiers fire.
- [ ] `./gradlew :neoforge:runGametest` + Fabric/Forge gametest runs — the shared alchemy + brewing-cauldron tests pass on every loader.
- [ ] `./gradlew :neoforge:runData` then confirm the copy task propagates generated resources to `common/src/generated/resources` and the Fabric/Forge jars include them.
- [ ] Manual smoke: brew a potion, drink it (config values respected), trigger a global loot modifier drop, plant a lunar berry bush.

---

## Risks / verify-at-implementation

- **Forge `BlockEntityType` ctor visibility (OPEN)** — NeoForge patches the private vanilla ctor to `public BlockEntityType(BlockEntitySupplier, Block...)`; Forge's universal jar doesn't bake patches in, so it's unverified. Try the NeoForge-style `new BlockEntityType<>(factory, block)` first; if it won't compile on Forge, use a fallback (reflect or Forge-idiomatic helper). The only open API question in Phase 1.
- **Forge `DeferredRegister.register(BusGroup)`** — verified the signature (not `IEventBus`); obtain via `FMLJavaModLoadingContext.get().getModBusGroup()` or injected `FMLModContainer`. Both verified present via javap.
- **Forge 26.1.2 networking API** — RESOLVED (Phase 2): Forge uses `ChannelBuilder` + `Channel` (`payloadChannel()`) + `PacketDistributor` + `CustomPayloadEvent.Context`, **not** NeoForge's `RegisterPayloadHandlersEvent` and **not** the old `SimpleChannel`. Full API map captured in the Phase 2 "VERIFIED API FACTS" note above.
- **Forge vs NeoForge API drift** — Forge removed `ModelEvent.RegisterAdditional`, kept `ModelData`/`ModelProperty` (NeoForge dropped them), moved `@SubscribeEvent` to `net.minecraftforge.eventbus.api.listener.SubscribeEvent`, and changed several record/ctor signatures. Write Forge code against Forge's actual API, per the apt-ores `PORTING.md` divergence table.
- **Forge jar reliability** — pin `forge_version`; don't use "Latest"/"Recommended" (corrupted-jar history).
- **Block-entity valid-blocks on Fabric** — `FabricBlockEntityTypeBuilder` + `((FabricBlockEntityType) BlockEntityType.DISPENSER).addValidBlock(...)` (replaces NeoForge `BlockEntityTypeAddBlocksEvent`).
- **Common `Supplier`/`Holder` stubs differ per loader** — NeoForge `DeferredHolder` is both a `Holder` and a `Supplier`; Forge `RegistryObject` is only a `Supplier` (hence the `ForgeHolder` adapter); Fabric `Holder.Reference` is neither (`() -> holder.value()` for the common Supplier fields).

---

## Progress log

<!-- Append a dated entry each session: what was done, what's next, and any blockers. -->

- **2026-08-29** — Plan authored. No implementation yet.
- **2026-08-29** — **Phase 0 complete.** All three modules compile and produce jars (`./gradlew :common:test :neoforge:build :fabric:build :forge:build` green). Bumped loom-no-remap → 1.17.491 and Gradle → 9.5.0 to match apt-ores mc-26.1 (1.14-SNAPSHOT NPEs on Forge's McpExecutor; 1.17 needs Gradle 9.5). Created fabric/ + forge/ modules (build.gradle, gradle.properties, fabric.mod.json, mods.toml, stub entrypoints) and common's generated-resources srcDir. Next: Phase 1 registration hubs.
- **2026-08-29** — **Phase 1 pre-implementation research complete** (all findings captured in the Phase 1 section above — no code written yet). Verified from source: (a) common `init()` = `BiFunction<String, Supplier<T>, Holder<T>>`, satisfied on NeoForge by `DeferredHolder` and on Fabric by `Registry.registerForHolder`'s `Holder.Reference`; (b) **Forge's `DeferredRegister.register` returns `RegistryObject` (a `Supplier`, not a `Holder`)** → requires a new `ForgeHolder<T>` adapter (full sketch in Phase 1); (c) Forge 26.1.2 `DR.register()` takes a `BusGroup` from `FMLJavaModLoadingContext.get().getModBusGroup()`; (d) `unwrapKey()` is the only sync deref at init-time, `value()` is always deferred inside lambdas; (e) Fabric BEs need `FabricBlockEntityTypeBuilder` + `FabricBlockEntityType.addValidBlock` for the DISPENSER association; (f) vanilla `CreativeModeTab.builder(Row, int)` (no no-arg/`withSearchBar()` on Fabric/Forge); (g) `FlowerBlocks` must switch `neoforge.common.Tags.Blocks.ORES_*` → common `ConventionalTags.Blocks.ORES_*` (same `c:` tag, no-op semantically); (h) `PercentageAttribute` → vanilla `RangedAttribute`; (i) **only open question: Forge `BlockEntityType` ctor visibility** — try NeoForge-style `new BlockEntityType<>(factory, block)` first, fall back if it won't compile. apt-ores Forge survey done (no content-registration reference; confirmed Forge registration API + BusGroup + registry-key targets). Next: write the Fabric + Forge hubs per the runbook, then minimal PlatformImpls + entrypoint wiring, then compile.
- **2026-08-29** — **Phase 1 complete.** Wrote the full Fabric + Forge registration hubs, the `ForgeHolder<T>` adapter, minimal `PlatformImpl`s (both loaders), and rewired the two entrypoints; `./gradlew build` is green across all three modules and `common` JUnit stays green. Resolved the last open API question: **Forge's `BlockEntityType` ctor is `(BlockEntitySupplier, Set<Block>)`** → Forge BEs use `Set.of(block)` inside the deferred supplier (blocks flush before BEs). Two extra vanilla/Fabric facts surfaced during compile: `SimpleParticleType(boolean)` is `protected` on Fabric (→ `new SimpleParticleType(false) {}`), and `Registry` has no `holders()` (→ `entrySet()` for the creative tab). Fabric's `addValidBlock` was confirmed as the resolved `FabricBlockEntityType` method (the `addSupportedBlock` variant seen in a stale `fabric-object-builder` source jar is not what fabric-api 0.155.2 resolves). Forge `BusGroup` obtained via injected `FMLModContainer#getModBusGroup()` (avoids the `FMLJavaModLoadingContext.get()` removal warning). Next: Phase 2 (`@ExpectPlatform` impls + networking).
- **2026-08-29** — **Phase 3 complete.** The Fabric (`PotionsPlusFabric`/`PotionsPlusFabricClient`) and Forge (`PotionsPlusForge`) entrypoints already fully mirrored the NeoForge wiring from Phase 1/2 work; the only gap was the missing `PotionsPlusForgeClient` client entrypoint, now added. It uses eventbus 7's per-event `EventBus<T>` API (`FMLClientSetupEvent.getBus(BusGroup).addListener(...)`, verified via `javap` against the resolved `eventbus-7.0.5.jar` + `forge-26.1.2-64.1.0-universal.jar` — `BusGroup` has no `addListener`, only annotation-scanning `register(Lookup, Object)`, so the per-event static `getBus(BusGroup)` accessor is the direct route) rather than eagerly constructing client-only objects in the constructor (which is what NeoForge's sibling `PotionsPlusClient` does — safe there only because nothing in its body is dist-gated at the JVM-verification level the way a dedicated-server load of a naive Forge equivalent could be). Confirmed no `.get()`-on-unflushed-registry calls exist anywhere in the Forge/Fabric constructors, so the `FMLCommonSetupEvent.enqueueWork` deferral isn't needed yet. `./gradlew build` green across all three modules. Next: Phase 4 (event surface).
- **2026-08-29** — **Phase 2 complete.** Wrote the Fabric + Forge networking stacks and rewired the entrypoints; `./gradlew build` stays green across all three modules (common JUnit incl. `PotionContentsAccessTest` green). **Forge networking API verified from the `net.minecraftforge.network` sources jar** (the one area with no sibling reference): it is neither NeoForge's `RegisterPayloadHandlersEvent`/`PayloadRegistrar` nor the old `SimpleChannel`, but `ChannelBuilder.named(...).optional().payloadChannel().play().serverbound()/clientbound().add(Type, StreamCodec<RegistryFriendlyByteBuf, MSG>, BiConsumer<MSG, CustomPayloadEvent.Context>).build()` + `Channel.send(payload, PacketDistributor.PLAYER/TRACKING_ENTITY_AND_SELF/TRACKING_CHUNK/SERVER...)`. Two traps fixed during compile: (a) `NetworkProtocol.PLAY` is `RegistryFriendlyByteBuf`-typed, so the 7 `ByteBuf`-typed common codecs are narrow-cast via a `playCodec(...)` helper (safe — a `ByteBuf` codec handles a `RegistryFriendlyByteBuf`); (b) `ChunkPos` is a `record` → `x()`/`z()`, not `.x`/`.z`. Forge's `CustomPayloadEvent.Context` has no `player()` (client) and no `disconnect(...)` → `ForgePacketContext` resolves the client player via `Minecraft.getInstance().player` and disconnects via `getConnection().disconnect(reason)`. Fabric side uses `PayloadTypeRegistry.serverboundPlay()/clientboundPlay()` (throws on duplicate → client registrations wrap in try/catch) + `ServerPlayNetworking`/`ClientPlayNetworking.registerGlobalReceiver` + `PlayerLookup.tracking(...)`; unlike fishtastic's registrar, the client-side serverbound codec is registered so `sendToServer` (ClotheslineBehaviour) works on a dedicated client. Remaining `PlatformImpl` methods (`getChorusFruitTeleportTarget`, `onServerPlayerHeldItemChanged`, `fireCropGrowPost`, drink-time getters) stay stubbed for Phase 5. Next: Phase 3 (entrypoints) — largely already wired; formalize the wiring + `FMLCommonSetupEvent.enqueueWork` deferral.
- **2026-08-30** — **Phase 4 complete.** Ported the full NeoForge event surface to both Fabric and Forge; `./gradlew build` green across all four modules. **Fabric**: mixin-based listeners in `mixin/fabric/` — `LivingEntityMixin` (mob-effect added/expired/removed via `@Redirect` on `onEffectsRemoved` scoped to `tickEffects`/`removeAllEffects` + `@Inject` on `onEffectAdded`/`removeEffectNoUpdate`, fall via `@Inject causeFallDamage`, damage via `@Redirect getDamageAfterMagicAbsorb`, heal via `@ModifyVariable`, use-item via `@Inject updateUsingItem`), `EnchantmentHelperMixin` (`getItemEnchantmentLevel(Holder, ItemInstance)` — the 26.1.2 `ItemInstance` abstraction, cast-safe via `instanceof ItemStack`), `PlayerAdvancementsMixin` (`PlayerAdvancements#award`), `ItemEntityMixin` (`playerTouch` pickup at the `Player.onItemPickup` INVOKE); fabric-api callbacks in `event/fabric/` (`CommandRegistrationCallback`, `ServerTickEvents`, `ServerLivingEntityEvents.AFTER_DEATH`, `FabricDefaultAttributeRegistry` + `MODIFY`, `UseBlockCallback`, `ServerEntityEvents.ENTITY_LOAD`, `PlayerBlockBreakEvents.BEFORE`, `LevelRenderEvents`, `ClientTickEvents`, `ClientTooltipComponentCallback`, `ItemTooltipCallback`). **Forge**: `event/forge/` listeners against each event's static `BUS` (eventbus 7) — effects/advancement/attribute/command/tick/player events; predicate-cancel for `LivingFallEvent`+`RightClickBlock`, `Result.DENY` for `BlockEvent.BreakEvent` (no `BlockDropsEvent` on Forge — drops reconstructed via `Block.getDrops(...)`); `mixin/forge/EnchantmentHelperMixin` wired into `mods.toml` `[[mixins]]`. Key corrections vs the handoff research: (a) there is **no vanilla `Item.set(...)`** — NeoForge's `ModifyDefaultComponentsEvent` wraps `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS`, so potion stack-size=16 is done via `DATA_COMPONENT_INITIALIZERS.add(key, (components, ctx, key) -> components.set(MAX_STACK_SIZE, 16))` on both loaders; (b) `Holder.getKey()` and `ItemStack.getAttributeModifiers()` are NeoForge-only — use `Holder.unwrapKey()` and `ItemStack.getOrDefault(ATTRIBUTE_MODIFIERS, EMPTY)`. **Deferred to Phase 6**: the common `potionsplus.mixins.json` is still NOT referenced from `fabric.mod.json`/`mods.toml` — it contains `BucketItemMixin` whose `@At(INVOKE, target=…FluidType.onVaporize)` is a NeoForge-only call-site, so enabling it now would crash mixin application on Fabric/Forge (must be split first, per the Phase 6 note). Next: Phase 5 (NeoForge-only systems).
