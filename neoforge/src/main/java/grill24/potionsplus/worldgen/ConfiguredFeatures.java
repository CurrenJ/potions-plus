package grill24.potionsplus.worldgen;

import grill24.potionsplus.block.GeneticCropBlock;
import grill24.potionsplus.core.Features;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.Genotype;
import grill24.potionsplus.worldgen.biome.AridCave;
import grill24.potionsplus.worldgen.biome.IceCave;
import grill24.potionsplus.worldgen.biome.VolcanicCave;
import grill24.potionsplus.worldgen.biome.WoodedCave;
import grill24.potionsplus.worldgen.feature.GeneticCropConfiguration;
import grill24.potionsplus.worldgen.feature.PotionsPlusVegetationPatchConfiguration;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class ConfiguredFeatures {
    // Misc.

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DENSE_DIAMOND_SMALL_KEY = createKey("ore_dense_diamond_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_REMNANT_DEBRIS_KEY = createKey("ore_remnant_debris");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_URANIUM_KEY = createKey("ore_uranium");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH_KEY = createKey("lunar_berry_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TOMATO_PATCH_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId("tomato_patch"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> BRASSICA_OLERACEA_PATCH_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId("brassica_oleracea_patch"));

    // ----- Volcanic Cave -----
    public static final ResourceKey<ConfiguredFeature<?, ?>> VOLCANIC_PATCH_FLOOR_KEY = createKey("volcanic_cave_patch_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOOR_VEGETATION_KEY = createKey("volcanic_cave_floor_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VOLCANIC_PATCH_CEILING_KEY = createKey("volcanic_cave_patch_ceiling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CEILING_VEGETATION_KEY = createKey("volcanic_cave_ceiling_vegetation");

    public static final ResourceKey<ConfiguredFeature<?, ?>> FISSURE_KEY = createKey("fissure");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVA_GEYSER_KEY = createKey("lava_geyser");

    // ----- Ice Cave -----
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICE_FLOOR_VEGETATION_KEY = createKey("ice_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICE_CEILING_VEGETATION_KEY = createKey("icicle_on_ceiling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICICLE_KEY = createKey("icicle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICICLE_SELECTOR_KEY = createKey("icicle_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COOBLESTONE_PILE_KEY = createKey("cooblestone_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICE_PATCH_KEY = createKey("ice_cave_ice_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICE_PATCH_CEILING_KEY = createKey("ice_cave_ice_patch_ceiling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AQUIFER_FREEZE_KEY = createKey("aquifer_freeze");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CAMPFIRE_HUDDLE_KEY = createKey("campfire_huddle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_SNOWFLAKE_KEY = createKey("giant_snowflake");

    // ----- Arid Cave -----
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARID_CAVE_FLOOR_VEGETATION_KEY = createKey("arid_cave_floor_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARID_CAVE_CEILING_VEGETATION_KEY = createKey("arid_cave_ceiling_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARID_CAVE_FLOOR_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId("arid_cave_floor"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARID_CAVE_CEILING_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId("arid_cave_ceiling"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARID_CAVE_SUSPICOUS_SAND_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId("arid_cave_suspicious_sand"));

    // ----- Wooded Cave -----
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_CAVE_VEGETATION_KEY = createKey("wooded_cave_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_CAVE_CEILING_VEGETATION_KEY = createKey("wooded_cave_ceiling_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_CAVE_TREES_KEY = createKey("wooded_cave_trees");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_CAVE_FLOOR_KEY = createKey("wooded_cave_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_CAVE_CEILING_KEY = createKey("wooded_cave_ceiling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_CAVE_HOLLOW_TREES_KEY = createKey("wooded_cave_hollow_trees");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatureGetter = context.lookup(Registries.PLACED_FEATURE);

        // Misc.
        RuleTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        final Holder<ConfiguredFeature<?, ?>> ORE_DENSE_DIAMOND_SMALL = register(context, ORE_DENSE_DIAMOND_SMALL_KEY, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(stoneOreReplaceables, OreBlocks.DENSE_DIAMOND_ORE.value().defaultBlockState()),
                        OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value().defaultBlockState())
                ), 4, 0.5F));
        final Holder<ConfiguredFeature<?, ?>> ORE_REMNANT_DEBRIS = register(context, ORE_REMNANT_DEBRIS_KEY, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(stoneOreReplaceables, OreBlocks.REMNANT_DEBRIS.value().defaultBlockState()),
                        OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value().defaultBlockState())
                ), 4, 0.5F));
        final Holder<ConfiguredFeature<?, ?>> ORE_URANIUM = register(context, ORE_URANIUM_KEY, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(stoneOreReplaceables, OreBlocks.URANIUM_ORE.value().defaultBlockState()),
                        OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_URANIUM_ORE.value().defaultBlockState())
                ), 4, 0.5F));

        // ----- Lunar Berry Bush -----
        final Holder<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH = register(context, LUNAR_BERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                                .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 3)
                                .add(FlowerBlocks.LUNAR_BERRY_BUSH.value().defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1)
                        )))));

        // ----- Genetic Crop Patches -----
        final Holder<ConfiguredFeature<?, ?>> TOMATO_PATCH = register(context, TOMATO_PATCH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 8, 2, PlacementUtils.onlyWhenEmpty(Features.GENETIC_CROP,
                        new GeneticCropConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                                .add(FlowerBlocks.TOMATO_PLANT.value().defaultBlockState().setValue(GeneticCropBlock.AGE, 25).setValue(GeneticCropBlock.HARVESTABLE, GeneticCropBlock.HarvestState.MATURE), 1)),
                                new Genotype(0, 0, 0, 0), new Genotype(50, 75, 32, 32)))));
        final Holder<ConfiguredFeature<?, ?>> BRASSICA_OLERACEA_PATCH = register(context, BRASSICA_OLERACEA_PATCH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 8, 2, PlacementUtils.onlyWhenEmpty(Features.GENETIC_CROP,
                        new GeneticCropConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                                .add(FlowerBlocks.BRASSICA_OLERACEA_PLANT.value().defaultBlockState().setValue(GeneticCropBlock.AGE, 25).setValue(GeneticCropBlock.HARVESTABLE, GeneticCropBlock.HarvestState.MATURE), 1)),
                                new Genotype(0, 0, 0, 0), new Genotype(50, 0, 48, 48)))));

        // ----- Volcanic Cave -----
        final Holder<ConfiguredFeature<?, ?>> FLOOR_VEGETATION = register(context, FLOOR_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 12)
                        .add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1)
                        .add(Blocks.RED_MUSHROOM.defaultBlockState(), 1)
                )));
        final Holder<ConfiguredFeature<?, ?>> VOLCANIC_PATCH_FLOOR = register(
                context, VOLCANIC_PATCH_FLOOR_KEY, Features.POTIONS_PLUS_VEGETATION_PATCH,
                new PotionsPlusVegetationPatchConfiguration(
                        // replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance
                        BlockTags.MOSS_REPLACEABLE, VolcanicCave.VOLCANIC_CAVE_BLOCK_SAMPLER_FLOOR, PlacementUtils.inlinePlaced(FLOOR_VEGETATION), Direction.UP, UniformInt.of(5, 8), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F, BlockStateProvider.simple(Blocks.AIR))
        );
        final Holder<ConfiguredFeature<?, ?>> CEILING_VEGETATION = register(context, CEILING_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(SimpleStateProvider.simple(Blocks.AIR)));
        final Holder<ConfiguredFeature<?, ?>> VOLCANIC_PATCH_CEILING = register(
                context, VOLCANIC_PATCH_CEILING_KEY, Features.POTIONS_PLUS_VEGETATION_PATCH,
                new PotionsPlusVegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, VolcanicCave.VOLCANIC_CAVE_BLOCK_SAMPLER_CEILING, PlacementUtils.inlinePlaced(CEILING_VEGETATION), Direction.DOWN, UniformInt.of(1, 3), 0.0F, 5, 0.08F, UniformInt.of(4, 7), 0.3F, BlockStateProvider.simple(Blocks.AIR)));

        final Holder<ConfiguredFeature<?, ?>> FISSURE = register(context, FISSURE_KEY, Features.FISSURE);
        final Holder<ConfiguredFeature<?, ?>> LAVA_GEYSER = register(context, LAVA_GEYSER_KEY, Features.LAVA_GEYSER);

        // ----- Ice Cave -----
        final Holder<ConfiguredFeature<?, ?>> ICE_FLOOR_VEGETATION = register(context, ICE_FLOOR_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(Blocks.SNOW.defaultBlockState(), 8)
                        .add(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 2), 4)
                        .add(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 3), 2)
                        .add(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 4), 1)
                )));
        final Holder<ConfiguredFeature<?, ?>> ICE_CEILING_VEGETATION = register(context, ICE_CEILING_VEGETATION_KEY, Feature.BLOCK_COLUMN,
                new BlockColumnConfiguration(List.of(
                        BlockColumnConfiguration.layer(
                                new WeightedListInt(WeightedList.<IntProvider>builder()
                                        .add(UniformInt.of(0, 3), 5)
                                        .add(UniformInt.of(1, 7), 1).build()), BlockStateProvider.simple(Blocks.PACKED_ICE)),
                        BlockColumnConfiguration.layer(ConstantInt.of(1), BlockStateProvider.simple(Blocks.BLUE_ICE))), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));
        final Holder<ConfiguredFeature<?, ?>> ICICLE = register(context, ICICLE_KEY, Features.ICICLE, new PointedDripstoneConfiguration(0.85F, 0.8F, 0.7F, 0.7F));
        final Holder<ConfiguredFeature<?, ?>> ICICLE_SELECTOR = register(context, ICICLE_SELECTOR_KEY, Feature.SIMPLE_RANDOM_SELECTOR,
                new SimpleRandomFeatureConfiguration(HolderSet.direct(PlacementUtils.inlinePlaced(ICICLE), PlacementUtils.inlinePlaced(ICE_CEILING_VEGETATION))));
        final Holder<ConfiguredFeature<?, ?>> COOBLESTONE_PILE = register(context, COOBLESTONE_PILE_KEY, Feature.BLOCK_PILE,
                new BlockPileConfiguration(BlockStateProvider.simple(DecorationBlocks.COOBLESTONE.value())));
        final Holder<ConfiguredFeature<?, ?>> ICE_PATCH = register(context, ICE_PATCH_KEY, Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, IceCave.ICE_SAMPLER, PlacementUtils.inlinePlaced(ICE_FLOOR_VEGETATION), CaveSurface.FLOOR, UniformInt.of(2, 5), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F));
        final Holder<ConfiguredFeature<?, ?>> ICE_PATCH_CEILING = register(context, ICE_PATCH_CEILING_KEY, Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BlockStateProvider.simple(Blocks.ICE), PlacementUtils.inlinePlaced(ICICLE_SELECTOR), CaveSurface.CEILING, UniformInt.of(1, 3), 0.0F, 5, 0.08F, UniformInt.of(4, 7), 0.3F));
        final Holder<ConfiguredFeature<?, ?>> AQUIFER_FREEZE = register(context, AQUIFER_FREEZE_KEY, Features.AQUIFER_FREEZE);
        final Holder<ConfiguredFeature<?, ?>> CAMPFIRE_HUDDLE = register(context, CAMPFIRE_HUDDLE_KEY, Features.CAMPFIRE_HUDDLE);
        final Holder<ConfiguredFeature<?, ?>> GIANT_SNOWFLAKE = register(context, GIANT_SNOWFLAKE_KEY, Features.GIANT_SNOWFLAKE);

        // ----- Arid Cave -----
        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_FLOOR_VEGETATION = register(context, ARID_CAVE_FLOOR_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(Blocks.DEAD_BUSH.defaultBlockState(), 1)
                        .add(Blocks.CACTUS.defaultBlockState(), 1)
                ))
        );
        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_FLOOR = register(context, ARID_CAVE_FLOOR_KEY, Features.POTIONS_PLUS_VEGETATION_PATCH,
                new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE, AridCave.BLOCK_SAMPLER_FLOOR, PlacementUtils.inlinePlaced(ARID_CAVE_FLOOR_VEGETATION), Direction.UP, UniformInt.of(5, 8), 0.0F, 5, 0.02F, UniformInt.of(4, 7), 0.3F, BlockStateProvider.simple(Blocks.SAND)));

        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_CEILING_VEGETATION = register(context, ARID_CAVE_CEILING_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(SimpleStateProvider.simple(Blocks.AIR))
        );
        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_CEILING = register(context, ARID_CAVE_CEILING_KEY, Features.POTIONS_PLUS_VEGETATION_PATCH,
                new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE, AridCave.BLOCK_SAMPLER_CEILING, PlacementUtils.inlinePlaced(ARID_CAVE_CEILING_VEGETATION), Direction.DOWN, UniformInt.of(5, 8), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F, BlockStateProvider.simple(Blocks.SAND)));
        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_SUSPICOUS_SAND = register(context, ARID_CAVE_SUSPICOUS_SAND_KEY, Features.ARID_CAVE_SUSPICIOUS_SAND);

        // ----- Wooded Cave -----
        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_VEGETATION_CONFIGURED = register(context, WOODED_CAVE_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(Blocks.GRASS.defaultBlockState(), 4)
                        .add(Blocks.FERN.defaultBlockState(), 3)
                        .add(Blocks.DANDELION.defaultBlockState(), 1)
                        .add(Blocks.POPPY.defaultBlockState(), 1)
                        .add(Blocks.OXEYE_DAISY.defaultBlockState(), 1)
                        .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 2)
                ))
        );
        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_FLOOR = register(context, WOODED_CAVE_FLOOR_KEY, Features.POTIONS_PLUS_VEGETATION_PATCH,
                new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE, WoodedCave.WOODED_CAVE_BLOCK_SAMPLER_FLOOR, PlacementUtils.inlinePlaced(WOODED_CAVE_VEGETATION_CONFIGURED), Direction.UP, UniformInt.of(4, 7), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.6F, BlockStateProvider.simple(Blocks.DIRT)));

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_CEILING_VEGETATION_CONFIGURED = register(context, WOODED_CAVE_CEILING_VEGETATION_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(Blocks.VINE.defaultBlockState(), 3)
                        .add(Blocks.MOSS_CARPET.defaultBlockState(), 2)
                ))
        );
        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_CEILING = register(context, WOODED_CAVE_CEILING_KEY, Features.POTIONS_PLUS_VEGETATION_PATCH,
                new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE, WoodedCave.WOODED_CAVE_BLOCK_SAMPLER_CEILING, PlacementUtils.inlinePlaced(WOODED_CAVE_CEILING_VEGETATION_CONFIGURED), Direction.DOWN, UniformInt.of(4, 7), 0.0F, 5, 0.6F, UniformInt.of(3, 6), 0.5F, BlockStateProvider.simple(Blocks.OAK_LOG)));

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_TREES = register(context, WOODED_CAVE_TREES_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(Blocks.OAK_LOG),
                        new StraightTrunkPlacer(4, 2, 0),
                        BlockStateProvider.simple(Blocks.OAK_LEAVES),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).ignoreVines().build()
        );
        
        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_HOLLOW_TREES = register(context, WOODED_CAVE_HOLLOW_TREES_KEY, Features.HOLLOW_TREE,
                new NoneFeatureConfiguration()
        );

        // Registers all multi directional versatile plant features (configured features stage)
        VersatilePlantsWorldGenData.registerAllConfiguredFeatures(context);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId(key));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        return context.register(key, new ConfiguredFeature<>(feature, config));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature) {
        FC config = (FC) FeatureConfiguration.NONE;
        return context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
