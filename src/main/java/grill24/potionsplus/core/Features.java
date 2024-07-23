package grill24.potionsplus.core;

import grill24.potionsplus.core.feature.PrimaryAndSecondaryFlowerFeatureConfig;
import grill24.potionsplus.core.feature.PrimaryAndSecondaryFlowerPatchFeature;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Features {
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_PLACED_FEATURE = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "patch_lunar_berry_bush_placed"));
    public static Holder<PlacedFeature> lunarBerryBushCommon = null;
    public static Holder<PlacedFeature> lunarBerryBushRare = null;


    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        PotionsPlus.LOGGER.info("Registering potionsplus features");

        registerLunarBerryBushFeature();

        PotionsPlus.LOGGER.info("Registered potionsplus feature");
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
        addCommonBerryBushes(event.getGeneration());
        addRareBerryBushes(event.getGeneration());
    }

    public static void addCommonBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, lunarBerryBushCommon);
    }

    public static void addRareBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, lunarBerryBushRare);

    }
}
