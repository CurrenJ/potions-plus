package grill24.potionsplus.core.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class GiantSnowflakeFeature extends Feature<NoneFeatureConfiguration> {
    private static final List<BlockPos> snowflake = List.of(
            BlockPos.ZERO,
            // North, east diagonal branch
            BlockPos.ZERO.north(1).east(1),
            BlockPos.ZERO.north(2).east(2),
            BlockPos.ZERO.north(3).east(3),
            BlockPos.ZERO.north(4).east(3),
            BlockPos.ZERO.north(3).east(4),
            // East branch
            BlockPos.ZERO.east(2),
            BlockPos.ZERO.east(3),
            BlockPos.ZERO.east(4).north(1),
            BlockPos.ZERO.east(4).south(1),
            BlockPos.ZERO.east(5),

            // South, east diagonal branch
            BlockPos.ZERO.south(1).east(1),
            BlockPos.ZERO.south(2).east(2),
            BlockPos.ZERO.south(3).east(3),
            BlockPos.ZERO.south(4).east(3),
            BlockPos.ZERO.south(3).east(4),
            // South branch
            BlockPos.ZERO.south(2),
            BlockPos.ZERO.south(3),
            BlockPos.ZERO.south(4).east(1),
            BlockPos.ZERO.south(4).west(1),
            BlockPos.ZERO.south(5),

            // South, west diagonal branch
            BlockPos.ZERO.south(1).west(1),
            BlockPos.ZERO.south(2).west(2),
            BlockPos.ZERO.south(3).west(3),
            BlockPos.ZERO.south(4).west(3),
            BlockPos.ZERO.south(3).west(4),
            // West branch
            BlockPos.ZERO.west(2),
            BlockPos.ZERO.west(3),
            BlockPos.ZERO.west(4).north(1),
            BlockPos.ZERO.west(4).south(1),
            BlockPos.ZERO.west(5),

            // North, west diagonal branch
            BlockPos.ZERO.north(1).west(1),
            BlockPos.ZERO.north(2).west(2),
            BlockPos.ZERO.north(3).west(3),
            BlockPos.ZERO.north(4).west(3),
            BlockPos.ZERO.north(3).west(4),
            // North branch
            BlockPos.ZERO.north(2),
            BlockPos.ZERO.north(3),
            BlockPos.ZERO.north(4).east(1),
            BlockPos.ZERO.north(4).west(1),
            BlockPos.ZERO.north(5)
    );

    public GiantSnowflakeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos snowflakeOrigin = context.origin();
        System.out.println("Placing SNOWFLAKE at " + snowflakeOrigin);

        for(BlockPos snowflakePos : snowflake) {
            if(context.level().getBlockState(snowflakePos).isAir())
                context.level().setBlock(snowflakePos.offset(snowflakeOrigin), Blocks.SNOW.defaultBlockState(), 2);
        }

        return false;
    }
}
