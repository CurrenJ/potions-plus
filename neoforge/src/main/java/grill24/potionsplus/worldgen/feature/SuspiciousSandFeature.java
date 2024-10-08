package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SuspiciousSandFeature extends Feature<NoneFeatureConfiguration> {
    public SuspiciousSandFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        WorldGenLevel level = featurePlaceContext.level();
        BlockPos pos = featurePlaceContext.origin();
        BlockState blockState = level.getBlockState(pos);
        if (blockState.is(net.minecraft.world.level.block.Blocks.SAND) || blockState.is(net.minecraft.world.level.block.Blocks.SANDSTONE)) {
            level.setBlock(pos, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
            level.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK)
                    .ifPresent(brushableBlockEntity -> brushableBlockEntity.setLootTable(LootTables.ARID_CAVE_SUSPICIOUS_SAND, pos.asLong()));
            return true;
        }
        return false;
    }
}
