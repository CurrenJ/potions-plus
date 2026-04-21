package grill24.potionsplus.worldgen.feature;

import grill24.potionsplus.block.VersatilePlantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class VersatilePlantBlockFeature extends Feature<VersatilePlantBlockFeatureConfiguration> {
    public VersatilePlantBlockFeature() {
        super(VersatilePlantBlockFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<VersatilePlantBlockFeatureConfiguration> context) {
        VersatilePlantBlockFeatureConfiguration configuration = context.config();
        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        BlockState blockstate = configuration.toPlace().getState(context.random(), blockpos).setValue(VersatilePlantBlock.FACING, configuration.facing());
        Block block = blockstate.getBlock();
        if (block instanceof VersatilePlantBlock versatile && versatile.canSurviveFacing(blockstate, worldgenlevel, blockpos, configuration.facing())) {
            int length = configuration.length().getOrDefault(blockstate.getBlockHolder().getKey(), UniformInt.of(0, 0)).sample(worldgenlevel.getRandom());
            versatile.placeAt(worldgenlevel, blockstate, blockpos, length, 2);

            return true;
        } else if (blockstate.canSurvive(worldgenlevel, blockpos)) {
            if (blockstate.getBlock() instanceof DoublePlantBlock) {
                if (!worldgenlevel.isEmptyBlock(blockpos.above())) {
                    return false;
                }

                DoublePlantBlock.placeAt(worldgenlevel, blockstate, blockpos, 2);
            } else {
                worldgenlevel.setBlock(blockpos, blockstate, 2);
            }

            return true;
        } else {
            return false;
        }
    }
}
