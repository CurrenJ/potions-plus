package grill24.potionsplus.network;

import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundConstructClotheslinePacket(BlockPos pos, BlockPos otherPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundConstructClotheslinePacket> TYPE = new CustomPacketPayload.Type<>(ppId("construct_clothesline"));

    public static final StreamCodec<ByteBuf, ServerboundConstructClotheslinePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ServerboundConstructClotheslinePacket::pos,
            BlockPos.STREAM_CODEC,
            ServerboundConstructClotheslinePacket::otherPos,
            ServerboundConstructClotheslinePacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundConstructClotheslinePacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                            ServerPayloadContext serverContext = (ServerPayloadContext) context;

                            Level level = serverContext.player().level();

                            ClotheslineBehaviour.replaceWithClothelines(level, packet.pos, packet.otherPos);
                            CreatePotionsPlusBlockTrigger.INSTANCE.trigger(serverContext.player(), BlockEntityBlocks.CLOTHESLINE.value().defaultBlockState());
            }) .exceptionally(e -> {
                // Handle exception
                context.disconnect(Component.translatable("my_mod.configuration.failed", e.getMessage()));
                return null;
            });
        }


    }
}


