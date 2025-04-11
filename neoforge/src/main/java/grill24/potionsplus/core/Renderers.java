package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Renderers {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), BrewingCauldronBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get(), HerbalistsLecternBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get(), SanguineAltarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get(), AbyssalTroveBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), ClotheslineBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.POTION_BEACON_BLOCK_ENTITY.get(), PotionBeaconBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.FISH_TANK_BLOCK_ENTITY.get(), FishTankBlockEntityRenderer::new);

        event.registerEntityRenderer(Entities.INVISIBLE_FIRE_DAMAGER.get(), NoopRenderer::new);
    }
}
