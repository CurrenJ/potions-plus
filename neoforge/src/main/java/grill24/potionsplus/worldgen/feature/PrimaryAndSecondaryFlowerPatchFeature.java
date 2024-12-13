package grill24.potionsplus.worldgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class PrimaryAndSecondaryFlowerPatchFeature extends Feature<PrimaryAndSecondaryFlowerFeatureConfig> {
    public PrimaryAndSecondaryFlowerPatchFeature() {
        super(PrimaryAndSecondaryFlowerFeatureConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<PrimaryAndSecondaryFlowerFeatureConfig> ctx) {
        PrimaryAndSecondaryFlowerFeatureConfig config = ctx.config();
        WorldGenLevel level = ctx.level();
        BlockPos pos = ctx.origin();
        BlockState primary = config.primaryToPlace().getState(ctx.random(), pos);
        BlockState secondary = config.secondaryToPlace().getState(ctx.random(), pos);

        float secondaryChance = config.secondaryChance();
        if (primary.canSurvive(level, pos)) {
            if (ctx.random().nextFloat() < secondaryChance) {
                level.setBlock(pos, secondary, Block.UPDATE_CLIENTS);
            } else {
                level.setBlock(pos, primary, Block.UPDATE_CLIENTS);
            }

            return true;
        }

        return false;
    }
}
