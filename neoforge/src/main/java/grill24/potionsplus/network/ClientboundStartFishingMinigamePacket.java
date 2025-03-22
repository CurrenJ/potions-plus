package grill24.potionsplus.network;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.misc.LocalFishingGame;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public class ClientboundStartFishingMinigamePacket implements CustomPacketPayload {
    public static final Type<ClientboundStartFishingMinigamePacket> TYPE = new Type<>(ppId("start_fishing_minigame"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStartFishingMinigamePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(FishingGamePlayerAttachment.CODEC),
            instance -> instance.attachmentData,
            ClientboundStartFishingMinigamePacket::new
   );

    private final FishingGamePlayerAttachment attachmentData;
    private ClientboundStartFishingMinigamePacket(FishingGamePlayerAttachment attachmentData) {
        this.attachmentData = attachmentData;
    }

    public static ClientboundStartFishingMinigamePacket create(ServerPlayer player, FishingGamePlayerAttachment attachmentData) {
        player.setData(DataAttachments.FISHING_GAME_DATA, attachmentData);
        return new ClientboundStartFishingMinigamePacket(attachmentData);
    }

                              @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundStartFishingMinigamePacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null || mc.player == null) {
                            return;
                        }

                        // Display the item activation
                        mc.player.setData(DataAttachments.FISHING_GAME_DATA, packet.attachmentData);
                        LocalFishingGame.newLocalGame();
                    }
            );
        }
    }
}
