package grill24.potionsplus.utility.registration.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GenericIconItemBuilder extends ItemBuilder<Item, GenericIconItemBuilder> {
    private final Map<Identifier, Integer> textureToItemStackCountData;

    public GenericIconItemBuilder(Identifier overridePropertyName, Identifier... iconTextureLocations) {
        super();
        this.itemFactory(Item::new);

        this.textureToItemStackCountData = new HashMap<>();
        int startingCount = 1;
        for (Identifier textureLocation : iconTextureLocations) {
            this.textureToItemStackCountData.put(textureLocation, startingCount++);
        }

        this.modelGenerator(itemSupplier -> new ItemOverrideUtility.DynamicItemOverrideModelData(
                itemSupplier,
                overridePropertyName,
                iconTextureLocations,
                textureToItemStackCountData)
        );
    }

    public ItemOverrideUtility.DynamicItemOverrideModelData getItemModelGenerator() {
        return (ItemOverrideUtility.DynamicItemOverrideModelData) super.getModelGenerator();
    }

    public ItemStack getItemStackForTexture(Identifier textureLocation) {
        return new ItemStack(getValue(), textureToItemStackCountData.getOrDefault(textureLocation, 0));
    }

    @Override
    protected GenericIconItemBuilder self() {
        return this;
    }
}
