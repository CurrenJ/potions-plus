package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class VersatilePlantsWorldGenData {
    public static final Supplier<MultiDirectionalVersatilePlantFeatureData> lushCavesPlants = register(() -> new MultiDirectionalVersatilePlantFeatureData(
            new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN},
            SimpleStateProvider.simple(Blocks.MOSS_BLOCK),
            new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                            .add(FlowerBlocks.SURVIVOR_STICK.value().defaultBlockState(), 1)
                            .add(FlowerBlocks.COWLICK_VINE.value().defaultBlockState(), 1)
                            .add(FlowerBlocks.HANGING_FERN.value().defaultBlockState(), 13)
                            .add(FlowerBlocks.DROOPY_VINE.value().defaultBlockState(), 1)
                            .add(FlowerBlocks.LUMOSEED_SACKS.value().defaultBlockState(), 1)
            ),
            Map.of(
                    FlowerBlocks.SURVIVOR_STICK.unwrapKey().orElseThrow(), ClampedNormalInt.of(2, 2, 2, 6),
                    FlowerBlocks.COWLICK_VINE.unwrapKey().orElseThrow(), ClampedNormalInt.of(5, 2, 3, 9),
                    FlowerBlocks.HANGING_FERN.unwrapKey().orElseThrow(), ClampedNormalInt.of(2, 1, 2, 5),
                    FlowerBlocks.DROOPY_VINE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 7),
                    FlowerBlocks.LUMOSEED_SACKS.unwrapKey().orElseThrow(), ClampedNormalInt.of(4, 1, 2, 8)
            ),
            ppId("lush_caves_additional_plants")
    ));

    public static final Supplier<MultiDirectionalVersatilePlantFeatureData> vanillaFlowers = register(() -> new MultiDirectionalVersatilePlantFeatureData(
            new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN},
            SimpleStateProvider.simple(Blocks.MOSS_BLOCK),
            new WeightedStateProvider(
                    WeightedList.<BlockState>builder()
                            .add(FlowerBlocks.DANDELION_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.TORCHFLOWER_VERSATILE.value().defaultBlockState(), 3)
                            .add(FlowerBlocks.POPPY_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.BLUE_ORCHID_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.ALLIUM_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.AZURE_BLUET_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.RED_TULIP_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.ORANGE_TULIP_VERSATILE.value().defaultBlockState(), 7)
                            .add(FlowerBlocks.WHITE_TULIP_VERSATILE.value().defaultBlockState(), 10)
                            .add(FlowerBlocks.PINK_TULIP_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.OXEYE_DAISY_VERSATILE.value().defaultBlockState(), 10)
                            .add(FlowerBlocks.CORNFLOWER_VERSATILE.value().defaultBlockState(), 6)
                            .add(FlowerBlocks.WITHER_ROSE_VERSATILE.value().defaultBlockState(), 1)
                            .add(FlowerBlocks.LILY_OF_THE_VALLEY_VERSATILE.value().defaultBlockState(), 10)
                            .add(FlowerBlocks.BROWN_MUSHROOM_VERSATILE.value().defaultBlockState(), 5)
                            .add(FlowerBlocks.RED_MUSHROOM_VERSATILE.value().defaultBlockState(), 5)

                            .add(FlowerBlocks.SUNFLOWER_VERSATILE.value().defaultBlockState(), 5)
                            .add(FlowerBlocks.LILAC_VERSATILE.value().defaultBlockState(), 5)
                            .add(FlowerBlocks.ROSE_BUSH_VERSATILE.value().defaultBlockState(), 5)
                            .add(FlowerBlocks.PEONY_VERSATILE.value().defaultBlockState(), 5)

                            .add(FlowerBlocks.TALL_GRASS_VERSATILE.value().defaultBlockState(), 10)
                            .add(FlowerBlocks.LARGE_FERN_VERSATILE.value().defaultBlockState(), 10)
                            .add(FlowerBlocks.PITCHER_PLANT_VERSATILE.value().defaultBlockState(), 2)
            ),
            new HashMap<>() {{
                put(FlowerBlocks.DANDELION_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.TORCHFLOWER_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.POPPY_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.BLUE_ORCHID_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.ALLIUM_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.AZURE_BLUET_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.RED_TULIP_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.ORANGE_TULIP_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.WHITE_TULIP_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.PINK_TULIP_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.OXEYE_DAISY_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.CORNFLOWER_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.WITHER_ROSE_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.LILY_OF_THE_VALLEY_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.BROWN_MUSHROOM_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));
                put(FlowerBlocks.RED_MUSHROOM_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(1));

                put(FlowerBlocks.SUNFLOWER_VERSATILE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 5));
                put(FlowerBlocks.LILAC_VERSATILE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 5));
                put(FlowerBlocks.ROSE_BUSH_VERSATILE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 5));
                put(FlowerBlocks.PEONY_VERSATILE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 5));

                put(FlowerBlocks.TALL_GRASS_VERSATILE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 5));
                put(FlowerBlocks.LARGE_FERN_VERSATILE.unwrapKey().orElseThrow(), ClampedNormalInt.of(3, 1, 2, 5));
                put(FlowerBlocks.PITCHER_PLANT_VERSATILE.unwrapKey().orElseThrow(), ConstantInt.of(2));
            }},
            ppId("versatile_vanilla_flowers_patch")
    ));


    private static List<Supplier<MultiDirectionalVersatilePlantFeatureData>> ALL;

    private static Supplier<MultiDirectionalVersatilePlantFeatureData> register(Supplier<MultiDirectionalVersatilePlantFeatureData> data) {
        Supplier<MultiDirectionalVersatilePlantFeatureData> lazy = com.google.common.base.Suppliers.memoize(data);
        if (ALL == null) {
            ALL = new ArrayList<>();
        }
        ALL.add(lazy);
        return lazy;
    }

    public static void registerAllConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        ALL.forEach(data -> data.get().registerVegetationPatchConfiguredFeatures(context));
    }

    public static void registerAllPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        ALL.forEach(data -> data.get().registerPlacedFeatures(context));
    }
}
