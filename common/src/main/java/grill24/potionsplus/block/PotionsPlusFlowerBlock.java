package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

public class PotionsPlusFlowerBlock extends FlowerBlock {
    private final boolean spawnParticles;
    private final Supplier<ParticleOptions> particles;
    private final Function<BlockState, Boolean> mayPlaceOn;

    public PotionsPlusFlowerBlock(Holder<MobEffect> effectHolder, int effectDuration, Properties properties, boolean spawnParticles, Supplier<ParticleOptions> particles, Function<BlockState, Boolean> mayPlaceOn) {
        super(effectHolder, effectDuration, properties);
        this.spawnParticles = spawnParticles;
        this.particles = particles;
        this.mayPlaceOn = mayPlaceOn;
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return mayPlaceOn.apply(blockState);
    }

    public boolean mayPlaceOn(BlockState blockState) {
        return mayPlaceOn.apply(blockState);
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return spawnParticles;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        super.randomTick(blockState, serverLevel, blockPos, random);

        // spawn particles
        // particle, x, y, z, count, xSpeed, ySpeed, zSpeed, maxSpeed
        if (spawnParticles)
            serverLevel.sendParticles(particles.get(), blockPos.getX() + 0.5D, blockPos.getY() + 1.1D, blockPos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0F);
    }
}
