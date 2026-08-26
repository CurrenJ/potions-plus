package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

// Chunk 7 TODO: register render types per block via "render_type" in block model JSONs.
// ItemBlockRenderTypes.setRenderLayer was removed in 26.1.2.
@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class BlockRenderLayers {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Render type registration removed — migrate to model JSON "render_type" field in Chunk 7.
        // Affected blocks: ICICLE, POTION_BEACON (cutout);
        // all BushBlock subclasses (cutout); builder-specified render types.
    }
}
