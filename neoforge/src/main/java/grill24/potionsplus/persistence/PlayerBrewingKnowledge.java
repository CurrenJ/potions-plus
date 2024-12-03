package grill24.potionsplus.persistence;

import grill24.potionsplus.network.ClientboundAcquiredBrewingRecipeKnowledgePacket;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Lazy;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

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

    public void tryAddKnownRecipeServer(ServerPlayer player, String recipeId, ItemStack result) {
        if (!isRecipeKnown(recipeId)) {
            onNewRecipeKnowledgeAcquiredServer(player, recipeId, result);
        }
    }

    public void onNewRecipeKnowledgeAcquiredClient(String recipeId) {
        addKnownRecipe(recipeId);
    }

    private void onNewRecipeKnowledgeAcquiredServer(ServerPlayer player, String recipeId, ItemStack result) {
        addKnownRecipe(recipeId);
        PacketDistributor.sendToPlayer(player, new ClientboundAcquiredBrewingRecipeKnowledgePacket(recipeId, result));
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
        Optional<AbyssalTroveBlockEntity> abyssalTrove = level.getBlockEntity(pairedAbyssalTrovePos, Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get());
        return abyssalTrove.map(abyssalTroveBlockEntity -> abyssalTroveBlockEntity.getStoredIngredients().contains(ingredient)).orElse(false);
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
