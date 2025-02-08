package grill24.potionsplus.extension;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface IItemAttributeModifiersExtension {
    ItemAttributeModifiers potions_plus$withModifierRemoved(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot);
}
