package grill24.potionsplus.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

public class AridCave {
    public static WeightedStateProvider BLOCK_SAMPLER_FLOOR = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.SAND.defaultBlockState(), 4)
            .add(Blocks.SANDSTONE.defaultBlockState(), 1)
    );
    public static WeightedStateProvider BLOCK_SAMPLER_CEILING = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.SANDSTONE.defaultBlockState(), 1)
    );

    public static Biome aridCave(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder, true);
        BiomeDefaultFeatures.addExtraGold(biomegenerationsettings$builder);

        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Placements.ARID_CAVE_FLOOR_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Placements.ARID_CAVE_CEILING_KEY);

        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return (new Biome.BiomeBuilder())
                .hasPrecipitation(false)
                .downfall(0.0F)
                .temperature(0.5F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x050533)
                        .fogColor(0xc0d8ff)
                        .skyColor(0x77adff)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(music)
                        .build())
                .mobSpawnSettings(mobspawnsettings$builder.build())
                .generationSettings(biomegenerationsettings$builder.build())
                .build();
    }
//
}
