package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class AquiferFreezeFeature extends Feature<NoneFeatureConfiguration> {
    public static int maxSoFar = 0;

    public AquiferFreezeFeature(Codec<NoneFeatureConfiguration> p_66836_) {
        super(p_66836_);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160368_) {
        WorldGenLevel worldgenlevel = p_160368_.level();
        BlockPos blockpos = p_160368_.origin();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        final int diameter = 16;
        for(int y = 0; y < diameter; ++y) {
            int iceBlocksWithAirAbove = 0;
            for(int x = 0; x < diameter; ++x) {
                for(int z = 0; z < diameter; ++z) {
                    int k = blockpos.getX() + x;
                    int l = blockpos.getZ() + y;
                    int m = z + blockpos.getY();
                    blockpos$mutableblockpos.set(k, m, l);
                    Biome biome = worldgenlevel.getBiome(blockpos$mutableblockpos).value();
                    if (biome.shouldFreeze(worldgenlevel, blockpos$mutableblockpos, false)) {
                        worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.ICE.defaultBlockState(), 2);
                    }

                    if(worldgenlevel.getBlockState(blockpos$mutableblockpos).is(Blocks.ICE) && worldgenlevel.isEmptyBlock(blockpos$mutableblockpos.above())) {
                        iceBlocksWithAirAbove++;
                    }
                }
            }
        }

        return true;
    }
}
