package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Renderers {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), BrewingCauldronBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get(), HerbalistsLecternBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get(), SanguineAltarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get(), AbyssalTroveBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), ClotheslineBlockEntityRenderer::new);
    }
}
