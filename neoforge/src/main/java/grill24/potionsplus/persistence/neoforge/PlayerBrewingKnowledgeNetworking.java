package grill24.potionsplus.persistence.neoforge;

import grill24.potionsplus.network.neoforge.ClientboundAcquiredBrewingRecipeKnowledgePacket;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import grill24.potionsplus.platform.PacketNetwork;

/**
 * The NeoForge-only half of {@link PlayerBrewingKnowledge}'s server-side recipe-learning flow -
 * sends a client sync packet via the Phase 2 {@link grill24.potionsplus.platform.PacketNetwork}
 * abstraction. Will move to common/ alongside PlayerBrewingKnowledge in Phase 5.
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
