package grill24.potionsplus.item;

import grill24.potionsplus.item.builder.IItemModelGenerator;
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

        public SimpleItemModelGenerator(ResourceLocation textureLocation) {
            this.textureLocation = textureLocation;
        }

        public SimpleItemModelGenerator() {
            this.textureLocation = null;
        }

        @Override
        public void generate(BlockStateProvider provider, Holder<Item> item) {
            ResourceLocation modelLocation = getModelLocation(item.value());
            provider.itemModels().getBuilder(modelLocation.getPath())
                    .parent(provider.models().getExistingFile(mc("item/generated")))
                    .texture("layer0", textureLocation == null ? modelLocation : textureLocation);
        }
    }
}
