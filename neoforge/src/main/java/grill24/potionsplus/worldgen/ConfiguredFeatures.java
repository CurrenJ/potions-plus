package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class ConfiguredFeatures {
    // Misc.

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DENSE_DIAMOND_SMALL_KEY = createKey("ore_dense_diamond_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_REMNANT_DEBRIS_KEY = createKey("ore_remnant_debris");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH_KEY = createKey("lunar_berry_bush");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatureGetter = context.lookup(Registries.PLACED_FEATURE);

        // Misc.
        RuleTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        final Holder<ConfiguredFeature<?, ?>> ORE_DENSE_DIAMOND_SMALL = register(context, ORE_DENSE_DIAMOND_SMALL_KEY, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(stoneOreReplaceables, OreBlocks.DENSE_DIAMOND_ORE.value().defaultBlockState()),
                        OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_DENSE_DIAMOND_ORE.value().defaultBlockState())
                ), 4, 0.5F));
        final Holder<ConfiguredFeature<?, ?>> ORE_REMNANT_DEBRIS = register(context, ORE_REMNANT_DEBRIS_KEY, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(stoneOreReplaceables, OreBlocks.REMNANT_DEBRIS.value().defaultBlockState()),
                        OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value().defaultBlockState())
                ), 4, 0.5F));

        // ----- Lunar Berry Bush -----
        final Holder<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH = register(context, LUNAR_BERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 3)
                                .add(FlowerBlocks.LUNAR_BERRY_BUSH.value().defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1)
                        )))));
    }

    private static ResourceKey<ConfiguredFeature<?,?>> createKey(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId(key));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        return context.register(key, new ConfiguredFeature<>(feature, config));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature) {
        FC config = (FC) FeatureConfiguration.NONE;
        return context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
