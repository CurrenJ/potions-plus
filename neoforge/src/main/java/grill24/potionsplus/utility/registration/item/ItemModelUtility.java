package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import javax.annotation.Nullable;

import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class ItemModelUtility {
    /**
     * Generates a simple item model with a single texture. If no texture is provided, texture will default to "item/X", where X is the item name.
     */
    public static class SimpleItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        @Nullable
        private final ResourceLocation textureLocation;
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
        public void generate(BlockStateProvider provider) {
            ResourceLocation modelLocation = getModelLocation(getHolder().value());
            provider.itemModels().getBuilder(modelLocation.getPath())
                    .parent(provider.models().getExistingFile(mc("item/generated")))
                    .texture("layer0", textureLocation == null ? modelLocation : textureLocation);
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
        public void generate(BlockStateProvider provider) {
            ResourceLocation modelLocation = getModelLocation(getHolder().value());
            ResourceLocation blockModelLocation = getModelLocation(block.get().value());
            provider.itemModels().getBuilder(modelLocation.getPath())
                    .parent(provider.models().getExistingFile(parentModel != null ? parentModel : blockModelLocation));
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }
}
