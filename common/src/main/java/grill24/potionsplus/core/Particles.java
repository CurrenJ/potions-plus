package grill24.potionsplus.core;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Loader-agnostic stub for the particle type statics. The neoforge {@code DeferredRegister} lives in
 * {@code core.neoforge.Particles}, which populates these fields; Fabric/Forge register their own
 * {@code SimpleParticleType}s into the same statics. See docs/multi-loader-expansion.md Phase 4.
 */
public class Particles {
    public static Holder<SimpleParticleType> END_ROD_RAIN;
    public static Holder<SimpleParticleType> BLOOD_GOB;
    public static Holder<SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT;
    public static Holder<SimpleParticleType> LIGHTNING_BOLT;
    public static Holder<SimpleParticleType> LIGHTNING_BOLT_SMALL;
    public static Holder<SimpleParticleType> ELECTRICAL_SPARK;
    public static Holder<SimpleParticleType> STUN_STARS;

    public static Holder<SimpleParticleType> BLOOD_EMITTER;
    public static Holder<SimpleParticleType> LUNAR_BERRY_BUSH_AMBIENT_EMITTER;
}
