package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Random;
import java.util.Set;

public interface ISeededPotionRecipeGenerator {
    List<BrewingCauldronRecipe> generateRecipes(PotionBuilder.PotionsAmpDurMatrix potionAmpDurMatrix, Set<PpIngredient> allRecipes, Random random);

    ISeededPotionRecipeGenerator withTieredIngredientPools(LootPoolSupplier... tieredIngredientPools);

    ISeededPotionRecipeGenerator withTieredIngredientPool(int tier, LootPoolSupplier pool);

    ISeededPotionRecipeGenerator addItemsInTagsToTierPool(int tier, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, TagKey<Item>... tags);

    ISeededPotionRecipeGenerator addItemsToTierPool(int tier, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, ItemLike... items);

    ISeededPotionRecipeGenerator clearTierPool(int tier);

    ISeededPotionRecipeGenerator clearAllTierPools();
}
