package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.Tags;
import grill24.potionsplus.worldgen.feature.PotionsPlusVegetationPatchConfiguration;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

public class AridCave {
    public static final Holder<PlacedFeature> ORE_COPPER_EXTRA = PlacementUtils.register("ore_copper_extra", OreFeatures.ORE_GOLD, OrePlacements.commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(256))));

    public static WeightedStateProvider BLOCK_SAMPLER_FLOOR = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.SAND.defaultBlockState(), 4)
            .add(Blocks.SANDSTONE.defaultBlockState(), 1)
    );
    public static WeightedStateProvider BLOCK_SAMPLER_CEILING = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.SANDSTONE.defaultBlockState(), 1)
    );

    public static final Holder<ConfiguredFeature<SimpleBlockConfiguration, ?>> FLOOR_VEGETATION = FeatureUtils.register(
            "aird_cave_floor_vegetation", Feature.SIMPLE_BLOCK,
            new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                    .add(Blocks.DEAD_BUSH.defaultBlockState(), 1)
                    .add(Blocks.CACTUS.defaultBlockState(), 1)
            )));
    public static final Holder<ConfiguredFeature<SimpleBlockConfiguration, ?>> CEILING_VEGETATION = FeatureUtils.register(
            "arid_cave_ceiling_vegetation", Feature.SIMPLE_BLOCK,
            new SimpleBlockConfiguration(SimpleStateProvider.simple(Blocks.AIR)));



    public static final Holder<ConfiguredFeature<PotionsPlusVegetationPatchConfiguration, ?>> ARID_CAVE_FLOOR = FeatureUtils.register(
            "arid_cave_floor", MiscFeatures.POTIONS_PLUS_VEGETATION_PATCH,
            // replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance
            new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE, BLOCK_SAMPLER_FLOOR, PlacementUtils.inlinePlaced(FLOOR_VEGETATION), CaveSurface.FLOOR, UniformInt.of(5, 8), 0.0F, 5, 0.02F, UniformInt.of(4, 7), 0.3F, BlockStateProvider.simple(Blocks.SAND)));
    public static final Holder<PlacedFeature> ARID_CAVE_FLOOR_PLACED = PlacementUtils.register(
            "arid_cave_floor", ARID_CAVE_FLOOR, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.not(BlockPredicate.solid()), 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

    public static final Holder<ConfiguredFeature<PotionsPlusVegetationPatchConfiguration, ?>> ARID_CAVE_CEILING = FeatureUtils.register(
            "arid_cave_ceiling", MiscFeatures.POTIONS_PLUS_VEGETATION_PATCH,
            // replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance
            new PotionsPlusVegetationPatchConfiguration(Tags.Blocks.CAVE_REPLACEABLE, BLOCK_SAMPLER_CEILING, PlacementUtils.inlinePlaced(CEILING_VEGETATION), CaveSurface.CEILING, UniformInt.of(5, 8), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F, BlockStateProvider.simple(Blocks.SAND)));
    public static final Holder<PlacedFeature> ARID_CAVE_CEILING_PLACED = PlacementUtils.register(
            "arid_cave_ceiling", ARID_CAVE_CEILING, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.not(BlockPredicate.solid()), 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());



    public static Biome aridCave() {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addExtraGold(biomegenerationsettings$builder);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_COPPER_EXTRA);

        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ARID_CAVE_FLOOR_PLACED);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ARID_CAVE_CEILING_PLACED);

        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.UNDERGROUND).temperature(0.5F).downfall(0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(7982818).waterFogColor(7982818).fogColor(7982818).grassColorOverride(8444864).foliageColorOverride(8444864).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
    }

}
