package grill24.potionsplus.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.extension.IItemAttributeModifiersExtension;
import grill24.potionsplus.skill.reward.OwnerDataComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record PlayerLockedItemModifiersDataComponent(List<AttributeModifier> attributeModifiers) {
    public static final Codec<PlayerLockedItemModifiersDataComponent> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    AttributeModifier.CODEC.listOf().optionalFieldOf("ids", List.of()).forGetter(PlayerLockedItemModifiersDataComponent::attributeModifiers)
            ).apply(codecBuilder, PlayerLockedItemModifiersDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, PlayerLockedItemModifiersDataComponent> STREAM_CODEC = StreamCodec.composite(
            AttributeModifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
            PlayerLockedItemModifiersDataComponent::attributeModifiers,
            PlayerLockedItemModifiersDataComponent::new
    );

    public static PlayerLockedItemModifiersDataComponent modifiers(Collection<AttributeModifier> itemAttributeModifiers) {
        return new PlayerLockedItemModifiersDataComponent(itemAttributeModifiers.stream().toList());
    }

    public static PlayerLockedItemModifiersDataComponent empty() {
        return modifiers(List.of());
    }

    public PlayerLockedItemModifiersDataComponent with(Collection<AttributeModifier> attributeModifiers) {
        List<AttributeModifier> existing = new ArrayList<>(this.attributeModifiers());
        existing.addAll(attributeModifiers);
        return new PlayerLockedItemModifiersDataComponent(existing);
    }

    // ----- Implementation Logic -----

    public static void updateStack(Player player, ItemStack stack) {
        OwnerDataComponent owner = stack.get(DataComponents.OWNER);
        PlayerLockedItemModifiersDataComponent playerLockedItemModifiers = stack.get(DataComponents.PLAYER_LOCKED_ITEM_MODIFIERS);

        if (playerLockedItemModifiers == null) {
            return;
        }

        if (owner == null || !owner.isOwner(player)) {
            PlayerLockedItemModifiersDataComponent.clearModifiers(player, stack);
        }
    }

    public static void addModifiers(Player player, ItemStack stack, Holder<Attribute> attributeHolder, Collection<AttributeModifier> modifiers) {
        // Add attribute modifier to player entity
        addPlayerModifiers(player, attributeHolder, modifiers);
        // Add attribute modifier to item stacks
        addItemModifiers(stack, attributeHolder, modifiers);

        // Update component on item stack
        PlayerLockedItemModifiersDataComponent comp = stack.getOrDefault(DataComponents.PLAYER_LOCKED_ITEM_MODIFIERS, PlayerLockedItemModifiersDataComponent.empty())
                .with(modifiers);
        stack.set(DataComponents.PLAYER_LOCKED_ITEM_MODIFIERS, comp);

        // Set owner data - we use this in updateStack()
        OwnerDataComponent.addOwnerToStack(player, stack);
    }

    public static void addPlayerModifiers(Player player, Holder<Attribute> attributeHolder, Collection<AttributeModifier> modifiers) {
        for (AttributeModifier modifier : modifiers) {
            AttributeInstance attributeInstance = player.getAttribute(attributeHolder);
            if (attributeInstance != null) {
                attributeInstance.addOrUpdateTransientModifier(modifier);
            }
        }
    }

    public static void addItemModifiers(ItemStack stack, Holder<Attribute> attributeHolder, Collection<AttributeModifier> modifiers) {
        for (AttributeModifier modifier : modifiers) {
            stack.update(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY,
                    (m) -> m.withModifierAdded(attributeHolder, modifier, EquipmentSlotGroup.MAINHAND));
        }
    }

    public static void clearModifiers(Player player, ItemStack stack) {
        PlayerLockedItemModifiersDataComponent playerLockedItemModifiers = stack.get(DataComponents.PLAYER_LOCKED_ITEM_MODIFIERS);
        if (playerLockedItemModifiers == null) {
            return;
        }

        playerLockedItemModifiers.clearPlayersModifiers(player);
        playerLockedItemModifiers.clearItemModifiers(stack);
        stack.remove(DataComponents.PLAYER_LOCKED_ITEM_MODIFIERS);
    }

    public void clearItemModifiers(ItemStack stack) {
        ItemAttributeModifiers existingModifiers = stack.get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS);
        if (existingModifiers == null) {
            return;
        }

        IItemAttributeModifiersExtension extension = (IItemAttributeModifiersExtension) (Object) existingModifiers;
        ItemAttributeModifiers updated = extension.potions_plus$withModifiersRemoved(attributeModifiers());
        stack.set(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, updated);

        if(stack.has(DataComponents.OWNER)) {
            stack.remove(DataComponents.OWNER);
        }
    }

    public void clearPlayersModifiers(Player player) {
        Set<ResourceLocation> ids = attributeModifiers().stream().map(AttributeModifier::id).collect(Collectors.toSet());

        for (AttributeInstance instance : player.getAttributes().getSyncableAttributes()) {
            for (AttributeModifier attributeModifier : instance.getModifiers()) {
                ResourceLocation id = attributeModifier.id();
                if (ids.contains(id)) {
                    instance.removeModifier(id);
                }
            }
        }
    }

}
