package grill24.potionsplus.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayAlertWithParameter(String localizationKey, Boolean playSound,
                                                   List<String> params) implements CustomPacketPayload {
    public ClientboundDisplayAlertWithParameter(String localizationKey, String... param) {
        this(localizationKey, false, List.of(param));
    }

    public ClientboundDisplayAlertWithParameter(String localizationKey, Boolean playSound, String... param) {
        this(localizationKey, playSound, List.of(param));
    }

    public static final Type<ClientboundDisplayAlertWithParameter> TYPE = new Type<>(ppId("display_alert_with_param"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayAlertWithParameter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ClientboundDisplayAlertWithParameter::localizationKey,
            ByteBufCodecs.BOOL,
            ClientboundDisplayAlertWithParameter::playSound,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            ClientboundDisplayAlertWithParameter::params,
            ClientboundDisplayAlertWithParameter::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundDisplayAlertWithParameter packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        Player clientPlayer = context.player();
                        MutableComponent text;
                        if (packet.params.isEmpty()) {
                            text = Component.translatable(packet.localizationKey);
                        } else {
                            text = Component.translatable(packet.localizationKey, packet.params.toArray());
                        }
                        clientPlayer.displayClientMessage(text, true);
                        if (packet.playSound) {
                            clientPlayer.playSound(SoundEvents.PLAYER_LEVELUP, 0.75F, 1.0F);
                        }
                    }
            );
        }
    }
}
