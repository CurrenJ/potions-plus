package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.Features;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.worldgen.feature.PotionsPlusVegetationPatchConfiguration;
import grill24.potionsplus.worldgen.feature.VersatilePlantBlockFeatureConfiguration;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.HashMap;
import java.util.Map;

public class MultiDirectionalVersatilePlantFeatureData {
    private Direction[] directions;
    private BlockStateProvider surfaceBlock;
    private BlockStateProvider plantProvider;
    private Map<ResourceKey<Block>, IntProvider> length;

    // PotionsPlusVegetationPatchConfiguration fields
    private IntProvider depth;
    private float extraBottomBlockChance;
    private int verticalRange;
    private float vegetationChance;
    private IntProvider xzRadius;
    private float extraEdgeColumnChance;
    private BlockStateProvider oreBaseBlock;

    private Map<Direction, ResourceKey<ConfiguredFeature<?, ?>>> versatilePlantResourceKeys;
    private Map<Direction, ResourceKey<ConfiguredFeature<?, ?>>> vegetationPatchResourceKeys;
    private Map<Direction, ResourceKey<PlacedFeature>> placedFeatureResourceKeys;


    public MultiDirectionalVersatilePlantFeatureData(Direction[] directions, BlockStateProvider surfaceBlock, BlockStateProvider plantProvider, Map<ResourceKey<Block>, IntProvider> length, ResourceLocation baseKey) {
        this.directions = directions;
        this.surfaceBlock = surfaceBlock;
        this.plantProvider = plantProvider;
        this.length = length;

        this.versatilePlantResourceKeys = generateConfiguredFeatureResourceKeys(baseKey.withSuffix("_versatile_plant"));
        this.vegetationPatchResourceKeys = generateConfiguredFeatureResourceKeys(baseKey.withSuffix("_vegetation_patch"));
        this.placedFeatureResourceKeys = generatePlacedFeatureResourceKeys(baseKey.withSuffix("_placed"));

        // PotionsPlusVegetationPatchConfiguration defaults
        this.depth = UniformInt.of(1, 2);
        this.extraBottomBlockChance = 0.0F;
        this.verticalRange = 6;
        this.vegetationChance = 0.3F;
        this.xzRadius = UniformInt.of(6, 9);
        this.extraEdgeColumnChance = 0.3F;
        this.oreBaseBlock = surfaceBlock;
    }

    public MultiDirectionalVersatilePlantFeatureData(Direction[] directions, BlockStateProvider surfaceBlock, BlockStateProvider plantProvider, ResourceKey<Block> plantKey, IntProvider length, ResourceLocation baseKey,
                                                     IntProvider depth, float extraBottomBlockChance, int verticalRange, float vegetationChance, IntProvider xzRadius, float extraEdgeColumnChance, BlockStateProvider oreBaseBlock) {
        this(directions, surfaceBlock, plantProvider, Map.of(plantKey, length), baseKey);

        this.depth = depth;
        this.extraBottomBlockChance = extraBottomBlockChance;
        this.verticalRange = verticalRange;
        this.vegetationChance = vegetationChance;
        this.xzRadius = xzRadius;
        this.extraEdgeColumnChance = extraEdgeColumnChance;
        this.oreBaseBlock = oreBaseBlock;
    }

    private Map<Direction, ResourceKey<ConfiguredFeature<?, ?>>> generateConfiguredFeatureResourceKeys(ResourceLocation baseKey) {
        Map<Direction, ResourceKey<ConfiguredFeature<?, ?>>> keys = new HashMap<>();
        for (Direction direction : directions) {
            keys.put(direction, ResourceKey.create(Registries.CONFIGURED_FEATURE, baseKey.withSuffix("_" + direction.getName())));
        }
        return keys;
    }

