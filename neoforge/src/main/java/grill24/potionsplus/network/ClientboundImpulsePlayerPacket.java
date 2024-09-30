package grill24.potionsplus.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundImpulsePlayerPacket(double dx, double dy, double dz) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundImpulsePlayerPacket> TYPE = new CustomPacketPayload.Type<>(ppId("impulse_player"));

    public static final StreamCodec<ByteBuf, ClientboundImpulsePlayerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            ClientboundImpulsePlayerPacket::dx,
            ByteBufCodecs.DOUBLE,
            ClientboundImpulsePlayerPacket::dy,
            ByteBufCodecs.DOUBLE,
            ClientboundImpulsePlayerPacket::dz,
            ClientboundImpulsePlayerPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundImpulsePlayerPacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        if (Minecraft.getInstance().player != null) {
                            Minecraft.getInstance().player.push(packet.dx, packet.dy, packet.dz);
                        }
                    });
        }
    }
}
