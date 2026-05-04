package grill24.potionsplus.network;

import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import grill24.potionsplus.platform.PacketNetwork;
import net.minecraft.server.level.ServerPlayer;


import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundToggleAbilityPacket(
        ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredPlayerAbilityKey) implements CustomPacketPayload {
    public static final Type<ServerboundToggleAbilityPacket> TYPE = new Type<>(ppId("toggle_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundToggleAbilityPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY),
            (instance) -> instance.configuredPlayerAbilityKey,
            ServerboundToggleAbilityPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundToggleAbilityPacket packet, final PacketContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    SkillsData.updatePlayerData(serverPlayer, data -> {
                        data.getAbilityInstance(serverPlayer.registryAccess(), packet.configuredPlayerAbilityKey.identifier()).ifPresent(abilityInstance -> {
                            abilityInstance.toggle(serverPlayer);
                            PacketNetwork.sendToPlayer(serverPlayer, new ClientboundSyncPlayerSkillData(data));
                        });
                    });
                }
            });
        }
    }

    public static ServerboundToggleAbilityPacket of(AbilityInstanceSerializable<?, ?> abilityInstance) {
        return new ServerboundToggleAbilityPacket(abilityInstance.data().getHolder().unwrapKey().orElseThrow());
    }
}
