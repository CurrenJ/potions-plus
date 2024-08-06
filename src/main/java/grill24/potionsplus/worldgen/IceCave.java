package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.Features;
import grill24.potionsplus.core.feature.AquiferFreezeFeature;
import grill24.potionsplus.core.feature.CampfireHuddleFeature;
import grill24.potionsplus.core.feature.GiantSnowflakeFeature;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public class IceCave {
    public static WeightedStateProvider ICE_SAMPLER = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.ICE.defaultBlockState(), 5)
            .add(Blocks.PACKED_ICE.defaultBlockState(), 4)
            .add(Blocks.BLUE_ICE.defaultBlockState(), 1)
            .add(Blocks.SNOW_BLOCK.defaultBlockState(), 2)
    );

    public static final Holder<ConfiguredFeature<SimpleBlockConfiguration, ?>> ICE_FLOOR_VEGETATION = FeatureUtils.register(
            "ice_vegetation", Feature.SIMPLE_BLOCK,
            new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                    .add(Blocks.SNOW.defaultBlockState(), 8)
                    .add(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 2), 4)
                    .add(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 3), 2)
                    .add(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 4), 1)
            )));
    public static final Holder<ConfiguredFeature<BlockColumnConfiguration, ?>> ICE_CEILING_VEGETATION = FeatureUtils.register(
            "icicle_on_ceiling", Feature.BLOCK_COLUMN,
            new BlockColumnConfiguration(List.of(
                    BlockColumnConfiguration.layer(
                            new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder()
                                    .add(UniformInt.of(0, 3), 5)
                                    .add(UniformInt.of(1, 7), 1).build()), BlockStateProvider.simple(Blocks.PACKED_ICE)),
                    BlockColumnConfiguration.layer(ConstantInt.of(1), BlockStateProvider.simple(Blocks.BLUE_ICE))), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));
    public static final Holder<ConfiguredFeature<BlockPileConfiguration, ?>> COOBLESTONE_PILE = FeatureUtils.register(
            "cooblestone_pile", Feature.BLOCK_PILE,
            new BlockPileConfiguration(BlockStateProvider.simple(grill24.potionsplus.core.Blocks.COOBLESTONE.get())));

    public static final Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> ICE_PATCH = FeatureUtils.register(
            "ice_cave_ice_patch", Feature.VEGETATION_PATCH,
            // replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance
            new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, ICE_SAMPLER, PlacementUtils.inlinePlaced(ICE_FLOOR_VEGETATION), CaveSurface.FLOOR, UniformInt.of(2, 5), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F));
    public static final Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> ICE_PATCH_CEILING = FeatureUtils.register(
            "ice_cave_ice_patch_ceiling", Feature.VEGETATION_PATCH,
            new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BlockStateProvider.simple(Blocks.ICE), PlacementUtils.inlinePlaced(ICE_CEILING_VEGETATION), CaveSurface.CEILING, UniformInt.of(1, 3), 0.0F, 5, 0.08F, UniformInt.of(4, 7), 0.3F));


    public static final Holder<PlacedFeature> ICE_CAVE_VEGETATION = PlacementUtils.register(
            "ice_cave_vegetation", ICE_PATCH, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());
    public static final Holder<PlacedFeature> ICE_CAVE_CEILING_VEGETATION = PlacementUtils.register(
            "ice_caves_ceiling_vegetation", ICE_PATCH_CEILING, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());
    public static final Lazy<Holder<PlacedFeature>> COBBLESTONE_PILE_PLACED = Lazy.of(() -> PlacementUtils.register(
            "cobblestone_pile", COOBLESTONE_PILE, CountPlacement.of(15), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), BiomeFilter.biome()));

    public static final ResourceKey<PlacedFeature> AQUIFER_FREEZE_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "aquifer_freeze"));
    public static Feature<NoneFeatureConfiguration> AQUIFER_FREEZE = Features.register(AQUIFER_FREEZE_KEY.location().getPath(), new AquiferFreezeFeature(NoneFeatureConfiguration.CODEC));
    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> AQUIFER_FREEZE_CONFIGURED = FeatureUtils.register(AQUIFER_FREEZE_KEY.location().toString(), AQUIFER_FREEZE);
    public static Holder<PlacedFeature> AQUIFER_FREEZE_PLACED = PlacementUtils.register(AQUIFER_FREEZE_KEY.location().toString(), AQUIFER_FREEZE_CONFIGURED, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

    public static final ResourceKey<PlacedFeature> CAMPFIRE_HUDDLE_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "campfire_huddle"));
    public static Feature<NoneFeatureConfiguration> CAMPFIRE_HUDDLE = Features.register(CAMPFIRE_HUDDLE_KEY.location().getPath(), new CampfireHuddleFeature(NoneFeatureConfiguration.CODEC));
    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> CAMPFIRE_HUDDLE_CONFIGURED = FeatureUtils.register(CAMPFIRE_HUDDLE_KEY.location().toString(), CAMPFIRE_HUDDLE);
    public static Holder<PlacedFeature> CAMPFIRE_HUDDLE_PLACED = PlacementUtils.register(CAMPFIRE_HUDDLE_KEY.location().toString(), CAMPFIRE_HUDDLE_CONFIGURED,
            RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

    public static final ResourceKey<PlacedFeature> GIANT_SNOWFLAKE_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "giant_snowflake"));
    public static Feature<NoneFeatureConfiguration> GIANT_SNOWFLAKE = Features.register(GIANT_SNOWFLAKE_KEY.location().getPath(), new GiantSnowflakeFeature(NoneFeatureConfiguration.CODEC));
    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> GIANT_SNOWFLAKE_CONFIGURED = FeatureUtils.register(GIANT_SNOWFLAKE_KEY.location().toString(), GIANT_SNOWFLAKE);
    private static final EnvironmentScanPlacement SNOWFLAKE_SCAN = EnvironmentScanPlacement.scanningFor(
            Direction.DOWN,
            BlockPredicate.allOf(matchesBlockInSquare(Blocks.ICE, 5, true, BlockPos.ZERO), matchesBlockInSquare(Blocks.AIR, 5, true, new BlockPos(0, 1, 0))),
            BlockPredicate.ONLY_IN_AIR_PREDICATE, 12);
    public static Holder<PlacedFeature> GIANT_SNOWFLAKE_PLACED = PlacementUtils.register(GIANT_SNOWFLAKE_KEY.location().toString(), GIANT_SNOWFLAKE_CONFIGURED,
            CountPlacement.of(100), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, SNOWFLAKE_SCAN, RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

    private static BlockPredicate matchesBlockInSquare(Block block, int expand, boolean requireAll, Vec3i offset) {
        int sideLength = expand * 2 + 1;
        BlockPredicate[] predicates = new BlockPredicate[sideLength * sideLength];
        for(int x = -expand; x <= expand; x++) {
            for(int z = -expand; z <= expand; z++) {
                predicates[(x + expand) * sideLength + (z + expand)] = BlockPredicate.matchesBlock(block, new Vec3i(x, 0, z).offset(offset));
            }
        }

        return requireAll ? BlockPredicate.allOf(predicates) : BlockPredicate.anyOf(predicates);
    }
}
