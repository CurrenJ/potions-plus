package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Map;

public record VersatilePlantBlockFeatureConfiguration(BlockStateProvider toPlace,
                                                      Map<ResourceKey<Block>, IntProvider> length,
                                                      Direction facing) implements FeatureConfiguration {
    public static final Codec<VersatilePlantBlockFeatureConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            BlockStateProvider.CODEC.fieldOf("toPlace").forGetter(VersatilePlantBlockFeatureConfiguration::toPlace),
            Codec.unboundedMap(ResourceKey.codec(Registries.BLOCK), IntProvider.CODEC).fieldOf("length").forGetter(VersatilePlantBlockFeatureConfiguration::length),
            Direction.CODEC.fieldOf("facing").forGetter(VersatilePlantBlockFeatureConfiguration::facing)
    ).apply(codecBuilder, VersatilePlantBlockFeatureConfiguration::new));
}
