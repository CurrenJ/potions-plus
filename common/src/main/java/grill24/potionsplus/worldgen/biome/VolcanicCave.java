package grill24.potionsplus.worldgen.biome;

import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.worldgen.Placements;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class VolcanicCave {
    public static WeightedStateProvider VOLCANIC_CAVE_BLOCK_SAMPLER_FLOOR = new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(Blocks.BLACKSTONE.defaultBlockState(), 4)
            .add(Blocks.BASALT.defaultBlockState(), 4)
            .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 3)
            .add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 2)
            .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 1)
    );
    public static WeightedStateProvider VOLCANIC_CAVE_BLOCK_SAMPLER_CEILING = new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(Blocks.BLACKSTONE.defaultBlockState(), 4)
            .add(Blocks.BASALT.defaultBlockState(), 4)
            .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 3)
            .add(Blocks.NETHERRACK.defaultBlockState(), 2)
            .add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 2)
            .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 2)
    );

    public static final BlockPredicate VOLCANIC_CAVE_END_SCAN = BlockPredicate.allOf(
            BlockPredicate.solid(),
            BlockPredicate.not(BlockPredicate.matchesBlocks(List.of(
                    DecorationBlocks.UNSTABLE_BLACKSTONE.value(),
                    DecorationBlocks.UNSTABLE_DEEPSLATE.value(),
                    DecorationBlocks.UNSTABLE_MOLTEN_BLACKSTONE.value(),
                    DecorationBlocks.UNSTABLE_MOLTEN_DEEPSLATE.value()))
            ));
    public static final BlockPredicate VOLCANIC_CAVE_CONTINUE_SCAN = BlockPredicate.anyOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.matchesBlocks(List.of(
                    DecorationBlocks.UNSTABLE_BLACKSTONE.value(),
                    DecorationBlocks.UNSTABLE_DEEPSLATE.value(),
                    DecorationBlocks.UNSTABLE_MOLTEN_BLACKSTONE.value(),
                    DecorationBlocks.UNSTABLE_MOLTEN_DEEPSLATE.value())),
            BlockPredicate.matchesFluids(Vec3i.ZERO, Fluids.LAVA)
    );

    public static Biome volcanicCave(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addExtraGold(biomegenerationsettings$builder);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Placements.FISSURE_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Placements.LAVA_GEYSER_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.VOLCANIC_PATCH_FLOOR_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.VOLCANIC_PATCH_CEILING_KEY);


        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.5F)
                .downfall(0F)
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 11809282)
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 11809282)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 0)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(music))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(11809282)
                        .grassColorOverride(11809282)
                        .foliageColorOverride(11809282)
                        .build())
                .mobSpawnSettings(mobspawnsettings$builder.build())
                .generationSettings(biomegenerationsettings$builder.build()).build();
    }
}
