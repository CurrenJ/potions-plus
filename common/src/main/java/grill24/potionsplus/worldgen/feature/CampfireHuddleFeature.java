package grill24.potionsplus.worldgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class CampfireHuddleFeature extends Feature<NoneFeatureConfiguration> {
    private static List<BlockState> seats = ImmutableList.of(
            Blocks.OAK_STAIRS.defaultBlockState(),
            Blocks.SPRUCE_STAIRS.defaultBlockState(),
            Blocks.BIRCH_STAIRS.defaultBlockState(),
            Blocks.JUNGLE_STAIRS.defaultBlockState(),
            Blocks.ACACIA_STAIRS.defaultBlockState(),
            Blocks.DARK_OAK_STAIRS.defaultBlockState(),
            Blocks.CRIMSON_STAIRS.defaultBlockState(),
            Blocks.WARPED_STAIRS.defaultBlockState(),
            Blocks.OAK_SLAB.defaultBlockState(),
            Blocks.SPRUCE_SLAB.defaultBlockState(),
            Blocks.BIRCH_SLAB.defaultBlockState(),
            Blocks.JUNGLE_SLAB.defaultBlockState(),
            Blocks.ACACIA_SLAB.defaultBlockState(),
            Blocks.DARK_OAK_SLAB.defaultBlockState(),
            Blocks.CRIMSON_SLAB.defaultBlockState(),
            Blocks.WARPED_SLAB.defaultBlockState(),
            Blocks.WHITE_CARPET.defaultBlockState(),
            Blocks.ORANGE_CARPET.defaultBlockState(),
            Blocks.MAGENTA_CARPET.defaultBlockState(),
            Blocks.LIGHT_BLUE_CARPET.defaultBlockState(),
            Blocks.YELLOW_CARPET.defaultBlockState(),
            Blocks.LIME_CARPET.defaultBlockState(),
            Blocks.PINK_CARPET.defaultBlockState(),
            Blocks.GRAY_CARPET.defaultBlockState(),
            Blocks.LIGHT_GRAY_CARPET.defaultBlockState(),
            Blocks.CYAN_CARPET.defaultBlockState(),
            Blocks.PURPLE_CARPET.defaultBlockState(),
            Blocks.BLUE_CARPET.defaultBlockState(),
            Blocks.BROWN_CARPET.defaultBlockState(),
            Blocks.GREEN_CARPET.defaultBlockState(),
            Blocks.RED_CARPET.defaultBlockState(),
            Blocks.BLACK_CARPET.defaultBlockState()
    );

    public CampfireHuddleFeature(Codec<NoneFeatureConfiguration> p_66836_) {
        super(p_66836_);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        BlockPos.MutableBlockPos blockpos = context.origin().above().mutable();
        System.out.println("Attempting to place campfire huddle at " + blockpos);

        while (!context.level().isOutsideBuildHeight(blockpos.getY()) && !worldgenlevel.getBlockState(blockpos.below()).isFaceSturdy(worldgenlevel, blockpos.below(), Direction.UP)) {
            blockpos.move(Direction.DOWN);
        }

        if (worldgenlevel.isEmptyBlock(blockpos) && worldgenlevel.getBlockState(blockpos.below()).isFaceSturdy(worldgenlevel, blockpos.below(), Direction.UP)) {
            BlockState campfire = context.random().nextFloat() < 0.1F ? Blocks.SOUL_CAMPFIRE.defaultBlockState() : Blocks.CAMPFIRE.defaultBlockState();
            campfire = context.random().nextFloat() < 0.4F ? campfire.setValue(CampfireBlock.LIT, true) : campfire.setValue(CampfireBlock.LIT, false);
            campfire = context.random().nextFloat() < 0.02F ? Blocks.LAVA_CAULDRON.defaultBlockState() : campfire;
            worldgenlevel.setBlock(blockpos, campfire, 2);

            System.out.println("Campfire huddle succeeded at " + blockpos);

            BlockState seat = seats.get(context.random().nextInt(seats.size()));
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos around = blockpos.relative(direction, 2);
                boolean shouldPlace = context.random().nextFloat() < 0.5 && worldgenlevel.isEmptyBlock(around) && worldgenlevel.getBlockState(around.below()).isFaceSturdy(worldgenlevel, blockpos.below(), Direction.UP);
                if (shouldPlace) {
                    if (seat.hasProperty(HorizontalDirectionalBlock.FACING)) {
                        seat = seat.setValue(HorizontalDirectionalBlock.FACING, direction);
                    }
                    worldgenlevel.setBlock(around, seat, 2);
                }
            }

            return true;
        }

        System.out.println("Campfire huddle failed at " + blockpos);
        return false;
    }
}
