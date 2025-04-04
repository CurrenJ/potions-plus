package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.GeyserBlock;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LavaGeyserFeature extends Feature<NoneFeatureConfiguration> {
    Direction[] CHECK_ORDER = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN, Direction.UP };

    public LavaGeyserFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        for (Direction direction : CHECK_ORDER) {
            if(tryPlace(context, pos, direction)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValid(FeaturePlaceContext<NoneFeatureConfiguration> context, BlockPos pos, Direction direction) {
        BlockPos other = pos.relative(direction);
        return context.level().getBlockState(pos).isAir() && context.level().getBlockState(other).isFaceSturdy(context.level(), other, direction.getOpposite());
    }

    public boolean tryPlace(FeaturePlaceContext<NoneFeatureConfiguration> context, BlockPos pos, Direction direction) {
        if (isValid(context, pos, direction)) {
            switch (direction) {
                case UP:
                    context.level().setBlock(pos, DecorationBlocks.LAVA_GEYSER.value().defaultBlockState().setValue(GeyserBlock.FACE, AttachFace.CEILING), 2);
                    break;
                case DOWN:
                    context.level().setBlock(pos, DecorationBlocks.LAVA_GEYSER.value().defaultBlockState().setValue(GeyserBlock.FACE, AttachFace.FLOOR), 2);
                    break;
                default:
                    context.level().setBlock(pos, DecorationBlocks.LAVA_GEYSER.value().defaultBlockState().setValue(GeyserBlock.FACE, AttachFace.WALL), 2);
                    context.level().setBlock(pos, DecorationBlocks.LAVA_GEYSER.value().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite()), 2);
                    break;
            }
            if(PotionsPlus.Debug.DEBUG) {
                PotionsPlus.LOGGER.info("Placed Lava Geyser at " + pos + " facing " + direction);
            }
            return true;
        }
        return false;
    }
}
