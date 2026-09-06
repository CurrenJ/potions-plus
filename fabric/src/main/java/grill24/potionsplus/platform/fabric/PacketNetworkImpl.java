package grill24.potionsplus.platform.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class PacketNetworkImpl {
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToPlayers(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload... rest) {
        ServerPlayNetworking.send(player, first);
        for (CustomPacketPayload packet : rest) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, CustomPacketPayload packet) {
        for (ServerPlayer tracking : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(tracking, packet);
        }
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload packet) {
        for (ServerPlayer player : PlayerLookup.tracking(level, chunkPos)) {
            ServerPlayNetworking.send(player, packet);
        }
    }
}
