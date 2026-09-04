package grill24.potionsplus.core.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class Particles {

    // Particles
    public static final Holder<SimpleParticleType> END_ROD_RAIN = register("end_rod_rain");
    public static final Holder<SimpleParticleType> BLOOD_GOB = register("blood_gob");
    public static final Holder<SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT = register("lunar_berry_bush_ambient");
    public static final Holder<SimpleParticleType> LIGHTNING_BOLT = register("lightning_bolt");
    public static final Holder<SimpleParticleType> LIGHTNING_BOLT_SMALL = register("lightning_bolt_small");
    public static final Holder<SimpleParticleType> ELECTRICAL_SPARK = register("electrical_spark");
    public static final Holder<SimpleParticleType> STUN_STARS = register("stun_stars");

    // Emitters
    public static final Holder<SimpleParticleType> BLOOD_EMITTER = register("blood_emitter");
    public static final Holder<SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT_EMITTER = register("lunar_berry_bush_ambient_emitter");

    public static void init() {
        // No-op: forces class loading so the static initializers run.
    }

    private static Holder<SimpleParticleType> register(String name) {
        // Vanilla's SimpleParticleType constructor is protected (Forge/NeoForge patch it public), so
        // use an anonymous subclass to reach it.
        return FabricRegistration.register(BuiltInRegistries.PARTICLE_TYPE, name, () -> new SimpleParticleType(false) {});
    }

    static {
        grill24.potionsplus.core.Particles.END_ROD_RAIN = END_ROD_RAIN;
        grill24.potionsplus.core.Particles.BLOOD_GOB = BLOOD_GOB;
        grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT = LUNAR_BERRY_BUSH_AMBIENT;
        grill24.potionsplus.core.Particles.LIGHTNING_BOLT = LIGHTNING_BOLT;
        grill24.potionsplus.core.Particles.LIGHTNING_BOLT_SMALL = LIGHTNING_BOLT_SMALL;
        grill24.potionsplus.core.Particles.ELECTRICAL_SPARK = ELECTRICAL_SPARK;
        grill24.potionsplus.core.Particles.STUN_STARS = STUN_STARS;
        grill24.potionsplus.core.Particles.BLOOD_EMITTER = BLOOD_EMITTER;
        grill24.potionsplus.core.Particles.LUNAR_BERRY_BUSH_AMBIENT_EMITTER = LUNAR_BERRY_BUSH_AMBIENT_EMITTER;
    }
}
