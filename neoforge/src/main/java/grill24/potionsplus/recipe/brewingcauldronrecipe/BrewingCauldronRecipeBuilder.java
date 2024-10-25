package grill24.potionsplus.recipe.brewingcauldronrecipe;

import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrewingCauldronRecipeBuilder extends ShapelessProcessingRecipeBuilder<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {
    protected int durationToAdd;
    protected int amplifierToAdd;
    protected float experienceReward;
    protected float experienceRequired;
    protected List<BrewingCauldronRecipe.PotionMatchingCriteria> potionMatchingCriteria;
    protected boolean isSeededRuntimeRecipe = false;

    public BrewingCauldronRecipeBuilder() {
        super();
        this.durationToAdd = 0;
        this.amplifierToAdd = 0;
        this.experienceReward = 0.0F;
        this.potionMatchingCriteria = Collections.singletonList(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH);

        // All brewing cauldron recipes are in the BREWING category. You have no say in the matter.
        this.category = RecipeCategory.BREWING;
    }

    public BrewingCauldronRecipeBuilder(BrewingCauldronRecipe recipe) {
        super(recipe);
        this.durationToAdd = recipe.durationToAdd;
        this.experienceReward = recipe.experienceReward;
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

    public BrewingCauldronRecipeBuilder experienceReward(float experienceReward) {
        this.experienceReward = experienceReward;
        return self();
    }

    public BrewingCauldronRecipeBuilder experienceRequired(float experienceRequired) {
        this.experienceRequired = experienceRequired;
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

    public BrewingCauldronRecipeBuilder isSeededRuntimeRecipe() {
        this.isSeededRuntimeRecipe = true;
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
        return build(ModInfo.MOD_ID);
    }

    public RecipeHolder<BrewingCauldronRecipe> build(String namespace) {
        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(category, group, ingredients, result, processingTime, canShowInJei, experienceReward, experienceRequired, durationToAdd, amplifierToAdd, potionMatchingCriteria, isSeededRuntimeRecipe);
        String id = recipe.getUniqueRecipeName();
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(namespace, id);
        return new RecipeHolder<>(recipeId, recipe);
    }

    @Override
    protected BrewingCauldronRecipeBuilder self() {
        return this;
    }
}
