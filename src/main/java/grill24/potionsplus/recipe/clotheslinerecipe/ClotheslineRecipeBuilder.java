package grill24.potionsplus.recipe.clotheslinerecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ClotheslineRecipeBuilder extends ShapelessProcessingRecipeBuilder<ClotheslineRecipe, ClotheslineRecipeBuilder> {

    public ClotheslineRecipeBuilder ingredient(ItemLike item) {
        return ingredients(new ItemStack(item));
    }

    @Override
    protected ClotheslineRecipe build(ResourceLocation resourceLocation) {
        return new ClotheslineRecipe(resourceLocation, category, group, ingredients, result, processingTime);
    }

    @Override
    protected ClotheslineRecipeBuilder self() {
        return this;
    }

    @Override
    protected FinishedRecipe getFinishedRecipe(ResourceLocation resourceLocation) {
        return new ShapelessProcessingRecipeBuilder.Result<>(
                resourceLocation,
                resourceLocation.withPrefix("recipes/" + this.category.getFolderName() + "/"),
                this,
                Recipes.CLOTHESLINE_RECIPE_SERIALIZER.get()
        );
    }
}
