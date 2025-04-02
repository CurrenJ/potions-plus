package grill24.potionsplus.item.builder;

import grill24.potionsplus.item.ItemModelUtility;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;

public class FishItemBuilder extends ItemBuilder<Item, FishItemBuilder> {
    private boolean glint;

    public FishItemBuilder() {
        super();
        this.properties(new Item.Properties().food(Foods.COD));
        this.itemFactory(Item::new);
        this.itemModelGenerator(new ItemModelUtility.SimpleItemModelGenerator());
    }

    public FishItemBuilder glint(boolean glint) {
        this.glint = glint;
        return this;
    }

    public static FishItemBuilder create(String name, boolean glint) {
        FishItemBuilder builder = new FishItemBuilder();
        builder.name(name);
        builder.glint(glint);
        if (builder.glint) {
            builder.properties(new Item.Properties().food(Foods.COD).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
        }
        return builder;
    }

    public static FishItemBuilder create(String name) {
        return create(name, false);
    }

    @Override
    FishItemBuilder self() {
        return this;
    }
}
