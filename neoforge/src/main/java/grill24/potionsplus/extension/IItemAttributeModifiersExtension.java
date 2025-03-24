package grill24.potionsplus.extension;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Collection;

public interface IItemAttributeModifiersExtension {
    ItemAttributeModifiers potions_plus$withModifierRemoved(AttributeModifier modifierId);
    ItemAttributeModifiers potions_plus$withModifiersRemoved(Collection<AttributeModifier> modifierId);
}
