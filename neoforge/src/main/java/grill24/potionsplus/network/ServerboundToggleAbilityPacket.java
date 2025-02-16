package grill24.potionsplus.network;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.utility.DelayedServerEvents;
import grill24.potionsplus.utility.InvUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundToggleAbilityPacket(ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredPlayerAbilityKey) implements CustomPacketPayload {
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
        public static void handleDataOnMain(ServerboundToggleAbilityPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ServerPayloadContext serverContext = (ServerPayloadContext) context;

                if(serverContext.player() instanceof ServerPlayer serverPlayer) {
                    SkillsData.updatePlayerData(serverPlayer, data -> {
                        data.getAbilityInstance(serverContext.player().registryAccess(), packet.configuredPlayerAbilityKey.location()).ifPresent(abilityInstance -> {
                            abilityInstance.toggle(serverPlayer);
                            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundSyncPlayerSkillData(data));
                        });
                    });
                }
            });
        }
    }

    public static ServerboundToggleAbilityPacket of(AbilityInstanceSerializable<?, ?> abilityInstance) {
        return new ServerboundToggleAbilityPacket(abilityInstance.data().getHolder().getKey());
    }
}
