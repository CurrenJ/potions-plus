package grill24.potionsplus.core.fabric;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class PotionsPlusFabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Potions Plus (Fabric) client initializing");

        // Network packets (client-side: serverbound codec + clientbound codecs + handlers). Phase 5.
        Packets.registerClient();

        // Tick / lifecycle bucket (Phase 7).
        grill24.potionsplus.event.fabric.TickListeners.registerClient();

        // Client-side event listeners, block entity renderers, particle providers, tint sources.
        // Phase 7 (renderers need the BE-block/BE ports; particles/sounds are registered server-side
        // but their providers are client-side) - deferred until the common BE/particle classes exist.
    }
}
