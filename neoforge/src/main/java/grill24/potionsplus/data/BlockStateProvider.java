package grill24.potionsplus.data;

import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ModInfo.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        RegistrationUtility.generateItemModels(ModInfo.MOD_ID, this);

        registerItemFromParentWithTextureOverride(FishItems.COPPER_FISHING_ROD.value(), mc("item/handheld_rod"), ppId("item/copper_fishing_rod"));

        Holder<Item>[][] blockHatItems = new Holder[][]{
                HatItems.COAL_ORE_HATS,
                HatItems.COPPER_ORE_HATS,
                HatItems.IRON_ORE_HATS,
                HatItems.GOLD_ORE_HATS,
                HatItems.DIAMOND_ORE_HATS,
                HatItems.EMERALD_ORE_HATS
        };
        ResourceLocation[] blockHatTextures = new ResourceLocation[] { mc("block/coal_ore"), mc("block/copper_ore"), mc("block/iron_ore"), mc("block/gold_ore"), mc("block/diamond_ore"), mc("block/emerald_ore") };
        registerAllBlockHatVariantsForItem(HatItems.BLOCK_HAT_MODELS, blockHatTextures, blockHatItems);
    }

    private void registerAllBlockHatVariantsForItem(ResourceLocation[] parentModels, ResourceLocation[] textures, Holder<Item>[][] blockHatItems) {
        for (int t = 0; t < textures.length; t++) {
            ResourceLocation texture = textures[t];
            for (int m = 0; m < parentModels.length; m++) {
                Holder<Item> itemForTexture = blockHatItems[t][m];
                ResourceLocation resourceLocation = parentModels[m];
                registerBlockHatItem(itemForTexture, resourceLocation, texture);
            }
        }
    }

    private void registerBlockHatItem(Holder<Item> item, ResourceLocation parentModel, ResourceLocation texture) {
        ResourceLocation modelLocation = getModelLocation(item.value());
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parentModel))
                .texture("0", texture);
    }

    private void registerItemFromParentWithTextureOverride(Item item, ResourceLocation parent, ResourceLocation textureOverride) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parent))
                .texture("layer0", textureOverride);
    }
}
