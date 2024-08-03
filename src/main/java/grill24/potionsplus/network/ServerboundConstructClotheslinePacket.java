package grill24.potionsplus.network;

import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record ServerboundConstructClotheslinePacket(BlockPos pos, BlockPos otherPos) implements PotionsPlusPacket {
    public static final ResourceLocation ID = new ResourceLocation("potionsplus:construct_clothesline");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBlockPos(otherPos);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static ServerboundConstructClotheslinePacket decode(FriendlyByteBuf buf) {
        return new ServerboundConstructClotheslinePacket(buf.readBlockPos(), buf.readBlockPos());
    }

    public static class Handler {
        public static void handle(ServerboundConstructClotheslinePacket packet, MinecraftServer server, ServerPlayer player) {
            // Lambda trips verifier on forge
            server.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            Level level = player.level;

                            ClotheslineBehaviour.replaceWithClothelines(level, packet.pos, packet.otherPos);
                        }
                    }

            );
        }
    }
}

