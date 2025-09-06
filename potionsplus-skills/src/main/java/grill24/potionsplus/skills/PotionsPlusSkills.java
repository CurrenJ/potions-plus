package grill24.potionsplus.skills;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Simple Skills - Simple and intuitive skill system for Minecraft.
 * Handles skill systems, player abilities, leveling mechanics, and related functionality.
 */
@Mod("potionsplus_skills")
public class PotionsPlusSkills {
    public static final String MOD_ID = "potionsplus_skills";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PotionsPlusSkills(IEventBus bus, ModContainer container) {
        LOGGER.info("Simple Skills initializing...");
        
        // TODO: Register skill-related components here
        // - Skill system
        // - Player abilities
        // - Skill GUI
        // - Skill blocks (SkillJournals)
        // - Skill items
        
        LOGGER.info("Simple Skills initialized successfully!");
    }
}