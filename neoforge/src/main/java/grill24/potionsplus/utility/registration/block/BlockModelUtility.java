package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockModelUtility {
    public abstract static class BlockModelGenerator<T extends Block> implements IModelGenerator<T> {
        private final Supplier<Holder<T>> blockGetter;

        public BlockModelGenerator(Supplier<Holder<T>> blockGetter) {
            this.blockGetter = blockGetter;
        }

        @Override
        public Holder<? extends T> getHolder() {
            return blockGetter.get();
        }
    }

    public static class CubeAllBlockModelGenerator<T extends Block> extends BlockModelGenerator<T> {
        private final ResourceLocation textureLocation;

        private final boolean generateBlockModel;
        private final boolean generateBlockStates;
        private final boolean generateItemModel;

        public CubeAllBlockModelGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation textureLocation, boolean generateBlockModel, boolean generateBlockStates, boolean generateItemModel) {
            super(blockGetter);
            this.textureLocation = textureLocation;
            this.generateBlockModel = generateBlockModel;
            this.generateBlockStates = generateBlockStates;
            this.generateItemModel = generateItemModel;
        }

        public CubeAllBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
            this(blockGetter, null, true, true, true);
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Block block = getHolder().value();
            ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(block);

            if (generateBlockModel) {
                ResourceLocation tex = textureLocation != null ? textureLocation : modelLocation;
                TexturedModel.Provider textureModelProvider = TexturedModel.createDefault(b ->
                                new TextureMapping()
                                        .put(TextureSlot.ALL, tex),
                        ModelTemplates.CUBE_ALL);
                modelLocation = textureModelProvider.create(block, blockModelGenerators.modelOutput);
            }
            if (generateBlockStates) {
                MultiVariantGenerator blockstateGenerator = BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(modelLocation));
                blockModelGenerators.blockStateOutput.accept(blockstateGenerator);
            }

            if (generateItemModel) {
                ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(modelLocation);
                itemModelGenerators.itemModelOutput.accept(getHolder().value().asItem(), itemModel);
            }
        }
    }

    public static class OnlyBlockItemModelGenerator<T extends Block> extends CubeAllBlockModelGenerator<T> {
        public OnlyBlockItemModelGenerator(Supplier<Holder<T>> blockGetter) {
            super(blockGetter, null, false, false, true);
        }
    }

    /**
     * Generates a block model from a model .json file.
     */
    public static class FromModelFileBlockStateGenerator<T extends Block> implements IModelGenerator<T> {
        private final Supplier<Holder<T>> blockGetter;
        private final ResourceLocation modelLocation;
        private final boolean generateBlockStates;
        private final boolean generateItemModel;

        public FromModelFileBlockStateGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation modelLocation, boolean generateBlockStates, boolean generateItemModel) {
            this.blockGetter = blockGetter;
            this.modelLocation = modelLocation;
            this.generateItemModel = generateItemModel;
            this.generateBlockStates = generateBlockStates;
        }

        public FromModelFileBlockStateGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation modelLocation) {
            this(blockGetter, modelLocation, true, true);
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Block block = getHolder().value();

            // Generate the blockstates
            if (generateBlockStates) {
                MultiVariantGenerator blockstateGenerator = BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(modelLocation));
                blockModelGenerators.blockStateOutput.accept(blockstateGenerator);
            }

            // Generate the item model
            if (generateItemModel) {
                ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(modelLocation);
                itemModelGenerators.itemModelOutput.accept(getHolder().value().asItem(), itemModel);
            }
        }

        @Override
        public Holder<? extends T> getHolder() {
            return blockGetter.get();
        }
    }

    public static class BlockFamilyModelGenerator<T extends Block> implements IModelGenerator<T> {
        private final Supplier<Holder<T>> blockGetter;
        private final Function<BlockFamily.Builder, BlockFamily.Builder> family;

        public BlockFamilyModelGenerator(Supplier<Holder<T>> blockGetter, Function<BlockFamily.Builder, BlockFamily.Builder> family) {
            this.blockGetter = blockGetter;
            this.family = family;
        }

        @Override
        public Holder<? extends T> getHolder() {
            return blockGetter.get();
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Block block = getHolder().value();

            BlockFamily.Builder builder = new BlockFamily.Builder(block);
            BlockFamily.Builder modifiedBuilder = family.apply(builder);
            BlockFamily blockFamily = modifiedBuilder.getFamily();

            BlockModelGenerators.BlockFamilyProvider familyProvider = blockModelGenerators.family(block);
            familyProvider.generateFor(blockFamily);
        }
    }
}
