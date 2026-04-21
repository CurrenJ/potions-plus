package grill24.potionsplus.item;

import grill24.potionsplus.item.consumeeffect.EdibleChoiceItemConsumeEffect;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;

/**
 * Used in conjunction with {@link grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent} and
 * {@link grill24.potionsplus.skill.reward.EdibleChoiceReward} to grant rewards when eaten.
 */
public class EdibleChoiceItem extends Item {
    public EdibleChoiceItem(Properties properties) {
        super(properties
                .food(Foods.CHORUS_FRUIT)
                .component(DataComponents.CONSUMABLE,
                        Consumable.builder()
                                .consumeSeconds(1F)
                                .animation(ItemUseAnimation.EAT)
                                .sound(SoundEvents.GENERIC_EAT)
                                .hasConsumeParticles(true)
                                .onConsume(EdibleChoiceItemConsumeEffect.INSTANCE)
                                .build()));
    }
}
