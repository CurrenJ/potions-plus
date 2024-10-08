package grill24.potionsplus.network;

import grill24.potionsplus.blockentity.ICraftingBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundBlockEntityCraftRecipePacket(BlockPos pos, int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundBlockEntityCraftRecipePacket> TYPE = new CustomPacketPayload.Type<>(ppId("pp_block_entity_craft"));

    public static final StreamCodec<ByteBuf, ClientboundBlockEntityCraftRecipePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundBlockEntityCraftRecipePacket::pos,
            ByteBufCodecs.INT,
            ClientboundBlockEntityCraftRecipePacket::slot,
            ClientboundBlockEntityCraftRecipePacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain ( final ClientboundBlockEntityCraftRecipePacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if (mc.level.getBlockEntity(packet.pos) instanceof ICraftingBlockEntity blockEntity) {
                            blockEntity.craft(packet.slot);
                        }
                    }
            );
        }
    }
}
