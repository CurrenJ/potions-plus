package grill24.potionsplus.entity;

import grill24.potionsplus.core.Entities;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class LayerDefinitions {
    public static final ModelLayerLocation GRUNGLER = new ModelLayerLocation(
            // Should be the name of the entity this layer belongs to.
            // May be more generic if this layer can be used on multiple entities.
            ppId("grungler"),
            // The name of the layer itself. Should be main for the entity's base model,
            // and a more descriptive name (e.g. "wings") for more specific layers.
            "main"
    );

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GRUNGLER, GrunglerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.GRUNGLER.get(), GrunglerRenderer::new);
    }
}
