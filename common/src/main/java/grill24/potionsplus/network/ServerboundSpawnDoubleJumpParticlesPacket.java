package grill24.potionsplus.network;

import grill24.potionsplus.core.Particles;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;


import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundSpawnDoubleJumpParticlesPacket(Vec3 pos) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ServerboundSpawnDoubleJumpParticlesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Vec3.CODEC),
            ServerboundSpawnDoubleJumpParticlesPacket::pos,
            ServerboundSpawnDoubleJumpParticlesPacket::new
    );

    public static final Type<ServerboundSpawnDoubleJumpParticlesPacket> TYPE = new Type<>(ppId("spawn_dj_particles"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundSpawnDoubleJumpParticlesPacket packet, final PacketContext context) {
            context.enqueueWork(() -> {
                ServerPlayer serverPlayer = (ServerPlayer) context.player();
                serverPlayer.serverLevel().sendParticles(
                        Particles.END_ROD_RAIN.get(),
                        serverPlayer.position().x,
                        serverPlayer.position().y,
                        serverPlayer.position().z,
                        10,
                        0.25,
                        0.05,
                        0.25,
                        0.1
                );
            });
        }
    }
}
