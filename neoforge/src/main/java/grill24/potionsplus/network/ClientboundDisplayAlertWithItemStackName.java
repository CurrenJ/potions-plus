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
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayAlertWithItemStackName(String localizationKey, ItemStack param) implements CustomPacketPayload {
    public static final Type<ClientboundDisplayAlertWithItemStackName> TYPE = new Type<>(ppId("brewing_ingredient_knowledge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayAlertWithItemStackName> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ClientboundDisplayAlertWithItemStackName::localizationKey,
            ItemStack.STREAM_CODEC,
            ClientboundDisplayAlertWithItemStackName::param,
            ClientboundDisplayAlertWithItemStackName::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundDisplayAlertWithItemStackName packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        Player clientPlayer = context.player();
                        MutableComponent text = Component.translatable(packet.localizationKey, packet.param.getHoverName());
                        clientPlayer.displayClientMessage(text, true);
                        clientPlayer.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
                    }
            );
        }
    }
}
