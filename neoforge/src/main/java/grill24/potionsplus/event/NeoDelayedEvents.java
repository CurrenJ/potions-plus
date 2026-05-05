package grill24.potionsplus.event;

import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.TickHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * NeoForge-specific event handlers for delayed events on server and client ticks.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoDelayedEvents {

    @SubscribeEvent
    public static void onServerTickEnd(final ServerTickEvent.Post event) {
        DelayedEvents.tick(TickHandler.ticks());
    }

    @SubscribeEvent
    public static void onClientTickEnd(final ClientTickEvent.Post event) {
        DelayedEvents.tick(TickHandler.ticks());
    }
}
