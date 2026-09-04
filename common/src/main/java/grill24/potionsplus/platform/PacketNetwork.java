package grill24.potionsplus.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class PacketNetwork {
    @ExpectPlatform
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayers(ServerPlayer player, CustomPacketPayload first, CustomPacketPayload[] rest) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer player, CustomPacketPayload packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToServer(CustomPacketPayload packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload packet) {
        throw new AssertionError();
    }
}
