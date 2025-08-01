package grill24.potionsplus.network;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.persistence.SavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSyncPairedAbyssalTrove(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ClientboundSyncPairedAbyssalTrove> TYPE = new Type<>(ppId("sync_paired_abyssal_trove"));

    public static final StreamCodec<ByteBuf, ClientboundSyncPairedAbyssalTrove> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundSyncPairedAbyssalTrove::pos,
            ClientboundSyncPairedAbyssalTrove::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundSyncPairedAbyssalTrove packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Player clientPlayer = context.player();
                        SavedData.instance.getData(clientPlayer).pairAbyssalTroveAtPos(packet.pos());
                        PotionsPlus.LOGGER.info("Received paired Abyssal Trove position from server: {}", packet.pos());
                    }
            );
        }
    }
}
