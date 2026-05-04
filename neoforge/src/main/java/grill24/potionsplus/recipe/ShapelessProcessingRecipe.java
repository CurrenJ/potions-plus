package grill24.potionsplus.recipe;

import com.google.common.collect.ImmutableList;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeDisplay;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ShapelessProcessingRecipe implements Recipe<RecipeInput> {
    protected final RecipeCategory category;
    protected PlacementInfo placementInfo;
    protected final ItemStack result;
    protected final List<PpIngredient> ingredients;
    protected final int processingTime;
    protected final boolean canShowInJei;
    protected final float successChance; // 1.0 = 100% success

    private final NonNullList<Ingredient> nonNullIngredientList;

    public ShapelessProcessingRecipe(RecipeCategory category, List<PpIngredient> ingredients, ItemStack result, int processingTime, boolean canShowInJei) {
        this(category, ingredients, result, processingTime, canShowInJei, 1.0f);
    }

    public ShapelessProcessingRecipe(RecipeCategory category, List<PpIngredient> ingredients, ItemStack result, int processingTime, boolean canShowInJei, float successChance) {
        this.category = category;
        this.ingredients = ImmutableList.copyOf(ingredients);
        this.result = result;
        this.processingTime = processingTime;
        this.canShowInJei = canShowInJei;
        this.successChance = Math.max(0.0f, Math.min(1.0f, successChance)); // Clamp between 0 and 1

        NonNullList<Ingredient> nonNullIngredientsList = NonNullList.create();
        for (PpIngredient ppIngredient : ingredients) {
            nonNullIngredientsList.add(ppIngredient.asIngredient());
        }
        this.nonNullIngredientList = nonNullIngredientsList;

        if (this.nonNullIngredientList.isEmpty()) {
            PotionsPlus.LOGGER.warn("ShapelessProcessingRecipe created with no ingredients! This is likely a bug.");
        }
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
    public List<RecipeDisplay> display() {
        return List.of(
                new BrewingCauldronRecipeDisplay(
                        this.ingredients.stream().map(PpIngredient::display).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(BlockEntityBlocks.BREWING_CAULDRON.value().asItem())
                )
        );
    }

    @Override
    public PlacementInfo placementInfo() {
        // This delegate is done as the HolderSet backing the ingredient may not be fully populated in the constructor
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.nonNullIngredientList);
        }

        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        // Functions similar to the book category passed into the recipe builders during data generation
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public ItemStack getResult() {
        return this.result.copy();
    }

    public int getProcessingTime() {
        return this.processingTime;
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

    public float getSuccessChance() {
        return this.successChance;
    }

    public String getUniqueRecipeName() {
        return getUniqueRecipeName(this.ingredients, this.result);
    }

    public static String getUniqueRecipeName(List<PpIngredient> ingredients, ItemStack result) {
        List<String> ingredientNames = new ArrayList<>();
        for (PpIngredient ingredient : ingredients) {
            ingredientNames.add(PUtil.getNameOrVerbosePotionName(ingredient.getItemStack()));
        }
        ingredientNames.sort(String::compareTo);

        StringBuilder name = new StringBuilder();
        for (String ingredientName : ingredientNames) {
            name.append(ingredientName).append("_");
        }
        name.append("to_");
        name.append(PUtil.getNameOrVerbosePotionName(result));
        return name.toString();
    }
}
