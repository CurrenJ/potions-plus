# Potions Plus — Migration Progress (1.21.5 → 26.1.2 + Architectury)

> Full plan: `MIGRATION_PLAN_26.1.2.md`
> Branch: `26.1.2`

## Chunks

| # | Name | Status | Notes |
|---|------|--------|-------|
| 1 | [Build Infrastructure](#chunk-1--build-infrastructure) | ✅ Done | |
| 2 | [Source Relocation](#chunk-2--source-relocation) | ✅ Done | |
| 3 | [Metadata & Access Widener](#chunk-3--metadata--access-widener) | ✅ Done | |
| 4 | [Mechanical API Renames](#chunk-4--mechanical-api-renames) | ✅ Done | |
| 5 | [Loot + Data API Changes](#chunk-5--loot--data-api-changes) | ✅ Done | |
| 6 | [Platform Abstraction](#chunk-6--platform-abstraction) | ✅ Done | 3 files deferred to Chunk 7 (renderer, GrunglerModel, FilterHopperBlockEntity) |
| 7 | [Rendering Overhaul](#chunk-7--rendering-overhaul) | ⬜ Pending | |
| 8 | [GUI Overhaul](#chunk-8--gui-overhaul) | ⬜ Pending | |
| 9 | [Mixin Rewrites + Datagen + Verification](#chunk-9--mixin-rewrites--datagen--verification) | ⬜ Pending | |

---

## Chunk 1 — Build Infrastructure

**Phases:** 1.1–1.8 from migration plan

**Goal:** `./gradlew tasks` loads the build (compile failures expected — those are Chunks 2+).

### Steps
- [x] 1.1 Delete `forge/` directory
- [x] 1.2 Gradle wrapper → 9.2.1
- [x] 1.3 Rewrite `settings.gradle` (Architectury plugin repos, remove forge subproject)
- [x] 1.4 Rewrite `gradle.properties` (MC 26.1.2, NeoForge 26.1.2.4-beta, Java 25, dep placeholders)
- [x] 1.5 Rewrite root `build.gradle` (loom-no-remap + architectury-plugin)
- [x] 1.6 Write `common/build.gradle`
- [x] 1.7 Rewrite `neoforge/build.gradle`
- [x] 1.7.1 Create `license.txt` at repo root (MIT)
- [x] 1.8 Sanity check: `./gradlew tasks` loads

### Blockers / Notes
- JEI, GlitchCore, TerraBlender versions for 26.1.2 are **placeholders** in `gradle.properties` — must be verified before Chunk 5+ runtime work. Check:
  - JEI: https://maven.blamejared.com/mezz/jei/
  - GlitchCore / TerraBlender: https://github.com/glitchfiend/GlitchCore-Feedback / TerraBlender releases

---

## Chunk 2 — Source Relocation

**Phases:** 2.1–2.4 from migration plan

**Goal:** All 335 Java files moved to `common/src/main/java/`; NeoForge-specific files identified and kept in `neoforge/`; resources redistributed.

### Steps
- [x] 2.1 Move 563 Java files to `common/src/main/java/grill24/potionsplus/`
- [x] 2.2 Move shared resources to `common/src/main/resources/`; keep NeoForge metadata in `neoforge/src/main/resources/`
- [x] 2.3 Re-home platform-specific classes back to `neoforge/src/main/java/` — core/, event/, data/, client/integration/jei/, behaviour/*LootModifier+MossBehaviour, config/PotionsPlusConfig, utility/DelayedEvents+ServerTickHandler (117 files in neoforge, 446 in common)
- [x] 2.4 Compile errors expected — those are Chunks 4+

---

## Chunk 3 — Metadata & Access Widener

**Phases:** 3.1–3.4 from migration plan

**Goal:** Pack metadata, mod toml, and access widener all correct for 26.1.2.

### Steps
- [x] 3.1 Update `pack.mcmeta` → min/max_format 84 (SharedConstants.RESOURCE_PACK_FORMAT_MAJOR=84 for 26.1.2)
- [x] 3.2 Rewrite `neoforge.mods.toml` — modern type="required" style, glitchcore/terrablender deps
- [x] 3.3 Delete `accesstransformer.cfg`; write `potionsplus.accesswidener` — verified each entry against 26.1.2 sources; deleted 11 stale entries (WeightedEntry wrap, RecipeManager byType/byName, ItemProperties PROPERTIES, Item maxStackSize, BlockLoot SRG names, OverworldBiomes biome() overloads + globalOverworldGeneration (already public), SpriteResourceLoader ctor (now interface), LootModifier Forge import, RecipeProvider has (already public)); updated 3 entries (PotionBrewing field names, interactions type, MultiNoiseBiomeSource parameters type)
- [x] 3.4 Update `potionsplus.mixins.json` — minVersion 0.8.5, compatibilityLevel JAVA_25

---

## Chunk 4 — Mechanical API Renames

**Phases:** 4.1–4.5 from migration plan

**Goal:** Bulk of "symbol not found" compile errors cleared.

### Steps
- [x] 4.1 `ResourceLocation` → `Identifier` across 74 files (import + type rename via perl word-boundary); `ResourceKey#location()` → `#identifier()` (45 call sites); `soundEvent.location()` preserved (SoundEvent record accessor unchanged in 26.1.2)
- [x] 4.2 `ClickEvent` / `HoverEvent` — already using sealed subtype pattern (`ClickEvent.RunCommand`, `HoverEvent.ShowText`); skip
- [x] 4.3 `registryOrThrow` → `lookupOrThrow` — zero uses, skip
- [x] 4.4 `WeightedList` / `Weighted` — already migrated, skip
- [x] 4.5 `ItemBlockRenderTypes.setRenderLayer` removed from `BlockRenderLayers.java` (API gone in 26.1.2). **Follow-up resolved in Chunk 7:** no `"render_type"` field is needed. 26.1.2 derives translucency from baked-quad material flags (`BakedQuad.FLAG_TRANSLUCENT`, propagated through `BlockStateModel#materialFlags`), i.e. from the texture itself at bake time - there is no per-block render layer to register any more. `BlockRenderLayers.java` is now a no-op stub and can be deleted once cutout blocks (uranium glass, icicle, decorative fire, potion beacon, plants) are visually confirmed in game

---

## Chunk 5 — Loot + Data API Changes

**Phases:** 6.1–6.5, 7.1–7.7 from migration plan

**Goal:** Loot type unrolling complete; item model properties rewritten; no remaining DataComponent or PotionBrewing API breakage.

### Steps
- [x] 6.1–6.3 Loot type unrolling: 4 conditions + 1 number provider (`getType()` → `codec()`, registry holds `MapCodec` directly); `LootItemConditionType` / `LootNumberProviderType` wrappers gone — registry now holds `MapCodec<? extends LootItemCondition/NumberProvider>` directly; updated `HasPlayerAbilityCondition`, `IsInBiomeCondition`, `IsInBiomeTagCondition`, `LootItemBlockTagCondition`, `GaussianDistributionGenerator`; updated `LootItemConditions.java`, `NumberProviders.java`, `LootItemFunctions.java` registries
- [x] 6.4–6.5 Confirmed `ConsumeEffect` still uses `Type<>` wrapper (not unrolled) — no changes; `RecipeType` uses simple registry pattern — no changes
- [x] 7.1 DataComponents — no `HIDE_ADDITIONAL_TOOLTIP` / `HIDE_TOOLTIP` usage found — skip
- [x] 7.2 `Item#inventoryTick` — zero overrides in codebase — skip
- [x] 7.3 `BlockBehaviour#onRemove` — zero overrides — skip
- [x] 7.4 `PotionBrewing` fields (`potionMixes`, `containerMixes`) verified in AW; not directly accessed in mod code
- [x] 7.5 `RecipeManagerMixin` verified: shadows `RecipeMap recipes` (correct 26.1.2 field); injects into `prepare()` returning `RecipeMap`; old `byType`/`byName` fields gone and not referenced
- [x] 7.6 `ItemProperties` AT entry already deleted in Chunk 3
- [x] 7.7 All 3 item model properties already use 26.1.2 API: `GeneticProperty` / `EdibleChoiceProperty` implement `RangeSelectItemModelProperty`; `BrassicaOleraceaProperty` implements `SelectItemModelProperty<Variation>`; registered via `RegisterRangeSelectItemModelPropertyEvent` / `RegisterSelectItemModelPropertyEvent`

---

## Chunk 6 — Platform Abstraction

**Phase:** 5.1–5.4 from migration plan

**Goal:** `common/` module has zero `net.neoforged.*` imports. Pragmatic first pass — registry classes stay in `neoforge/`.

### Steps
- [x] 5.1 Identify all `net.neoforged.*` imports remaining in `common/` after Chunk 2
- [x] 5.2 Leave registry classes (`Blocks.java`, `Items.java`, etc.) in `neoforge/`; expose static holders to common
- [x] 5.3 `ServerTickHandler` / `DelayedEvents` stay in `neoforge/` for now
- [ ] 5.4 Scan common/ for `@OnlyIn` → convert to `@Environment(EnvType.CLIENT)`

### What was done
- Removed all `net.neoforged.*` imports from 30+ common/ files
- Moved NeoForge `@EventBusSubscriber`/`@SubscribeEvent` handlers to neoforge/ event listeners:
  - Created `AbilityListeners.java` (CriticalHitEvent → ChainLightning/StunShot, LivingDamageEvent.Pre → HotPotato, LivingDrownEvent → LastBreath)
  - Created `SkillListeners.java` (BlockEvent.BreakEvent, BlockDropsEvent, LivingExperienceDropEvent, StatAwardEvent)
  - Created `ClientGameListeners.java` (RenderFrameEvent → ClientTickHandler, ClientTickEvent → ClientTickHandler, MovementInputUpdateEvent → DoubleJumpAbility)
  - Created `WorldGenListeners.java` (FMLCommonSetupEvent → OverworldBiomesRegion)
  - Extended `EntityListeners.java` with ItemTossEvent → ServerPlayerHeldItemChangedEvent
  - Extended `PlayerListeners.java`: `tickPointEarningHistory(MinecraftServer)`, `ClotheslineBehaviour.doClotheslineInteractions` and `UraniumOreBlock.tryLeftClickBlock` now take plain params
- Tooltip handlers (`PotionEffectTooltips`, `OwnerDataComponent`, `EdibleRewardGranterDataComponent`, `BaitItem`, `WeightDataComponent`, `GeneticCropItem`, `BrewingTooltips`) now plain static methods called from `ItemListenersGame`
- Effect handlers (`BoneBuddyEffect`, `BouncingEffect`, `ExplodingEffect`, `FallOfTheVoidEffect`, `FlyingTimeEffect`, `GeodeGraceEffect`, `SoulMateEffect`, `TeleportationEffect`) already cleaned in previous session
- Packet handlers in common/ use `PacketContext` interface instead of `IPayloadContext`
- Added `Platform.@ExpectPlatform` stubs for: `onServerPlayerHeldItemChanged`, `fireCropGrowPost`, `postClientInjectResourcesEvent`, `postClientInjectResourceStacksEvent`
- Replaced `NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(...))` in `InventoryMixin` with `Platform.onServerPlayerHeldItemChanged`
- Replaced `CommonHooks.fireCropGrowPost` in `GeneticCropBlock` with `Platform.fireCropGrowPost`
- Replaced `ModLoader.postEvent(new ClientInjectResourcesEvent/StacksEvent)` in `ResourceUtility` with `Platform.*` methods
- Replaced `extends AttachmentHolder` in `EntityMixin` with no extension
- Replaced `implements IItemExtension` in `ItemMixin` with just MC interfaces
- Removed `@EventBusSubscriber` debug handler from `Genotype`; moved TerraBlender registration from `OverworldBiomesRegion` to `WorldGenListeners`
- 3 files still have NeoForge imports but are deferred: `ClotheslineBlockEntityRenderer` (RenderLevelStageEvent — Chunk 7), `GrunglerModel` (AnimationHolder — Chunk 7), `FilterHopperBlockEntity` (capabilities — deferred)

---

## Chunk 7 — Rendering Overhaul

**Phase:** 8.1–8.6 from migration plan

**Goal:** All 6 BE renderers and entity renderer converted to extract+submit pattern; `BlockRenderDispatcher` and `ItemRenderer` references replaced.

### Steps
- [ ] 8.1 Convert 6 `BlockEntityRenderer`s to two-generic pattern with `RenderState` classes
  - [x] HerbalistsLecternBlockEntityRenderer - full render logic ported (input item animation, orbiting recipe icons + sub-icons, center rarity display); also gave the block a real blockstate (was `modelGenerator(null)`)
  - [x] PotionBeaconBlockEntityRenderer - input item rest animation + drifting item-particle physics; real blockstate added
  - [x] SanguineAltarBlockEntityRenderer
  - [x] AbyssalTroveBlockEntityRenderer - orbiting ingredient grid (tiers/rows) ported to extract/submit; per-item scale/subIconScale animation state still lives on `AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem` and is mutated during extraction (matches persistent-state pattern used elsewhere); `currentDisplayRotation` lerp moved from being recomputed per-item each frame (old bug) to once per extractRenderState call; real blockstate added (was `modelGenerator(null)`), removed from `BlockStateProvider` exclusion list
  - [x] ClotheslineBlockEntityRenderer - rope, fence posts and hanging items ported to extract/submit. Rope geometry now goes through `LeashRenderer.submitLeashBetweenPoints` (new `submitCustomGeometry` path); endpoints are stored **relative to the block entity origin** since `submit()` has no world coordinates. Fence posts use `BlockModelRenderState` + `BlockModelResolver.update`. **Behaviour change:** the old per-frame `clotheslinesRendered` set (cleared by `RenderLevelStageEvent`) no longer lines up with extract/submit, so the shared geometry is now drawn only from the *left* end (`state.isLeftEnd`); deleted the now-dead `NeoClotheslineRendererEvents`
  - [x] BrewingCauldronBlockEntityRenderer - floating brew result, orbiting ingredients (per-ingredient shrink as the brew progresses, computed at extract time) and the no-xp/no-heat status icon
- [x] 8.2 Replace `BlockRenderDispatcher` with `BlockModelResolver` - zero references remain
- [x] 8.3 Replace `ItemRenderer` with `ItemStackRenderState` / `ItemModelResolver` - zero `getItemRenderer()` / `renderStatic` references remain
- [x] 8.4 Convert `GrunglerRenderer` entity renderer - carried block now resolved into a `BlockModelRenderState` on `GrunglerRenderState` at extract time and submitted with entity light + `LivingEntityRenderer.getOverlayCoords`
- [x] 8.5 `GameRenderer` references audited - `minecraft` field, `tick()` and `displayItemActivation(ItemStack)` all still valid targets in 26.1.2; no `PROJECTION_Z_NEAR` / `ModelManager` usage in mod code. **Gap found:** the `renderItemActivationAnimation` inject was dropped during the port, so `ItemActivationAnimation` is stored and ticked but never drawn. In 26.1.2 that method moved to `ScreenEffectRenderer.renderItemActivationAnimation(PoseStack, float, SubmitNodeCollector)` - deferred to Chunk 8 (9.7) because the animation renders through `GuiGraphics`
- [x] 8.6 Audit `LeashRenderer` - already on 26.1.2 APIs (`RenderTypes.leash()`, `LightCoordsUtil.pack`); added the submit-pipeline variant. The `MultiBufferSource` overloads are kept only for `calculateLeashPoints` (used by `ClotheslineBlockEntityBakedRenderData`)

---

## Chunk 8 — GUI Overhaul

**Phase:** 9.1–9.6 from migration plan

**Goal:** All screens and screen elements compile and render with `GuiGraphicsExtractor`.

### Steps
- [ ] 9.1 Reference gelatin-ui's `GuiGraphicsMixin` for 26.1.2 pattern
- [ ] 9.2 Rewrite `GuiGraphicsMixin` — target `GuiGraphicsExtractor`, `Matrix3x2fStack`, `extractText`
- [ ] 9.3 Update `IGuiGraphicsExtension` method signatures
- [ ] 9.4 Update 4 container screens (`PotionsPlusScreen`, 3× FilterHopperScreen) — `renderBg` → `extractBackground`
- [ ] 9.5 Rename `render` → `extractRenderState` + `GuiGraphics` → `GuiGraphicsExtractor` across 17 screen element files
- [ ] 9.6 Verify keymapping APIs unchanged
- [ ] 9.7 Restore the item-activation animation hook (carried over from 8.5): retarget the dropped `renderItemActivationAnimation` inject to `ScreenEffectRenderer.renderItemActivationAnimation(PoseStack, float, SubmitNodeCollector)` and rewrite `ItemActivationAnimation.render` off `GuiGraphics`

---

## Chunk 9 — Mixin Rewrites + Datagen + Verification

**Phases:** 10.1–10.4, 11.1–11.6, 12.1–12.6 from migration plan

**Goal:** Clean build, working client + server + datagen.

### Steps
- [ ] 10.1 Retarget `GuiGraphicsMixin` → `GuiGraphicsExtractor`, `GameRendererMixin` method audit
- [ ] 10.2 Verify all 36 mixins against 26.1.2 sources (method names, descriptors, @At targets)
- [ ] 10.3 Per-mixin: open target in debug_src, check every @Inject/@Shadow/@Redirect
- [ ] 10.4 Opportunistically replace mixins with NeoForge events (`BucketItemMixin`, `ItemEntityMixin`, `AbstractProjectileDispenseBehaviorMixin`)
- [ ] 11.1 Fix `BlockModelGenerators` API changes across 10 datagen files (removed methods, Material vs Identifier)
- [ ] 11.2 Verify `BiomeModifierProvider` constructor signature
- [ ] 11.3–11.6 Audit remaining datagen providers (loot, recipes, lang)
- [ ] 12.1–12.6 Full build + runtime verification checklist
