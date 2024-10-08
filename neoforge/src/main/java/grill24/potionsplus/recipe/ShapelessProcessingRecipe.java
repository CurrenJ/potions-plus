package grill24.potionsplus.recipe;

import com.google.common.collect.ImmutableList;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ShapelessProcessingRecipe implements Recipe<RecipeInput> {
    protected final RecipeCategory category;
    protected final String group;
    protected final ItemStack result;
    protected final List<PpIngredient> ingredients;
    protected final int processingTime;
    protected final boolean canShowInJei;

    private final NonNullList<Ingredient> nonNullIngredientList;

    public ShapelessProcessingRecipe(RecipeCategory category, String group, List<PpIngredient> ingredients, ItemStack result, int processingTime, boolean canShowInJei) {
        this.category = category;
        this.group = group;
        this.ingredients = ImmutableList.copyOf(ingredients);
        this.result = result;
        this.processingTime = processingTime;
        this.canShowInJei = canShowInJei;

        NonNullList<Ingredient> nonNullIngredientsList = NonNullList.create();
        for (PpIngredient ppIngredient : ingredients) {
            nonNullIngredientsList.add(Ingredient.of(ppIngredient.getItemStack()));
        }
        this.nonNullIngredientList = nonNullIngredientsList;
    }

    public List<ItemStack> getIngredientsAsItemStacks() {
        return this.ingredients.stream().map(PpIngredient::getItemStack).toList();
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        boolean hasAllIngredients = true;
        for (PpIngredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < recipeInput.size(); i++) {
                ItemStack itemStack = recipeInput.getItem(i);
                if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItemStack(), Collections.singletonList(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH))) {
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                hasAllIngredients = false;
                break;
            }
        }
        return hasAllIngredients;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registryAccess) {
        return getResult();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return getResult();
    }

    public ItemStack getResult() {
        return this.result.copy();
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.nonNullIngredientList;
    }

    public @NotNull List<PpIngredient> getPpIngredients() {
        return this.ingredients;
    }

    public RecipeCategory getCategory() {
        return this.category;
    }

    public boolean canShowInJei() {
        return this.canShowInJei;
    }
}
