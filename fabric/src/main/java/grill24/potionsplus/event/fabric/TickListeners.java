package grill24.potionsplus.event.fabric;

import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.ServerTickHandler;
import grill24.potionsplus.utility.TickHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * Fabric equivalent of NeoForge's {@code event/neoforge/{ClientGameListeners,NeoServerTickEvents,
 * NeoDelayedEvents}} (Phase 7 tick/lifecycle bucket). Registered from
 * {@code PotionsPlusFabric.onInitialize} (server ticks) and
 * {@code PotionsPlusFabricClient.onInitializeClient} (client ticks).
 */
public final class TickListeners {
    private TickListeners() {
    }

    public static void registerServer() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DelayedEvents.tick(TickHandler.ticks());
            ServerTickHandler.increment();
        });
    }

    public static void registerClient() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            DelayedEvents.tick(TickHandler.ticks());
            ClientTickHandler.clientTickEnd();
            grill24.potionsplus.event.KeyMappingsListener.onClientTick();
        });

        // RenderFrameEvent.Post equivalent. Only fires while a level is being rendered (not at the
        // main menu) - acceptable since ClientTickHandler is only consumed for in-game tooltip/
        // animation timing. fabric-rendering-v1 5.1.0 (resolved by fabric-api 0.116.7+1.21.1) has no
        // LevelRenderEvents (that's a later fabric-api addition) - WorldRenderEvents.START is this
        // version's equivalent hook, and WorldRenderContext.tickCounter() is the DeltaTracker.
        WorldRenderEvents.START.register(context ->
                ClientTickHandler.renderTick(context.tickCounter().getGameTimeDeltaPartialTick(true)));
    }
}
