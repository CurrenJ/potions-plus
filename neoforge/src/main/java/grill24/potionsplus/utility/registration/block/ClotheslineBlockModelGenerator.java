package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.ClotheslinePart;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import java.util.Objects;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class ClotheslineBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    public ClotheslineBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
        super(blockGetter);
    }

    public static void registerClothesline(BlockStateProvider provider) {
        provider.getVariantBuilder(BlockEntityBlocks.CLOTHESLINE.value())
                .partialState().with(ClotheslineBlock.PART, ClotheslinePart.LEFT).modelForState()
                .modelFile(provider.models().getExistingFile(provider.mcLoc("oak_fence_post")))
                .addModel()
                .partialState().with(ClotheslineBlock.PART, ClotheslinePart.RIGHT).modelForState()
                .modelFile(provider.models().getExistingFile(provider.mcLoc("oak_fence_post")))
                .addModel();

        provider.itemModels().getBuilder(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(BlockEntityBlocks.CLOTHESLINE.value())).getPath())
                .parent(provider.models().getExistingFile(ppId("block/clothesline_inventory")));
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerClothesline(provider);
    }
}
