package grill24.potionsplus.block;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

public class OreFlowerBlock extends PotionsPlusFlowerBlock {
    private Function<BlockState, Boolean> mayGenerateOn;
    private final float generationChance;

    public OreFlowerBlock(Holder<MobEffect> effectHolder, int effectDuration, Properties properties, boolean spawnParticles, Supplier<ParticleOptions> particles, Function<BlockState, Boolean> mayPlaceOn, Function<BlockState, Boolean> mayGenerateOn, float generationChance) {
        super(effectHolder, effectDuration, properties, spawnParticles, particles, mayPlaceOn);
        this.generationChance = generationChance;
        this.mayGenerateOn = mayGenerateOn;
    }

    public float getGenerationChance() {
        return generationChance;
    }

    public boolean mayGenerateOn(BlockState blockState) {
        return mayGenerateOn.apply(blockState);
    }

    @Override
    public boolean mayPlaceOn(BlockState blockState) {
        return super.mayPlaceOn(blockState) || mayGenerateOn.apply(blockState);
    }
}
