package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.CooldownAbilityInstanceData;
import grill24.potionsplus.utility.DelayedEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public abstract class CooldownTriggerableAbility<E, P extends CustomPacketPayload> extends SimplePlayerAbility implements ITriggerablePlayerAbility<E, P> {
    public CooldownTriggerableAbility() {
        super(Set.of(AbilityInstanceTypes.COOLDOWN.get()));
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.COOLDOWN.value(),
                new CooldownAbilityInstanceData(ability, true, 0, 1, 0, 0));
    }

    public CooldownTriggerableAbility(Set<AbilityInstanceType<?>> types) {
        super(types);
    }

    protected abstract int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance);

    protected int getCooldownSeconds(AbilityInstanceSerializable<?, ?> instance) {
        return getCooldownDurationForAbility(instance) / 20;
    }

    protected Component getCooldownComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_ALERT, getCooldownSeconds(instance)).withStyle(ChatFormatting.GREEN);
    }

    abstract protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance);

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        List<List<Component>> components = new ArrayList<>();

        List<Component> abilityTag = List.of(getRichEnablementTooltipComponent(instance.data().isEnabled()), Component.literal(" "), Component.translatable(Translations.TOOLTIP_POTIONSPLUS_ABILITY_TAG));
        components.add(abilityTag);
        components.add(List.of());

        Optional<List<List<Component>>> component = super.getLongDescription(instance, config, params);
        component.ifPresent(components::addAll);

        int cooldownTicks = getCooldownDurationForAbility(instance);
        if (cooldownTicks > 0) {
            List<Component> row2 = List.of(
                    Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_INFO,
                                    String.valueOf(getCooldownDurationForAbility(instance) / 20))
                            .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)
            );

            // Padding row
            components.add(List.of());
            components.add(row2);
        }

        return !components.isEmpty() ? Optional.of(components) : Optional.empty();
    }

    protected boolean hasFinishedCooldown(AbilityInstanceSerializable<?, ?> instance, long timestamp) {
        if (instance.data() instanceof CooldownAbilityInstanceData cooldownAbilityInstanceData) {
            return cooldownAbilityInstanceData.getLastTriggeredTick() < timestamp - getCooldownDurationForAbility(instance);
        }

        PotionsPlus.LOGGER.error("CooldownTriggerableAbility::hasFinishedCooldown: instance data is not an instance of CooldownAbilityInstanceData");
        return false;
    }

    protected void updateLastTriggeredTime(AbilityInstanceSerializable<?, ?> instance, long timestamp) {
        if (instance.data() instanceof CooldownAbilityInstanceData cooldownAbilityInstanceData) {
            cooldownAbilityInstanceData.setLastTriggeredTick(timestamp);
        } else {
            PotionsPlus.LOGGER.error("CooldownTriggerableAbility::updateLastTriggeredTime: instance data is not an instance of CooldownAbilityInstanceData");
        }
    }

    protected void scheduleCooldownDoneNotification(Component component, Player player, AbilityInstanceSerializable<?, ?> instance) {
        DelayedEvents.queueDelayedEvent(() -> player.displayClientMessage(component, true), this.getCooldownDurationForAbility(instance));
    }

    public boolean doForAbility(Player player, ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityResourceKey, Consumer<AbilityInstanceSerializable<?, ?>> consumer, E eventData) {
        SkillsData skillsData = SkillsData.getPlayerData(player);
        long timestamp = player.level().getGameTime();

        Optional<AbilityInstanceSerializable<?, ?>> instanceOpt = skillsData.getAbilityInstance(player.registryAccess(), configuredAbilityResourceKey);
        if (instanceOpt.isPresent()) {
            AbilityInstanceSerializable<?, ?> instance = instanceOpt.get();
            if (instance.data().isEnabled() && hasFinishedCooldown(instance, timestamp)) {
                consumer.accept(instance);
                // Note: cooldown is now managed by the caller based on success/failure
                return true;
            }
        }

        return false;
    }

    /**
     * Trigger the ability for the player, from the server side, and notify the client of the ability's activation.
     * Only to be called from the server side.
     *
     * @param player             The player to trigger the ability for.
     * @param abilityResourceKey The resource key of the ability type to trigger.
     * @param eventData          The event data to pass to the ability.
     */
    public boolean triggerFromServer(Player player, ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityResourceKey, E eventData) {
        final boolean[] abilitySucceeded = {false};
        final AbilityInstanceSerializable<?, ?>[] instanceRef = {null};
        Consumer<AbilityInstanceSerializable<?, ?>> runnable = (instance) -> {
            instanceRef[0] = instance;
            Optional<P> packet = onTriggeredFromServer(player, instance, eventData);
            // If packet is null, it means the ability failed and we shouldn't activate cooldown
            if (packet != null) {
                abilitySucceeded[0] = true;
                if (player instanceof ServerPlayer serverPlayer) {
                    packet.ifPresent(p -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, p));
                }
            }
        };

        boolean wasExecuted = doForAbility(player, abilityResourceKey, runnable, eventData);
        
        // Only apply cooldown if ability was executed and succeeded
        if (wasExecuted && abilitySucceeded[0] && instanceRef[0] != null) {
            AbilityInstanceSerializable<?, ?> instance = instanceRef[0];
            long timestamp = player.level().getGameTime();
            
            if (getCooldownDurationForAbility(instance) > 0) {
                // Set timestamp after triggering ability
                updateLastTriggeredTime(instance, timestamp);

                // Notify client of cooldown
                player.displayClientMessage(getCooldownComponent(instance), true);
                // Schedule cooldown over notification
                scheduleCooldownDoneNotification(getCooldownOverComponent(instance), player, instance);
            }
        }
        
        return wasExecuted && abilitySucceeded[0];
    }

    /**
     * Trigger the ability for the player, from the client side, and notify the server of the ability's activation.
     * Only to be called from the client side.
     *
     * @param player             The player to trigger the ability for.
     * @param abilityResourceKey The resource key of the ability type to trigger.
     * @param eventData          The event data to pass to the ability.
     */
    public boolean triggerFromClient(Player player, ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityResourceKey, E eventData) {
        final boolean[] abilitySucceeded = {false};
        final AbilityInstanceSerializable<?, ?>[] instanceRef = {null};
        Consumer<AbilityInstanceSerializable<?, ?>> runnable = (instance) -> {
            instanceRef[0] = instance;
            Optional<P> packet = onTriggeredFromClient(player, instance, eventData);
            // If packet is null, it means the ability failed and we shouldn't activate cooldown
            if (packet != null) {
                abilitySucceeded[0] = true;
                if (player.isLocalPlayer()) {
                    packet.ifPresent(PacketDistributor::sendToServer);
                }
            }
        };

        boolean wasExecuted = doForAbility(player, abilityResourceKey, runnable, eventData);
        
        // Only apply cooldown if ability was executed and succeeded
        if (wasExecuted && abilitySucceeded[0] && instanceRef[0] != null) {
            AbilityInstanceSerializable<?, ?> instance = instanceRef[0];
            long timestamp = player.level().getGameTime();
            
            if (getCooldownDurationForAbility(instance) > 0) {
                // Set timestamp after triggering ability
                updateLastTriggeredTime(instance, timestamp);

                // Notify client of cooldown
                player.displayClientMessage(getCooldownComponent(instance), true);
                // Schedule cooldown over notification
                scheduleCooldownDoneNotification(getCooldownOverComponent(instance), player, instance);
            }
        }
        
        return wasExecuted && abilitySucceeded[0];
    }

    public static class Builder<A extends CooldownTriggerableAbility<?, ?>> extends CooldownTriggerableAbility.AbstractBuilder<PlayerAbilityConfiguration, A, Builder<A>> {
        public Builder(String key) {
            super(key);
        }

        @Override
        protected PlayerAbilityConfiguration buildConfig(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            return new PlayerAbilityConfiguration(buildBaseConfigurationData(context));
        }

        @Override
        public Builder<A> self() {
            return this;
        }
    }
}
