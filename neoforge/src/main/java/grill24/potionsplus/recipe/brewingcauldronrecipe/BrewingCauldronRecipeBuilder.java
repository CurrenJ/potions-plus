package grill24.potionsplus.recipe.brewingcauldronrecipe;

import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class BrewingCauldronRecipeBuilder extends ShapelessProcessingRecipeBuilder<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {
    protected int durationToAdd;
    protected int amplifierToAdd;
    protected float experience;
    protected List<BrewingCauldronRecipe.PotionMatchingCriteria> potionMatchingCriteria;

    public BrewingCauldronRecipeBuilder() {
        super();
        this.durationToAdd = 0;
        this.amplifierToAdd = 0;
        this.experience = 0.0F;
        this.potionMatchingCriteria = Collections.singletonList(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH);

        // All brewing cauldron recipes are in the BREWING category. You have no say in the matter.
        this.category = RecipeCategory.BREWING;
    }

    public BrewingCauldronRecipeBuilder(BrewingCauldronRecipe recipe) {
        super(recipe);
        this.durationToAdd = recipe.durationToAdd;
        this.experience = recipe.experience;
        this.potionMatchingCriteria = recipe.matchingCriteria;
    }

    public BrewingCauldronRecipeBuilder durationToAdd(int durationToAdd) {
        this.durationToAdd = durationToAdd;
        return self();
    }

    public BrewingCauldronRecipeBuilder amplifierToAdd(int amplifierToAdd) {
        this.amplifierToAdd = amplifierToAdd;
        return self();
    }

    public BrewingCauldronRecipeBuilder experience(float experience) {
        this.experience = experience;
        return self();
    }

    public BrewingCauldronRecipeBuilder potionMatchingCriteria(List<BrewingCauldronRecipe.PotionMatchingCriteria> potionMatchingCriteria) {
        this.potionMatchingCriteria = potionMatchingCriteria;
        return self();
    }

    public BrewingCauldronRecipeBuilder potionMatchingCriteria(BrewingCauldronRecipe.PotionMatchingCriteria potionMatchingCriteria) {
        this.potionMatchingCriteria = new ArrayList<>();
        this.potionMatchingCriteria.add(potionMatchingCriteria);
        return self();
    }

    @Override
    protected void ensureValid() {
        super.ensureValid();

        if (potionMatchingCriteria == null) {
            throw new IllegalStateException("Potion matching criteria must be set.");
        }
    }

        @Override
    public RecipeHolder<BrewingCauldronRecipe> build() {
        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(category, group, ingredients, result, processingTime, canShowInJei, experience, durationToAdd, amplifierToAdd, potionMatchingCriteria);
        String id = PUtil.getUniqueRecipeName(ingredients, result);
        if (recipe.isAmpUpgrade()) {
            id += "_a" + amplifierToAdd;
        }
        if (recipe.isDurationUpgrade()) {
            id += "_d" + durationToAdd;
        }
        ResourceLocation recipeId = ppId(id);
        return new RecipeHolder<>(recipeId, recipe);
    }

    @Override
    protected BrewingCauldronRecipeBuilder self() {
        return this;
    }
}
