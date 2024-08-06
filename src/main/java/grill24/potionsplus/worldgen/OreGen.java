package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.Features;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

import static net.minecraft.data.worldgen.features.OreFeatures.DEEPSLATE_ORE_REPLACEABLES;
import static net.minecraft.data.worldgen.features.OreFeatures.STONE_ORE_REPLACEABLES;

public class OreGen {
    private final static List<OreConfiguration.TargetBlockState> ORE_DENSE_DIAMOND_TARGET_LIST = List.of(OreConfiguration.target(STONE_ORE_REPLACEABLES, grill24.potionsplus.core.Blocks.DENSE_DIAMOND_ORE.get().defaultBlockState()), OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, grill24.potionsplus.core.Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get().defaultBlockState()));
    public static final ResourceKey<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "ore_dense_diamond_small"));
    public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_DENSE_DIAMOND_SMALL = FeatureUtils.register(ORE_DENSE_DIAMOND_SMALL_KEY.location().toString(), Feature.ORE, new OreConfiguration(ORE_DENSE_DIAMOND_TARGET_LIST, 4, 0.5F));
    public static Holder<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED = PlacementUtils.register(ORE_DENSE_DIAMOND_SMALL_KEY.location().toString(), ORE_DENSE_DIAMOND_SMALL, OrePlacements.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));

    public static void addDenseDiamondOre(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_DENSE_DIAMOND_SMALL_PLACED);
    }
}
