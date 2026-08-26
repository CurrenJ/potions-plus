package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.core.Entities;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntityRenderer;
import grill24.potionsplus.entity.GrunglerModel;
import grill24.potionsplus.entity.GrunglerRenderer;
import grill24.potionsplus.entity.LayerDefinitions;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntityRenderer;
import grill24.potionsplus.blockentity.ClotheslineBlockEntityRenderer;
import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntityRenderer;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntityRenderer;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntityRenderer;
import grill24.potionsplus.item.tintsource.AnyPotionTintSource;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class Renderers {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), BrewingCauldronBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get(), HerbalistsLecternBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get(), SanguineAltarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get(), AbyssalTroveBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), ClotheslineBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.POTION_BEACON_BLOCK_ENTITY.get(), PotionBeaconBlockEntityRenderer::new);

        event.registerEntityRenderer(Entities.INVISIBLE_FIRE_DAMAGER.value(), NoopRenderer::new);
        event.registerEntityRenderer(Entities.GRUNGLER.value(), GrunglerRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LayerDefinitions.GRUNGLER, GrunglerModel::createBodyLayer);
    }

    
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(AnyPotionTintSource.ID, AnyPotionTintSource.CODEC);
    }
}
