package grill24.potionsplus.core;

import grill24.potionsplus.particle.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import static grill24.potionsplus.core.Items.*;
import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Register item models
        event.enqueueWork(() -> {
            // Register item properties
            ClampedItemPropertyFunction clampedItemPropertyFunction =
                    (stack, world, entity, i) -> (float) (stack.getCount() - 1) / 64.0F + 0.01F;
            ResourceLocation propertyId = ppId(DYNAMIC_ICON_INDEX_PROPERTY_NAME);
            net.minecraft.client.renderer.item.ItemProperties.register(POTION_EFFECT_ICON.value(), propertyId, clampedItemPropertyFunction);
            net.minecraft.client.renderer.item.ItemProperties.register(GENERIC_ICON.value(), propertyId, clampedItemPropertyFunction);
        });
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(Particles.WANDERING_HEART.get(), WanderingHeartParticle.WanderingHeartProvider::new);
        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN.get(), EndRodRainParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.RANDOM_NOTE.get(), RandomNoteParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_GOB.get(), BloodGobParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.LUNAR_BERRY_BUSH_AMBIENT.get(), LunarBerryBushAmbientParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.END_ROD_RAIN::sampleParticleType));
        Minecraft.getInstance().particleEngine.register(Particles.FIREY_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.FIREY::sampleParticleType, 100, 2, 2, 16));
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.BLOOD::sampleParticleType, 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
        Minecraft.getInstance().particleEngine.register(Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER.get(),
                new EmitterParticle.Provider(ParticleConfigurations.LUNAR_BERRY_BUSH_AMBIENT::sampleParticleType, 20, 20, 2, 0.5F, Vec3.ZERO, Vec3.ZERO, false, true));
        Minecraft.getInstance().particleEngine.register(Particles.LAVA_GEYSER_BLOCK_LINKED_EMITTER.get(),
                new BlockLinkedEmitterParticle.Provider(20));
    }
}
