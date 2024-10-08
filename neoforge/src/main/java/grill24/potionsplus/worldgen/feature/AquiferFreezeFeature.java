package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.common.Tags;

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
            for(int x = 0; x < diameter; ++x) {
                for(int z = 0; z < diameter; ++z) {
                    int k = blockpos.getX() + x;
                    int l = blockpos.getY() + y;
                    int m = blockpos.getZ() + z;
                    blockpos$mutableblockpos.set(k, l, m);

                    if (worldgenlevel.isOutsideBuildHeight(blockpos$mutableblockpos) || !worldgenlevel.ensureCanWrite(blockpos$mutableblockpos) || !worldgenlevel.getFluidState(blockpos$mutableblockpos).is(Tags.Fluids.WATER)) {
                        continue;
                    }

                    // TODO: Fix freezing. Trycatch is a major performance hit
//                    try {
//                        Biome biome = worldgenlevel.getBiome(blockpos$mutableblockpos).value();
//                        if (biome.shouldFreeze(worldgenlevel, blockpos$mutableblockpos, false)) {
//                            worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.ICE.defaultBlockState(), 2);
//                        }
//                    } catch (Exception ignored) {
//                    }

                    worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.ICE.defaultBlockState(), 2);
                }
            }
        }

        return true;
    }
}
