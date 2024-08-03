package grill24.potionsplus.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

public class OreFlowerBlock extends PotionsPlusFlowerBlock {
    private final float generationChance;

    public OreFlowerBlock(Supplier<MobEffect> effectSupplier, int effectDuration, Properties properties, boolean spawnParticles, Supplier<ParticleOptions> particles, Function<BlockState, Boolean> mayPlaceOn, float generationChance) {
        super(effectSupplier, effectDuration, properties, spawnParticles, particles, mayPlaceOn);
        this.generationChance = generationChance;
    }

    public float getGenerationChance() {
        return generationChance;
    }
}
