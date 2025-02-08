package grill24.potionsplus.network;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.ServerLifecycleListeners;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.utility.DelayedServerEvents;
import grill24.potionsplus.utility.InvUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundEndFishingMinigame(Result result) implements CustomPacketPayload {
    public enum Result implements StringRepresentable {
        SUCCESS("success"),
        FAILURE("failure");

        public static final Codec<Result> CODEC = StringRepresentable.fromEnum(Result::values);
        public static final StreamCodec<ByteBuf, Result> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

        private final String name;

        Result(String s) {
            this.name = s;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
    public static final Type<ServerboundEndFishingMinigame> TYPE = new Type<>(ppId("end_fishing_minigame"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEndFishingMinigame> STREAM_CODEC = StreamCodec.composite(
            Result.STREAM_CODEC,
            ServerboundEndFishingMinigame::result,
            ServerboundEndFishingMinigame::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundEndFishingMinigame packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ServerPayloadContext serverContext = (ServerPayloadContext) context;

                if(serverContext.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasData(DataAttachments.FISHING_GAME_DATA)) {
                    FishingGamePlayerAttachment fishingGamePlayerAttachment = serverPlayer.getData(DataAttachments.FISHING_GAME_DATA);
                    if(packet.result == Result.SUCCESS) {
                        DelayedServerEvents.queueDelayedEvent(() -> InvUtil.giveOrDropItem(serverPlayer, fishingGamePlayerAttachment.fishReward()), 10);
                    }
                    serverPlayer.removeData(DataAttachments.FISHING_GAME_DATA);
                }
            });
        }
    }
}
