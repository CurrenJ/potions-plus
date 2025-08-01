package grill24.potionsplus.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayAlert(Component component, Optional<SoundEvent> sound,
                                      float volume) implements CustomPacketPayload {
    public ClientboundDisplayAlert(Component component) {
        this(component, Optional.empty(), 1.0F);
    }

    public ClientboundDisplayAlert(Component component, SoundEvent sound) {
        this(component, Optional.of(sound), 1.0F);
    }

    public static final Type<ClientboundDisplayAlert> TYPE = new Type<>(ppId("display_alert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayAlert> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            ClientboundDisplayAlert::component,
            ByteBufCodecs.optional(SoundEvent.DIRECT_STREAM_CODEC),
            ClientboundDisplayAlert::sound,
            ByteBufCodecs.FLOAT,
            ClientboundDisplayAlert::volume,
            ClientboundDisplayAlert::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundDisplayAlert packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        Player clientPlayer = context.player();
                        clientPlayer.displayClientMessage(packet.component, true);
                        packet.sound.ifPresent(soundEvent -> clientPlayer.playSound(soundEvent, packet.volume, 1.0F));
                    }
            );
        }
    }
}
