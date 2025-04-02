package grill24.potionsplus.item.builder;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.item.EdibleChoiceItem;
import grill24.potionsplus.item.ItemOverrideUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

import static grill24.potionsplus.utility.Utility.ppId;

public class EdibleChoiceItemBuilder extends ItemBuilder<EdibleChoiceItem, EdibleChoiceItemBuilder> {
    public static EdibleChoiceItemBuilder create(String name, ResourceLocation textureLocation) {
        EdibleChoiceItemBuilder builder = new EdibleChoiceItemBuilder();
        builder.name(name);
        builder.itemFactory(EdibleChoiceItem::new);
        builder.properties(new Item.Properties().food(Foods.CHORUS_FRUIT));
        builder.itemModelGenerator(new ItemOverrideUtility.EdibleChoiceItemOverrideModelData(
                Items.EDIBLE_CHOICE_ITEM_FLAG_PROPERTY_NAME,
                textureLocation,
                new ArrayList<>() {{
                    add(ppId("item/red_flag"));
                    add(ppId("item/green_flag"));
                    add(ppId("item/blue_flag"));
                    add(ppId("item/yellow_flag"));
                    add(ppId("item/orange_flag"));
                }}));

        return builder;
    }

    public ItemOverrideUtility.EdibleChoiceItemOverrideModelData getItemOverrideModelData() {
        return (ItemOverrideUtility.EdibleChoiceItemOverrideModelData) getItemModelGenerator();
    }

    @Override
    EdibleChoiceItemBuilder self() {
        return this;
    }
}
