package grill24.potionsplus.core.forge;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntityRenderer;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntityRenderer;
import grill24.potionsplus.blockentity.ClotheslineBlockEntityRenderer;
import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntityRenderer;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntityRenderer;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntityRenderer;
import grill24.potionsplus.core.Entities;
import grill24.potionsplus.entity.GrunglerModel;
import grill24.potionsplus.entity.GrunglerRenderer;
import grill24.potionsplus.entity.LayerDefinitions;
import grill24.potionsplus.particle.BloodGobParticle;
import grill24.potionsplus.particle.ElectricalSparkParticle;
import grill24.potionsplus.particle.EmitterParticle;
import grill24.potionsplus.particle.EndRodRainParticle;
import grill24.potionsplus.particle.LightningBoltParticle;
import grill24.potionsplus.particle.LunarBerryBushAmbientParticle;
import grill24.potionsplus.particle.ParticleConfigurations;
import grill24.potionsplus.particle.SmallLightningBoltParticle;
import grill24.potionsplus.particle.StunStarsParticle;
import grill24.potionsplus.item.tintsource.AnyPotionTintSource;
import grill24.potionsplus.utility.ClientItemStacksTooltip;
import grill24.potionsplus.utility.ItemStacksTooltip;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client-side renderer/particle/tooltip registration.
 *
 * <p>These events fire during {@code Minecraft.<init>} (via {@code ForgeHooksClient.initClientHooks}
 * and the {@code Minecraft} patch), <em>before</em> {@code FMLClientSetupEvent} — so a listener
 * registered from an FMLClientSetupEvent handler would be too late. {@code @Mod.EventBusSubscriber}
 * with {@code bus = BOTH, value = Dist.CLIENT} registers these methods during mod loading (before
 * Minecraft construction) and only on the physical client, matching apt-ores's Forge client.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.BOTH, value = Dist.CLIENT)
public class Renderers {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Blocks.BREWING_CAULDRON_BLOCK_ENTITY.value(), BrewingCauldronBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.value(), HerbalistsLecternBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.value(), SanguineAltarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.value(), AbyssalTroveBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.CLOTHESLINE_BLOCK_ENTITY.value(), ClotheslineBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.POTION_BEACON_BLOCK_ENTITY.value(), PotionBeaconBlockEntityRenderer::new);

        event.registerEntityRenderer(Entities.GRUNGLER.value(), GrunglerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LayerDefinitions.GRUNGLER, GrunglerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Particles.END_ROD_RAIN.value(), EndRodRainParticle.Provider::new);
        event.registerSpriteSet(Particles.BLOOD_GOB.value(), BloodGobParticle.Provider::new);
        event.registerSpriteSet(Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), LunarBerryBushAmbientParticle.Provider::new);
        event.registerSpriteSet(Particles.LIGHTNING_BOLT.value(), LightningBoltParticle.Provider::new);
        event.registerSpriteSet(Particles.LIGHTNING_BOLT_SMALL.value(), SmallLightningBoltParticle.Provider::new);
        event.registerSpriteSet(Particles.ELECTRICAL_SPARK.value(), ElectricalSparkParticle.Provider::new);
        event.registerSpriteSet(Particles.STUN_STARS.value(), StunStarsParticle.Provider::new);

        event.registerSpecial(Particles.BLOOD_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        event.registerSpecial(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));
    }

    @SubscribeEvent
    public static void registerTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class,
                (tooltip) -> new ClientItemStacksTooltip(tooltip.items(), tooltip.hideUnknownPotionIngredients(), tooltip.renderItemDecorations()));
    }

    /**
     * Client-only wiring that used to live in a second {@code @Mod} class. Forge de-duplicates
     * {@code @Mod} classes by modid (first one wins), so a sibling {@code @Mod} entrypoint is never
     * constructed — the client listeners and item-tint source must instead hang off this dist-gated
     * subscriber (fired on the mod bus, after the renderer/particle events above but before model
     * baking, which is when the item-tint codec is read).
     */
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        grill24.potionsplus.event.forge.ForgeClientEventListeners.register();

        // Item tint source: 26.1.2 replaced ColorProviderRegistry with data-driven ItemTintSource
        // codecs. Forge has no item-tint event (RegisterColorHandlersEvent only covers Block and
        // ColorResolvers), so put directly on the vanilla ID_MAPPER before model baking.
        ItemTintSources.ID_MAPPER.put(AnyPotionTintSource.ID, AnyPotionTintSource.CODEC);
    }
}
