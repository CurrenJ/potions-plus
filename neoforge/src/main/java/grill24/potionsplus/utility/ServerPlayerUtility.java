package grill24.potionsplus.utility;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerPlayerUtility
{
    public static HashMap<UUID, ItemStack> lastHeldItem = new HashMap<>();

    @SubscribeEvent
    public static void onPreTick(ServerTickEvent.Pre event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            updateHeldItem(event.getServer(), player);
        }
    }

    @SubscribeEvent
    public static void onTossItemEvent(final ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(server, serverPlayer, event.getEntity().getItem(), ItemStack.EMPTY));
        }
    }

    private static void updateHeldItem(MinecraftServer server, ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Store the last held item
        ItemStack lastHeldItem = ServerPlayerUtility.lastHeldItem.get(uuid);
        if (lastHeldItem == null) {
            lastHeldItem = ItemStack.EMPTY;
        }

        // Get the current held item
        ItemStack heldItem = player.getMainHandItem();
        // If the item has changed, post an event and update the last held item
        if (!ItemStack.isSameItemSameComponents(lastHeldItem, heldItem)) {
            // Store the current held item
            ServerPlayerUtility.lastHeldItem.put(uuid, heldItem);
            // Post the event
            NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(server, player, lastHeldItem, heldItem));
        }
    }

    public static ItemStack getLastHeldItem(ServerPlayer player) {
        ItemStack itemStack = lastHeldItem.get(player.getUUID());
        return itemStack == null ? ItemStack.EMPTY : itemStack;
    }
}
