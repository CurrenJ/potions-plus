package grill24.potionsplus.data;

import grill24.potionsplus.core.items.HatItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class BlockHatModelProvider {
    public static final TextureSlot HAT_BASE = TextureSlot.create("0", TextureSlot.ALL);

    public static final ModelTemplate[] BLOCK_HAT_TEMPLATES = createHatTemplates(HatItems.BLOCK_HAT_MODELS);
    public static final TexturedModel.Provider[] BLOCK_HAT_PROVIDERS = createHatTemplateProviders(BLOCK_HAT_TEMPLATES);

    public static ModelTemplate createHatTemplate(ResourceLocation parentModel, String suffix) {
        return new ModelTemplate(
                Optional.of(parentModel),
                Optional.of(suffix + "_block_hat"),
                HAT_BASE
        );
    }
    public static ModelTemplate[] createHatTemplates(ResourceLocation[] parentModels) {
        ModelTemplate[] templates = new ModelTemplate[parentModels.length];
        for (int i = 0; i < parentModels.length; i++) {
            templates[i] = createHatTemplate(parentModels[i], Integer.toString(i+1));
        }
        return templates;
    }

    public static TexturedModel.Provider createHatTemplateProvider(ModelTemplate template) {
        return TexturedModel.createDefault(
                block -> new TextureMapping()
                        .put(HAT_BASE, TextureMapping.getBlockTexture(block)),
                template
        );
    }
    public static TexturedModel.Provider[] createHatTemplateProviders(ModelTemplate[] templates) {
        TexturedModel.Provider[] providers = new TexturedModel.Provider[templates.length];
        for (int i = 0; i < templates.length; i++) {
            providers[i] = createHatTemplateProvider(templates[i]);
        }
        return providers;
    }

    static void registerBlockHatItem(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Holder<Item>[] blockHatItems, Block blockTex) {
        for (int i = 0; i < blockHatItems.length; i++) {
            if (i < BLOCK_HAT_TEMPLATES.length) {
                Holder<Item> item = blockHatItems[i];
                TexturedModel.Provider provider = BLOCK_HAT_PROVIDERS[i];

                // Generate the hat as a block model
                ResourceLocation generatedModel = provider.create(blockTex, blockModelGenerators.modelOutput);

                // Generate an item model from the block model
                ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(generatedModel);
                // Generate client item definition from the item model
                itemModelGenerators.itemModelOutput.accept(item.value(), itemModel);
            } else {
                throw new IllegalArgumentException("Not enough block hat templates registered for the number of block hat items.");
            }
        }
    }
}
