package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.UraniumOreBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;

import java.util.Objects;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class UraniumOreBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    private ResourceLocation textureShortId;

    public UraniumOreBlockModelGenerator(Supplier<Holder<B>> blockGetter, ResourceLocation textureShortId) {
        super(blockGetter);
        this.textureShortId = textureShortId;
    }

    public static void registerUraniumOre(BlockStateProvider provider, Block block, ResourceLocation textureShortId) {
        VariantBlockStateBuilder builder = provider.getVariantBuilder(block);
        for (UraniumOreBlock.UraniumState state : UraniumOreBlock.UraniumState.values()) {
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(textureShortId.getNamespace(), textureShortId.getPath() + "_" + state.getSerializedName());
            ResourceLocation model = ppId(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath() + "_"+ state.getSerializedName());
            provider.models().cubeAll(model.getPath(), textureLocation);

            builder.partialState().with(UraniumOreBlock.URANIUM_STATE, state).modelForState()
                    .modelFile(provider.models().getExistingFile(model))
                    .addModel();

            provider.simpleBlockItem(block, provider.models().getExistingFile(model));
        }
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerUraniumOre(provider, getHolder().value(), textureShortId);
    }
}
