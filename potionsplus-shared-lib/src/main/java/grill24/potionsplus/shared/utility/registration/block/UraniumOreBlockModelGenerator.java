package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.UraniumOreBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class UraniumOreBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    private ResourceLocation textureShortId;

    public UraniumOreBlockModelGenerator(Supplier<Holder<B>> blockGetter, ResourceLocation textureShortId) {
        super(blockGetter);
        this.textureShortId = textureShortId;
    }

    public static void registerUraniumOre(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block, ResourceLocation textureShortId) {
        BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(UraniumOreBlock.URANIUM_STATE)
                        .generate(state -> {
                            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(textureShortId.getNamespace(), textureShortId.getPath() + "_" + state.getSerializedName());
                            TextureMapping textureMapping = new TextureMapping().put(TextureSlot.ALL, textureLocation);

                            // Generate block model
                            ResourceLocation model = ppId("block/" + Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath() + "_" + state.getSerializedName());
                            ModelTemplates.CUBE_ALL.create(model, textureMapping, blockModelGenerators.modelOutput);

                            // Generate blockstate definition
                            return BlockModelGenerators.plainVariant(model);
                        }));
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);

        // Generate item model from fully exposed uranium ore block model
        ResourceLocation blockModelLocation = ModelLocationUtils.getModelLocation(block);
        ResourceLocation defaultBlockModel = ResourceLocation.fromNamespaceAndPath(blockModelLocation.getNamespace(), blockModelLocation.getPath() + "_" + UraniumOreBlock.UraniumState.FULLY_EXPOSED.getSerializedName());
        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(defaultBlockModel);
        itemModelGenerators.itemModelOutput.accept(block.asItem(), itemModel);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerUraniumOre(blockModelGenerators, itemModelGenerators, getHolder().value(), textureShortId);
    }
}
