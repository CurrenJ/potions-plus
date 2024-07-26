package grill24.potionsplus.recipe.brewingcauldronrecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

public class BrewingCauldronRecipeBuilder extends ShapelessProcessingRecipeBuilder<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {
    protected int tier;
    protected float experience;

    public BrewingCauldronRecipeBuilder() {
        this.tier = -1;
        this.experience = 0.0F;
    }

    public BrewingCauldronRecipeBuilder(BrewingCauldronRecipe recipe) {
        super(recipe);
        this.tier = recipe.tier;
        this.experience = recipe.experience;
    }

    public BrewingCauldronRecipeBuilder tier(int tier) {
        this.tier = tier;
        return self();
    }

    public BrewingCauldronRecipeBuilder experience(float experience) {
        this.experience = experience;
        return self();
    }


    @Override
    protected BrewingCauldronRecipe build(ResourceLocation resourceLocation) {
        return new BrewingCauldronRecipe(resourceLocation, group, tier, ingredients, result, experience, processingTime);
    }

    @Override
    protected BrewingCauldronRecipeBuilder self() {
        return this;
    }

    @Override
    protected FinishedRecipe getFinishedRecipe(ResourceLocation resourceLocation) {
        return new ShapelessProcessingRecipeBuilder.Result<>(
                resourceLocation,
                new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath()),
                this,
                Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get()
        );
    }
}
