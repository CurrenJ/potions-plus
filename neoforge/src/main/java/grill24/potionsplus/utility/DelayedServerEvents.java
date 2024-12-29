package grill24.potionsplus.utility;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class DelayedServerEvents {
    private record DelayedEvent(Runnable runnable, int executionTimestamp) { }
    private static final List<DelayedEvent> delayedEvents = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTickEnd(final ServerTickEvent.Post post) {
        delayedEvents.removeIf(event -> {
            if (ServerTickHandler.ticksInGame >= event.executionTimestamp) {
                event.runnable().run();
                return true;
            }
            return false;
        });
    }

    public static void queueDelayedEvent(Runnable runnable, int delay) {
        delayedEvents.add(new DelayedEvent(runnable, ServerTickHandler.ticksInGame + delay));
    }
}
