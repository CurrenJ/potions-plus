package grill24.potionsplus.platform.forge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class PacketNetworkImpl {
    // PHASE 5 (networking): the SimpleChannel / payload channel is built in forge/core/forge/Packets.
    // Until then no payloads are registered on Forge, so these are no-ops.

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
    }

    public static void sendToPlayers(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload... rest) {
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, CustomPacketPayload packet) {
    }

    public static void sendToServer(CustomPacketPayload packet) {
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload packet) {
    }
}
