package grill24.potionsplus.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundBrewingIngredientKnowledgePacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ClientboundBrewingIngredientKnowledgePacket> TYPE = new Type<>(ppId("brewing_ingredient_knowledge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBrewingIngredientKnowledgePacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ClientboundBrewingIngredientKnowledgePacket::stack,
            ClientboundBrewingIngredientKnowledgePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundBrewingIngredientKnowledgePacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        Player clientPlayer = context.player();
                        MutableComponent text = Component.translatable("chat.potionsplus.acquired_ingredient_knowledge_" + mc.level.getRandom().nextInt(1, 4), packet.stack.getHoverName());
                        clientPlayer.displayClientMessage(text, true);
                        clientPlayer.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
                    }
            );
        }
    }
}
