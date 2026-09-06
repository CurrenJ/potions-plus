package grill24.potionsplus.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

/**
 * Describes a weighted set of particles an emitter can spawn. Shared by core particle
 * systems (Sanguine Altar blood, Lunar Berry Bush ambient) and consumed via
 * {@link #sampleParticleType(RandomSource)}.
 */
public class ParticleEmitterConfiguration {
    public Block blockModel;
    public WeightedParticleType[] weightedParticleTypes;
    public int particleCount;
    public boolean isEmitter;

    public static class WeightedParticleType {
        public SimpleParticleType particleType;
        public int weight;

        public WeightedParticleType(SimpleParticleType particleType, int weight) {
            this.particleType = particleType;
            this.weight = weight;
        }
    }

    public ParticleEmitterConfiguration(Block blockModel, int particleCount, WeightedParticleType... weightedParticleTypes) {
        this.blockModel = blockModel;
        this.weightedParticleTypes = weightedParticleTypes;
        this.particleCount = particleCount;
    }

    public ParticleEmitterConfiguration(Block blockModel, int particleCount, boolean isEmitter, WeightedParticleType... weightedParticleTypes) {
        this.blockModel = blockModel;
        this.weightedParticleTypes = weightedParticleTypes;
        this.particleCount = particleCount;
        this.isEmitter = isEmitter;
    }

    public SimpleParticleType sampleParticleType(RandomSource random) {
        int totalWeight = 0;
        for (WeightedParticleType weightedParticleType : weightedParticleTypes) {
            totalWeight += weightedParticleType.weight;
        }

        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (WeightedParticleType weightedParticleType : weightedParticleTypes) {
            currentWeight += weightedParticleType.weight;
            if (randomWeight < currentWeight) {
                return weightedParticleType.particleType;
            }
        }

        throw new IllegalStateException("Could not sample a particle type. This should never happen.");
    }
}
