package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unchecked")
public class Particles {


    // Particles
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ModInfo.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> END_ROD_RAIN = register("end_rod_rain", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_GOB = register("blood_gob", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT = register("lunar_berry_bush_ambient", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_BOLT = register("lightning_bolt", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_BOLT_SMALL = register("lightning_bolt_small", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ELECTRICAL_SPARK = register("electrical_spark", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STUN_STARS = register("stun_stars", false);

    // Emitters
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_EMITTER = register("blood_emitter", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT_EMITTER = register("lunar_berry_bush_ambient_emitter", false);

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name, boolean b) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(b));
    }

    static {
        // Populate common stubs. 1.21.1 NeoForge's DeferredHolder implements Holder<R-registry>, not
        // Holder<T-value>, so the assignment needs the raw cast (matches the 26.1.2 mirror).
        grill24.potionsplus.core.Particles.END_ROD_RAIN = (Holder) (Object) END_ROD_RAIN;
        grill24.potionsplus.core.Particles.BLOOD_GOB = (Holder) (Object) BLOOD_GOB;
        grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT = (Holder) (Object) LUNAR_BERRY_BUSH_AMBIENT;
        grill24.potionsplus.core.Particles.LIGHTNING_BOLT = (Holder) (Object) LIGHTNING_BOLT;
        grill24.potionsplus.core.Particles.LIGHTNING_BOLT_SMALL = (Holder) (Object) LIGHTNING_BOLT_SMALL;
        grill24.potionsplus.core.Particles.ELECTRICAL_SPARK = (Holder) (Object) ELECTRICAL_SPARK;
        grill24.potionsplus.core.Particles.STUN_STARS = (Holder) (Object) STUN_STARS;
        grill24.potionsplus.core.Particles.BLOOD_EMITTER = (Holder) (Object) BLOOD_EMITTER;
        grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER = (Holder) (Object) LUNAR_BERRY_BUSH_AMBIENT_EMITTER;
    }
}
