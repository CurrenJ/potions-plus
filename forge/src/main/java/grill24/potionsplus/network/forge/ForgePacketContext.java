package grill24.potionsplus.network.forge;

import grill24.potionsplus.network.PacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Adapts Forge's {@link CustomPayloadEvent.Context} to the common {@link PacketContext} interface.
 *
 * Forge's context exposes only {@link CustomPayloadEvent.Context#getSender()} (server-side) with no
 * client-player accessor, so the client player is resolved through {@link Minecraft} when the packet
 * is received client-side. There is no {@code disconnect} method on the context itself; disconnect
 * is done through the underlying {@code Connection}.
 */
public class ForgePacketContext implements PacketContext {
    private final CustomPayloadEvent.Context context;

    public ForgePacketContext(CustomPayloadEvent.Context context) {
        this.context = context;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        return context.enqueueWork(runnable);
    }

    @Override
    public Player player() {
        if (context.isServerSide()) {
            return context.getSender();
        }
        return Minecraft.getInstance().player;
    }

    @Override
    public void disconnect(Component reason) {
        context.getConnection().disconnect(reason);
    }
}
