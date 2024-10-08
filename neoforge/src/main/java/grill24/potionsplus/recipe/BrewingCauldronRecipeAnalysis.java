package grill24.potionsplus.recipe;

import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrewingCauldronRecipeAnalysis extends RecipeAnalysis<BrewingCauldronRecipe> {
    // Any recipe input ingredient that is not a potion
    protected Set<PpIngredient> allPotionsPlusIngredientsNoPotions; // 1 item per ingredient.
    // Any recipe input ingredient that is not a potion, but the result is a potion
    protected Set<PpIngredient> allPotionBrewingIngredientsNoPotions; // 1 item per ingredient.

    public BrewingCauldronRecipeAnalysis() {
        super();

        allPotionBrewingIngredientsNoPotions = new HashSet<>();
        allPotionsPlusIngredientsNoPotions = new HashSet<>();
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
}
