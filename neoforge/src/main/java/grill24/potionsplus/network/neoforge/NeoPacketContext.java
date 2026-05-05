package grill24.potionsplus.network.neoforge;

import grill24.potionsplus.network.PacketContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import java.util.concurrent.CompletableFuture;

public class NeoPacketContext implements PacketContext {
    private final IPayloadContext context;

    public NeoPacketContext(IPayloadContext context) {
        this.context = context;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        return context.enqueueWork(runnable);
    }

    @Override
    public Player player() {
        if (context instanceof ServerPayloadContext serverContext) {
            return serverContext.player();
        }
        return context.player();
    }

    @Override
    public void disconnect(Component reason) {
        context.disconnect(reason);
    }
}
