package grill24.potionsplus.core.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record PrimaryAndSecondaryFlowerFeatureConfig(BlockStateProvider primaryToPlace,
                                                     BlockStateProvider secondaryToPlace,
                                                     float secondaryChance) implements FeatureConfiguration {
    public static final Codec<PrimaryAndSecondaryFlowerFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("primary_to_place").forGetter(PrimaryAndSecondaryFlowerFeatureConfig::primaryToPlace),
            BlockStateProvider.CODEC.fieldOf("secondary_to_place").forGetter(PrimaryAndSecondaryFlowerFeatureConfig::secondaryToPlace),
            Codec.FLOAT.fieldOf("secondary_chance").forGetter(PrimaryAndSecondaryFlowerFeatureConfig::secondaryChance)
    ).apply(instance, PrimaryAndSecondaryFlowerFeatureConfig::new));
}
