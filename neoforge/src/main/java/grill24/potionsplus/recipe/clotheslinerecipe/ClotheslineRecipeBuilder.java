package grill24.potionsplus.recipe.clotheslinerecipe;

import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import static grill24.potionsplus.utility.Utility.ppId;

public class ClotheslineRecipeBuilder extends ShapelessProcessingRecipeBuilder<ClotheslineRecipe, ClotheslineRecipeBuilder> {

    public ClotheslineRecipeBuilder ingredient(ItemLike item) {
        return ingredients(new ItemStack(item));
    }

    @Override
    protected RecipeHolder<ClotheslineRecipe> build() {
        ClotheslineRecipe recipe = new ClotheslineRecipe(category, ingredients, result, processingTime, canShowInJei);
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, ppId(recipe.getUniqueRecipeName()));
        return new RecipeHolder<>(recipeKey, recipe);
    }

    @Override
    protected ClotheslineRecipeBuilder self() {
        return this;
    }
}
