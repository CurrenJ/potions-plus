package grill24.potionsplus.platform.neoforge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class PacketNetworkImpl {
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToPlayers(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload... rest) {
        PacketDistributor.sendToPlayer(player, first, rest);
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        // DIVERGENCE from 26.1.2: NeoForge 21.1.209 has a static PacketDistributor.sendToServer; the
        // client-side PacketDistributor split (ClientPacketDistributor) only exists in newer NeoForge.
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, packet);
    }
}
