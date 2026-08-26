package grill24.potionsplus.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class Placements {
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_KEY = createKey("patch_lunar_berry_bush");
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_RARE_KEY = createKey("patch_lunar_berry_bush_rare");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        final Holder<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.LUNAR_BERRY_BUSH_KEY);
        register(context, LUNAR_BERRY_BUSH_KEY,
                LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        register(context, LUNAR_BERRY_BUSH_RARE_KEY,
                LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    }

    private static ResourceKey<PlacedFeature> createKey(String key) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ppId(key));
    }

    public static Holder<PlacedFeature> register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers) {
        return context.register(key, new PlacedFeature(configuredFeature, List.of(modifiers)));
    }
}
