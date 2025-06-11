package grill24.potionsplus.mixin;

import com.google.common.collect.ImmutableList;
import grill24.potionsplus.extension.IItemAttributeModifiersExtension;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ItemAttributeModifiers.class)
public abstract class ItemAttributeModifiersMixin implements IItemAttributeModifiersExtension {
    @Shadow @Final private List<ItemAttributeModifiers.Entry> modifiers;

    public ItemAttributeModifiers potions_plus$withModifierRemoved(AttributeModifier modifier) {
        return potions_plus$withModifiersRemoved(List.of(modifier));
    }

    public ItemAttributeModifiers potions_plus$withModifiersRemoved(Collection<AttributeModifier> modifiersToRemove) {
        ImmutableList.Builder<ItemAttributeModifiers.Entry> builder = ImmutableList.builder();
        Set<ResourceLocation> ids = modifiersToRemove.stream().map(AttributeModifier::id).collect(Collectors.toSet());

        for (ItemAttributeModifiers.Entry entry : this.modifiers) {
            if (!ids.contains(entry.modifier().id())) {
                builder.add(entry);
            }
        }

        return new ItemAttributeModifiers(builder.build());
    }
}
