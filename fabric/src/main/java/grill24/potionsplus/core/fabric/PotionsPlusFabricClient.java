package grill24.potionsplus.core.fabric;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntityRenderer;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntityRenderer;
import grill24.potionsplus.blockentity.ClotheslineBlockEntityRenderer;
import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntityRenderer;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntityRenderer;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntityRenderer;
import grill24.potionsplus.entity.GrunglerModel;
import grill24.potionsplus.entity.GrunglerRenderer;
import grill24.potionsplus.entity.LayerDefinitions;
import grill24.potionsplus.item.tintsource.AnyPotionTintSource;
import grill24.potionsplus.particle.BloodGobParticle;
import grill24.potionsplus.particle.ElectricalSparkParticle;
import grill24.potionsplus.particle.EmitterParticle;
import grill24.potionsplus.particle.EndRodRainParticle;
import grill24.potionsplus.particle.LightningBoltParticle;
import grill24.potionsplus.particle.LunarBerryBushAmbientParticle;
import grill24.potionsplus.particle.ParticleConfigurations;
import grill24.potionsplus.particle.SmallLightningBoltParticle;
import grill24.potionsplus.particle.StunStarsParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class PotionsPlusFabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Potions Plus (Fabric) client initializing");

        // Network packets (client-side: serverbound codec + clientbound codecs + handlers).
        Packets.registerClient();

        // Client-side event listeners (tick/render-tick, tooltip component factory, tooltip animation).
        grill24.potionsplus.event.fabric.FabricClientEventListeners.register();

        // Block entity renderers (vanilla BlockEntityRenderers is public in 26.1.2).
        BlockEntityRenderers.register(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.value(), BrewingCauldronBlockEntityRenderer::new);
        BlockEntityRenderers.register(Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.value(), HerbalistsLecternBlockEntityRenderer::new);
        BlockEntityRenderers.register(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.value(), SanguineAltarBlockEntityRenderer::new);
        BlockEntityRenderers.register(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.value(), AbyssalTroveBlockEntityRenderer::new);
        BlockEntityRenderers.register(Blocks.CLOTHESLINE_BLOCK_ENTITY.value(), ClotheslineBlockEntityRenderer::new);
        BlockEntityRenderers.register(Blocks.POTION_BEACON_BLOCK_ENTITY.value(), PotionBeaconBlockEntityRenderer::new);

        // Entity renderer + model layer definition.
        EntityRenderers.register(grill24.potionsplus.core.Entities.GRUNGLER.value(), GrunglerRenderer::new);
        ModelLayerRegistry.registerModelLayer(LayerDefinitions.GRUNGLER, GrunglerModel::createBodyLayer);

        // Particle providers.
        ParticleProviderRegistry.getInstance().register(Particles.END_ROD_RAIN.value(), EndRodRainParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(Particles.BLOOD_GOB.value(), BloodGobParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), LunarBerryBushAmbientParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(Particles.LIGHTNING_BOLT.value(), LightningBoltParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(Particles.LIGHTNING_BOLT_SMALL.value(), SmallLightningBoltParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(Particles.ELECTRICAL_SPARK.value(), ElectricalSparkParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(Particles.STUN_STARS.value(), StunStarsParticle.Provider::new);

        ParticleProviderRegistry.getInstance().register(Particles.BLOOD_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        ParticleProviderRegistry.getInstance().register(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));

        // Item tint source: 26.1.2 replaced ColorProviderRegistry with data-driven ItemTintSource codecs.
        // NeoForge wraps this in RegisterColorHandlersEvent.ItemTintSources; Fabric/Forge put directly
        // on the vanilla ID_MAPPER (a live BiMap, so late additions before model bake are fine).
        ItemTintSources.ID_MAPPER.put(AnyPotionTintSource.ID, AnyPotionTintSource.CODEC);
    }
}
