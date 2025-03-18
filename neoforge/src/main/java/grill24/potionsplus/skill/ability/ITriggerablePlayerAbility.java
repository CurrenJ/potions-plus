package grill24.potionsplus.skill.ability;

import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

/**
 * Represents a player ability that can be triggered by an event.
 * @param <E> The type of event data that triggers the ability. This can be any type.
 *            The event hook is not subscribed or registered inside this class, and should be done elsewhere.
 *           See {@link grill24.potionsplus.skill.ability.ChainLightningAbility} for an example.
 */
public interface ITriggerablePlayerAbility<E, P extends CustomPacketPayload> {
    /**
     * Trigger the ability for the player. Override this method to implement the ability's behavior.
     * @return True if the ability was triggered successfully, false otherwise.
     *         If true, the client will be notified of the ability's activation.
     */
    Optional<P> onTrigger(ServerPlayer player, AbilityInstanceSerializable<?, ?> instance, E eventData);

    /**
     * Trigger the ability for the player.
     * @param player The player to trigger the ability for.
     * @param abilityResourceKey The resource key of the ability type to trigger.
     * @param eventData The event data to pass to the ability.
     */
    default void trigger(ServerPlayer player, ResourceKey<PlayerAbility<?>> abilityResourceKey, E eventData) {
        SkillsData skillsData = SkillsData.getPlayerData(player);
        if (!player.isLocalPlayer()) {
            if (skillsData.unlockedAbilities().containsKey(abilityResourceKey)) {
                for (AbilityInstanceSerializable<?, ?> instance : skillsData.unlockedAbilities().get(abilityResourceKey)) {
                    if (instance.data().isEnabled()) {
                        Optional<P> packet = onTrigger(player, instance, eventData);
                        packet.ifPresent(p -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, p));
                    }
                }
            }
        }
    }
}
