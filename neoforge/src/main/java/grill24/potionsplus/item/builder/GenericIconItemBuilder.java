package grill24.potionsplus.item.builder;

import grill24.potionsplus.item.ItemOverrideUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class GenericIconItemBuilder extends ItemBuilder<Item, GenericIconItemBuilder> {
    public GenericIconItemBuilder(ResourceLocation overridePropertyName, ResourceLocation... iconTextureLocations) {
        super();
        this.itemFactory(Item::new);
        this.itemModelGenerator(new ItemOverrideUtility.DynamicItemOverrideModelData(overridePropertyName,
                Arrays.stream(iconTextureLocations).toList()));
    }

    public ItemOverrideUtility.DynamicItemOverrideModelData getItemModelGenerator() {
        return (ItemOverrideUtility.DynamicItemOverrideModelData) super.getItemModelGenerator();
    }

    public ItemStack getItemStackForTexture(ResourceLocation textureLocation) {
        return new ItemStack(getItem(), getItemModelGenerator().getItemStackCountForTexture(textureLocation));
    }

    @Override
    GenericIconItemBuilder self() {
        return this;
    }
}
