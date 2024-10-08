package grill24.potionsplus.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class RecipeAnalysis<R extends ShapelessProcessingRecipe> {
    protected final List<RecipeHolder<R>> recipes;
    protected final Set<PpIngredient> uniqueIngredients;
    protected final Map<PpIngredient, List<RecipeHolder<R>>> ingredientToRecipeMap;

    public RecipeAnalysis() {
        this.recipes = new ArrayList<>();
        this.uniqueIngredients = new HashSet<>();
        this.ingredientToRecipeMap = new HashMap<>();
    }

    public RecipeAnalysis(List<RecipeHolder<R>> recipe) {
        this();
        compute(recipe);
    }

    /**
     * Analyze the given recipes and populate the internal data structures
     * @param recipes The recipes to analyze
     */
    public void compute(List<RecipeHolder<R>> recipes) {
        // Clear the previous analysis, if any
        this.recipes.clear();
        this.uniqueIngredients.clear();
        this.ingredientToRecipeMap.clear();

        // Remember all recipes
        this.recipes.addAll(recipes);

        // Run through recipes and populate hashsets and hashmaps
        for (RecipeHolder<R> recipe : recipes) {
            List<ItemStack> itemStacks = recipe.value().getIngredientsAsItemStacks();
            for (ItemStack itemStack : itemStacks) {
                if (PUtil.isPotion(itemStack)) {
                    continue;
                }

                PpIngredient ingredient = PpIngredient.of(itemStack);
                uniqueIngredients.add(ingredient);
                ingredientToRecipeMap.computeIfAbsent(ingredient, (key) -> new ArrayList<>()).add(recipe);
            }
        }
    }

    public Optional<RecipeHolder<R>> getRecipeForIngredient(PpIngredient ingredient) {
        List<RecipeHolder<R>> recipes = ingredientToRecipeMap.get(ingredient);
        if (recipes.isEmpty()) {
            return Optional.empty();
        }

        if (recipes.size() > 1) {
            PotionsPlus.LOGGER.warn("Called getRecipeForIngredient on ingredient {} with more than 1 recipe", ingredient);
        }

        return Optional.of(recipes.getFirst());
    }

    public List<RecipeHolder<R>> getRecipesForIngredient(PpIngredient ingredient) {
        return ingredientToRecipeMap.getOrDefault(ingredient, Collections.emptyList());
    }

    public boolean isIngredientUsed(PpIngredient ingredient) {
        return ingredientToRecipeMap.containsKey(ingredient);
    }

    public List<RecipeHolder<R>> getRecipes() {
        return ImmutableList.copyOf(recipes);
    }

    public Set<PpIngredient> getUniqueIngredients() {
        return ImmutableSet.copyOf(uniqueIngredients);
    }
}

