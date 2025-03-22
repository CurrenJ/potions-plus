package grill24.potionsplus.utility;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DelayedEvents {
    private record DelayedEvent(Runnable runnable, long executionTimestamp) { }
    private static final List<DelayedEvent> delayedEvents = new ArrayList<>();

    protected static void tick(long timestamp) {
        delayedEvents.removeIf(event -> {
            if (timestamp >= event.executionTimestamp) {
                event.runnable().run();
                return true;
            }
            return false;
        });
    }

    public static void queueDelayedEvent(Runnable runnable, long delay) {
        delayedEvents.add(new DelayedEvent(runnable, TickHandler.ticks() + delay));
    }

    @SubscribeEvent
    public static void onServerTickEnd(final ServerTickEvent.Post event) {
        tick(TickHandler.ticks());
    }

    @SubscribeEvent
    public static void onClientTickEnd(final ClientTickEvent.Post event) {
        tick(TickHandler.ticks());
    }
}
