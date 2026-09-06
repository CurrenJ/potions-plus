package grill24.potionsplus.event;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.RecipesRegistrar;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import grill24.potionsplus.network.ClientboundSyncKnownBrewingRecipesPacket;
import grill24.potionsplus.network.ClientboundSyncPairedAbyssalTrove;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.platform.PacketNetwork;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Loader-agnostic bodies for the "on item pickup" / "on player join" halves of NeoForge's
 * {@code event.neoforge.PlayerListeners} (Phase 7 explicit-listeners bucket, re-audited 2026-09-04).
 * Both former blockers - {@code RecipesRegistrar} and the two sync packets
 * ({@code ClientboundSyncKnownBrewingRecipesPacket}/{@code ClientboundSyncPairedAbyssalTrove}) - are
 * common/ now, so this is extracted the same way the mob-effect-behaviour bucket extracted its
 * {@code @SubscribeEvent} bodies: each loader supplies its own event/mixin hook and calls straight
 * into here. NeoForge's {@code ItemEntityPickupEvent.Post} fires after the pickup already happened;
 * Fabric's {@code ItemEntity.playerTouch} mixin and Forge's {@code EntityItemPickupEvent} both fire
 * with the stack still whole - harmless here since every caller immediately reduces to a
 * single-count copy for identity purposes only.
 */
public final class PlayerListeners {
    private PlayerListeners() {
    }

    public static void onItemPickedUp(ServerPlayer player, ItemStack originalStack) {
        PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.computeIfAbsent(player.getUUID(), (uuid) -> new PlayerBrewingKnowledge());
        ItemStack stack = originalStack.copy();
        stack.setCount(1);
        PpIngredient ppIngredient = PpIngredient.of(stack);

        // Create a priority queue to sort the alerts by priority - an item could trigger multiple alerts, so only send the highest priority alert
        PriorityQueue<Pair<ClientboundDisplayAlertWithItemStackName, Integer>> alerts = new PriorityQueue<>((a, b) -> Integer.compare(a.getB(), b.getB()));
        List<RecipeHolder<BrewingCauldronRecipe>> learnedRecipes = new ArrayList<>();

        // Get all *recipe* knowledge that is triggered by picking up this ingredient. If there is any, try to trigger an alert for the respective category.
        int count;
        learnedRecipes.addAll(PlayerBrewingKnowledge.getUnknownRecipesWithIngredient(RecipesRegistrar.DURATION_UPGRADE_ANALYSIS, ppIngredient, playerBrewingKnowledge));
        if (!learnedRecipes.isEmpty()) {
            alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.duration_ingredient", stack, true), 1));
        }
        count = learnedRecipes.size();
        learnedRecipes.addAll(PlayerBrewingKnowledge.getUnknownRecipesWithIngredient(RecipesRegistrar.AMPLIFICATION_UPGRADE_ANALYSIS, ppIngredient, playerBrewingKnowledge));
        if (learnedRecipes.size() > count) {
            alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.amplification_ingredient", stack, true), 2));
        }

        // Add the *ingredient* to the player's knowledge if it is unknown
        // For some reason when I made the saved data I decided to use itemstacks, but we only really care about the item id. So set count to 1 for consistency.
        stack.setCount(1);
        if (playerBrewingKnowledge.isIngredientUnknown(stack) && AbyssalTroveBlockEntity.getAcceptedIngredients().contains(ppIngredient)) {
            // At the time of writing this, *ingredient* knowledge is not synced to the client, because it is only used for server-side checks. If this changes, we should sync it here.
            playerBrewingKnowledge.addIngredient(stack);
            // Alert the player that they have picked up this brewing ingredient for the first time.
            alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.acquired_ingredient_knowledge_" + player.getRandom().nextInt(1, 4), stack, true), 3));
        }

        // Gather all the packets to send to player
        List<CustomPacketPayload> packets = new ArrayList<>();
        if (!learnedRecipes.isEmpty()) {
            // Update the server-side recipe knowledge
            for (RecipeHolder<BrewingCauldronRecipe> recipe : learnedRecipes) {
                playerBrewingKnowledge.addKnownRecipe(recipe.id().toString());
            }
            // Sync new recipe knowledge to the client
            packets.add(ClientboundSyncKnownBrewingRecipesPacket.of(learnedRecipes.stream().map(RecipeHolder::id).map(Object::toString).toList()));
        }
        if (!alerts.isEmpty()) {
            // Only send the highest priority alert
            packets.add(alerts.poll().getA());
        }

        // Send the packets
        if (!packets.isEmpty()) {
            CustomPacketPayload first = packets.getFirst();
            CustomPacketPayload[] rest = packets.stream().skip(1).toArray(CustomPacketPayload[]::new);
            PacketNetwork.sendToPlayers(player, first, rest);
        }
    }

    public static void onPlayerJoin(ServerPlayer player) {
        // Sync known brewing cauldron recipe and sync paired abyssal trove.
        PacketNetwork.sendToPlayers(player,
                ClientboundSyncKnownBrewingRecipesPacket.of(SavedData.instance.getData(player).getKnownRecipesSerializableData()),
                new CustomPacketPayload[]{new ClientboundSyncPairedAbyssalTrove(SavedData.instance.getData(player).getPairedAbyssalTrovePos())}
        );
    }
}
