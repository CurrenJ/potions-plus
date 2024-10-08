package grill24.potionsplus.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;

public class IceCave {
    public static WeightedStateProvider ICE_SAMPLER = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(Blocks.ICE.defaultBlockState(), 5)
            .add(Blocks.PACKED_ICE.defaultBlockState(), 4)
            .add(Blocks.BLUE_ICE.defaultBlockState(), 1)
            .add(Blocks.SNOW_BLOCK.defaultBlockState(), 2)
    );
    public static final EnvironmentScanPlacement SNOWFLAKE_SCAN = EnvironmentScanPlacement.scanningFor(
            Direction.DOWN,
            BlockPredicate.allOf(matchesBlockInSquare(Blocks.ICE, 5, true, BlockPos.ZERO), matchesBlockInSquare(Blocks.AIR, 5, true, new BlockPos(0, 1, 0))),
            BlockPredicate.ONLY_IN_AIR_PREDICATE, 12);
    private static BlockPredicate matchesBlockInSquare(Block block, int expand, boolean requireAll, Vec3i offset) {
        int sideLength = expand * 2 + 1;
        BlockPredicate[] predicates = new BlockPredicate[sideLength * sideLength];
        for(int x = -expand; x <= expand; x++) {
            for(int z = -expand; z <= expand; z++) {
                predicates[(x + expand) * sideLength + (z + expand)] = BlockPredicate.matchesBlocks(new Vec3i(x, 0, z).offset(offset), block);
            }
        }

        return requireAll ? BlockPredicate.allOf(predicates) : BlockPredicate.anyOf(predicates);
    }

    public static Biome iceCave(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SNOW_GOLEM, 10, 4, 6));
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 10, 4, 6));
        mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 25, 8, 8));
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addLushCavesSpecialOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.ICE_CAVE_VEGETATION_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.ICE_CAVE_CEILING_VEGETATION_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.COOBLESTONE_PILE_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Placements.CAMPFIRE_HUDDLE_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placements.GIANT_SNOWFLAKE_KEY);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.LAKES, Placements.AQUIFER_FREEZE_KEY);


        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return (new Biome.BiomeBuilder())
                .hasPrecipitation(true)
                .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .temperature(-0.5F)
                .downfall(0.5F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x050533)
                        .fogColor(0xc0d8ff)
                        .skyColor(0x77adff)
                        .grassColorOverride(0x7f9e5f)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(music)
                        .build())
                .mobSpawnSettings(mobspawnsettings$builder.build())
                .generationSettings(biomegenerationsettings$builder.build())
                .build();
    }
}
