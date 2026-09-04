package grill24.potionsplus.core.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public class PotionsPlusFabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Potions Plus (Fabric) client initializing");

        // Network packets (client-side: serverbound codec + clientbound codecs + handlers). Phase 5.
        Packets.registerClient();

        // Tick / lifecycle bucket (Phase 7).
        grill24.potionsplus.event.fabric.TickListeners.registerClient();

        // Client tooltips bucket (Phase 7).
        grill24.potionsplus.event.fabric.TooltipListeners.registerClient();

        // Particle providers (Phase 11). 1.21.1-era fabric-api still exposes
        // ParticleFactoryRegistry (not the later ParticleProviderRegistry rename) - see
        // docs/multi-loader-expansion.md Phase 11.
        ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
        particleRegistry.register(grill24.potionsplus.core.Particles.END_ROD_RAIN.value(), EndRodRainParticle.Provider::new);
        particleRegistry.register(grill24.potionsplus.core.Particles.BLOOD_GOB.value(), BloodGobParticle.Provider::new);
        particleRegistry.register(grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), LunarBerryBushAmbientParticle.Provider::new);
        particleRegistry.register(grill24.potionsplus.core.Particles.LIGHTNING_BOLT.value(), LightningBoltParticle.Provider::new);
        particleRegistry.register(grill24.potionsplus.core.Particles.LIGHTNING_BOLT_SMALL.value(), SmallLightningBoltParticle.Provider::new);
        particleRegistry.register(grill24.potionsplus.core.Particles.ELECTRICAL_SPARK.value(), ElectricalSparkParticle.Provider::new);
        particleRegistry.register(grill24.potionsplus.core.Particles.STUN_STARS.value(), StunStarsParticle.Provider::new);

        particleRegistry.register(grill24.potionsplus.core.Particles.BLOOD_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        particleRegistry.register(grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));

        // Item color (potion tint - rainbow-cycles for "any potion" placeholder effects). No block
        // (cauldron water) tint here: BrewingCauldronBlockEntity hasn't been ported off neoforge yet
        // (see docs/multi-loader-expansion.md Phase 11 progress log) - Fabric has no cauldron BE to
        // tint at all right now, so registering one would be a no-op stub.
        ColorProviderRegistry.ITEM.register(PotionsPlusItemColors::anyPotionItemColor, Items.POTION);

        // Block entity renderers (Phase 11a). Only Clothesline/PotionBeacon are portable so far - the
        // other four BE renderers still reference neoforge-only BE classes (see
        // docs/multi-loader-expansion.md Phase 11a progress log).
        BlockEntityRendererRegistry.register(Blocks.CLOTHESLINE_BLOCK_ENTITY.value(), ClotheslineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(Blocks.POTION_BEACON_BLOCK_ENTITY.value(), PotionBeaconBlockEntityRenderer::new);

        // Key mapping (Phase 11).
        KeyMapping activateAbility = new KeyMapping(
                grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY_TRANSLATION_KEY,
                InputConstants.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_2,
                grill24.potionsplus.core.KeyMappings.CATEGORY_TRANSLATION_KEY);
        grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY = KeyBindingHelper.registerKeyBinding(activateAbility);

        // Tooltip component factory (ClientItemStacksTooltip, e.g. brewing-knowledge item grids) is
        // NOT wired here. Phase 11a step 3 ported its DynamicIconItems dependency to common/, so
        // that blocker is gone, but ClientItemStacksTooltip is still registered through NeoForge's
        // own client tooltip-component-factory extension point (ClientTooltipComponentFactoriesListeners
        // + ItemMixin, neoforge/), which has no vanilla/Fabric equivalent - porting it needs a
        // per-loader tooltip-component registration mechanism, not attempted this session. See the
        // Phase 11a progress log entry.
    }
}
