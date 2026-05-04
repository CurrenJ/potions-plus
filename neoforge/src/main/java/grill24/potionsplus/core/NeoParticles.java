package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoParticles {


    // Particles
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ModInfo.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WANDERING_HEART = register("wandering_heart", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> END_ROD_RAIN = register("end_rod_rain", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RANDOM_NOTE = register("random_note", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_GOB = register("blood_gob", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT = register("lunar_berry_bush_ambient", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_BOLT = register("lightning_bolt", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_BOLT_SMALL = register("lightning_bolt_small", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ELECTRICAL_SPARK = register("electrical_spark", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STUN_STARS = register("stun_stars", false);

    // Emitters
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> END_ROD_RAIN_EMITTER = register("end_rod_rain_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIREY_EMITTER = register("firey_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_EMITTER = register("blood_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT_EMITTER = register("lunar_berry_bush_ambient_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LAVA_GEYSER_BLOCK_LINKED_EMITTER = register("lava_geyser_emitter", false);

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name, boolean b) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(b));
    }

    static {
        Particles.WANDERING_HEART = WANDERING_HEART;
        Particles.END_ROD_RAIN = END_ROD_RAIN;
        Particles.RANDOM_NOTE = RANDOM_NOTE;
        Particles.BLOOD_GOB = BLOOD_GOB;
        Particles.LUNAR_BERRY_BUSH_AMBIENT = LUNAR_BERRY_BUSH_AMBIENT;
        Particles.LIGHTNING_BOLT = LIGHTNING_BOLT;
        Particles.LIGHTNING_BOLT_SMALL = LIGHTNING_BOLT_SMALL;
        Particles.ELECTRICAL_SPARK = ELECTRICAL_SPARK;
        Particles.STUN_STARS = STUN_STARS;
        Particles.END_ROD_RAIN_EMITTER = END_ROD_RAIN_EMITTER;
        Particles.FIREY_EMITTER = FIREY_EMITTER;
        Particles.BLOOD_EMITTER = BLOOD_EMITTER;
        Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER = LUNAR_BERRY_BUSH_AMBIENT_EMITTER;
        Particles.LAVA_GEYSER_BLOCK_LINKED_EMITTER = LAVA_GEYSER_BLOCK_LINKED_EMITTER;
    }
}
