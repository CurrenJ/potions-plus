package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

import java.util.function.Supplier;

public class ItemModelUtility {
    /**
     * Generates a simple item model with a single texture. If no texture is provided, texture will default to "item/X", where X is the item name.
     */
    public static class SimpleItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        @Nullable
        protected final ResourceLocation textureLocation;
        private final Supplier<Holder<I>> itemGetter;

        public SimpleItemModelGenerator(Supplier<Holder<I>> itemGetter, ResourceLocation textureLocation) {
            super();
            this.textureLocation = textureLocation;
            this.itemGetter = itemGetter;
        }

        public SimpleItemModelGenerator(Supplier<Holder<I>> itemSupplier) {
            super();
            this.textureLocation = null;
            this.itemGetter = itemSupplier;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
            ResourceLocation texture = textureLocation == null ? modelLocation : textureLocation;

            ModelTemplates.FLAT_ITEM.create(modelLocation, new TextureMapping().put(TextureSlot.LAYER0, texture), itemModelGenerators.modelOutput);
            itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLocation));
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class SimpleBlockItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final Supplier<Holder<Block>> block;
        @Nullable private final ResourceLocation parentModel;

        public SimpleBlockItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<Holder<Block>> block) {
            this(itemGetter, block, null);
        }

        public SimpleBlockItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<Holder<Block>> block, ResourceLocation parentModel) {
            super();
            this.itemGetter = itemGetter;
            this.block = block;
            this.parentModel = parentModel;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            ResourceLocation blockModelLocation = parentModel != null ? parentModel : ModelLocationUtils.getModelLocation(block.get().value());

            ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(blockModelLocation);
            itemModelGenerators.itemModelOutput.accept(getHolder().value(), itemModel);
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class ItemFromModelFileGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final ResourceLocation modelFile;

        public ItemFromModelFileGenerator(Supplier<Holder<I>> itemGetter, ResourceLocation modelFile) {
            super();
            this.itemGetter = itemGetter;
            this.modelFile = modelFile;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelFile));
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }
}
