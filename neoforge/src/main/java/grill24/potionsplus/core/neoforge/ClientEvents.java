package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.particle.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Register item model overrides
        event.enqueueWork(() -> {
            // Register item properties
            ClampedItemPropertyFunction clampedItemStackCountPropertyFunction =
                    (stack, world, entity, i) -> (float) (stack.getCount() - 1) / 64.0F + 0.01F;

            net.minecraft.client.renderer.item.ItemProperties.register(DynamicIconItems.POTION_EFFECT_ICON.value(), DynamicIconItems.DYNAMIC_ICON_INDEX_PROPERTY_NAME, clampedItemStackCountPropertyFunction);
            net.minecraft.client.renderer.item.ItemProperties.register(DynamicIconItems.GENERIC_ICON.value(), DynamicIconItems.DYNAMIC_ICON_INDEX_PROPERTY_NAME, clampedItemStackCountPropertyFunction);
        });
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN.value(), EndRodRainParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_GOB.value(), BloodGobParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), LunarBerryBushAmbientParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LIGHTNING_BOLT.value(), LightningBoltParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LIGHTNING_BOLT_SMALL.value(), SmallLightningBoltParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.ELECTRICAL_SPARK.value(), ElectricalSparkParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.STUN_STARS.value(), StunStarsParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        Minecraft.getInstance().particleEngine.register(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.value(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));
    }
}
