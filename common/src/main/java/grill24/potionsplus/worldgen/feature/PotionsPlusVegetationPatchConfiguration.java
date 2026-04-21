package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PotionsPlusVegetationPatchConfiguration implements FeatureConfiguration {
    public static final Codec<PotionsPlusVegetationPatchConfiguration> CODEC = RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                    TagKey.hashedCodec(Registries.BLOCK).fieldOf("replaceable").forGetter((configuration) -> configuration.replaceable),
                    BlockStateProvider.CODEC.fieldOf("ground_state").forGetter((configuration) -> configuration.groundState),
                    PlacedFeature.CODEC.fieldOf("vegetation_feature").forGetter((configuration) -> configuration.vegetationFeature),
                    Direction.CODEC.fieldOf("facing").forGetter((configuration) -> configuration.facing),
                    IntProvider.codec(1, 128).fieldOf("depth").forGetter((configuration) -> configuration.depth),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter((configuration) -> configuration.extraBottomBlockChance),
                    Codec.intRange(1, 256).fieldOf("vertical_range").forGetter((configuration) -> configuration.verticalRange),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter((configuration) -> configuration.vegetationChance),
                    IntProvider.CODEC.fieldOf("xz_radius").forGetter((configuration) -> configuration.xzRadius),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter((configuration) -> configuration.extraEdgeColumnChance),
                    BlockStateProvider.CODEC.fieldOf("ore_base_block").forGetter(configuration -> configuration.oreBaseBlock))
            .apply(codecBuilder, PotionsPlusVegetationPatchConfiguration::new));

    public final TagKey<Block> replaceable;
    public final BlockStateProvider groundState;
    public final Holder<PlacedFeature> vegetationFeature;
    public final Direction facing;
    public final IntProvider depth;
    public final float extraBottomBlockChance;
    public final int verticalRange;
    public final float vegetationChance;
    public final IntProvider xzRadius;
    public final float extraEdgeColumnChance;
    public final BlockStateProvider oreBaseBlock;

    public PotionsPlusVegetationPatchConfiguration(TagKey<Block> replaceable, BlockStateProvider surfaceToPlace, Holder<PlacedFeature> vegetation, Direction facing, IntProvider depth, float extraBottomBlockChance, int verticalRange, float vegetationChance, IntProvider xzRadius, float extraEdgeColumnChance, BlockStateProvider oreBaseBlock) {
        this.replaceable = replaceable;
        this.groundState = surfaceToPlace;
        this.vegetationFeature = vegetation;
        this.facing = facing;
        this.depth = depth;
        this.extraBottomBlockChance = extraBottomBlockChance;
        this.verticalRange = verticalRange;
        this.vegetationChance = vegetationChance;
        this.xzRadius = xzRadius;
        this.extraEdgeColumnChance = extraEdgeColumnChance;
        this.oreBaseBlock = oreBaseBlock;
    }
}
