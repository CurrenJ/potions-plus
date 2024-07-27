package grill24.potionsplus.network;

import grill24.potionsplus.core.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundClotheslineCraftPacket(BlockPos pos, int slot) implements PotionsPlusPacket {
    public static final ResourceLocation ID = new ResourceLocation("potionsplus:clothesline_craft");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slot);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static ClientboundClotheslineCraftPacket decode(FriendlyByteBuf buf) {
        return new ClientboundClotheslineCraftPacket(buf.readBlockPos(), buf.readInt());
    }

    public static class Handler {
        public static void handle(ClientboundClotheslineCraftPacket packet) {
            // Lambda trips verifier on forge
            Minecraft.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.level == null) {
                                return;
                            }

                            mc.level.getBlockEntity(packet.pos, Blocks.CLOTHESLINE_BLOCK_ENTITY.get()).ifPresent(
                                    blockEntity -> {
                                        blockEntity.craft(packet.slot);
                                    }
                            );
                        }
                    }

            );
        }
    }
}

