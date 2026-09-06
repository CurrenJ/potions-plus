package grill24.potionsplus.network.fabric;

import grill24.potionsplus.network.PacketContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Adapts Fabric's networking contexts to the common {@link PacketContext} interface.
 *
 * Fabric splits the context into two unrelated interfaces ({@link ServerPlayNetworking.Context} and
 * {@link ClientPlayNetworking.Context}), so both are wrapped into the same functional shape here.
 * Fabric's enqueue is fire-and-forget ({@code server().execute} / {@code client().execute}), so the
 * returned future is already-complete rather than tracking the runnable's outcome.
 */
public final class FabricPacketContext implements PacketContext {
    private final Supplier<Player> playerSupplier;
    private final Function<Runnable, CompletableFuture<Void>> enqueuer;
    private final Consumer<Component> disconnecter;

    private FabricPacketContext(Supplier<Player> playerSupplier,
                                Function<Runnable, CompletableFuture<Void>> enqueuer,
                                Consumer<Component> disconnecter) {
        this.playerSupplier = playerSupplier;
        this.enqueuer = enqueuer;
        this.disconnecter = disconnecter;
    }

    public static FabricPacketContext server(ServerPlayNetworking.Context context) {
        return new FabricPacketContext(
                context::player,
                runnable -> {
                    context.server().execute(runnable);
                    return CompletableFuture.completedFuture(null);
                },
                reason -> context.player().connection.disconnect(reason)
        );
    }

    public static FabricPacketContext client(ClientPlayNetworking.Context context) {
        return new FabricPacketContext(
                context::player,
                runnable -> {
                    context.client().execute(runnable);
                    return CompletableFuture.completedFuture(null);
                },
                reason -> context.player().connection.getConnection().disconnect(reason)
        );
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        return enqueuer.apply(runnable);
    }

    @Override
    public Player player() {
        return playerSupplier.get();
    }

    @Override
    public void disconnect(Component reason) {
        disconnecter.accept(reason);
    }
}
