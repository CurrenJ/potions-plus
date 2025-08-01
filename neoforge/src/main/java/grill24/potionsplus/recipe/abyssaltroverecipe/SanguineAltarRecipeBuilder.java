package grill24.potionsplus.recipe.abyssaltroverecipe;

import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import static grill24.potionsplus.utility.Utility.ppId;

public class SanguineAltarRecipeBuilder extends ShapelessProcessingRecipeBuilder<SanguineAltarRecipe, SanguineAltarRecipeBuilder> {
    @Override
    public RecipeHolder<SanguineAltarRecipe> build() {
        // TODO: Move recipe naming to relevant spot

        SanguineAltarRecipe recipe = new SanguineAltarRecipe(category, ingredients, result, processingTime, canShowInJei);
        ResourceLocation recipeId = ppId(recipe.getUniqueRecipeName());
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
        return new RecipeHolder<>(recipeKey, recipe);
    }

    @Override
    protected SanguineAltarRecipeBuilder self() {
        return this;
    }
}
