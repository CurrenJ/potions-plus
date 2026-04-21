# Potions Plus — Migration Progress (1.21.5 → 26.1.2 + Architectury)

> Full plan: `MIGRATION_PLAN_26.1.2.md`
> Branch: `26.1.2`

## Chunks

| # | Name | Status | Notes |
|---|------|--------|-------|
| 1 | [Build Infrastructure](#chunk-1--build-infrastructure) | ✅ Done | |
| 2 | [Source Relocation](#chunk-2--source-relocation) | ✅ Done | |
| 3 | [Metadata & Access Widener](#chunk-3--metadata--access-widener) | ✅ Done | |
| 4 | [Mechanical API Renames](#chunk-4--mechanical-api-renames) | ⬜ Pending | |
| 5 | [Loot + Data API Changes](#chunk-5--loot--data-api-changes) | ⬜ Pending | |
| 6 | [Platform Abstraction](#chunk-6--platform-abstraction) | ⬜ Pending | |
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
- [ ] 4.1 `ResourceLocation` → `Identifier` (562 occurrences, 97 files); `ResourceKey#location()` → `#identifier()`
- [ ] 4.2 `ClickEvent` / `HoverEvent` — check for constructor calls; convert to sealed interface pattern
- [ ] 4.3 `registryOrThrow` → `lookupOrThrow` — confirmed zero uses, skip
- [ ] 4.4 `WeightedList` / `Weighted` — already migrated, skip
- [ ] 4.5 Remove `ItemBlockRenderTypes.setRenderLayer` from `core/BlockRenderLayers.java`

---

## Chunk 5 — Loot + Data API Changes

**Phases:** 6.1–6.5, 7.1–7.7 from migration plan

**Goal:** Loot type unrolling complete; item model properties rewritten; no remaining DataComponent or PotionBrewing API breakage.

### Steps
- [ ] 6.1–6.3 Loot type unrolling: 4 conditions + 1 number provider (`getType()` → `codec()`, registry holds `MapCodec` directly)
- [ ] 6.4–6.5 Confirm `ConsumeEffect` and `RecipeType` getType() are NOT affected — skip
- [ ] 7.1 DataComponents — confirm no `HIDE_ADDITIONAL_TOOLTIP` / `HIDE_TOOLTIP` usage, skip
- [ ] 7.2 `Item#inventoryTick` signature change — confirm zero overrides, skip
- [ ] 7.3 `BlockBehaviour#onRemove` split — confirm zero overrides, skip
- [ ] 7.4 `PotionBrewing` API audit (`PotionBuilder.java`, `Potions.java`, `SeededPotionRecipeBuilder.java`)
- [ ] 7.5 `RecipeManager` internals audit — verify AT fields still exist in 26.1.2
- [ ] 7.6 Delete stale `ItemProperties` AT entry
- [ ] 7.7 Rewrite 3 item model properties to `SelectItemModelProperty` / `ConditionalItemModelProperty`

---

## Chunk 6 — Platform Abstraction

**Phase:** 5.1–5.4 from migration plan

**Goal:** `common/` module has zero `net.neoforged.*` imports. Pragmatic first pass — registry classes stay in `neoforge/`.

### Steps
- [ ] 5.1 Identify all `net.neoforged.*` imports remaining in `common/` after Chunk 2
- [ ] 5.2 Leave registry classes (`Blocks.java`, `Items.java`, etc.) in `neoforge/`; expose static holders to common
- [ ] 5.3 `ServerTickHandler` / `DelayedEvents` stay in `neoforge/` for now
- [ ] 5.4 Scan common/ for `@OnlyIn` → convert to `@Environment(EnvType.CLIENT)`

---

## Chunk 7 — Rendering Overhaul

**Phase:** 8.1–8.6 from migration plan

**Goal:** All 6 BE renderers and entity renderer converted to extract+submit pattern; `BlockRenderDispatcher` and `ItemRenderer` references replaced.

### Steps
- [ ] 8.1 Convert 6 `BlockEntityRenderer`s to two-generic pattern with `RenderState` classes
- [ ] 8.2 Replace `BlockRenderDispatcher` with `BlockModelResolver`
- [ ] 8.3 Replace `ItemRenderer` with `ItemStackRenderState` / `ItemModelResolver`
- [ ] 8.4 Convert `GrunglerRenderer` entity renderer
- [ ] 8.5 Fix `GameRenderer` references (`ModelManager` param, `PROJECTION_Z_NEAR` moved)
- [ ] 8.6 Audit `LeashRenderer` for breaking changes

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
