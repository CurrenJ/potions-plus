package grill24.potionsplus.core;

import grill24.potionsplus.core.feature.PrimaryAndSecondaryFlowerFeatureConfig;
import grill24.potionsplus.core.feature.PrimaryAndSecondaryFlowerPatchFeature;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static net.minecraft.data.worldgen.features.OreFeatures.DEEPSLATE_ORE_REPLACEABLES;
import static net.minecraft.data.worldgen.features.OreFeatures.STONE_ORE_REPLACEABLES;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Features {
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_PLACED_FEATURE = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "patch_lunar_berry_bush_placed"));
    public static Holder<PlacedFeature> lunarBerryBushCommon = null;
    public static Holder<PlacedFeature> lunarBerryBushRare = null;

    public static final ResourceKey<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED_FEATURE = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "ore_dense_diamond_small_placed"));
    public static Holder<PlacedFeature> ORE_DENSE_DIAMOND_SMALL_PLACED = null;

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        PotionsPlus.LOGGER.info("Registering potionsplus features");

        registerLunarBerryBushFeature();

        registerDenseDiamondFeature();

        PotionsPlus.LOGGER.info("Registered potionsplus feature");
    }

    private static void registerDenseDiamondFeature() {
        String denseDiamondId = ORE_DENSE_DIAMOND_SMALL_PLACED_FEATURE.location().toString();
        final List<OreConfiguration.TargetBlockState> ORE_DENSE_DIAMOND_TARGET_LIST = List.of(OreConfiguration.target(STONE_ORE_REPLACEABLES, grill24.potionsplus.core.Blocks.DENSE_DIAMOND_ORE.get().defaultBlockState()), OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, grill24.potionsplus.core.Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get().defaultBlockState()));
        Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_DENSE_DIAMOND_SMALL = FeatureUtils.register(denseDiamondId, Feature.ORE, new OreConfiguration(ORE_DENSE_DIAMOND_TARGET_LIST, 4, 0.5F));
        ORE_DENSE_DIAMOND_SMALL_PLACED = PlacementUtils.register(denseDiamondId, ORE_DENSE_DIAMOND_SMALL, OrePlacements.commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
    }

    private static void registerLunarBerryBushFeature() {
        String lunarBerriesId = LUNAR_BERRY_BUSH_PLACED_FEATURE.location().toString();
        var configuredLunarBerries = FeatureUtils.register(
                lunarBerriesId,
                Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3)), 3)
                                .add(grill24.potionsplus.core.Blocks.LUNAR_BERRY_BUSH.get().defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3)), 1)
                        )), List.of(Blocks.GRASS_BLOCK)));

        lunarBerryBushCommon = PlacementUtils.register(lunarBerriesId, configuredLunarBerries, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        lunarBerryBushRare = PlacementUtils.register(lunarBerriesId + "_rare", configuredLunarBerries, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    }

    public static void registerLunarBerryBushCustomFeature(RegistryEvent.Register<Feature<?>> event) {
        String lunarBerriesId = LUNAR_BERRY_BUSH_PLACED_FEATURE.location().toString();
        var lunarBerriesFeature = new PrimaryAndSecondaryFlowerPatchFeature();
        lunarBerriesFeature.setRegistryName(LUNAR_BERRY_BUSH_PLACED_FEATURE.location());
        event.getRegistry().register(lunarBerriesFeature);
        var lunarBerriesFeatureConfig = new PrimaryAndSecondaryFlowerFeatureConfig(BlockStateProvider.simple(Blocks.SWEET_BERRY_BUSH), BlockStateProvider.simple(Blocks.RED_TULIP), 0.5F);
        var configuredLunarBerries = FeatureUtils.register(lunarBerriesId, lunarBerriesFeature, lunarBerriesFeatureConfig);

        lunarBerryBushCommon = PlacementUtils.register(lunarBerriesId, configuredLunarBerries, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        lunarBerryBushRare = PlacementUtils.register(lunarBerriesId + "_rare", configuredLunarBerries, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    }

    @SubscribeEvent
    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        addLunarBerryBushes(event);
        addDenseDiamondOre(event.getGeneration());
    }

    public static void addDenseDiamondOre(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_DENSE_DIAMOND_SMALL_PLACED);
    }

    public static void addLunarBerryBushes(BiomeLoadingEvent event) {
        // Only add berries to taiga, mimicing the vanilla behaviour in @OverworldBiomes
        Biome.BiomeCategory biome = event.getCategory();
        if (biome == Biome.BiomeCategory.TAIGA) {
            switch (event.getClimate().precipitation) {
                case RAIN:
                    addCommonBerryBushes(event.getGeneration());
                    break;
                case SNOW:
                    addRareBerryBushes(event.getGeneration());
                    break;
            }
        }
    }

    public static void addCommonBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, lunarBerryBushCommon);
    }

    public static void addRareBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, lunarBerryBushRare);

    }
}
