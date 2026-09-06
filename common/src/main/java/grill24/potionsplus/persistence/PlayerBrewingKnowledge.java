package grill24.potionsplus.persistence;

import grill24.potionsplus.blockentity.IStoredIngredientsContainer;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerBrewingKnowledge {
    private final List<ItemStack> knownIngredientsSerializableData = new ArrayList<>();
    private final List<String> knownRecipesSerializableData = new ArrayList<>();
    private BlockPos pairedAbyssalTrovePos = BlockPos.ZERO;

    private final transient Lazy<Set<PpIngredient>> knownIngredients = Lazy.of(this::buildUniqueIngredientsFromSerializableData);
    private final transient Lazy<Set<String>> knownRecipes = Lazy.of(this::buildKnownRecipesFromSerializableData);

    public PlayerBrewingKnowledge() {}

    public static List<RecipeHolder<BrewingCauldronRecipe>> getUnknownRecipesWithIngredient(RecipeAnalysis<BrewingCauldronRecipe> recipeAnalysis, PpIngredient ingredient, PlayerBrewingKnowledge playerBrewingKnowledge) {
        return recipeAnalysis.getRecipesForIngredient(ingredient).stream()
                .filter(recipe -> playerBrewingKnowledge.isRecipeUnknown(recipe.id().toString()))
                .toList();
    }

    public void addIngredient(ItemStack ingredient) {
        knownIngredientsSerializableData.add(ingredient);
        knownIngredients.get().add(PpIngredient.of(ingredient));

        SavedData.instance.setDirty();
    }

    private Set<PpIngredient> buildUniqueIngredientsFromSerializableData() {
        return knownIngredientsSerializableData.stream()
                .map(PpIngredient::of)
                .collect(Collectors.toSet());
    }

    public void onNewRecipeKnowledgeAcquiredClient(String recipeId) {
        addKnownRecipe(recipeId);
    }

    public void addKnownRecipe(String recipeId) {
        knownRecipesSerializableData.add(recipeId);
        knownRecipes.get().add(recipeId);

        SavedData.instance.setDirty();
    }

    private Set<String> buildKnownRecipesFromSerializableData() {
        return new HashSet<>(knownRecipesSerializableData);
    }

    public boolean isIngredientUnknown(ItemStack ingredient) {
        return !knownIngredients.get().contains(PpIngredient.of(ingredient));
    }

    public boolean isRecipeUnknown(String recipeId) {
        return !knownRecipes.get().contains(recipeId);
    }

    public boolean isRecipeKnown(String recipeId) {
        return knownRecipes.get().contains(recipeId);
    }
    // Abyssal Trove

    public void pairAbyssalTroveAtPos(BlockPos pos) {
        pairedAbyssalTrovePos = pos;
    }

    public BlockPos getPairedAbyssalTrovePos() {
        return pairedAbyssalTrovePos;
    }

    public boolean abyssalTroveContainsIngredient(Level level, PpIngredient ingredient) {
        BlockEntity blockEntity = level.getBlockEntity(pairedAbyssalTrovePos);
        return blockEntity instanceof IStoredIngredientsContainer storedIngredientsContainer
                && storedIngredientsContainer.getStoredIngredients().contains(ingredient);
    }

    // Getter

    public List<String> getKnownRecipesSerializableData() {
        return knownRecipesSerializableData;
    }

    public void clearKnownRecipes() {
        knownRecipesSerializableData.clear();
        knownRecipes.get().clear();
    }

    public void clearKnownIngredients() {
        knownIngredientsSerializableData.clear();
        knownIngredients.get().clear();
    }
}
