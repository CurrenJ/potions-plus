package grill24.potionsplus.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class Placements {

    // Misc.
    public static final ResourceKey<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED_KEY = createKey("ore_dense_diamond_small");
    public static final ResourceKey<PlacedFeature> ORE_REMNANT_DEBRIS_KEY = createKey("ore_remnant_debris");

    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_KEY = createKey("patch_lunar_berry_bush");
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_RARE_KEY = createKey("patch_lunar_berry_bush_rare");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        // Misc.
        final Holder<ConfiguredFeature<?, ?>> ORE_DENSE_DIAMOND_SMALL = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ORE_DENSE_DIAMOND_SMALL_KEY);
        final Holder<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED = register(context, ORE_DENSE_DIAMOND_SMALL_PLACED_KEY,
                ORE_DENSE_DIAMOND_SMALL, OrePlacements.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))).toArray(new PlacementModifier[0]));
        final Holder<ConfiguredFeature<?, ?>> ORE_REMNANT_DEBRIS = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.ORE_REMNANT_DEBRIS_KEY);
        final Holder<PlacedFeature> ORE_REMNANT_DEBRIS_PLACED = register(context, ORE_REMNANT_DEBRIS_KEY, ORE_REMNANT_DEBRIS, OrePlacements.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))).toArray(new PlacementModifier[0]));

        final Holder<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH = configuredFeatureGetter.getOrThrow(ConfiguredFeatures.LUNAR_BERRY_BUSH_KEY);
        final Holder<PlacedFeature> LUNAR_BERRY_BUSH_PLACED = register(context, LUNAR_BERRY_BUSH_KEY,
                LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        final Holder<PlacedFeature> LUNAR_BERRY_BUSH_RARE_PLACED = register(context, LUNAR_BERRY_BUSH_RARE_KEY,
                LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    }

    private static ResourceKey<PlacedFeature> createKey(String key) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ppId(key));
    }

    public static Holder<PlacedFeature> register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers) {
        return context.register(key, new PlacedFeature(configuredFeature, List.of(modifiers)));
    }
}
