package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.UraniumOreBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class UraniumOreBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    private Identifier textureShortId;

    public UraniumOreBlockModelGenerator(Supplier<Holder<B>> blockGetter, Identifier textureShortId) {
        super(blockGetter);
        this.textureShortId = textureShortId;
    }

    public static void registerUraniumOre(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block, Identifier textureShortId) {
        BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(UraniumOreBlock.URANIUM_STATE)
                        .generate(state -> {
                            Identifier textureLocation = Identifier.fromNamespaceAndPath(textureShortId.getNamespace(), textureShortId.getPath() + "_" + state.getSerializedName());
                            TextureMapping textureMapping = new TextureMapping().put(TextureSlot.ALL, new Material(textureLocation));

                            // Generate block model
                            Identifier model = ppId("block/" + Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath() + "_" + state.getSerializedName());
                            ModelTemplates.CUBE_ALL.create(model, textureMapping, blockModelGenerators.modelOutput);

                            // Generate blockstate definition
                            return BlockModelGenerators.plainVariant(model);
                        }));
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);

        // Generate item model from fully exposed uranium ore block model
        Identifier blockModelLocation = ModelLocationUtils.getModelLocation(block);
        Identifier defaultBlockModel = Identifier.fromNamespaceAndPath(blockModelLocation.getNamespace(), blockModelLocation.getPath() + "_" + UraniumOreBlock.UraniumState.FULLY_EXPOSED.getSerializedName());
        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(defaultBlockModel);
        blockModelGenerators.itemModelOutput.accept(block.asItem(), itemModel);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerUraniumOre(blockModelGenerators, itemModelGenerators, getHolder().value(), textureShortId);
    }
}
