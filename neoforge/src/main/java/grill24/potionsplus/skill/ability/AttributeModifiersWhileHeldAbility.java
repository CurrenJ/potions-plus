package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.extension.IItemAttributeModifiersExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
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

public class AttributeModifiersWhileHeldAbility<T extends Item> extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration> {
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
                AttributeModifier modifierWithStrength = new AttributeModifier(modifier.id(), strength, modifier.operation());
                // Add attribute modifier to player entity
                player.getAttribute(config.getAttributeHolder()).addOrUpdateTransientModifier(modifierWithStrength);

                // Add attribute modifier to item stacks
                player.getMainHandItem().update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, (modifiers) -> modifiers.withModifierAdded(config.getAttributeHolder(), modifierWithStrength, EquipmentSlotGroup.MAINHAND));
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

    private static <T extends AttributeModifiersWhileHeldAbility<?>> void updatePlayerAttributeModifiers(ServerPlayer player, DeferredHolder<PlayerAbility<?>, T> abilityHolder, boolean enable) {
        SkillsData.updatePlayerData(player, (skillsData) -> {
            List<AbilityInstanceSerializable<?, ?>> configuredAbilities = skillsData.activeAbilities().get(abilityHolder.getKey());
            if (configuredAbilities == null) return;

            for (AbilityInstanceSerializable<?, ?> abilityInstance : configuredAbilities) {
                // Unchecked cast. We pray that the type is correctly linked in SkillsData to a key of the same type.
                ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T> configuredAbility = (ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T>) abilityInstance.data().getHolder().value();

                if (enable && abilityInstance.data().isEnabled()) {
                    float strength = (abilityInstance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable) ? adjustable.getAbilityStrength() : 1F;
                    configuredAbility.ability().enable(player, configuredAbility.config(), strength);
                } else {
                    configuredAbility.ability().disable(player, configuredAbility.config());
                }
            }
        });
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value(),
                new AdjustableStrengthAbilityInstanceData(ability, true));
    }

    // ----- Helper Methods -----

    public static void onPreTick(final ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) {
            return;
        }

        ServerPlayer player = players.get(server.getTickCount() % players.size());
        updateAllToolBonusAbilities(player, true);
    }

    public static void onPostTick(final ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) {
            return;
        }

        ServerPlayer player = players.get(server.getTickCount() % players.size());
        updateAllToolBonusAbilities(player, false);
    }

    private static void updateAllToolBonusAbilities(ServerPlayer player, boolean enable) {
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_PICKAXE_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_AXE_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_HOE_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_SHOVEL_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_SWORD_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_BOW_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_CROSSBOW_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_TRIDENT_HELD, enable);
        updatePlayerAttributeModifiers(player, PlayerAbilities.MODIFIERS_WHILE_SHIELD_HELD, enable);
    }
}