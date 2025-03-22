package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class KeyMappingsListener {
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        if (KeyMappings.ACTIVATE_ABILITY.get().consumeClick()) {
            // TODO: Implement ability activation
        }
    }
}
