package grill24.potionsplus.network;

import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.WheelItemActivationAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayWheelAnimationPacket(List<ItemStack> stacks,
                                                     int winnerIndex) implements CustomPacketPayload {
    public static final Type<ClientboundDisplayWheelAnimationPacket> TYPE = new Type<>(ppId("display_wheel_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayWheelAnimationPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ClientboundDisplayWheelAnimationPacket::stacks,
            ByteBufCodecs.INT,
            ClientboundDisplayWheelAnimationPacket::winnerIndex,
            ClientboundDisplayWheelAnimationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundDisplayWheelAnimationPacket packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if (packet.stacks != null) {
                            // Display the item activation
                            ((IGameRendererMixin) mc.gameRenderer).potions_plus$displayItemActivation(WheelItemActivationAnimation.withWinner(packet.stacks(), packet.winnerIndex()));
                        }
                    }
            );
        }
    }
}
