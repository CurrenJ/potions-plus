package grill24.potionsplus.worldgen;

import grill24.potionsplus.worldgen.biome.IceCave;
import grill24.potionsplus.worldgen.biome.VolcanicCave;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class Placements {

    // Misc.
    public static final ResourceKey<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED_KEY = createKey("ore_dense_diamond_small");
    public static final ResourceKey<PlacedFeature> ORE_REMNANT_DEBRIS_KEY = createKey("ore_remnant_debris");
    public static final ResourceKey<PlacedFeature> ORE_URANIUM_KEY = createKey("ore_uranium");

    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_KEY = createKey("patch_lunar_berry_bush");
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_RARE_KEY = createKey("patch_lunar_berry_bush_rare");

    public static final ResourceKey<PlacedFeature> TOMATO_PATCH_KEY = createKey("tomato_patch");
    public static final ResourceKey<PlacedFeature> BRASSICA_OLERACEA_PATCH_KEY = createKey("brassica_oleracea_patch");

    // ----- Volcanic Cave -----
    public static ResourceKey<PlacedFeature> VOLCANIC_PATCH_FLOOR_KEY = createKey("volcanic_patch_floor");
    public static ResourceKey<PlacedFeature> VOLCANIC_PATCH_CEILING_KEY = createKey("volcanic_patch_ceiling");
    public static ResourceKey<PlacedFeature> FISSURE_KEY = createKey("fissure");
    public static ResourceKey<PlacedFeature> LAVA_GEYSER_KEY = createKey("lava_geyser");

    // ----- Ice Cave -----
    public static final ResourceKey<PlacedFeature> ICE_CAVE_VEGETATION_KEY = createKey("ice_cave_vegetation");
    public static final ResourceKey<PlacedFeature> ICE_CAVE_CEILING_VEGETATION_KEY = createKey("ice_caves_ceiling_vegetation");
    public static final ResourceKey<PlacedFeature> AQUIFER_FREEZE_KEY = createKey("aquifer_freeze");
    public static final ResourceKey<PlacedFeature> CAMPFIRE_HUDDLE_KEY = createKey("campfire_huddle");
    public static final ResourceKey<PlacedFeature> GIANT_SNOWFLAKE_KEY = createKey("giant_snowflake");
    public static final ResourceKey<PlacedFeature> COOBLESTONE_PILE_KEY = createKey("cooblestone_pile");

    // ----- Arid Cave -----
    public static final ResourceKey<PlacedFeature> ARID_CAVE_FLOOR_KEY = createKey("arid_cave_floor");
    public static final ResourceKey<PlacedFeature> ARID_CAVE_CEILING_KEY = createKey("arid_cave_ceiling");
    public static final ResourceKey<PlacedFeature> ARID_CAVE_SUSPICIOUS_SAND_KEY = createKey("arid_cave_suspicious_sand");

    // ----- Wooded Cave -----
    public static final ResourceKey<PlacedFeature> WOODED_CAVE_VEGETATION_KEY = createKey("wooded_cave_vegetation");
    public static final ResourceKey<PlacedFeature> WOODED_CAVE_CEILING_VEGETATION_KEY = createKey("wooded_cave_ceiling_vegetation");
    public static final ResourceKey<PlacedFeature> WOODED_CAVE_TREES_KEY = createKey("wooded_cave_trees");
    public static final ResourceKey<PlacedFeature> WOODED_CAVE_FLOOR_KEY = createKey("wooded_cave_floor");
    public static final ResourceKey<PlacedFeature> WOODED_CAVE_CEILING_KEY = createKey("wooded_cave_ceiling");
    public static final ResourceKey<PlacedFeature> WOODED_CAVE_HOLLOW_TREES_KEY = createKey("wooded_cave_hollow_trees");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        // Misc.
        final Holder<ConfiguredFeature<?, ?>> ORE_DENSE_DIAMOND_SMALL = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ORE_DENSE_DIAMOND_SMALL_KEY);
        final Holder<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED = register(context, ORE_DENSE_DIAMOND_SMALL_PLACED_KEY,
                ORE_DENSE_DIAMOND_SMALL, OrePlacements.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))).toArray(new PlacementModifier[0]));
        final Holder<ConfiguredFeature<?, ?>> ORE_REMNANT_DEBRIS = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ORE_REMNANT_DEBRIS_KEY);
        final Holder<PlacedFeature> ORE_REMNANT_DEBRIS_PLACED = register(context, ORE_REMNANT_DEBRIS_KEY, ORE_REMNANT_DEBRIS, OrePlacements.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))).toArray(new PlacementModifier[0]));
        final Holder<PlacedFeature> ORE_URANIUM = register(context, ORE_URANIUM_KEY, configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ORE_URANIUM_KEY),
                OrePlacements.commonOrePlacement(6, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))).toArray(new PlacementModifier[0]));

        final Holder<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.LUNAR_BERRY_BUSH_KEY);
        final Holder<PlacedFeature> LUNAR_BERRY_BUSH_PLACED = register(context, LUNAR_BERRY_BUSH_KEY,
                LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        final Holder<PlacedFeature> LUNAR_BERRY_BUSH_RARE_PLACED = register(context, LUNAR_BERRY_BUSH_RARE_KEY,
                LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> TOMATO_PATCH = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.TOMATO_PATCH_KEY);
        final Holder<PlacedFeature> TOMATO_PATCH_PLACED = register(context, TOMATO_PATCH_KEY,
                TOMATO_PATCH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> BRASSICA_OLERACEA_PATCH = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.BRASSICA_OLERACEA_PATCH_KEY);
        final Holder<PlacedFeature> BRASSICA_OLERACEA_PATCH_PLACED = register(context, BRASSICA_OLERACEA_PATCH_KEY,
                BRASSICA_OLERACEA_PATCH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());

        // ----- Volcanic Cave -----
        final Holder<ConfiguredFeature<?, ?>> VOLCANIC_PATCH_FLOOR_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.VOLCANIC_PATCH_FLOOR_KEY);
        final Holder<PlacedFeature> FLOOR_VEGETATION = register(context, VOLCANIC_PATCH_FLOOR_KEY,
                VOLCANIC_PATCH_FLOOR_CONFIGURED, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, VolcanicCave.VOLCANIC_CAVE_END_SCAN, VolcanicCave.VOLCANIC_CAVE_CONTINUE_SCAN, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> VOLCANIC_PATCH_CEILING_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.VOLCANIC_PATCH_CEILING_KEY);
        final Holder<PlacedFeature> VOLCANIC_CAVE_VEGETATION_CEILING = register(context, VOLCANIC_PATCH_CEILING_KEY,
                VOLCANIC_PATCH_CEILING_CONFIGURED, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, VolcanicCave.VOLCANIC_CAVE_END_SCAN, VolcanicCave.VOLCANIC_CAVE_CONTINUE_SCAN, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> FISSURE_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.FISSURE_KEY);
        final Holder<PlacedFeature> FISSURE = register(context, FISSURE_KEY, FISSURE_CONFIGURED, CountPlacement.of(45), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(12), VerticalAnchor.absolute(48))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> LAVA_GEYSER_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.LAVA_GEYSER_KEY);
        final Holder<PlacedFeature> LAVA_GEYSER = register(context, LAVA_GEYSER_KEY, LAVA_GEYSER_CONFIGURED, CountPlacement.of(128), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(12), VerticalAnchor.absolute(48))), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        // ----- Ice Cave -----
        final Holder<ConfiguredFeature<?, ?>> ICE_PATCH = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ICE_PATCH_KEY);
        final Holder<PlacedFeature> ICE_CAVE_VEGETATION = register(context, ICE_CAVE_VEGETATION_KEY,
                ICE_PATCH, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> ICE_PATCH_CEILING = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ICE_PATCH_CEILING_KEY);
        final Holder<PlacedFeature> ICE_CAVE_CEILING_VEGETATION = register(context, ICE_CAVE_CEILING_VEGETATION_KEY,
                ICE_PATCH_CEILING, CountPlacement.of(196), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> AQUIFER_FREEZE = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.AQUIFER_FREEZE_KEY);
        final Holder<PlacedFeature> AQUIFER_FREEZE_PLACED = register(context, AQUIFER_FREEZE_KEY, AQUIFER_FREEZE, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> CAMPFIRE_HUDDLE = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.CAMPFIRE_HUDDLE_KEY);
        final Holder<PlacedFeature> CAMPFIRE_HUDDLE_PLACED = register(context, CAMPFIRE_HUDDLE_KEY, CAMPFIRE_HUDDLE,
                RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> GIANT_SNOWFLAKE = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.GIANT_SNOWFLAKE_KEY);
        final Holder<PlacedFeature> GIANT_SNOWFLAKE_PLACED = register(context, GIANT_SNOWFLAKE_KEY, GIANT_SNOWFLAKE,
                CountPlacement.of(100), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, IceCave.SNOWFLAKE_SCAN, RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> COOBLESTONE_PILE = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.COOBLESTONE_PILE_KEY);
        final Holder<PlacedFeature> COOBLESTONE_PILE_PLACED = register(context, COOBLESTONE_PILE_KEY, COOBLESTONE_PILE,
                CountPlacement.of(100), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

        // ----- Arid Cave -----
        // Register the placed features in the bootstrap method
        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_FLOOR_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ARID_CAVE_FLOOR_KEY);
        final Holder<PlacedFeature> ARID_CAVE_FLOOR_PLACED = register(context, ARID_CAVE_FLOOR_KEY,
                ARID_CAVE_FLOOR_CONFIGURED, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.not(BlockPredicate.solid()), 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_CEILING_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ARID_CAVE_CEILING_KEY);
        final Holder<PlacedFeature> ARID_CAVE_CEILING_PLACED = register(context, ARID_CAVE_CEILING_KEY,
                ARID_CAVE_CEILING_CONFIGURED, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.not(BlockPredicate.solid()), 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());
        final Holder<ConfiguredFeature<?, ?>> ARID_CAVE_SUSPICIOUS_SAND = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ARID_CAVE_SUSPICOUS_SAND_KEY);
        final Holder<PlacedFeature> ARID_CAVE_SUSPICIOUS_SAND_PLACED = register(context, ARID_CAVE_SUSPICIOUS_SAND_KEY,
                ARID_CAVE_SUSPICIOUS_SAND, CountPlacement.of(48), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.not(BlockPredicate.solid()), 12), BiomeFilter.biome());

        // ----- Wooded Cave -----
        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_VEGETATION_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.WOODED_CAVE_VEGETATION_KEY);
        final Holder<PlacedFeature> WOODED_CAVE_VEGETATION_PLACED = register(context, WOODED_CAVE_VEGETATION_KEY,
                WOODED_CAVE_VEGETATION_CONFIGURED, CountPlacement.of(128), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_CEILING_VEGETATION_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.WOODED_CAVE_CEILING_VEGETATION_KEY);
        final Holder<PlacedFeature> WOODED_CAVE_CEILING_VEGETATION_PLACED = register(context, WOODED_CAVE_CEILING_VEGETATION_KEY,
                WOODED_CAVE_CEILING_VEGETATION_CONFIGURED, CountPlacement.of(96), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_TREES_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.WOODED_CAVE_TREES_KEY);
        final Holder<PlacedFeature> WOODED_CAVE_TREES_PLACED = register(context, WOODED_CAVE_TREES_KEY,
                WOODED_CAVE_TREES_CONFIGURED, CountPlacement.of(64), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_FLOOR_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.WOODED_CAVE_FLOOR_KEY);
        final Holder<PlacedFeature> WOODED_CAVE_FLOOR_PLACED = register(context, WOODED_CAVE_FLOOR_KEY,
                WOODED_CAVE_FLOOR_CONFIGURED, CountPlacement.of(200), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_CEILING_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.WOODED_CAVE_CEILING_KEY);
        final Holder<PlacedFeature> WOODED_CAVE_CEILING_PLACED = register(context, WOODED_CAVE_CEILING_KEY,
                WOODED_CAVE_CEILING_CONFIGURED, CountPlacement.of(200), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());

        final Holder<ConfiguredFeature<?, ?>> WOODED_CAVE_HOLLOW_TREES_CONFIGURED = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.WOODED_CAVE_HOLLOW_TREES_KEY);
        final Holder<PlacedFeature> WOODED_CAVE_HOLLOW_TREES_PLACED = register(context, WOODED_CAVE_HOLLOW_TREES_KEY,
                WOODED_CAVE_HOLLOW_TREES_CONFIGURED, RarityFilter.onAverageOnceEvery(60), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

        // ----- Misc. Vegetation -----
        /**
         * Register all multi-directional versatile plant features (placed features stage) - see {@link VersatilePlantsWorldGenData} and {@link MultiDirectionalVersatilePlantFeatureData}
         */
        VersatilePlantsWorldGenData.registerAllPlacedFeatures(context);
    }

    private static ResourceKey<PlacedFeature> createKey(String key) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ppId(key));
    }

    public static Holder<PlacedFeature> register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers) {
        return context.register(key, new PlacedFeature(configuredFeature, List.of(modifiers)));
    }
}
