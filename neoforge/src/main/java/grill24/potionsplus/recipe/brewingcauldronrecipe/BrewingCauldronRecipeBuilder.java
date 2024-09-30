package grill24.potionsplus.recipe.brewingcauldronrecipe;

import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import static grill24.potionsplus.utility.Utility.ppId;

public class BrewingCauldronRecipeBuilder extends ShapelessProcessingRecipeBuilder<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {
    protected int tier;
    protected float experience;

    public BrewingCauldronRecipeBuilder() {
        super();
        this.tier = -1;
        this.experience = 0.0F;

        // All brewing cauldron recipes are in the BREWING category. You have no say in the matter.
        this.category = RecipeCategory.BREWING;
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
    public RecipeHolder<BrewingCauldronRecipe> build() {

        ResourceLocation recipeId = ppId(PUtil.getUniqueRecipeName(ingredients, result));
        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(category, group, tier, ingredients, result, experience, processingTime);
        return new RecipeHolder<>(recipeId, recipe);
    }

    @Override
    protected BrewingCauldronRecipeBuilder self() {
        return this;
    }
}
