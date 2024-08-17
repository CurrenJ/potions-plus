package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.Features;
import grill24.potionsplus.worldgen.feature.LavaGeyserFeature;
import grill24.potionsplus.worldgen.feature.VolcanicFissureFeature;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class VolcanicCave {
    public static WeightedStateProvider BLOCK_SAMPLER_FLOOR = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.BLACKSTONE.defaultBlockState(), 4)
            .add(Blocks.BASALT.defaultBlockState(), 4)
            .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 3)
            .add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 2)
            .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 1)
    );
    public static WeightedStateProvider BLOCK_SAMPLER_CEILING = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.BLACKSTONE.defaultBlockState(), 4)
            .add(Blocks.BASALT.defaultBlockState(), 4)
            .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 3)
            .add(Blocks.NETHERRACK.defaultBlockState(), 2)
            .add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 2)
            .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 2)
    );

    public static final Holder<ConfiguredFeature<SimpleBlockConfiguration, ?>> FLOOR_VEGETATION = FeatureUtils.register(
            "floor_vegetation", Feature.SIMPLE_BLOCK,
            new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                    .add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 12)
                    .add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1)
                    .add(Blocks.RED_MUSHROOM.defaultBlockState(), 1)
            )));

    public static final Holder<ConfiguredFeature<SimpleBlockConfiguration, ?>> CEILING_VEGETATION = FeatureUtils.register(
            "ceiling_vegetation", Feature.SIMPLE_BLOCK,
            new SimpleBlockConfiguration(SimpleStateProvider.simple(Blocks.AIR)));

    public static final Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> VOLCANIC_PATCH_FLOOR = FeatureUtils.register(
            "volcanic_patch_floor", MiscFeatures.NO_UPDATE_VEGETATION_PATCH,
            // replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance
            new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BLOCK_SAMPLER_FLOOR, PlacementUtils.inlinePlaced(FLOOR_VEGETATION), CaveSurface.FLOOR, UniformInt.of(5, 8), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F));
    public static final Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> VOLCANIC_PATCH_CEILING = FeatureUtils.register(
            "volcanic_patch_ceiling", MiscFeatures.NO_UPDATE_VEGETATION_PATCH,
            new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BLOCK_SAMPLER_CEILING, PlacementUtils.inlinePlaced(CEILING_VEGETATION), CaveSurface.CEILING, UniformInt.of(1, 3), 0.0F, 5, 0.08F, UniformInt.of(4, 7), 0.3F));

    public static final BlockPredicate VOLCANIC_CAVE_END_SCAN = BlockPredicate.allOf(
            BlockPredicate.solid(),
            BlockPredicate.not(BlockPredicate.matchesBlocks(List.of(
                            grill24.potionsplus.core.Blocks.UNSTABLE_BLACKSTONE.get(),
                            grill24.potionsplus.core.Blocks.UNSTABLE_DEEPSLATE.get(),
                            grill24.potionsplus.core.Blocks.UNSTABLE_MOLTEN_BLACKSTONE.get(),
                            grill24.potionsplus.core.Blocks.UNSTABLE_MOLTEN_DEEPSLATE.get()))));
    public static final BlockPredicate VOLCANIC_CAVE_CONTINUE_SCAN = BlockPredicate.anyOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.matchesBlocks(List.of(
                            grill24.potionsplus.core.Blocks.UNSTABLE_BLACKSTONE.get(),
                            grill24.potionsplus.core.Blocks.UNSTABLE_DEEPSLATE.get(),
                            grill24.potionsplus.core.Blocks.UNSTABLE_MOLTEN_BLACKSTONE.get(),
                            grill24.potionsplus.core.Blocks.UNSTABLE_MOLTEN_DEEPSLATE.get())),
            BlockPredicate.matchesFluid(Fluids.LAVA, Vec3i.ZERO)
            );

    public static final Holder<PlacedFeature> VOLCANIC_CAVE_VEGETATION_FLOOR = PlacementUtils.register(
            "volcanic_cave_vegetation_floor", VOLCANIC_PATCH_FLOOR, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, VOLCANIC_CAVE_END_SCAN, VOLCANIC_CAVE_CONTINUE_SCAN, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());
    public static final Holder<PlacedFeature> VOLCANIC_CAVE_VEGETATION_CEILING = PlacementUtils.register(
            "volcanic_cave_vegetation_ceiling", VOLCANIC_PATCH_CEILING, CountPlacement.of(250), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, VOLCANIC_CAVE_END_SCAN, VOLCANIC_CAVE_CONTINUE_SCAN, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());

    public static final ResourceKey<PlacedFeature> FISSURE_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "fissure"));
    public static Feature<NoneFeatureConfiguration> FISSURE = Features.register(FISSURE_KEY.location().getPath(), new VolcanicFissureFeature(NoneFeatureConfiguration.CODEC));
    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> FISSURE_CONFIGURED = FeatureUtils.register(FISSURE_KEY.location().toString(), FISSURE);
    public static Holder<PlacedFeature> FISSURE_PLACED = PlacementUtils.register(FISSURE_KEY.location().toString(), FISSURE_CONFIGURED, CountPlacement.of(75), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(12), VerticalAnchor.absolute(48))), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

    public static final ResourceKey<PlacedFeature> LAVA_GEYSER_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "lava_geyser"));
    public static final Feature<NoneFeatureConfiguration> LAVA_GEYSER = Features.register(LAVA_GEYSER_KEY.location().getPath(), new LavaGeyserFeature(NoneFeatureConfiguration.CODEC));
    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> LAVA_GEYSER_CONFIGURED = FeatureUtils.register(LAVA_GEYSER_KEY.location().toString(), LAVA_GEYSER);
    public static Holder<PlacedFeature> LAVA_GEYSER_PLACED = PlacementUtils.register(LAVA_GEYSER_KEY.location().toString(), LAVA_GEYSER_CONFIGURED, CountPlacement.of(128), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.aboveBottom(12), VerticalAnchor.absolute(48))), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());

    public static Biome volcanicCave() {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addExtraGold(biomegenerationsettings$builder);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, FISSURE_PLACED);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, LAVA_GEYSER_PLACED);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VOLCANIC_CAVE_VEGETATION_FLOOR);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VOLCANIC_CAVE_VEGETATION_CEILING);

        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.UNDERGROUND).temperature(0.5F).downfall(0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(11809282).waterFogColor(11809282).fogColor(11809282).grassColorOverride(11809282).foliageColorOverride(11809282).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
    }

}
