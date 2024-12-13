package grill24.potionsplus.data;

import grill24.potionsplus.worldgen.VersatilePlantsWorldGenData;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static grill24.potionsplus.utility.Utility.ppId;

public class BiomeModifierProvider {
    private static final ResourceKey<BiomeModifier> LUSH_CAVES_ADDITIONAL_PLANTS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ppId("lush_cave_additional_plants"));
    private static final ResourceKey<BiomeModifier> LUSH_CAVES_VERSATILE_VANILLA_PLANTS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ppId("lush_cave_versatile_vanilla_plants"));

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(LUSH_CAVES_ADDITIONAL_PLANTS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.LUSH_CAVES)),
                VersatilePlantsWorldGenData.lushCavesPlants.get().getPlacedFeatures(placedFeatures),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(LUSH_CAVES_VERSATILE_VANILLA_PLANTS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.LUSH_CAVES)),
                VersatilePlantsWorldGenData.vanillaFlowers.get().getPlacedFeatures(placedFeatures),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }
}
