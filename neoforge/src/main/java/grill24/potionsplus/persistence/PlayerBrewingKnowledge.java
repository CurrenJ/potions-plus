package grill24.potionsplus.persistence;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.network.ClientboundAcquiredBrewingRecipeKnowledgePacket;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class PlayerBrewingKnowledge {
    private BlockPos pairedAbyssalTrovePos = BlockPos.ZERO;

    private final HashSet<PpIngredient> knownIngredients;
    private final HashSet<ResourceKey<Recipe<?>>> knownRecipes;

    public static final Codec<PlayerBrewingKnowledge> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockPos.CODEC.optionalFieldOf("pairedAbyssalTrovePos", BlockPos.ZERO).forGetter(PlayerBrewingKnowledge::getPairedAbyssalTrovePos),
            PpIngredient.LIST_CODEC.xmap(Sets::newHashSet, Lists::newArrayList).fieldOf("knownIngredients").forGetter(data -> data.knownIngredients),
            ResourceKey.codec(Registries.RECIPE).listOf().xmap(Sets::newHashSet, Lists::newArrayList).fieldOf("knownRecipes").forGetter(data -> data.knownRecipes)
    ).apply(builder, PlayerBrewingKnowledge::new));

    public PlayerBrewingKnowledge(BlockPos pairedAbyssalTrovePos, HashSet<PpIngredient> knownIngredients, HashSet<ResourceKey<Recipe<?>>> knownRecipes) {
        this.pairedAbyssalTrovePos = pairedAbyssalTrovePos;
        this.knownIngredients = knownIngredients;
        this.knownRecipes = knownRecipes;
    }

    public PlayerBrewingKnowledge() {
        this(BlockPos.ZERO, new HashSet<>(), new HashSet<>());
    }

    public static List<RecipeHolder<BrewingCauldronRecipe>> getUnknownRecipesWithIngredient(RecipeAnalysis<BrewingCauldronRecipe> recipeAnalysis, PpIngredient ingredient, PlayerBrewingKnowledge playerBrewingKnowledge) {
        return recipeAnalysis.getRecipesForIngredient(ingredient).stream()
                .filter(recipe -> playerBrewingKnowledge.isRecipeUnknown(recipe.id()))
                .toList();
    }

    public void addIngredient(ItemStack ingredient) {
        knownIngredients.add(PpIngredient.of(ingredient));

        SavedData.instance.setDirty();
    }

    public void tryAddKnownRecipeServer(ServerPlayer player, ResourceKey<Recipe<?>> recipeId, ItemStack result) {
        if (!isRecipeKnown(recipeId)) {
            onNewRecipeKnowledgeAcquiredServer(player, recipeId, result);
        }
    }

    public void onNewRecipeKnowledgeAcquiredClient(ResourceKey<Recipe<?>> recipeId) {
        addKnownRecipe(recipeId);
    }

    private void onNewRecipeKnowledgeAcquiredServer(ServerPlayer player, ResourceKey<Recipe<?>> recipeId, ItemStack result) {
        addKnownRecipe(recipeId);
        PacketDistributor.sendToPlayer(player, new ClientboundAcquiredBrewingRecipeKnowledgePacket(recipeId, result));
    }

    public void addKnownRecipe(ResourceKey<Recipe<?>> recipeId) {
        knownRecipes.add(recipeId);

        SavedData.instance.setDirty();
    }

    public boolean isIngredientUnknown(ItemStack ingredient) {
        return !knownIngredients.contains(PpIngredient.of(ingredient));
    }

    public boolean isRecipeUnknown(ResourceKey<Recipe<?>> recipeId) {
        return !knownRecipes.contains(recipeId);
    }

    public boolean isRecipeKnown(ResourceKey<Recipe<?>> recipeId) {
        return knownRecipes.contains(recipeId);
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

    public List<ResourceKey<Recipe<?>>> getKnownRecipeKeys() {
        return knownRecipes.stream().toList();
    }

    public void clearKnownRecipes() {
        knownRecipes.clear();
    }

    public void clearKnownIngredients() {
        knownIngredients.clear();
    }
}
