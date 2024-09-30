package grill24.potionsplus.network;

import grill24.potionsplus.core.Blocks;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSanguineAltarConversionProgressPacket(BlockPos pos, int healthDrained) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundSanguineAltarConversionProgressPacket> TYPE = new CustomPacketPayload.Type<>(ppId("sanguine_altar_conversion_progress"));

    public static final StreamCodec<ByteBuf, ClientboundSanguineAltarConversionProgressPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundSanguineAltarConversionProgressPacket::pos,
            ByteBufCodecs.INT,
            ClientboundSanguineAltarConversionProgressPacket::healthDrained,
            ClientboundSanguineAltarConversionProgressPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain ( final ClientboundSanguineAltarConversionProgressPacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        mc.level.getBlockEntity(packet.pos, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get()).ifPresent(
                                blockEntity -> {
                                    blockEntity.setHealthDrained(packet.healthDrained);
                                }
                        );
                    }
            );
        }
    }
}
