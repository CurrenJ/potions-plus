package grill24.potionsplus.persistence.neoforge;

import grill24.potionsplus.network.ClientboundAcquiredBrewingRecipeKnowledgePacket;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The NeoForge-only half of {@link PlayerBrewingKnowledge}'s server-side recipe-learning flow -
 * needs to send a client sync packet, which has no loader-agnostic abstraction yet (Phase 2's
 * PacketNetwork hasn't landed). See docs/multi-loader-expansion.md Phase 2 and Phase 5.
 */
public class PlayerBrewingKnowledgeNetworking {
    public static void tryAddKnownRecipeServer(PlayerBrewingKnowledge knowledge, ServerPlayer player, String recipeId, ItemStack result) {
        if (!knowledge.isRecipeKnown(recipeId)) {
            onNewRecipeKnowledgeAcquiredServer(knowledge, player, recipeId, result);
        }
    }

    private static void onNewRecipeKnowledgeAcquiredServer(PlayerBrewingKnowledge knowledge, ServerPlayer player, String recipeId, ItemStack result) {
        knowledge.addKnownRecipe(recipeId);
        PacketDistributor.sendToPlayer(player, new ClientboundAcquiredBrewingRecipeKnowledgePacket(recipeId, result));
    }
}
