package grill24.potionsplus.network;

import grill24.potionsplus.core.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SanguineAltarConversionProgressPacket(BlockPos pos, int healthDrained) implements PotionsPlusPacket {
    public static final ResourceLocation ID = new ResourceLocation("potionsplus:sanguine_altar_conversion_progress");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(healthDrained);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static SanguineAltarConversionProgressPacket decode(FriendlyByteBuf buf) {
        return new SanguineAltarConversionProgressPacket(buf.readBlockPos(), buf.readInt());
    }

    public static class Handler {
        public static void handle(SanguineAltarConversionProgressPacket packet) {
            // Lambda trips verifier on forge
            Minecraft.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.level == null) {
                                return;
                            }

                            mc.level.getBlockEntity(packet.pos, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get()).ifPresent(
                                    blockEntity -> {
                                        blockEntity.setHealthDrained(packet.healthDrained);
                                    }
                            );
                        }
                    }

            );
        }
    }
}
