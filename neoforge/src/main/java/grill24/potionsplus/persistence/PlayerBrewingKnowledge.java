package grill24.potionsplus.persistence;

import grill24.potionsplus.network.ClientboundBrewingIngredientKnowledgePacket;
import grill24.potionsplus.network.ClientboundBrewingRecipeKnowledgePacket;
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

    public boolean addKnownRecipe(ServerPlayer player, String recipeId) {
        if (!knownRecipesContains(recipeId)) {
            knownRecipesSerializableData.add(recipeId);
            knownRecipes.get().add(recipeId);

            onNewRecipeKnowledgeAcquired(player, recipeId);
            return true;
        }
        return false;
    }

    private void onNewRecipeKnowledgeAcquired(ServerPlayer player, String recipeId) {
        PacketDistributor.sendToPlayer(player, new ClientboundBrewingRecipeKnowledgePacket(recipeId));
        SavedData.instance.setDirty();
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

    public static void onAcquiredNewIngredientKnowledge(Level level, ServerPlayer player, ItemStack ingredient) {
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
}
