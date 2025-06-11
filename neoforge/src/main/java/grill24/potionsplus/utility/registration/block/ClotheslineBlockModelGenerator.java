package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.ClotheslinePart;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class ClotheslineBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    public ClotheslineBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
        super(blockGetter);
    }

    public static void registerClothesline(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        Block block = BlockEntityBlocks.CLOTHESLINE.value();

        ResourceLocation fencePostModelLocation = mc("block/oak_fence_post");

        // Generate blockstate definition
        MultiVariant fencePost = BlockModelGenerators.plainVariant(fencePostModelLocation);
        BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                .with(
                        PropertyDispatch.initial(ClotheslineBlock.PART)
                                .select(ClotheslinePart.LEFT, fencePost)
                                .select(ClotheslinePart.RIGHT, fencePost)
                );
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);

        // Generate client item definition from existing model
        ResourceLocation clotheslineInventoryModelLocation = ppId("block/clothesline_inventory");
        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(clotheslineInventoryModelLocation);
        itemModelGenerators.itemModelOutput.accept(block.asItem(), itemModel);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerClothesline(blockModelGenerators, itemModelGenerators);
    }
}
