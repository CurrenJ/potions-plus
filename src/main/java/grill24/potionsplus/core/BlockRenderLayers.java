package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.BuiltinRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockRenderLayers {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Render layers are registered here
        // TODO: Do this automatically for all flower cutouts
        ItemBlockRenderTypes.setRenderLayer(Blocks.LUNAR_BERRY_BUSH.get(), RenderType.cutout());
    }
}
