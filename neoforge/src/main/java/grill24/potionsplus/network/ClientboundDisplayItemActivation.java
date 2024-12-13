package grill24.potionsplus.network;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.IGameRendererMixin;
import grill24.potionsplus.utility.ItemActivationAnimationEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundDisplayItemActivation(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ClientboundDisplayItemActivation> TYPE = new Type<>(ppId("display_item_activation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisplayItemActivation> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ClientboundDisplayItemActivation::stack,
            ClientboundDisplayItemActivation::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundDisplayItemActivation packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if(packet.stack != null) {
                            // Display the item activation
                            ((IGameRendererMixin) mc.gameRenderer).potions_plus$displayItemActivation(packet.stack, ItemActivationAnimationEnum.FADE_IN);
                        }
                    }
            );
        }
    }
}
