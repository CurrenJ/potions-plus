package grill24.potionsplus.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Singleplayer runs the client tick and the integrated server's tick on separate threads in the
// same JVM, and both call tick() here (see ForgeClientEventListeners/ForgeEventListeners and their
// Fabric/NeoForge equivalents) while other code concurrently calls queueDelayedEvent() from either
// thread. A plain ArrayList's removeIf() is not safe under concurrent mutation and throws
// ConcurrentModificationException under that access pattern; CopyOnWriteArrayList tolerates it.
public class DelayedEvents {
    private record DelayedEvent(Runnable runnable, long executionTimestamp) {}

    private static final List<DelayedEvent> delayedEvents = new CopyOnWriteArrayList<>();

    public static void tick(long timestamp) {
        List<DelayedEvent> ready = new ArrayList<>();
        delayedEvents.removeIf(event -> {
            if (timestamp >= event.executionTimestamp) {
                ready.add(event);
                return true;
            }
            return false;
        });
        ready.forEach(event -> event.runnable().run());
    }

    public static void queueDelayedEvent(Runnable runnable, long delay) {
        delayedEvents.add(new DelayedEvent(runnable, TickHandler.ticks() + delay));
    }
}
