package grill24.potionsplus.item;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;

/**
 * Used in conjunction with {@link grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent} and
 * {@link grill24.potionsplus.skill.reward.EdibleChoiceReward} to grant rewards when eaten.
 */
public class EdibleChoiceItem extends Item {
    public EdibleChoiceItem(Properties properties) {
        super(properties.food(Foods.CHORUS_FRUIT));
    }
}
