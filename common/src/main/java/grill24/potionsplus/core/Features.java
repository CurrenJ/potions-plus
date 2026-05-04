package grill24.potionsplus.core;

import grill24.potionsplus.worldgen.ConfiguredFeatures;
import grill24.potionsplus.worldgen.feature.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static grill24.potionsplus.utility.Utility.ppId;

public class Features {
    public static final ResourceKey<PlacedFeature> POTIONS_PLUS_VEGETATION_PATCH_KEY = ResourceKey.create(Registries.PLACED_FEATURE, ppId("no_update_vegetation_patch"));

    public static Feature<PotionsPlusVegetationPatchConfiguration> POTIONS_PLUS_VEGETATION_PATCH = new PotionsPlusVegetationPatchFeature(PotionsPlusVegetationPatchConfiguration.CODEC);
    public static Feature<GeneticCropConfiguration> GENETIC_CROP = new GeneticCropFeature(GeneticCropConfiguration.CODEC);

    public static Feature<?> FISSURE = new VolcanicFissureFeature(NoneFeatureConfiguration.CODEC);
    public static Feature<?> LAVA_GEYSER = new LavaGeyserFeature(NoneFeatureConfiguration.CODEC);

    public static Feature<PointedDripstoneConfiguration> ICICLE = new IcicleFeature(PointedDripstoneConfiguration.CODEC);
    public static Feature<NoneFeatureConfiguration> AQUIFER_FREEZE = new AquiferFreezeFeature(NoneFeatureConfiguration.CODEC);
    public static Feature<NoneFeatureConfiguration> CAMPFIRE_HUDDLE = new CampfireHuddleFeature(NoneFeatureConfiguration.CODEC);
    public static Feature<NoneFeatureConfiguration> GIANT_SNOWFLAKE = new GiantSnowflakeFeature(NoneFeatureConfiguration.CODEC);

    public static Feature<NoneFeatureConfiguration> ARID_CAVE_SUSPICIOUS_SAND = new SuspiciousSandFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<VersatilePlantBlockFeatureConfiguration> VERSATILE_PLANT = new VersatilePlantBlockFeature();
}
