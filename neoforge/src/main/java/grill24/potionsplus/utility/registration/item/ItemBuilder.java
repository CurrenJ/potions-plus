package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.utility.registration.AbstractRegistererBuilder;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public abstract class ItemBuilder<I extends Item, B extends ItemBuilder<I, B>> extends AbstractRegistererBuilder<Item, B> {
    private Item.Properties properties;

    public ItemBuilder() {
        this.properties = new Item.Properties();
        this.factory = null;
        this.modelGenerator = new ItemModelUtility.SimpleItemModelGenerator<>(this::getHolder);
        this.recipeGenerators = null;
    }

    public B properties(Item.Properties properties) {
        this.properties = properties;
        return self();
    }

    public B itemFactory(Function<Item.Properties, Item> factory) {
        if (factory == null) {
            this.factory = null;
            return self();
        }
        
        this.factory = () -> factory.apply(this.properties);
        return self();
    }
}
