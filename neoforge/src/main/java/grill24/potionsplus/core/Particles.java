package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Particles {
    // Particles
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ModInfo.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WANDERING_HEART = register("wandering_heart", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> END_ROD_RAIN = register("end_rod_rain", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RANDOM_NOTE = register("random_note", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_GOB = register("blood_gob", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT = register("lunar_berry_bush_ambient", false);

    // Emitters
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> END_ROD_RAIN_EMITTER = register("end_rod_rain_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIREY_EMITTER = register("firey_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_EMITTER = register("blood_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT_EMITTER = register("lunar_berry_bush_ambient_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LAVA_GEYSER_BLOCK_LINKED_EMITTER = register("lava_geyser_emitter", false);

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String p_123825_, boolean p_123826_) {
        return PARTICLE_TYPES.register(p_123825_, () -> new SimpleParticleType(p_123826_));
    }

}
