package grill24.potionsplus.core;

import grill24.potionsplus.worldgen.AridCave;
import grill24.potionsplus.worldgen.IceCave;
import grill24.potionsplus.worldgen.VolcanicCave;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static grill24.potionsplus.utility.Utility.ppId;

public class Biomes {
    public static final ResourceKey<Biome> ICE_CAVE_KEY = register("ice_cave");
    public static final ResourceKey<Biome> VOLCANIC_CAVE_KEY = register("volcanic_cave");
    public static final ResourceKey<Biome> ARID_CAVE_KEY = register("arid_cave");

    public static void bootstrap(BootstrapContext<Biome> context)
    {
        HolderGetter<PlacedFeature> placedFeatureGetter = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverGetter = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(ICE_CAVE_KEY, IceCave.iceCave(placedFeatureGetter, carverGetter));
        context.register(VOLCANIC_CAVE_KEY, VolcanicCave.volcanicCave(placedFeatureGetter, carverGetter));
        context.register(ARID_CAVE_KEY, AridCave.aridCave(placedFeatureGetter, carverGetter));
    }

    private static ResourceKey<Biome> register(String key)
    {
        return ResourceKey.create(Registries.BIOME, ppId(key));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder)
    {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }
}
