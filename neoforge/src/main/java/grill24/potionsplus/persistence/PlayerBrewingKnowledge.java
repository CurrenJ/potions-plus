package grill24.potionsplus.persistence;

import grill24.potionsplus.network.ClientboundBrewingIngredientKnowledgePacket;
import grill24.potionsplus.network.ClientboundAcquiredBrewingRecipeKnowledgePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    private final List<ItemStack> uniqueIngredientsSerializableData = new ArrayList<>();
    private final List<String> knownRecipesSerializableData = new ArrayList<>();
    private BlockPos pairedAbyssalTrovePos = BlockPos.ZERO;

    private final transient Lazy<Set<PpIngredient>> uniqueIngredients = Lazy.of(this::buildUniqueIngredientsFromSerializableData);
    private final transient Lazy<Set<String>> knownRecipes = Lazy.of(this::buildKnownRecipesFromSerializableData);


    public PlayerBrewingKnowledge() {
    }

    public void addIngredient(ItemStack ingredient) {
        uniqueIngredientsSerializableData.add(ingredient);
        uniqueIngredients.get().add(PpIngredient.of(ingredient));
    }

    private Set<PpIngredient> buildUniqueIngredientsFromSerializableData() {
        return uniqueIngredientsSerializableData.stream()
                .map(PpIngredient::of)
                .collect(Collectors.toSet());
    }

    public void tryAddKnownRecipeServer(ServerPlayer player, String recipeId, ItemStack result) {
        if (!knownRecipesContains(recipeId)) {
            onNewRecipeKnowledgeAcquiredServer(player, recipeId, result);
        }
    }

    public void onNewRecipeKnowledgeAcquiredClient(String recipeId) {
        addKnownRecipe(recipeId);
    }

    private void onNewRecipeKnowledgeAcquiredServer(ServerPlayer player, String recipeId, ItemStack result) {
        addKnownRecipe(recipeId);
        PacketDistributor.sendToPlayer(player, new ClientboundAcquiredBrewingRecipeKnowledgePacket(recipeId, result));
        SavedData.instance.setDirty();
    }

    public void addKnownRecipe(String recipeId) {
        knownRecipesSerializableData.add(recipeId);
        knownRecipes.get().add(recipeId);
    }

    private Set<String> buildKnownRecipesFromSerializableData() {
        return new HashSet<>(knownRecipesSerializableData);
    }

    public boolean uniqueIngredientsContains(ItemStack ingredient) {
        return uniqueIngredients.get().contains(PpIngredient.of(ingredient));
    }

    public boolean knownRecipesContains(String recipeId) {
        return knownRecipes.get().contains(recipeId);
    }

    public static void alertClientOfNewIngredient(Level level, ServerPlayer player, ItemStack ingredient) {
        PacketDistributor.sendToPlayer(player, new ClientboundBrewingIngredientKnowledgePacket(ingredient));
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
}
