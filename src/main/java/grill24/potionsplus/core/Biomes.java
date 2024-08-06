package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.IceCave;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Biomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, ModInfo.MOD_ID);

    public static final RegistryObject<Biome> ICE_CAVE = BIOMES.register("ice_cave", Biomes::iceCave);

    public static Biome iceCave() {
        MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SNOW_GOLEM, 10, 4, 6));
        mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 10, 4, 6));
        mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 25, 8, 8));
        BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
        OverworldBiomes.globalOverworldGeneration(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addLushCavesSpecialOres(biomegenerationsettings$builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, IceCave.ICE_CAVE_VEGETATION);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, IceCave.ICE_CAVE_CEILING_VEGETATION);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, IceCave.AQUIFER_FREEZE_PLACED);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, IceCave.COBBLESTONE_PILE_PLACED.get());
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, IceCave.CAMPFIRE_HUDDLE_PLACED);
        biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, IceCave.GIANT_SNOWFLAKE_PLACED);

        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
        return OverworldBiomes.biome(Biome.Precipitation.SNOW, Biome.BiomeCategory.UNDERGROUND, -0.5F, 0.5F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
    }

}