    private Map<Direction, ResourceKey<PlacedFeature>> generatePlacedFeatureResourceKeys(ResourceLocation baseKey) {
        Map<Direction, ResourceKey<PlacedFeature>> keys = new HashMap<>();
        for (Direction direction : directions) {
            keys.put(direction, ResourceKey.create(Registries.PLACED_FEATURE, baseKey.withSuffix("_" + direction.getName())));
        }
        return keys;
    }

    private Map<Direction, Holder<ConfiguredFeature<?, ?>>> registerVersatilePlantConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        Map<Direction, Holder<ConfiguredFeature<?, ?>>> configuredFeatures = new HashMap<>();
        for (Direction facing : directions) {
            ResourceKey<ConfiguredFeature<?, ?>> key = versatilePlantResourceKeys.get(facing);
            configuredFeatures.computeIfAbsent(facing, dir -> ConfiguredFeatures.register(context, key, Features.VERSATILE_PLANT, new VersatilePlantBlockFeatureConfiguration(plantProvider, length, dir)));
        }
        return configuredFeatures;
    }

    public  Map<Direction, Holder<ConfiguredFeature<?, ?>>> registerVegetationPatchConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        Map<Direction, Holder<ConfiguredFeature<?, ?>>> versatilePlantConfiguredFeatures = registerVersatilePlantConfiguredFeatures(context);

        Map<Direction, Holder<ConfiguredFeature<?, ?>>> configuredFeatures = new HashMap<>();
        for (Direction facing : directions) {
            Holder<ConfiguredFeature<?, ?>> versatilePlantFeature = versatilePlantConfiguredFeatures.get(facing);

            ResourceKey<ConfiguredFeature<?, ?>> key = vegetationPatchResourceKeys.get(facing);
            configuredFeatures.computeIfAbsent(facing, dir -> ConfiguredFeatures.register(context, key, Features.POTIONS_PLUS_VEGETATION_PATCH,
                    new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE,
                            surfaceBlock,
                            PlacementUtils.inlinePlaced(versatilePlantFeature),
                            facing,
                            depth,
                            extraBottomBlockChance,
                            verticalRange,
                            vegetationChance,
                            xzRadius,
                            extraEdgeColumnChance,
                            plantProvider)));
        }
        return configuredFeatures;
    }

    public Map<Direction, Holder<PlacedFeature>> registerPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        Map<Direction, Holder<PlacedFeature>> placedFeatures = new HashMap<>();
        for (Direction facing : directions) {
            Holder<ConfiguredFeature<?, ?>> configuredFeature = configuredFeatureGetter.getOrThrow(vegetationPatchResourceKeys.get(facing));

            ResourceKey<PlacedFeature> key = placedFeatureResourceKeys.get(facing);
            placedFeatures.computeIfAbsent(facing, dir -> Placements.register(context, key, configuredFeature,
                    CountPlacement.of(100),
                    InSquarePlacement.spread(),
                    PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                    EnvironmentScanPlacement.scanningFor(facing.getOpposite(), BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
                    getPlacementOffset(facing),
                    BiomeFilter.biome()));
        }
        return placedFeatures;
    }

    private static PlacementModifier getPlacementOffset(Direction facing) {
        Direction.Axis axis = facing.getAxis();
        return switch (axis) {
            case X -> OffsetPlacement.of(ConstantInt.of(facing.getStepX()), ConstantInt.of(0), ConstantInt.of(0));
            case Y -> OffsetPlacement.of(ConstantInt.of(0), ConstantInt.of(facing.getStepY()), ConstantInt.of(0));
            case Z -> OffsetPlacement.of(ConstantInt.of(0), ConstantInt.of(0), ConstantInt.of(facing.getStepZ()));
        };
    }

    public Map<Direction, ResourceKey<PlacedFeature>> getPlacedFeaturesMap() {
        return placedFeatureResourceKeys;
    }

    public HolderSet<PlacedFeature> getPlacedFeatures(HolderGetter<PlacedFeature> holderGetter) {
        return HolderSet.direct(placedFeatureResourceKeys.values().stream().map(holderGetter::getOrThrow).toArray(Holder[]::new));
    }

}
