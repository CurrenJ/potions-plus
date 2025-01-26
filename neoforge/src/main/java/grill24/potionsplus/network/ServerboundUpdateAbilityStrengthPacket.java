package grill24.potionsplus.network;

import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundUpdateAbilityStrengthPacket(ResourceLocation abilityId, float amount, int operation) implements CustomPacketPayload {
    public static class Operation {
        public static final int SET = 0;
        public static final int ADD = 1;
    }

    public static final Type<ServerboundUpdateAbilityStrengthPacket> TYPE = new Type<>(ppId("update_ability_strength"));

    public static final StreamCodec<ByteBuf, ServerboundUpdateAbilityStrengthPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ServerboundUpdateAbilityStrengthPacket::abilityId,
            ByteBufCodecs.FLOAT,
            ServerboundUpdateAbilityStrengthPacket::amount,
            ByteBufCodecs.INT,
            ServerboundUpdateAbilityStrengthPacket::operation,
            ServerboundUpdateAbilityStrengthPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundUpdateAbilityStrengthPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ServerPayloadContext serverContext = (ServerPayloadContext) context;
                ServerPlayer player = serverContext.player();
                SkillsData.getPlayerData(player).getAbilityInstance(player.registryAccess(), packet.abilityId).ifPresent(instance -> {
                    if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthInstance) {
                        player.sendSystemMessage(Component.literal("Updated type strength for " + packet.abilityId + " by " + packet.amount + " (op " + packet.operation + ")"));
                        switch (packet.operation) {
                            case Operation.SET -> adjustableStrengthInstance.setAbilityStrength(player, packet.amount);
                            case Operation.ADD -> adjustableStrengthInstance.setAbilityStrength(player, adjustableStrengthInstance.getAbilityStrength() + packet.amount);
                        }

                        PacketDistributor.sendToPlayer(player, new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player)));
                    }
                });
            });
        }


    }
}


