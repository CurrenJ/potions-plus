package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.*;

public class SeededPotionRecipeBuilder implements ISeededPotionRecipeBuilder {
    // TODO: New Brewing Cauldron Recipes Rarity System
    public static final SeededPotionRecipeBuilder DEFAULT_POTION_RECIPE_GENERATOR = emptyPools().withRaritySamplingConfigs(new HashMap<>() {{
        put(PotionUpgradeIngredients.Rarity.COMMON, new PotionUpgradeIngredients.IngredientSamplingConfig(SeededIngredientsLootTables.COMMON_INGREDIENTS, 1));
        put(PotionUpgradeIngredients.Rarity.RARE, new PotionUpgradeIngredients.IngredientSamplingConfig(SeededIngredientsLootTables.RARE_INGREDIENTS, 0));
    }});
    private Map<PotionUpgradeIngredients.Rarity, PotionUpgradeIngredients.IngredientSamplingConfig> ingredientSamplingConfig;

    public static SeededPotionRecipeBuilder emptyPools() {
        return new SeededPotionRecipeBuilder();
    }

    public static SeededPotionRecipeBuilder defaultPools() {
        return new SeededPotionRecipeBuilder(DEFAULT_POTION_RECIPE_GENERATOR);
    }

    public SeededPotionRecipeBuilder(SeededPotionRecipeBuilder seededPotionRecipeBuilder) {
        this.ingredientSamplingConfig = new HashMap<>(seededPotionRecipeBuilder.ingredientSamplingConfig);
    }

    protected SeededPotionRecipeBuilder() {
        this.ingredientSamplingConfig = new HashMap<>();
    }

    @Override
    public SeededPotionRecipeBuilder withRaritySamplingConfigs(Map<PotionUpgradeIngredients.Rarity, PotionUpgradeIngredients.IngredientSamplingConfig> rarityConfig) {
        this.ingredientSamplingConfig.clear();
        this.ingredientSamplingConfig.putAll(rarityConfig);
        return this;
    }

    @Override
    public SeededPotionRecipeBuilder withIngredientPoolForRarity(PotionUpgradeIngredients.Rarity rarity, LootPoolSupplier pool) {
        PotionUpgradeIngredients.IngredientSamplingConfig existingConfig = this.ingredientSamplingConfig.getOrDefault(rarity, PotionUpgradeIngredients.IngredientSamplingConfig.empty());
        PotionUpgradeIngredients.IngredientSamplingConfig newConfig = new PotionUpgradeIngredients.IngredientSamplingConfig(pool, existingConfig.count());

        this.ingredientSamplingConfig.put(rarity, newConfig);
        return this;
    }

    @SafeVarargs
    @Override
    public final SeededPotionRecipeBuilder addItemsInTagsToRaritySamplingPool(PotionUpgradeIngredients.Rarity rarity, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, TagKey<Item>... tags) {
        PotionUpgradeIngredients.IngredientSamplingConfig existingConfig = this.ingredientSamplingConfig.getOrDefault(rarity, PotionUpgradeIngredients.IngredientSamplingConfig.empty());

        final LootPoolSupplier existingPoolSupplier = existingConfig.pool();
        LootPoolSupplier newPoolSupplier = () -> {
            LootPool.Builder pool = existingPoolSupplier.getLootPool();
            SeededIngredientsLootTables.addItemsInTags(pool, weightingMode, weight, tags);
            return pool;
        };

        PotionUpgradeIngredients.IngredientSamplingConfig newConfig = new PotionUpgradeIngredients.IngredientSamplingConfig(newPoolSupplier, existingConfig.count());
        this.ingredientSamplingConfig.put(rarity, newConfig);
        return this;
    }

    @Override
    public SeededPotionRecipeBuilder addItemsToRaritySamplingPool(PotionUpgradeIngredients.Rarity rarity, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, ItemLike... items) {
        PotionUpgradeIngredients.IngredientSamplingConfig existingConfig = this.ingredientSamplingConfig.getOrDefault(rarity, PotionUpgradeIngredients.IngredientSamplingConfig.empty());

        final LootPoolSupplier existingPoolSupplier = existingConfig.pool();
        LootPoolSupplier newPoolSupplier = () -> {
            LootPool.Builder pool = existingPoolSupplier.getLootPool();
            SeededIngredientsLootTables.addItems(pool, weightingMode, weight, items);
            return pool;
        };
        PotionUpgradeIngredients.IngredientSamplingConfig newConfig = new PotionUpgradeIngredients.IngredientSamplingConfig(newPoolSupplier, existingConfig.count());

        this.ingredientSamplingConfig.put(rarity, newConfig);
        return this;
    }

