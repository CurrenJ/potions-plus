package grill24.potionsplus.event;

import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import grill24.potionsplus.network.ClientboundSyncKnownBrewingRecipesPacket;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import oshi.util.tuples.Pair;

import java.util.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerListeners {

    @SubscribeEvent
    public static void onItemPickedUp(final ItemEntityPickupEvent.Post event) {
        Level level = event.getPlayer().level();
        if (!level.isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.computeIfAbsent(player.getUUID(), (uuid) -> new PlayerBrewingKnowledge());
            ItemStack stack = event.getOriginalStack().copy();
            stack.setCount(1);
            PpIngredient ppIngredient = PpIngredient.of(stack);

            // Create a priority queue to sort the alerts by priority - an item could trigger multiple alerts, so only send the highest priority alert
            PriorityQueue<Pair<ClientboundDisplayAlertWithItemStackName, Integer>> alerts = new PriorityQueue<>((a, b) -> Integer.compare(a.getB(), b.getB()));
            List<RecipeHolder<BrewingCauldronRecipe>> learnedRecipes = new ArrayList<>();

            // Get all *recipe* knowledge that is triggered by picking up this ingredient. If there is any, try to trigger an alert for the respective category.
            int count;
            learnedRecipes.addAll(PlayerBrewingKnowledge.getUnknownRecipesWithIngredient(Recipes.DURATION_UPGRADE_ANALYSIS, ppIngredient, playerBrewingKnowledge));
            if (!learnedRecipes.isEmpty()) {
                alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.duration_ingredient", stack), 1));
            }
            count = learnedRecipes.size();
            learnedRecipes.addAll(PlayerBrewingKnowledge.getUnknownRecipesWithIngredient(Recipes.AMPLIFICATION_UPGRADE_ANALYSIS, ppIngredient, playerBrewingKnowledge));
            if (learnedRecipes.size() > count) {
                alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.amplification_ingredient", stack), 2));
            }


            // Add the *ingredient* to the player's knowledge if it is unknown
            // For some reason when I made the saved data I decided to use itemstacks, but we only really care about the item id. So set count to 1 for consistency.
            stack.setCount(1);
            if (playerBrewingKnowledge.isIngredientUnknown(stack)) {
                // At the time of writing this, *ingredient* knowledge is not synced to the client, because it is only used for server-side checks. If this changes, we should sync it here.
                playerBrewingKnowledge.addIngredient(stack);
                // Alert the player that they have picked up this brewing ingredient for the first time.
                alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.acquired_ingredient_knowledge_" + player.getRandom().nextInt(1, 4), stack), 3));
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
                PacketDistributor.sendToPlayer(player, first, rest);
            }
        }
    }

    private enum KnowledgeType {
        RECIPE,
        INGREDIENT
    }

    @SubscribeEvent
    public static void on(final PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();

        MossBehaviour.doMossInteractions(event, pos);
        ClotheslineBehaviour.doClotheslineInteractions(event);
    }

    @SubscribeEvent
    public static void onPlayerJoin(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, ClientboundSyncKnownBrewingRecipesPacket.of(SavedData.instance.getData(player).getKnownRecipesSerializableData()));
        }
    }
}
