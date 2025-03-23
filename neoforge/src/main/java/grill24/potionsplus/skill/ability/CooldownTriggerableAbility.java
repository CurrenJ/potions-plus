package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.*;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.CooldownAbilityInstanceData;
import grill24.potionsplus.utility.DelayedEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

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
                new CooldownAbilityInstanceData(ability, true, 0, 0, 0));
    }

    public CooldownTriggerableAbility(Set<AbilityInstanceType<?>> types) {
        super(types);
    }

    protected abstract int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance);

    protected int getCooldownSeconds(AbilityInstanceSerializable<?, ?> instance) {
        return getCooldownDurationForAbility(instance) / 20;
    }

    protected Component getCooldownComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY, getCooldownSeconds(instance)).withStyle(ChatFormatting.GREEN);
    }

    abstract protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance);

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

                if (getCooldownDurationForAbility(instance) > 0) {
                    // Set timestamp after triggering ability
                    updateLastTriggeredTime(instance, timestamp);

                    // Notify client of cooldown
                    player.displayClientMessage(getCooldownComponent(instance), true);
                    // Schedule cooldown over notification
                    scheduleCooldownDoneNotification(getCooldownOverComponent(instance), player, instance);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Trigger the ability for the player, from the server side, and notify the client of the ability's activation.
     * Only to be called from the server side.
     *
     * @param player The player to trigger the ability for.
     * @param abilityResourceKey The resource key of the ability type to trigger.
     * @param eventData The event data to pass to the ability.
     */
    public boolean triggerFromServer(Player player, ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityResourceKey, E eventData) {
        Consumer<AbilityInstanceSerializable<?, ?>> runnable = (instance) -> {
            Optional<P> packet = onTriggeredFromServer(player, instance, eventData);
            if (player instanceof ServerPlayer serverPlayer) {
                packet.ifPresent(p -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, p));
            }
        };

        return doForAbility(player, abilityResourceKey, runnable, eventData);
    }

    /**
     * Trigger the ability for the player, from the client side, and notify the server of the ability's activation.
     * Only to be called from the client side.
     *
     * @param player The player to trigger the ability for.
     * @param abilityResourceKey  The resource key of the ability type to trigger.
     * @param eventData The event data to pass to the ability.
     */
    public boolean triggerFromClient(Player player, ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityResourceKey, E eventData) {
        Consumer<AbilityInstanceSerializable<?, ?>> runnable = (instance) -> {
            Optional<P> packet = onTriggeredFromClient(player, instance, eventData);
            if (player.isLocalPlayer()) {
                packet.ifPresent(PacketDistributor::sendToServer);
            }
        };

        return doForAbility(player, abilityResourceKey, runnable, eventData);
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