    @Override
    public SeededPotionRecipeBuilder clearRaritySamplingConfig(PotionUpgradeIngredients.Rarity rarity) {
        this.ingredientSamplingConfig.put(rarity, PotionUpgradeIngredients.IngredientSamplingConfig.empty());
        return this;
    }

    @Override
    public SeededPotionRecipeBuilder clearAllRaritySamplingConfigs() {
        this.ingredientSamplingConfig.clear();
        return this;
    }

    @Override
    public ISeededPotionRecipeBuilder withRarityCount(PotionUpgradeIngredients.Rarity rarity, int count) {
        this.ingredientSamplingConfig.put(rarity, new PotionUpgradeIngredients.IngredientSamplingConfig(ingredientSamplingConfig.getOrDefault(rarity, PotionUpgradeIngredients.IngredientSamplingConfig.empty()).pool(), count));
        return this;
    }

    @Deprecated
    public List<RecipeHolder<BrewingCauldronRecipe>> generateRecipes(PotionBuilder.PotionsPlusPotionGenerationData generationData, Set<PpMultiIngredient> alreadyUsedRecipeInputs, RandomSource random) {
        IPotionUpgradeIngredients potionUpgradeIngredients = new PotionUpgradeIngredients(
                generationData.potion,
                ingredientSamplingConfig,
                random,
                alreadyUsedRecipeInputs);

        return generateRecipes(generationData, potionUpgradeIngredients);
    }

    @Override
    public ISeededPotionRecipeBuilder withSamplingCountForRarity(PotionUpgradeIngredients.Rarity rarity, int count) {
        PotionUpgradeIngredients.IngredientSamplingConfig existingConfig = this.ingredientSamplingConfig.getOrDefault(rarity, PotionUpgradeIngredients.IngredientSamplingConfig.empty());
        PotionUpgradeIngredients.IngredientSamplingConfig newConfig = new PotionUpgradeIngredients.IngredientSamplingConfig(existingConfig.pool(), count);

        this.ingredientSamplingConfig.put(rarity, newConfig);
        return this;
    }

    protected List<RecipeHolder<BrewingCauldronRecipe>> generateRecipes(PotionBuilder.PotionsPlusPotionGenerationData potionsPlusPotionGenerationData, IPotionUpgradeIngredients ingredients) {
        return brewingCauldronPotionUpgrades(0.1F, 100, "has_potion", potionsPlusPotionGenerationData, ingredients);
    }

    protected static List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronPotionUpgrades(float experience, int baseProcessingTime, String advancementNameIngredient, PotionBuilder.PotionsPlusPotionGenerationData potionsPlusPotionGenerationData, IPotionUpgradeIngredients potionUpgradeIngredients) {
        List<RecipeHolder<BrewingCauldronRecipe>> allRecipes = new ArrayList<>();

        PpMultiIngredient nonPotionIngredients = potionUpgradeIngredients.getBasePotionIngredients();
        if (nonPotionIngredients.size() == 0) {
            if (PotionsPlus.Debug.DEBUG && PotionsPlus.Debug.DEBUG_POTION_INGREDIENTS_GENERATION) {
                PotionsPlus.LOGGER.error("[BCR] Ingredients for base potion are null: {}", potionsPlusPotionGenerationData.potion);
            }
        } else {
            List<PpIngredient> allIngredients = new ArrayList<>();
            allIngredients.addAll(nonPotionIngredients.split());
            allIngredients.add(PpIngredient.of(PUtil.createPotionItemStack(Potions.AWKWARD, PUtil.PotionType.POTION)));

            RecipeHolder<BrewingCauldronRecipe> recipe = new BrewingCauldronRecipeBuilder()
                    .result(PUtil.createPotionItemStack(potionsPlusPotionGenerationData.potion, PUtil.PotionType.POTION))
                    .ingredients(allIngredients.toArray(new PpIngredient[0]))
                    .processingTime(baseProcessingTime)
                    .group(advancementNameIngredient)
                    .experience(experience)
                    .build();
            allRecipes.add(recipe);
        }

        return allRecipes;
    }
}
