package grill24.potionsplus.recipe.clotheslinerecipe;

import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import static grill24.potionsplus.utility.Utility.ppId;

public class ClotheslineRecipeBuilder extends ShapelessProcessingRecipeBuilder<ClotheslineRecipe, ClotheslineRecipeBuilder> {

    public ClotheslineRecipeBuilder ingredient(ItemLike item) {
        return ingredients(new ItemStack(item));
    }

    @Override
    protected RecipeHolder<ClotheslineRecipe> build() {
        ClotheslineRecipe recipe = new ClotheslineRecipe(category, group, ingredients, result, processingTime, canShowInJei);
        ResourceLocation recipeId = ppId(recipe.getUniqueRecipeName());
        return new RecipeHolder<>(recipeId, recipe);
    }

    @Override
    protected ClotheslineRecipeBuilder self() {
        return this;
    }
}
