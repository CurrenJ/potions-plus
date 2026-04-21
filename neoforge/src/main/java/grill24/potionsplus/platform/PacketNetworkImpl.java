package grill24.potionsplus.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class PacketNetworkImpl {
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToPlayers(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload[] rest) {
        CustomPacketPayload[] all = new CustomPacketPayload[1 + rest.length];
        all[0] = first;
        System.arraycopy(rest, 0, all, 1, rest.length);
        PacketDistributor.sendToPlayer(player, all);
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, packet);
    }
}
