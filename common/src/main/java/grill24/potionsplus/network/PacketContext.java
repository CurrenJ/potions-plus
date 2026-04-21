package grill24.potionsplus.network;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;

public interface PacketContext {
    CompletableFuture<Void> enqueueWork(Runnable runnable);

    Player player();

    void disconnect(Component reason);
}
