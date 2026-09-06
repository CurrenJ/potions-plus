package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;

@SuppressWarnings("unchecked")
public class Particles {

    // Particles
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ModInfo.MOD_ID);
    public static final ForgeHolder<SimpleParticleType> END_ROD_RAIN = register("end_rod_rain");
    public static final ForgeHolder<SimpleParticleType> BLOOD_GOB = register("blood_gob");
    public static final ForgeHolder<SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT = register("lunar_berry_bush_ambient");
    public static final ForgeHolder<SimpleParticleType> LIGHTNING_BOLT = register("lightning_bolt");
    public static final ForgeHolder<SimpleParticleType> LIGHTNING_BOLT_SMALL = register("lightning_bolt_small");
    public static final ForgeHolder<SimpleParticleType> ELECTRICAL_SPARK = register("electrical_spark");
    public static final ForgeHolder<SimpleParticleType> STUN_STARS = register("stun_stars");

    // Emitters
    public static final ForgeHolder<SimpleParticleType> BLOOD_EMITTER = register("blood_emitter");
    public static final ForgeHolder<SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT_EMITTER = register("lunar_berry_bush_ambient_emitter");

    private static ForgeHolder<SimpleParticleType> register(String name) {
        return ForgeHolder.of(PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false)));
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
