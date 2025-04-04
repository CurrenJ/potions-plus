package grill24.potionsplus.utility.registration.item;

import net.minecraft.world.item.Item;

public class SimpleItemBuilder extends ItemBuilder<Item, SimpleItemBuilder> {
    public static <I extends Item, B extends ItemBuilder<I, B>> SimpleItemBuilder create(String name) {
        SimpleItemBuilder builder = new SimpleItemBuilder();
        builder.name(name);
        return builder;
    }

    public static SimpleItemBuilder createSimple(String name) {
        SimpleItemBuilder builder = new SimpleItemBuilder();
        builder.name(name);
        builder.itemFactory(Item::new);
        return builder;
    }

    @Override
    protected SimpleItemBuilder self() {
        return this;
    }
}
