package grill24.potionsplus.persistence;

import grill24.potionsplus.network.ClientboundAcquiredBrewingRecipeKnowledgePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import grill24.potionsplus.platform.PacketNetwork;

/**
 * {@link PlayerBrewingKnowledge}'s server-side recipe-learning flow - sends a client sync packet
 * via the Phase 2 {@link grill24.potionsplus.platform.PacketNetwork} abstraction. Ported to
 * common/ in Phase 11a (its only former neoforge coupling, {@code ClientboundAcquiredBrewingRecipeKnowledgePacket},
 * had already moved to common/network/ earlier in this task chain).
 */
public class PlayerBrewingKnowledgeNetworking {
    public static void tryAddKnownRecipeServer(PlayerBrewingKnowledge knowledge, ServerPlayer player, String recipeId, ItemStack result) {
        if (!knowledge.isRecipeKnown(recipeId)) {
            onNewRecipeKnowledgeAcquiredServer(knowledge, player, recipeId, result);
        }
    }

    private static void onNewRecipeKnowledgeAcquiredServer(PlayerBrewingKnowledge knowledge, ServerPlayer player, String recipeId, ItemStack result) {
        knowledge.addKnownRecipe(recipeId);
        PacketNetwork.sendToPlayer(player, new ClientboundAcquiredBrewingRecipeKnowledgePacket(recipeId, result));
    }
}
