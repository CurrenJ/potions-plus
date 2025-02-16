package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.extension.IItemAttributeModifiersExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public class AttributeModifiersWhileHeldAbility<T extends Item> extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration> implements IAdjustableStrengthAbility<AttributeModifiersAbilityConfiguration> {
    private final Class<T> toolType;

    public AttributeModifiersWhileHeldAbility(Class<T> toolType) {
        super(AttributeModifiersAbilityConfiguration.CODEC);

        this.toolType = toolType;
    }

    public Class<T> getItemType() {
        return toolType;
    }

    public boolean isMatchingItemClass(ItemStack stack) {
        return getItemType().isAssignableFrom(stack.getItem().getClass());
    }

    public void enable(ServerPlayer player, AttributeModifiersAbilityConfiguration config, float strength) {
        for (AttributeModifier modifier : config.getModifiers()) {
            if (isMatchingItemClass(player.getMainHandItem())) {
                AttributeModifier modifierStrengthScaled = new AttributeModifier(modifier.id(), modifier.amount() * strength, modifier.operation());
                // Add attribute modifier to player entity
                player.getAttribute(config.getAttributeHolder()).addOrUpdateTransientModifier(modifierStrengthScaled);

                // Add attribute modifier to item stacks
                player.getMainHandItem().update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, (modifiers) -> modifiers.withModifierAdded(config.getAttributeHolder(), modifierStrengthScaled, EquipmentSlotGroup.MAINHAND));
            } else {
                disable(player, config);
            }
        }
    }

    public void disable(ServerPlayer player, AttributeModifiersAbilityConfiguration config) {
        for (AttributeModifier modifier : config.getModifiers()) {
            // Remove attribute modifier from player entity
            player.getAttribute(config.getAttributeHolder()).removeModifier(modifier);

            // Remove attribute modifier from item stacks
            ItemAttributeModifiers modifiers = player.getMainHandItem().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            modifiers = ((IItemAttributeModifiersExtension) (Object) modifiers).potions_plus$withModifierRemoved(config.getAttributeHolder(), modifier, EquipmentSlotGroup.MAINHAND);
            player.getMainHandItem().set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
        }
    }

    public static <T extends AttributeModifiersWhileHeldAbility<?>> void updatePlayerAttributeModifiers(ServerPlayer player, DeferredHolder<PlayerAbility<?>, T> abilityHolder) {
        SkillsData.updatePlayerData(player, (skillsData) -> {
            List<AbilityInstanceSerializable<?, ?>> configuredAbilities = skillsData.activeAbilities().get(abilityHolder.getKey());
            if (configuredAbilities == null) return;

            for (AbilityInstanceSerializable<?, ?> abilityInstance : configuredAbilities) {
                // Unchecked cast. We pray that the type is correctly linked in SkillsData to a key of the same type.
                ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T> configuredAbility = (ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T>) abilityInstance.data().getHolder().value();

                if (abilityInstance.data().isEnabled()) {
                    float strength = (abilityInstance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable) ? adjustable.getAbilityStrength() : 1F;
                    configuredAbility.ability().enable(player, configuredAbility.config(), strength);
                } else {
                    configuredAbility.ability().disable(player, configuredAbility.config());
                }
            }
        });
    }

    private Component getDescriptionWithStrength(AttributeModifiersAbilityConfiguration config, float strength) {
        if (config.getModifiers().size() == 1) {
            double amount = config.getModifiers().get(0).amount() * strength;
            AttributeModifier.Operation operation = config.getModifiers().get(0).operation();

            String param = "";
            // Format float to 2 decimal places
            String amountStr = String.format("%.2f", amount);
            switch (operation) {
                case ADD_VALUE -> param = "+" + amountStr;
                case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> param = "x" + (amountStr + 1);
            }

            return Component.translatable(config.getData().translationKey(), param);
        } else {
            return Component.translatable(config.getData().translationKey());
        }
    }

    @Override
    public Component getDescription(AttributeModifiersAbilityConfiguration config) {
        return getDescriptionWithStrength(config, 1F);
    }

    @Override
    public Component getDescription(AttributeModifiersAbilityConfiguration config, float strength) {
        return getDescriptionWithStrength(config, strength);
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value(),
                new AdjustableStrengthAbilityInstanceData(player, ability, true, 1F));
    }

    // ----- Helper Methods -----

    public static void onTick(final ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) {
            return;
        }

        ServerPlayer player = players.get(server.getTickCount() % players.size());
        updateAllToolBonusAbilities(player);
    }

    public static void updateAllToolBonusAbilities(ServerPlayer player) {
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_AXE_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_HOE_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_SHOVEL_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_BOW_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_CROSSBOW_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_TRIDENT_HELD);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_SHIELD_HELD);
    }
}