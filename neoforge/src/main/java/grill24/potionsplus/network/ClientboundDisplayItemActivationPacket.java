package grill24.potionsplus.network;

import grill24.potionsplus.render.animation.FadeInRotateItemActivationAnimation;
import grill24.potionsplus.render.IGameRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayItemActivationPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ClientboundDisplayItemActivationPacket> TYPE = new Type<>(ppId("display_item_activation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayItemActivationPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ClientboundDisplayItemActivationPacket::stack,
            ClientboundDisplayItemActivationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundDisplayItemActivationPacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if(packet.stack != null) {
                            // Display the item activation
                            ((IGameRendererMixin) mc.gameRenderer).potions_plus$displayItemActivation(FadeInRotateItemActivationAnimation.defaultAnimation(packet.stack()));
                        }
                    }
            );
        }
    }
}
