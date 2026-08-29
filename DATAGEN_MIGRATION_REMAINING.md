# Datagen Migration Remaining Tasks (MC 26.1)

The following data providers are temporarily disabled because they create `ItemStack`
objects during generation, which fails in MC 26.1 because data components must be
bound before `new ItemStack(item)` can be called.

When re-enabling each provider, wrap all `new ItemStack()` calls with
`PUtil.safeStack()` or similar try-catch pattern.

## Disabled Providers

### 1. RecipeProvider
**File:** `neoforge/src/main/java/grill24/potionsplus/data/neoforge/RecipeProvider.java`
**Disabled in:** `DataGen.java` (line: `event.createProvider(RecipeProvider.Runner::new)`)
**Issue:** `buildRecipes()` creates ItemStacks via `new ItemStack(ItemLike)` and
`PotionContents.createItemStack()`. Also `ShapelessProcessingRecipeBuilder.ingredients()`
streams items through `ItemStack::new`.
**Fix plan:**
- Wrap all `new ItemStack()` calls in RecipeProvider with `safeStack()`
- Fix `ShapelessProcessingRecipeBuilder.ingredients(ItemLike...)` to use safe wrapper
- Fix `PotionContents.createItemStack()` calls via PUtil.createPotionItemStack try-catch

### 2. AdvancementProvider
**File:** `neoforge/src/main/java/grill24/potionsplus/data/neoforge/AdvancementProvider.java`
**Disabled in:** `DataGen.java` (line: `event.createProvider(AdvancementProvider::new)`)
**Issue:** `createBrewingCauldronAdvancements()` (line 398) creates ItemStacks that fail
DataResult validation because components aren't bound.
**Fix plan:**
- Audit all ItemStack creation in AdvancementProvider
- Wrap in safe creation pattern or refactor to use ItemLike references

### 3. LootTableProvider
**File:** `neoforge/src/main/java/grill24/potionsplus/data/neoforge/LootTableProvider.java`
**Disabled in:** `DataGen.java` (line: `event.createProvider(LootTableProvider::new)`)
**Issue:** Likely creates ItemStacks for loot entries.
**Fix plan:**
- Audit ItemStack creation in LootTableProvider
- Wrap in safe pattern

### 4. GlobalLootModifierProvider
**File:** `neoforge/src/main/java/grill24/potionsplus/data/loot/neoforge/GlobalLootModifierProvider.java`
**Disabled in:** `DataGen.java` (line: `event.createProvider(GlobalLootModifierProvider::new)`)
**Issue:** References `ConfiguredPlayerAbilities` stubs which are null during datagen
(common stubs not populated by neoforge counterpart yet).
**Fix plan:**
- Fix common stub population for ConfiguredPlayerAbilities before datagen
- Or refactor GlobalLootModifierProvider to not depend on ConfiguredPlayerAbilities at construction time

## Disabled Datapack Registries

### 5. Custom Datapack Registries (Skills, SkillPointSources, PlayerAbilities, GrantableRewards)
**Disabled in:** `DataGen.java` (RegistrySetBuilder)
**Issue:** `ConfiguredGrantableRewards.<clinit>` creates ItemStack objects during static
initialization (e.g., `new ItemStack(SkillLootItems.MOSSASHIMI.getValue())` at line 27).
All skill classes (Archery, Axes, etc.) create ItemStacks for skill icons.
**Fix plan:**
- Wrap all `new ItemStack()` in skill class `generate()` methods with `PUtil.safeStack()`
  (DONE for skill/configured/*.java, but static field initialization in
  ConfiguredGrantableRewards still needs fixing)
- Change ConfiguredGrantableRewards static fields to use lazy Supplier<ItemStack> pattern
- Re-enable `.add()` entries in RegistrySetBuilder

## Other Changes Made for Datagen

### Inlined Private API Calls
- `Placements.java`: Replaced `OrePlacements.commonOrePlacement()` (now private) with
  inline `CountPlacement.of()`, `InSquarePlacement.spread()`, `BiomeFilter.biome()`
- `IceCave.java`, `AridCave.java`, `VolcanicCave.java`: Replaced
  `OverworldBiomes.globalOverworldGeneration()` (package not exported to our module)
  with inline `BiomeDefaultFeatures` calls

### BlockStateProvider
- Excluded blocks without generated blockstates from `getKnownBlocks()`
- Excluded items without generated item models from `getKnownItems()`

### Source Set
- Fixed `sourceSets.main.resources.srcDir` from `src/main/generated` to
  `src/generated/resources` — this was preventing generated worldgen data from loading

### Configured Feature JSONs
- Manually fixed `random_patch` -> `vegetation_patch` in 3 JSON files
