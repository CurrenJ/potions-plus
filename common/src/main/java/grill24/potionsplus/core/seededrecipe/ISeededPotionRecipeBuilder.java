package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients.*;

public interface ISeededPotionRecipeBuilder {
    List<RecipeHolder<BrewingCauldronRecipe>> generateRecipes(PotionBuilder.PotionsPlusPotionGenerationData potionsPlusPotionGenerationData, Set<PpMultiIngredient> allRecipes, RandomSource random);

    ISeededPotionRecipeBuilder withSamplingCountForRarity(Rarity rarity, int count);

    ISeededPotionRecipeBuilder withRaritySamplingConfigs(Map<Rarity, IngredientSamplingConfig> rarityConfig);

    ISeededPotionRecipeBuilder withIngredientPoolForRarity(Rarity rarity, LootPoolSupplier pool);

    ISeededPotionRecipeBuilder addItemsToRaritySamplingPool(PotionUpgradeIngredients.Rarity rarity, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, ItemLike... items);

    ISeededPotionRecipeBuilder clearRaritySamplingConfig(PotionUpgradeIngredients.Rarity rarity);

    ISeededPotionRecipeBuilder clearAllRaritySamplingConfigs();

    ISeededPotionRecipeBuilder withRarityCount(Rarity rarity, int count);
}
