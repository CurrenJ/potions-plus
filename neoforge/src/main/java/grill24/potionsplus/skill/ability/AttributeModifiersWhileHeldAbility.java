package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.IItemAttributeModifiersMixin;
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

public class AttributeModifiersWhileHeldAbility<T extends Item> extends PermanentAttributeModifiersAbility<T, AttributeModifiersAbilityConfiguration> {
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

    // ----- Helper Methods -----

    public static void onTick(final ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if(players.isEmpty()) {
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

    public void enable(ServerPlayer player, AttributeModifiersAbilityConfiguration config, ItemStack itemStack) {
        for (AttributeModifier modifier : config.getModifiers()) {
            if (isMatchingItemClass(itemStack)) {
                // Add attribute modifier to player entity
                player.getAttribute(config.getAttributeHolder()).addOrUpdateTransientModifier(modifier);

                // Add attribute modifier to item stack
                player.getMainHandItem().update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, (modifiers) -> modifiers.withModifierAdded(config.getAttributeHolder(), modifier, EquipmentSlotGroup.MAINHAND));
            } else {
                disable(player, config, itemStack);
            }
        }
    }

    public void disable(ServerPlayer player, AttributeModifiersAbilityConfiguration config, ItemStack itemStack) {
        for (AttributeModifier modifier : config.getModifiers()) {
            // Remove attribute modifier from player entity
            player.getAttribute(config.getAttributeHolder()).removeModifier(modifier);

            // Remove attribute modifier from item stack
            ItemAttributeModifiers modifiers = player.getMainHandItem().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            modifiers = ((IItemAttributeModifiersMixin) (Object) modifiers).potions_plus$withModifierRemoved(config.getAttributeHolder(), modifier, EquipmentSlotGroup.MAINHAND);
            player.getMainHandItem().set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
        }
    }

    @Override
    public void onAbilityGranted(ServerPlayer player, AttributeModifiersAbilityConfiguration config) {
        enable(player, config, player.getMainHandItem());
    }

    @Override
    public void onAbilityRevoked(ServerPlayer player, AttributeModifiersAbilityConfiguration config) {
        disable(player, config, player.getMainHandItem());
    }

    public static <T extends AttributeModifiersWhileHeldAbility<?>> void updatePlayerAttributeModifiers(ServerPlayer player, DeferredHolder<PlayerAbility<?, ?>, T> abilityHolder) {
        ItemStack stack = player.getMainHandItem();

        SkillsData.updatePlayerData(player, (skillsData) -> {
            List<AbilityInstance> configuredAbilities = skillsData.activeAbilities().get(abilityHolder.getKey());
            if (configuredAbilities == null) return;

            for (AbilityInstance abilityInstance : configuredAbilities) {
                // Unchecked cast. We pray that the ability is correctly linked in SkillsData to a key of the same type.
                ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T> configuredAbility = (ConfiguredPlayerAbility<AttributeModifiersAbilityConfiguration, T>) abilityInstance.getHolder().value();

                if(abilityInstance.isEnabled()) {
                    configuredAbility.ability().enable(player, configuredAbility.config(), stack);
                } else {
                    configuredAbility.ability().disable(player, configuredAbility.config(), stack);
                }
            }
        });
    }

    @Override
    public Component getDescription(AttributeModifiersAbilityConfiguration config) {
        if (config.getModifiers().size() == 1) {
            double amount = config.getModifiers().get(0).amount();
            AttributeModifier.Operation operation = config.getModifiers().get(0).operation();

            String param = "";
            switch (operation) {
                case ADD_VALUE -> param = "+" + amount;
                case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> param = "x" + (amount + 1);
            }

            return Component.translatable(config.getData().translationKey(), param);
        } else {
            return Component.translatable(config.getData().translationKey());
        }
    }
}
