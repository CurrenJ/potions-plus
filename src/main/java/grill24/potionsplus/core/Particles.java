package grill24.potionsplus.core;

import grill24.potionsplus.particle.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Particles {
    // Particles
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ModInfo.MOD_ID);
    public static final RegistryObject<SimpleParticleType> WANDERING_HEART = register("wandering_heart", false);
    public static final RegistryObject<SimpleParticleType> END_ROD_RAIN = register("end_rod_rain", false);
    public static final RegistryObject<SimpleParticleType> RANDOM_NOTE = register("random_note", false);
    public static final RegistryObject<SimpleParticleType> BLOOD_GOB = register("blood_gob", false);

    // Emitters
    public static final RegistryObject<SimpleParticleType> END_ROD_RAIN_EMITTER = register("end_rod_rain_emitter", false);
    public static final RegistryObject<SimpleParticleType> FIREY_EMITTER = register("firey_emitter", false);
    public static final RegistryObject<SimpleParticleType> BLOOD_EMITTER = register("blood_emitter", false);

    private static RegistryObject<SimpleParticleType> register(String p_123825_, boolean p_123826_) {
        return PARTICLE_TYPES.register(p_123825_, () -> new SimpleParticleType(p_123826_));
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(Particles.WANDERING_HEART.get(), WanderingHeartParticle.WanderingHeartProvider::new);
        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN.get(), EndRodRainParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.RANDOM_NOTE.get(), RandomNoteParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_GOB.get(), BloodGobParticle.Provider::new);

        Minecraft.getInstance().particleEngine.register(Particles.END_ROD_RAIN_EMITTER.get(),
                new EmitterParticle.Provider((random) -> ParticleConfigurations.END_ROD_RAIN.sampleParticleType(random).get()));
        Minecraft.getInstance().particleEngine.register(Particles.FIREY_EMITTER.get(),
                new EmitterParticle.Provider((random) -> ParticleConfigurations.FIREY.sampleParticleType(random).get(), 100, 2, 2, 16));
        Minecraft.getInstance().particleEngine.register(Particles.BLOOD_EMITTER.get(),
                new EmitterParticle.Provider((random) -> ParticleConfigurations.BLOOD.sampleParticleType(random).get(), 20, 2, 2, 0.4F, Vec3.ZERO, Vec3.ZERO, true));
    }
}
