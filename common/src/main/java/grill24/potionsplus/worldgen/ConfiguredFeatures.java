package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static grill24.potionsplus.utility.Utility.ppId;

public class ConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_BERRY_BUSH_KEY = createKey("lunar_berry_bush");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatureGetter = context.lookup(Registries.PLACED_FEATURE);

        register(context, LUNAR_BERRY_BUSH_KEY, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 3)
                        .add(FlowerBlocks.LUNAR_BERRY_BUSH.value().defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1)
                )));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ppId(key));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        return context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
