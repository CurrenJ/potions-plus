package grill24.potionsplus.worldgen;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class VersatilePlantsWorldGenData {
    public static final Lazy<MultiDirectionalVersatilePlantFeatureData> lushCavesPlants = register(() -> new MultiDirectionalVersatilePlantFeatureData(
            new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN},
            SimpleStateProvider.simple(Blocks.MOSS_BLOCK),
            new WeightedStateProvider(
                    SimpleWeightedRandomList.<BlockState>builder()
                            .add(grill24.potionsplus.core.Blocks.SURVIVOR_STICK.value().defaultBlockState(), 1)
                            .add(grill24.potionsplus.core.Blocks.COWLICK_VINE.value().defaultBlockState(), 1)
                            .add(grill24.potionsplus.core.Blocks.HANGING_FERN.value().defaultBlockState(), 13)
                            .add(grill24.potionsplus.core.Blocks.DROOPY_VINE.value().defaultBlockState(), 1)
                            .add(grill24.potionsplus.core.Blocks.LUMOSEED_SACKS.value().defaultBlockState(), 1)
            ),
            Map.of(
                    grill24.potionsplus.core.Blocks.SURVIVOR_STICK.getKey(), ClampedNormalInt.of(2, 2, 2, 6),
                    grill24.potionsplus.core.Blocks.COWLICK_VINE.getKey(), ClampedNormalInt.of(5, 2, 3, 9),
                    grill24.potionsplus.core.Blocks.HANGING_FERN.getKey(), ClampedNormalInt.of(2, 1, 2, 5),
                    grill24.potionsplus.core.Blocks.DROOPY_VINE.getKey(), ClampedNormalInt.of(3, 1, 2, 7),
                    grill24.potionsplus.core.Blocks.LUMOSEED_SACKS.getKey(), ClampedNormalInt.of(4, 1, 2, 8)
            ),
            ppId("lush_caves_additional_plants")
    ));

    public static final Lazy<MultiDirectionalVersatilePlantFeatureData> vanillaFlowers = register(() -> new MultiDirectionalVersatilePlantFeatureData(
            new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN},
            SimpleStateProvider.simple(Blocks.MOSS_BLOCK),
            new WeightedStateProvider(
                    SimpleWeightedRandomList.<BlockState>builder()
                            .add(grill24.potionsplus.core.Blocks.DANDELION_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.TORCHFLOWER_VERSATILE.value().defaultBlockState(), 3)
                            .add(grill24.potionsplus.core.Blocks.POPPY_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.BLUE_ORCHID_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.ALLIUM_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.AZURE_BLUET_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.RED_TULIP_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.ORANGE_TULIP_VERSATILE.value().defaultBlockState(), 7)
                            .add(grill24.potionsplus.core.Blocks.WHITE_TULIP_VERSATILE.value().defaultBlockState(), 10)
                            .add(grill24.potionsplus.core.Blocks.PINK_TULIP_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.OXEYE_DAISY_VERSATILE.value().defaultBlockState(), 10)
                            .add(grill24.potionsplus.core.Blocks.CORNFLOWER_VERSATILE.value().defaultBlockState(), 6)
                            .add(grill24.potionsplus.core.Blocks.WITHER_ROSE_VERSATILE.value().defaultBlockState(), 1)
                            .add(grill24.potionsplus.core.Blocks.LILY_OF_THE_VALLEY_VERSATILE.value().defaultBlockState(), 10)
                            .add(grill24.potionsplus.core.Blocks.BROWN_MUSHROOM_VERSATILE.value().defaultBlockState(), 5)
                            .add(grill24.potionsplus.core.Blocks.RED_MUSHROOM_VERSATILE.value().defaultBlockState(), 5)

                            .add(grill24.potionsplus.core.Blocks.SUNFLOWER_VERSATILE.value().defaultBlockState(), 5)
                            .add(grill24.potionsplus.core.Blocks.LILAC_VERSATILE.value().defaultBlockState(), 5)
                            .add(grill24.potionsplus.core.Blocks.ROSE_BUSH_VERSATILE.value().defaultBlockState(), 5)
                            .add(grill24.potionsplus.core.Blocks.PEONY_VERSATILE.value().defaultBlockState(), 5)

                            .add(grill24.potionsplus.core.Blocks.TALL_GRASS_VERSATILE.value().defaultBlockState(), 10)
                            .add(grill24.potionsplus.core.Blocks.LARGE_FERN_VERSATILE.value().defaultBlockState(), 10)
                            .add(grill24.potionsplus.core.Blocks.PITCHER_PLANT_VERSATILE.value().defaultBlockState(), 2)
            ),
            new HashMap<>() {{
                put(grill24.potionsplus.core.Blocks.DANDELION_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.TORCHFLOWER_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.POPPY_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.BLUE_ORCHID_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.ALLIUM_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.AZURE_BLUET_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.RED_TULIP_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.ORANGE_TULIP_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.WHITE_TULIP_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.PINK_TULIP_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.OXEYE_DAISY_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.CORNFLOWER_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.WITHER_ROSE_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.LILY_OF_THE_VALLEY_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.BROWN_MUSHROOM_VERSATILE.getKey(), ConstantInt.of(1));
                put(grill24.potionsplus.core.Blocks.RED_MUSHROOM_VERSATILE.getKey(), ConstantInt.of(1));

                put(grill24.potionsplus.core.Blocks.SUNFLOWER_VERSATILE.getKey(), ClampedNormalInt.of(3, 1, 2, 5));
                put(grill24.potionsplus.core.Blocks.LILAC_VERSATILE.getKey(), ClampedNormalInt.of(3, 1, 2, 5));
                put(grill24.potionsplus.core.Blocks.ROSE_BUSH_VERSATILE.getKey(), ClampedNormalInt.of(3, 1, 2, 5));
                put(grill24.potionsplus.core.Blocks.PEONY_VERSATILE.getKey(), ClampedNormalInt.of(3, 1, 2, 5));

                put(grill24.potionsplus.core.Blocks.TALL_GRASS_VERSATILE.getKey(), ClampedNormalInt.of(3, 1, 2, 5));
                put(grill24.potionsplus.core.Blocks.LARGE_FERN_VERSATILE.getKey(), ClampedNormalInt.of(3, 1, 2, 5));
                put(grill24.potionsplus.core.Blocks.PITCHER_PLANT_VERSATILE.getKey(), ConstantInt.of(2));
            }},
            ppId("versatile_vanilla_flowers_patch")
    ));



    private static List<Lazy<MultiDirectionalVersatilePlantFeatureData>> ALL;
    private static Lazy<MultiDirectionalVersatilePlantFeatureData> register(Supplier<MultiDirectionalVersatilePlantFeatureData> data) {
        Lazy<MultiDirectionalVersatilePlantFeatureData> lazy = Lazy.of(data);
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
