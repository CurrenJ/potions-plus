package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.IcicleBlock;
import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

import java.util.Optional;
import java.util.Random;

public class IcicleFeature extends Feature<PointedDripstoneConfiguration> {
    public IcicleFeature(Codec<PointedDripstoneConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<PointedDripstoneConfiguration> p_191078_) {
        LevelAccessor levelaccessor = p_191078_.level();
        BlockPos blockpos = p_191078_.origin();
        Random random = p_191078_.random();

        if (levelaccessor.getBlockState(blockpos).isAir()) {
            BlockPos above = blockpos.above();
            BlockPos below = blockpos.below();
            if (!levelaccessor.getBlockState(above).isAir()) {
                levelaccessor.setBlock(blockpos, Blocks.ICICLE.get().defaultBlockState().setValue(IcicleBlock.TIP_DIRECTION, Direction.DOWN), 2);
            } else if (!levelaccessor.getBlockState(below).isAir()) {
                levelaccessor.setBlock(blockpos, Blocks.ICICLE.get().defaultBlockState().setValue(IcicleBlock.TIP_DIRECTION, Direction.UP), 2);
            } else {
                return false;
            }
        }

        PointedDripstoneConfiguration pointeddripstoneconfiguration = p_191078_.config();
        Optional<Direction> optional = getTipDirection(levelaccessor, blockpos, random);
        if (optional.isEmpty()) {
            return false;
        } else {
//            BlockPos blockpos1 = blockpos.relative(optional.get().getOpposite());
//            createPatchOfDripstoneBlocks(levelaccessor, random, blockpos1, pointeddripstoneconfiguration);
            int i = (int) Math.round(random.nextGaussian(3.0D, 1.5D));


            IcicleUtils.growPointedIcicle(levelaccessor, blockpos, optional.get(), i, false);
            return true;
        }
    }

    private static Optional<Direction> getTipDirection(LevelAccessor p_191069_, BlockPos p_191070_, Random p_191071_) {
        boolean flag = IcicleUtils.isIcicleBase(p_191069_.getBlockState(p_191070_.above()));
        boolean flag1 = IcicleUtils.isIcicleBase(p_191069_.getBlockState(p_191070_.below()));
        if (flag && flag1) {
            return Optional.of(p_191071_.nextBoolean() ? Direction.DOWN : Direction.UP);
        } else if (flag) {
            return Optional.of(Direction.DOWN);
        } else {
            return flag1 ? Optional.of(Direction.UP) : Optional.empty();
        }
    }

    private static void createPatchOfDripstoneBlocks(LevelAccessor p_191073_, Random p_191074_, BlockPos p_191075_, PointedDripstoneConfiguration p_191076_) {
        IcicleUtils.placeIcicleBlockIfPossible(p_191073_, p_191075_);

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (!(p_191074_.nextFloat() > p_191076_.chanceOfDirectionalSpread)) {
                BlockPos blockpos = p_191075_.relative(direction);
                IcicleUtils.placeIcicleBlockIfPossible(p_191073_, blockpos);
                if (!(p_191074_.nextFloat() > p_191076_.chanceOfSpreadRadius2)) {
                    BlockPos blockpos1 = blockpos.relative(Direction.getRandom(p_191074_));
                    IcicleUtils.placeIcicleBlockIfPossible(p_191073_, blockpos1);
                    if (!(p_191074_.nextFloat() > p_191076_.chanceOfSpreadRadius3)) {
                        BlockPos blockpos2 = blockpos1.relative(Direction.getRandom(p_191074_));
                        IcicleUtils.placeIcicleBlockIfPossible(p_191073_, blockpos2);
                    }
                }
            }
        }

    }
}
