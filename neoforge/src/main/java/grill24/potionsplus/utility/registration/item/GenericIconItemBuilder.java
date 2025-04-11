package grill24.potionsplus.utility.registration.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class GenericIconItemBuilder extends ItemBuilder<Item, GenericIconItemBuilder> {
    public GenericIconItemBuilder(ResourceLocation overridePropertyName, ResourceLocation... iconTextureLocations) {
        super();
        this.itemFactory(Item::new);
        this.modelGenerator(itemSupplier -> new ItemOverrideUtility.DynamicItemOverrideModelData(
                itemSupplier,
                overridePropertyName,
                Arrays.stream(iconTextureLocations).toList()));
    }

    public ItemOverrideUtility.DynamicItemOverrideModelData getItemModelGenerator() {
        return (ItemOverrideUtility.DynamicItemOverrideModelData) super.getModelGenerator();
    }

    public ItemStack getItemStackForTexture(ResourceLocation textureLocation) {
        return new ItemStack(getValue(), getItemModelGenerator().getItemStackCountForTexture(textureLocation));
    }

    @Override
    protected GenericIconItemBuilder self() {
        return this;
    }
}
