package grill24.potionsplus.shared;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Coo Core - Core library providing common utilities and base functionality
 * for modular Minecraft mods, including Potions Plus and Simple Skills.
 */
@Mod("potionsplus_shared")
public class PotionsPlusShared {
    public static final String MOD_ID = "potionsplus_shared";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PotionsPlusShared(IEventBus bus, ModContainer container) {
        LOGGER.info("Coo Core initializing...");
        
        // Register shared components here
        // TODO: Move shared registries here
        
        LOGGER.info("Coo Core initialized successfully!");
    }
}