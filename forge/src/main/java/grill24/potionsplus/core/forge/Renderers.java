package grill24.potionsplus.core.forge;

import com.mojang.blaze3d.platform.InputConstants;
import grill24.potionsplus.blockentity.ClotheslineBlockEntityRenderer;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntityRenderer;
import grill24.potionsplus.item.tintsource.PotionsPlusItemColors;
import grill24.potionsplus.particle.BloodGobParticle;
import grill24.potionsplus.particle.ElectricalSparkParticle;
import grill24.potionsplus.particle.EmitterParticle;
import grill24.potionsplus.particle.EndRodRainParticle;
import grill24.potionsplus.particle.LightningBoltParticle;
import grill24.potionsplus.particle.LunarBerryBushAmbientParticle;
import grill24.potionsplus.particle.ParticleConfigurations;
import grill24.potionsplus.particle.SmallLightningBoltParticle;
import grill24.potionsplus.particle.StunStarsParticle;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Forge client-side particle/color/key-mapping registration.
 *
 * <p>{@code @Mod.EventBusSubscriber(bus = MOD, value = Dist.CLIENT)} is FML's own dist-gated
 * subscriber mechanism (present since old Forge; this version - 52.1.2, eventbus 6.x - exposes
 * {@code SubscribeEvent} at {@code net.minecraftforge.eventbus.api}, not the {@code .api.listener}
 * package the 26.1.2 reference tree's newer Forge uses, and its {@code Bus} enum has only
 * {@code MOD}/{@code FORGE} - no {@code BOTH}) - FML scans and registers this class only on the
 * matching physical side, so the class (and the client-only event/particle/KeyMapping types it
 * references) is never loaded on a dedicated server. These three events fire during
 * {@code Minecraft.<init>}, <em>before</em> {@code FMLClientSetupEvent} (the Phase 11 timing warning
 * in docs/multi-loader-expansion.md) - a plain listener nested inside {@code PotionsPlusForge}'s
 * existing {@code FMLClientSetupEvent} handler would be too late, which is why this needs its own
 * dist-gated subscriber class rather than reusing that hook.
 *
 * <p><b>Only 2 of 6 BE renderers are registered here (Phase 11a).</b> Clothesline and PotionBeacon
 * were ported to {@code common/} in Phase 11a (their only neoforge coupling was the
 * {@code BlockEntityType} holder lookup, now {@code core.Blocks}). The other four (brewing cauldron,
 * herbalist's lectern, sanguine altar, abyssal trove) still reference their concrete
 * {@code BlockEntity} subclasses under {@code blockentity.neoforge.*}, blocked on the
 * {@code DynamicIconItems}/{@code RecipesRegistrar} registration DSL (Phase 11a steps 3/4) - see
 * docs/multi-loader-expansion.md. The block (cauldron water) tint is skipped for the same reason -
 * registering it here would be a no-op stub.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Renderers {

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(grill24.potionsplus.core.Particles.END_ROD_RAIN.value(), EndRodRainParticle.Provider::new);
        event.registerSpriteSet(grill24.potionsplus.core.Particles.BLOOD_GOB.value(), BloodGobParticle.Provider::new);
        event.registerSpriteSet(grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), LunarBerryBushAmbientParticle.Provider::new);
        event.registerSpriteSet(grill24.potionsplus.core.Particles.LIGHTNING_BOLT.value(), LightningBoltParticle.Provider::new);
        event.registerSpriteSet(grill24.potionsplus.core.Particles.LIGHTNING_BOLT_SMALL.value(), SmallLightningBoltParticle.Provider::new);
        event.registerSpriteSet(grill24.potionsplus.core.Particles.ELECTRICAL_SPARK.value(), ElectricalSparkParticle.Provider::new);
        event.registerSpriteSet(grill24.potionsplus.core.Particles.STUN_STARS.value(), StunStarsParticle.Provider::new);

        event.registerSpecial(grill24.potionsplus.core.Particles.BLOOD_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        event.registerSpecial(grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));
    }

    @SubscribeEvent
    public static void registerColorHandlers(final RegisterColorHandlersEvent.Item event) {
        // Potion item tint (rainbow-cycles for "any potion" placeholder effects). Mirrors NeoForge's
        // core.neoforge.Blocks#registerItemColors via the shared PotionsPlusItemColors helper. No
        // block (cauldron water) tint here - see class javadoc.
        event.register((stack, i) -> PotionsPlusItemColors.anyPotionItemColor(stack, i), Items.POTION);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Blocks.CLOTHESLINE_BLOCK_ENTITY.value(), ClotheslineBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Blocks.POTION_BEACON_BLOCK_ENTITY.value(), PotionBeaconBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY = new KeyMapping(
                grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY_TRANSLATION_KEY,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_2,
                grill24.potionsplus.core.KeyMappings.CATEGORY_TRANSLATION_KEY);
        event.register(grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY);
    }
}
