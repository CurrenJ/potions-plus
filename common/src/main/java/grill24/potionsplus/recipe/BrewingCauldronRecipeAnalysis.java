package grill24.potionsplus.recipe;

import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public class BrewingCauldronRecipeAnalysis extends RecipeAnalysis<BrewingCauldronRecipe> {
    // Any recipe input ingredient that is not a potion
    protected Set<PpIngredient> allPotionsPlusIngredientsNoPotions;
    // Any recipe input ingredient that is not a potion, but the result is a potion
    protected Set<PpIngredient> allPotionBrewingIngredientsNoPotions;
    protected Map<ResourceKey<MobEffect>, List<RecipeHolder<BrewingCauldronRecipe>>> mobEffectToBasePotionRecipes;

    public BrewingCauldronRecipeAnalysis() {
        super();

        allPotionBrewingIngredientsNoPotions = new HashSet<>();
        allPotionsPlusIngredientsNoPotions = new HashSet<>();
        mobEffectToBasePotionRecipes = new HashMap<>();
    }

    public BrewingCauldronRecipeAnalysis(List<RecipeHolder<BrewingCauldronRecipe>> recipes) {
        super(recipes);
    }

    @Override
    public void compute(List<RecipeHolder<BrewingCauldronRecipe>> recipes) {
        super.compute(recipes);

        allPotionBrewingIngredientsNoPotions.clear();
        allPotionsPlusIngredientsNoPotions.clear();

        // Any recipe input ingredient that is not a potion
        this.uniqueIngredients.stream().filter(ingredient -> !PUtil.isPotion(ingredient.getItemStack())).forEach(allPotionsPlusIngredientsNoPotions::add);

        // Any recipe input ingredient that is not a potion, but the result is a potion
        recipes.forEach(recipe -> {
            ItemStack result = recipe.value().getResult();
            if (PUtil.isPotion(result)) {
                for (PpIngredient ingredient : recipe.value().getPpIngredients()) {
                    if (!PUtil.isPotion(ingredient.getItemStack())) {
                        allPotionBrewingIngredientsNoPotions.add(ingredient);
                    }
                }

                if (recipe.value().getDurationToAdd() == 0 && recipe.value().getAmplifierToAdd() == 0 && recipe.value().getResult().is(Items.POTION)) {
                    List<MobEffectInstance> effects = PUtil.getAllEffects(result);
                    if (effects.size() == 1) {
                        mobEffectToBasePotionRecipes.computeIfAbsent(effects.get(0).getEffect().getKey(), k -> new ArrayList<>()).add(recipe);
                    }
                }
            }
        });
    }

    public boolean isAnyIngredientNotPotion(PpIngredient ppIngredient) {
        return allPotionsPlusIngredientsNoPotions.contains(ppIngredient);
    }

    public boolean isAnyBrewingIngredientNotPotion(PpIngredient ppIngredient) {
        return allPotionBrewingIngredientsNoPotions.contains(ppIngredient);
    }

    public Set<PpIngredient> getAllPotionsPlusIngredientsNoPotions() {
        return Set.copyOf(allPotionsPlusIngredientsNoPotions);
    }

    public Set<PpIngredient> getAllPotionBrewingIngredientsNoPotions() {
        return Set.copyOf(allPotionBrewingIngredientsNoPotions);
    }

    public List<RecipeHolder<BrewingCauldronRecipe>> getRecipesForMobEffect(ResourceKey<MobEffect> mobEffect) {
        return mobEffectToBasePotionRecipes.getOrDefault(mobEffect, Collections.emptyList());
    }
}
