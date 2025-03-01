package grill24.potionsplus.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import javax.annotation.Nullable;

import static grill24.potionsplus.utility.Utility.mc;
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class ItemModelUtility {
    /**
     * Generates a simple item model with a single texture. If no texture is provided, texture will default to "item/X", where X is the item name.
     */
    public static class SimpleItemModelGenerator implements IItemModelGenerator {
        @Nullable
        private final ResourceLocation textureLocation;
        private final Holder<Item> itemGetter;

        public SimpleItemModelGenerator(ResourceLocation textureLocation, Holder<Item> itemGetter) {
            this.textureLocation = textureLocation;
            this.itemGetter = itemGetter;
        }

        public SimpleItemModelGenerator(Holder<Item> itemGetter) {
            this.textureLocation = null;
            this.itemGetter = itemGetter;
        }

        @Override
        public void generate(BlockStateProvider provider) {
            ResourceLocation modelLocation = getModelLocation(itemGetter.value());
            provider.itemModels().getBuilder(modelLocation.getPath())
                    .parent(provider.models().getExistingFile(mc("item/generated")))
                    .texture("layer0", textureLocation == null ? modelLocation : textureLocation);
        }
    }
}
