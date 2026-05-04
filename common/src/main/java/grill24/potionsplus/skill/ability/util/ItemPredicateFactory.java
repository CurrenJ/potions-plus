package grill24.potionsplus.skill.ability.util;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface ItemPredicateFactory extends RegistryAccessibleFactory<ItemPredicate, Item> {
}
