package grill24.potionsplus.utility;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface IItemAttributeModifiersMixin {
    ItemAttributeModifiers potions_plus$withModifierRemoved(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot);
}
