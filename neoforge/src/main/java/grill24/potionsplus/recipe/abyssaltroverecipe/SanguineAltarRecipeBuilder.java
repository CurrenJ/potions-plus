package grill24.potionsplus.recipe.abyssaltroverecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import static grill24.potionsplus.utility.Utility.ppId;

public class SanguineAltarRecipeBuilder extends ShapelessProcessingRecipeBuilder<SanguineAltarRecipe, SanguineAltarRecipeBuilder> {
    @Override
    public RecipeHolder<SanguineAltarRecipe> build() {
        // TODO: Move recipe naming to relevant spot

        SanguineAltarRecipe recipe = new SanguineAltarRecipe(category, group, ingredients, result, processingTime, canShowInJei);
        ResourceLocation recipeId = ppId(recipe.getUniqueRecipeName());
        return new RecipeHolder<>(recipeId, recipe);
    }

    @Override
    protected SanguineAltarRecipeBuilder self() {
        return this;
    }
}
