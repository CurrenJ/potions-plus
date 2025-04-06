package grill24.potionsplus.network;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.event.PpFishCaughtEvent;
import grill24.potionsplus.item.FishingRodDataComponent;
import grill24.potionsplus.item.FishingRodItem;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.InvUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundEndFishingMinigame(Result result) implements CustomPacketPayload {
    public enum Result implements StringRepresentable {
        SUCCESS("success"),
        FAILURE("failure"),
        RESET("reset");

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
                endGame(serverContext.player(), packet.result());
            });
        }

        public static void endGame(ServerPlayer player, Result result) {
            if (result == Result.RESET) {
                PacketDistributor.sendToPlayer(player, new ClientboundResetFishingMinigame());
            }

            if (player.hasData(DataAttachments.FISHING_GAME_DATA)) {
                FishingGamePlayerAttachment fishingGamePlayerAttachment = player.getData(DataAttachments.FISHING_GAME_DATA);
                if (result == Result.SUCCESS) {
                    DelayedEvents.queueDelayedEvent(() -> {
                        NeoForge.EVENT_BUS.post(new PpFishCaughtEvent(fishingGamePlayerAttachment.fishReward(), player));
                        InvUtil.giveOrDropItem(player, fishingGamePlayerAttachment.fishReward().copy());
                    }, 10);
                }

                player.removeData(DataAttachments.FISHING_GAME_DATA);
                if (player.fishing != null) {
                    player.fishing.discard();
                }

                // Consume bait if any from the fishing rod
                if (result != Result.RESET) {
                    ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
                    if (heldItem.getItem() instanceof FishingRodItem) {
                        FishingRodDataComponent fishingRodData = heldItem.getOrDefault(DataComponents.FISHING_ROD, new FishingRodDataComponent());
                        fishingRodData.consumeBait();
                        heldItem.set(DataComponents.FISHING_ROD, fishingRodData);

                        player.inventoryMenu.sendAllDataToRemote();
                    }
                }
            }
        }
    }
}
