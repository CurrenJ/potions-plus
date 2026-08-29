package grill24.potionsplus.recipe.brewingcauldronrecipe;

import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.alchemy.PotionContainer;
import grill24.potionsplus.platform.Platform;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrewingCauldronRecipeBuilder extends ShapelessProcessingRecipeBuilder<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {
    protected int durationToAdd;
    protected int amplifierToAdd;
    protected float experienceReward;
    protected float experienceRequired;
    protected List<EffectComparison.MatchCriteria> potionMatchingCriteria;
    protected boolean isSeededRuntimeRecipe = false;

    public BrewingCauldronRecipeBuilder() {
        super();
        this.durationToAdd = 0;
        this.amplifierToAdd = 0;
        this.experienceReward = 0.0F;
        this.potionMatchingCriteria = Collections.singletonList(EffectComparison.MatchCriteria.EXACT_MATCH);

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

    public BrewingCauldronRecipeBuilder potionMatchingCriteria(List<EffectComparison.MatchCriteria> potionMatchingCriteria) {
        this.potionMatchingCriteria = potionMatchingCriteria;
        return self();
    }

    public BrewingCauldronRecipeBuilder potionMatchingCriteria(EffectComparison.MatchCriteria potionMatchingCriteria) {
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
        ItemStack finalResult = applyDrinkTimeAndCooldown(result);
        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(category, ingredients, finalResult, processingTime, canShowInJei, experienceReward, experienceRequired, durationToAdd, amplifierToAdd, potionMatchingCriteria, isSeededRuntimeRecipe);
        String id = recipe.getUniqueRecipeName();
        Identifier recipeId = Identifier.fromNamespaceAndPath(namespace, id);
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
        return new RecipeHolder<>(recipeKey, recipe);
    }

    /**
     * Sets the server-configurable drink time and post-drink use-cooldown onto drinkable potion
     * results. Both components are static per-{@link ItemStack}, so they are baked in here rather
     * than read live at use-time - a config change only affects potions brewed after the change.
     */
    private static ItemStack applyDrinkTimeAndCooldown(ItemStack result) {
        if (result == null || result.isEmpty() || PotionContainer.of(result).filter(container -> container == PotionContainer.POTION).isEmpty()) {
            return result;
        }

        ItemStack withComponents = result.copy();

        Consumable consumable = withComponents.getOrDefault(DataComponents.CONSUMABLE, Consumable.builder().build());
        float drinkSeconds = Platform.getPotionDrinkTimeTicks() / 20.0F;
        withComponents.set(DataComponents.CONSUMABLE, new Consumable(
                drinkSeconds, consumable.animation(), consumable.sound(), consumable.hasConsumeParticles(), consumable.onConsumeEffects()));

        int cooldownTicks = Platform.getPotionDrinkCooldownTimeTicks();
        if (cooldownTicks > 0) {
            withComponents.set(DataComponents.USE_COOLDOWN, new UseCooldown(cooldownTicks / 20.0F));
        }

        return withComponents;
    }

    @Override
    protected BrewingCauldronRecipeBuilder self() {
        return this;
    }
}
