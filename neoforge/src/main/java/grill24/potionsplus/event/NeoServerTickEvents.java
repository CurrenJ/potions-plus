package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.ServerTickHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * NeoForge-specific event handler for server tick counting.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoServerTickEvents {

    @SubscribeEvent
    public static void onServerTickEnd(final ServerTickEvent.Post event) {
        ServerTickHandler.increment();
    }
}
