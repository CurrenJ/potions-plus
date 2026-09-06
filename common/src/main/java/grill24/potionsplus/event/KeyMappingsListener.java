package grill24.potionsplus.event;

import grill24.potionsplus.core.KeyMappings;

/**
 * Shared client-tick handling for {@link KeyMappings#ACTIVATE_ABILITY}. Each loader calls {@link
 * #onClientTick()} from its own client tick event after registering the key mapping (NeoForge:
 * {@code ClientTickEvent.Post} on the game bus; Fabric: {@code ClientTickEvents.END_CLIENT_TICK};
 * Forge: {@code TickEvent.ClientTickEvent.Post}).
 */
public final class KeyMappingsListener {
    private KeyMappingsListener() {
    }

    public static void onClientTick() {
        if (KeyMappings.ACTIVATE_ABILITY != null && KeyMappings.ACTIVATE_ABILITY.consumeClick()) {
            // TODO: Implement ability activation
        }
    }
}
