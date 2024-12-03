package grill24.potionsplus.mixin;

import com.google.common.collect.ImmutableList;
import grill24.potionsplus.utility.IItemAttributeModifiersMixin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ItemAttributeModifiers.class)
public abstract class ItemAttributeModifiersMixin implements IItemAttributeModifiersMixin {
    @Shadow @Final private List<ItemAttributeModifiers.Entry> modifiers;

    @Shadow @Final private boolean showInTooltip;

    public ItemAttributeModifiers potions_plus$withModifierRemoved(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
        ImmutableList.Builder<ItemAttributeModifiers.Entry> builder = ImmutableList.builderWithExpectedSize(this.modifiers.size());

        for (ItemAttributeModifiers.Entry itemattributemodifiers$entry : this.modifiers) {
            if (!itemattributemodifiers$entry.matches(attribute, modifier.id())) {
                builder.add(itemattributemodifiers$entry);
            }
        }

        return new ItemAttributeModifiers(builder.build(), this.showInTooltip);
    }
}
