package grill24.potionsplus.utility;

import java.util.ArrayList;
import java.util.List;

public class DelayedEvents {
    private record DelayedEvent(Runnable runnable, long executionTimestamp) {}

    private static final List<DelayedEvent> delayedEvents = new ArrayList<>();

    public static void tick(long timestamp) {
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
}
