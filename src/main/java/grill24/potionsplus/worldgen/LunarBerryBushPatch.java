package grill24.potionsplus.worldgen;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class LunarBerryBushPatch {
    public static final ResourceKey<PlacedFeature> LUNAR_BERRY_BUSH_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "patch_lunar_berry_bush"));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> LUNAR_BERRY_BUSH = FeatureUtils.register(
            LUNAR_BERRY_BUSH_KEY.location().toString(),
            Feature.RANDOM_PATCH,
            FeatureUtils.simplePatchConfiguration(
                    Feature.SIMPLE_BLOCK,
                    new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                            .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3)), 3)
                            .add(grill24.potionsplus.core.Blocks.LUNAR_BERRY_BUSH.get().defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3)), 1)
                    )), List.of(Blocks.GRASS_BLOCK)));
    public static final Holder<PlacedFeature> LUNAR_BERRY_BUSH_RARE_PLACED = PlacementUtils.register(LUNAR_BERRY_BUSH_KEY.location() + "_rare", LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    public static final Holder<PlacedFeature> LUNAR_BERRY_BUSH_PLACED = PlacementUtils.register(LUNAR_BERRY_BUSH_KEY.location().toString(), LUNAR_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());

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

    private static void addCommonBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LUNAR_BERRY_BUSH_PLACED);
    }

    private static void addRareBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LUNAR_BERRY_BUSH_RARE_PLACED);
    }
}
