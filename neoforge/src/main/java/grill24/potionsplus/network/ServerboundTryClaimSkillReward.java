package grill24.potionsplus.network;

import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundTryClaimSkillReward(
        ResourceKey<ConfiguredGrantableReward<?, ?>> key) implements CustomPacketPayload {
    public static final Type<ServerboundTryClaimSkillReward> TYPE = new Type<>(ppId("claim_skill_reward"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTryClaimSkillReward> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD),
            (instance) -> instance.key,
            ServerboundTryClaimSkillReward::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundTryClaimSkillReward packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ServerPayloadContext serverContext = (ServerPayloadContext) context;

                if (serverContext.player() instanceof ServerPlayer serverPlayer) {
                    SkillsData.updatePlayerData(serverPlayer, data -> {
                        data.pendingRewards().giveConsumableRewardItem(serverPlayer, packet.key);
                    });
                }
            });
        }
    }
}
