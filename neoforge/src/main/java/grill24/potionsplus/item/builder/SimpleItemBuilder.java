package grill24.potionsplus.item.builder;

import net.minecraft.world.item.Item;

public class SimpleItemBuilder<I extends Item> extends ItemBuilder<I, SimpleItemBuilder<I>> {
    @Override
    SimpleItemBuilder<I> self() {
        return this;
    }

    public static <I extends Item, B extends ItemBuilder<I, B>> SimpleItemBuilder<I> create(String name) {
        SimpleItemBuilder<I> builder = new SimpleItemBuilder<>();
        builder.name(name);
        return builder;
    }

    public static SimpleItemBuilder<Item> createSimple(String name) {
        SimpleItemBuilder<Item> builder = new SimpleItemBuilder<>();
        builder.name(name);
        builder.itemFactory(Item::new);
        return builder;
    }
}
