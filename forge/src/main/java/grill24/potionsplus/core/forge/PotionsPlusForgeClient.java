package grill24.potionsplus.core.forge;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.slf4j.Logger;

@Mod(ModInfo.MOD_ID)
public class PotionsPlusForgeClient {
    public static final Logger LOGGER = LogUtils.getLogger();

    public PotionsPlusForgeClient(FMLModContainer container) {
        FMLClientSetupEvent.getBus(container.getModBusGroup()).addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Potions Plus (Forge) client initializing");

        // Client-side event listeners (tick/render-tick, tooltip component factory, tooltip animation, use-item).
        grill24.potionsplus.event.forge.ForgeClientEventListeners.register();

        // Client wiring (renderers, particles, tooltips, colors, models, JEI) lands in Phase 8.
    }
}
