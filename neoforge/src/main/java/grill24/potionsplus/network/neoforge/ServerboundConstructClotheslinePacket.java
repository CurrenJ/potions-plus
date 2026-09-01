package grill24.potionsplus.network.neoforge;

import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.network.PacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

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
        public static void handleDataOnMain(ServerboundConstructClotheslinePacket packet, final PacketContext context) {
            context.enqueueWork(() -> {
                            ServerPlayer player = (ServerPlayer) context.player();

                            Level level = player.level();

                            ClotheslineBehaviour.replaceWithClothelines(level, packet.pos, packet.otherPos);
                            CreatePotionsPlusBlockTrigger.INSTANCE.trigger(player, BlockEntityBlocks.CLOTHESLINE.value().defaultBlockState());
            }) .exceptionally(e -> {
                // Handle exception
                context.disconnect(Component.translatable("my_mod.configuration.failed", e.getMessage()));
                return null;
            });
        }


    }
}


