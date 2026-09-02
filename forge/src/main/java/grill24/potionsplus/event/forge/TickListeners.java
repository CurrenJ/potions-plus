package grill24.potionsplus.event.forge;

import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.ServerTickHandler;
import grill24.potionsplus.utility.TickHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

/**
 * Forge equivalent of NeoForge's {@code event/neoforge/{ClientGameListeners,NeoServerTickEvents,
 * NeoDelayedEvents}} (Phase 7 tick/lifecycle bucket). Forge 52.1.2's {@link TickEvent} predates the
 * {@code .Post.BUS}-field refactor 26.1.2's Forge tree relies on (verified via javap: the nested
 * {@code Post}/{@code Pre} classes here carry no static {@code BUS} field) - so, matching the rest
 * of this module's explicit-registration style, these are plain lambda listeners against
 * {@link MinecraftForge#EVENT_BUS}, registered from {@code PotionsPlusForge}'s constructor
 * {@code registerServer()} is called unconditionally from {@code PotionsPlusForge}'s constructor.
 * {@code registerClient()} is called from the same constructor via a mod-bus
 * {@code FMLClientSetupEvent} listener - that event is only ever posted on the physical client, so
 * this needs no separate dist-gated {@code @Mod.EventBusSubscriber} class (unlike heavier client
 * wiring - renderers, particles - which touches client classes eagerly at registration time; here
 * we only register lambdas, and {@link ClientTickHandler}'s {@code Minecraft.getInstance()} call
 * inside them is never reached unless the event actually fires).
 */
public final class TickListeners {
    private TickListeners() {
    }

    public static void registerServer() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent.Post event) -> {
            DelayedEvents.tick(TickHandler.ticks());
            ServerTickHandler.increment();
        });
    }

    public static void registerClient() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent.Post event) -> {
            DelayedEvents.tick(TickHandler.ticks());
            ClientTickHandler.clientTickEnd();
        });

        MinecraftForge.EVENT_BUS.addListener((TickEvent.RenderTickEvent.Post event) ->
                ClientTickHandler.renderTick(event.getTimer().getGameTimeDeltaPartialTick(true)));
    }
}
