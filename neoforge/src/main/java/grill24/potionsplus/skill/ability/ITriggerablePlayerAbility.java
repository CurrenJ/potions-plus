package grill24.potionsplus.skill.ability;

import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
     * Trigger the ability for the player. Override this method to implement the ability's behavior if it is triggered by the server.
     * NOTE: Will not be called if the ability is triggered from the client. Instead, a packet will be sent to the client.
     * @return A packet to send to the client, or an empty optional if no packet should be sent.
     */
    Optional<P> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, E eventData);

    /**
     * Trigger the ability for the player. Override this method to implement the ability's behavior if it is triggered by the client.
     * NOTE: Will not be called if the ability is triggered from the server. Instead, a packet will be sent to the server.
     * @return A packet to send to the server, or an empty optional if no packet should be sent.
     */
    Optional<P> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, E eventData);
}
