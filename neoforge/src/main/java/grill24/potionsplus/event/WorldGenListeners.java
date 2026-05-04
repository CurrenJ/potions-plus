package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.OverworldBiomesRegion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class WorldGenListeners {
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(OverworldBiomesRegion::register);
    }
}
