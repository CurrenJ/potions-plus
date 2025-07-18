package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.utility.Genotype;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GeneticCropConfiguration(BlockStateProvider toPlace, boolean scheduleTick, Genotype gLowerBound, Genotype gUpperBound) implements FeatureConfiguration {
    public static final Codec<GeneticCropConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            BlockStateProvider.CODEC.fieldOf("to_place").forGetter((p_161168_) -> p_161168_.toPlace),
            Codec.BOOL.optionalFieldOf("schedule_tick", false).forGetter((p_382777_) -> p_382777_.scheduleTick),
            Genotype.CODEC.optionalFieldOf("genotype_lower_bound", new Genotype()).forGetter((p_161157_) -> p_161157_.gLowerBound),
            Genotype.CODEC.optionalFieldOf("genotype_upper_bound", new Genotype()).forGetter((p_161159_) -> p_161159_.gUpperBound)
    ).apply(codecBuilder, GeneticCropConfiguration::new));

    public GeneticCropConfiguration(BlockStateProvider blockStateProvider, Genotype gLowerBound, Genotype gUpperBound) {
        this(blockStateProvider, false, gLowerBound, gUpperBound);
    }
}

