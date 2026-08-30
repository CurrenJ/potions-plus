package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.command.PpCommands;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * NeoForge command-registration hook; the actual command tree lives in the
 * loader-agnostic {@link PpCommands} so Fabric/Forge share the same definitions.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoCommandEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PpCommands.register(event.getDispatcher());
    }
}
