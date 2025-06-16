package grill24.potionsplus.event;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class ServerPlayerHeldItemChangedEvent extends Event {
    private final MinecraftServer server;
    private final ServerPlayer player;
    private final ItemStack lastHeldItem;
    private final ItemStack heldItem;

    public ServerPlayerHeldItemChangedEvent(MinecraftServer server, ServerPlayer player, ItemStack lastHeldItem, ItemStack heldItem) {
        this.server = server;
        this.player = player;
        this.lastHeldItem = lastHeldItem;
        this.heldItem = heldItem;

//        PotionsPlus.LOGGER.info(this.lastHeldItem + " -> " + this.heldItem);
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public ItemStack getLastHeldItem() {
        return lastHeldItem;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }
}
