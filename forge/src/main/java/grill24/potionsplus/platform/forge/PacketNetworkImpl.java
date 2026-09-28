package grill24.potionsplus.platform.forge;

import grill24.potionsplus.core.forge.Packets;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.PacketDistributor;

public class PacketNetworkImpl {
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        Packets.CHANNEL.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToPlayers(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload... rest) {
        Packets.CHANNEL.send(first, PacketDistributor.PLAYER.with(player));
        for (CustomPacketPayload packet : rest) {
            Packets.CHANNEL.send(packet, PacketDistributor.PLAYER.with(player));
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, CustomPacketPayload packet) {
        Packets.CHANNEL.send(packet, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(player));
    }

    public static void sendToServer(CustomPacketPayload packet) {
        Packets.CHANNEL.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload packet) {
        Packets.CHANNEL.send(packet, PacketDistributor.TRACKING_CHUNK.with(level.getChunk(chunkPos.x(), chunkPos.z())));
    }
}
