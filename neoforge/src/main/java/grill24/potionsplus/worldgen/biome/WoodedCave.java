package grill24.potionsplus.worldgen.biome;

import grill24.potionsplus.worldgen.Placements;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WoodedCave {
    public static WeightedStateProvider WOODED_CAVE_BLOCK_SAMPLER_FLOOR = new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(Blocks.GRASS_BLOCK.defaultBlockState(), 4)
            .add(Blocks.DIRT.defaultBlockState(), 3)
            .add(Blocks.COARSE_DIRT.defaultBlockState(), 2)
            .add(Blocks.MOSS_BLOCK.defaultBlockState(), 2)
            .add(Blocks.PODZOL.defaultBlockState(), 1)
    );
    
    public static WeightedStateProvider WOODED_CAVE_BLOCK_SAMPLER_CEILING = new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(Blocks.OAK_LOG.defaultBlockState(), 3)
            .add(Blocks.OAK_LEAVES.defaultBlockState(), 4)
            .add(Blocks.MOSS_BLOCK.defaultBlockState(), 2)
            .add(Blocks.VINE.defaultBlockState(), 1)
    );

    public static Biome woodedCave(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 6));
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, 6, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 4, 8));
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addLushCavesSpecialOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);

        // Add wooded cave features
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.WOODED_CAVE_VEGETATION_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.WOODED_CAVE_CEILING_VEGETATION_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.WOODED_CAVE_TREES_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Placements.WOODED_CAVE_FLOOR_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Placements.WOODED_CAVE_CEILING_KEY);

        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3d57d6)
                        .waterFogColor(0x050533)
                        .fogColor(0x91bd59)
                        .skyColor(0x79a05a)
                        .grassColorOverride(0x79c05a)
                        .foliageColorOverride(0x59ae30)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(music)
                        .build())
                .mobSpawnSettings(mobspawnsettings$builder.build())
                .generationSettings(biomegenerationsettings$builder.build())
                .build();
    }
}