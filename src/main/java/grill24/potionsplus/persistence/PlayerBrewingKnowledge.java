package grill24.potionsplus.persistence;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

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

    public boolean addKnownRecipe(String recipeId) {
        if(!knownRecipesContains(recipeId)) {
            knownRecipesSerializableData.add(recipeId);
            knownRecipes.get().add(recipeId);

            onNewRecipeKnowledgeAcquired();
            return true;
        }
        return false;
    }

    private void onNewRecipeKnowledgeAcquired() {
        JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
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

    public static void onAcquiredNewIngredientKnowledge(Level level, Player player, ItemStack ingredient) {
        if (level != null) {
            TranslatableComponent text = new TranslatableComponent("chat.potionsplus.acquired_ingredient_knowledge_" + level.getRandom().nextInt(1, 4), ingredient.getHoverName());
            player.displayClientMessage(text, true);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), 1.0F, 1.0F);
        }
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
