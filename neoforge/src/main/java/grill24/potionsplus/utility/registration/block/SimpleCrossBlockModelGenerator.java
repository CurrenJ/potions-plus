package grill24.potionsplus.utility.registration.block;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import java.util.Objects;
import java.util.function.Supplier;

public class SimpleCrossBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    public SimpleCrossBlockModelGenerator(Supplier<Holder<B>> blockGetter) {
        super(blockGetter);
    }

    public static void registerFlowerBlock(BlockStateProvider provider, Block block, String texture) {
        provider.models().withExistingParent(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath(), provider.mcLoc("block/cross"))
                .texture("cross", texture);

        provider.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(provider.models().getExistingFile(BuiltInRegistries.BLOCK.getKey(block)))
                .build());
    }

    public static void registerFlowerBlock(BlockStateProvider provider, Block block) {
        registerFlowerBlock(provider, block, "block/" + Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath());
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerFlowerBlock(provider, getHolder().value());
    }
}
