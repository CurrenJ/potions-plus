package grill24.potionsplus.network;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.FishingLeaderboards;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSyncFishingLeaderboardsPacket(FishingLeaderboards fishingLeaderboards) implements CustomPacketPayload {
    public static final Type<ClientboundSyncFishingLeaderboardsPacket> TYPE = new Type<>(ppId("sync_fishing_leaderboards"));

    public static final StreamCodec<ByteBuf, ClientboundSyncFishingLeaderboardsPacket> STREAM_CODEC = StreamCodec.composite(
            FishingLeaderboards.STREAM_CODEC,
            ClientboundSyncFishingLeaderboardsPacket::fishingLeaderboards,
            ClientboundSyncFishingLeaderboardsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundSyncFishingLeaderboardsPacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        SavedData.instance.fishingLeaderboards = packet.fishingLeaderboards;
                        PotionsPlus.LOGGER.info("Received {} fishing leaderboards from server", packet.fishingLeaderboards.getFishingData().size());
                    }
            );
        }
    }

    public static ClientboundSyncFishingLeaderboardsPacket create() {
        return new ClientboundSyncFishingLeaderboardsPacket(SavedData.instance.fishingLeaderboards);
    }
}
