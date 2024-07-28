package grill24.potionsplus.recipe.clotheslinerecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ClotheslineRecipeBuilder extends ShapelessProcessingRecipeBuilder<ClotheslineRecipe, ClotheslineRecipeBuilder> {
    @Override
    protected ClotheslineRecipe build(ResourceLocation resourceLocation) {
        return new ClotheslineRecipe(resourceLocation, group, ingredients, result, processingTime);
    }

    @Override
    protected ClotheslineRecipeBuilder self() {
        return this;
    }

    @Override
    protected FinishedRecipe getFinishedRecipe(ResourceLocation resourceLocation) {
        return new ShapelessProcessingRecipeBuilder.Result<>(
                resourceLocation,
                new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath()),
                this,
                Recipes.CLOTHESLINE_RECIPE_SERIALIZER.get()
        );
    }
}