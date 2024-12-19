package grill24.potionsplus.item;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;

public class EdibleChoiceItem extends Item {
    public EdibleChoiceItem(Properties properties) {
        super(properties.food(Foods.GOLDEN_CARROT));
    }
}
