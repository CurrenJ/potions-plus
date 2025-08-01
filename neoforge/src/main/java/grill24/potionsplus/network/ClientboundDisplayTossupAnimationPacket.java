package grill24.potionsplus.network;

import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.ItemTossupAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayTossupAnimationPacket(List<ItemStack> stacks, int ticksPerItem,
                                                      float timescale) implements CustomPacketPayload {
    public static final Type<ClientboundDisplayTossupAnimationPacket> TYPE = new Type<>(ppId("display_tossup_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayTossupAnimationPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ClientboundDisplayTossupAnimationPacket::stacks,
            ByteBufCodecs.INT,
            ClientboundDisplayTossupAnimationPacket::ticksPerItem,
            ByteBufCodecs.FLOAT,
            ClientboundDisplayTossupAnimationPacket::timescale,
            ClientboundDisplayTossupAnimationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundDisplayTossupAnimationPacket packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if (packet.stacks != null) {
                            // Display the item activation
                            ((IGameRendererMixin) mc.gameRenderer).potions_plus$displayItemActivation(
                                    ItemTossupAnimation.withItems(packet.stacks(), packet.ticksPerItem(), packet.timescale())
                            );
                        }
                    }
            );
        }
    }
}
