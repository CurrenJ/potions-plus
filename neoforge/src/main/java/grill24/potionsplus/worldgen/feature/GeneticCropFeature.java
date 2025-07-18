package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.GeneticCropBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.Genotype;
import net.minecraft.client.color.item.Potion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Optional;

public class GeneticCropFeature extends Feature<GeneticCropConfiguration> {
    public GeneticCropFeature(Codec<GeneticCropConfiguration> p_66808_) {
        super(p_66808_);
    }

    @Override
    public boolean place(FeaturePlaceContext<GeneticCropConfiguration> p_160341_) {
        GeneticCropConfiguration GeneticCropConfiguration = p_160341_.config();
        WorldGenLevel worldgenlevel = p_160341_.level();
        BlockPos blockpos = p_160341_.origin();
        BlockState blockstate = GeneticCropConfiguration.toPlace().getState(p_160341_.random(), blockpos);
        if (blockstate.canSurvive(worldgenlevel, blockpos)) {
            if (blockstate.getBlock() instanceof DoublePlantBlock) {
                if (!worldgenlevel.isEmptyBlock(blockpos.above())) {
                    return false;
                }

                DoublePlantBlock.placeAt(worldgenlevel, blockstate, blockpos, 2);
            } else if (blockstate.getBlock() instanceof MossyCarpetBlock) {
                MossyCarpetBlock.placeAt(worldgenlevel, blockpos, worldgenlevel.getRandom(), 2);
            } else {
                worldgenlevel.setBlock(blockpos, blockstate, 2);
            }

            if (GeneticCropConfiguration.scheduleTick()) {
                worldgenlevel.scheduleTick(blockpos, worldgenlevel.getBlockState(blockpos).getBlock(), 1);
            }

            trySetGenetics(worldgenlevel, blockpos, GeneticCropConfiguration);

            return true;
        } else {
            return false;
        }
    }

    private static void trySetGenetics(WorldGenLevel level, BlockPos pos, GeneticCropConfiguration config) {
        Optional<GeneticCropBlockEntity> cropEntity = level.getBlockEntity(pos, Blocks.GENETIC_CROP_BLOCK_ENTITY.value());
        if (cropEntity.isPresent()) {
            RandomSource random = level.getRandom();
            Genotype genotype = sampleRandomGenotype(random, config);
            Genotype pollinatorGenotype = sampleRandomGenotype(random, config);

            GeneticCropBlockEntity entity = cropEntity.get();
            entity.setGenotype(genotype);
            entity.setPollinatorGenotype(pollinatorGenotype);
        } else {
            PotionsPlus.LOGGER.warn("Failed to set genetics for GeneticCropBlockEntity at {}: No block entity found.", pos);
        }
    }

    private static Genotype sampleRandomGenotype(RandomSource random, GeneticCropConfiguration config) {
        int[] lowerBounds = config.gLowerBound().getGenotypeAsIntArray();
        int[] upperBounds = config.gUpperBound().getGenotypeAsIntArray();
        int[] result = new int[Genotype.MAX_CHROMOSOMES];
        for (int i = 0; i < Genotype.MAX_CHROMOSOMES; i++) {
            int lowerBound = lowerBounds[i];
            int upperBound = upperBounds[i];
            result[i] = random.nextInt(lowerBound, upperBound + 1);
        }
        return new Genotype(result);
    }
}
