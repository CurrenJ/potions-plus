package grill24.potionsplus.core.fabric;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class PotionsPlusFabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Potions Plus (Fabric) client initializing");
        // Client wiring (renderers, particles, tooltips, colors, models, JEI) lands in Phase 8.
    }
}
