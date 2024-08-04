package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class OreFlowerBlock extends PotionsPlusFlowerBlock implements BonemealableBlock {
    private final float generationChance;

    public OreFlowerBlock(Supplier<MobEffect> effectSupplier, int effectDuration, Properties properties, boolean spawnParticles, Supplier<ParticleOptions> particles, Function<BlockState, Boolean> mayPlaceOn, float generationChance) {
        super(effectSupplier, effectDuration, properties, spawnParticles, particles, mayPlaceOn);
        this.generationChance = generationChance;
    }

    public float getGenerationChance() {
        return generationChance;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean b) {
        BlockPos down = blockPos.below();
        // Iterate horizontal
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos offset = down.relative(direction);
            BlockState state = blockGetter.getBlockState(offset);
            if (mayPlaceOn(state) && blockGetter.getBlockState(offset.above()).isAir()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
        return isValidBonemealTarget(level, blockPos, blockState, true);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, Random random, BlockPos blockPos, BlockState blockState) {
        grow(serverLevel, random, blockPos, blockState, true);
    }

    public void grow(ServerLevel serverLevel, Random random, BlockPos blockPos, BlockState blockState, boolean consumeOre) {
        BlockPos down = blockPos.below();
        // Iterate horizontal
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos offset = down.relative(direction);
            BlockState state = serverLevel.getBlockState(offset);
            if(mayPlaceOn(state) && serverLevel.getBlockState(offset.above()).isAir()) {
                if(consumeOre) {
                    Block stoneBlock = state.getBlock().getRegistryName().getPath().contains("deepslate") ? Blocks.DEEPSLATE : Blocks.STONE;
                    serverLevel.setBlock(offset, stoneBlock.defaultBlockState(), 3);
                    serverLevel.levelEvent(2001, offset, Block.getId(stoneBlock.defaultBlockState()));
                    popResource(serverLevel, offset.above(), new ItemStack(this));
                } else if (random.nextFloat() < generationChance * 0.5f){
                    serverLevel.setBlock(offset.above(), this.defaultBlockState(), 3);
                }

                break;
            }
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        super.tick(blockState, serverLevel, blockPos, random);

        if (serverLevel.random.nextFloat() < generationChance) {
            grow(serverLevel, random, blockPos, blockState, false);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }
}
