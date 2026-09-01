package grill24.potionsplus.particle.neoforge;

import grill24.potionsplus.core.neoforge.Particles;
import grill24.potionsplus.particle.ParticleEmitterConfiguration;
import net.minecraft.world.level.block.Blocks;

public class ParticleConfigurations {
    // Sanguine Altar blood ambient
    public static final ParticleEmitterConfiguration BLOOD = new ParticleEmitterConfiguration(Blocks.REDSTONE_BLOCK, 1, new ParticleEmitterConfiguration.WeightedParticleType(Particles.BLOOD_GOB.value(), 1));

    // Emitter for particles that are not particles themselves, but rather emitters themselves. This is used for particles that are not directly rendered, but rather spawn other particles.
    public static final ParticleEmitterConfiguration LUNAR_BERRY_BUSH_AMBIENT = new ParticleEmitterConfiguration(Blocks.MOSS_BLOCK, 1, true, new ParticleEmitterConfiguration.WeightedParticleType(Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), 1));
}
