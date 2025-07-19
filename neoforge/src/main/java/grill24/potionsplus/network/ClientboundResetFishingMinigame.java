package grill24.potionsplus.network;

import grill24.potionsplus.misc.LocalFishingGame;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundResetFishingMinigame() implements CustomPacketPayload {
    public static final Type<ClientboundResetFishingMinigame> TYPE = new Type<>(ppId("reset_fishing_minigame"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundResetFishingMinigame> STREAM_CODEC = StreamCodec.of(
            (instance, buf) -> new ClientboundResetFishingMinigame(),
            (buf) -> new ClientboundResetFishingMinigame()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundResetFishingMinigame packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null || mc.player == null) {
                            return;
                        }

                        // Display the item activation
                        LocalFishingGame.resetLocalGameData();
                    }
            );
        }
    }
}
