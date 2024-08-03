package grill24.potionsplus.network;

import grill24.potionsplus.blockentity.ICraftingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundBlockEntityCraftRecipePacket(BlockPos pos, int slot) implements PotionsPlusPacket {
    public static final ResourceLocation ID = new ResourceLocation("potionsplus:block_entity_craft_recipe");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slot);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static ClientboundBlockEntityCraftRecipePacket decode(FriendlyByteBuf buf) {
        return new ClientboundBlockEntityCraftRecipePacket(buf.readBlockPos(), buf.readInt());
    }

    public static class Handler {
        public static void handle(ClientboundBlockEntityCraftRecipePacket packet) {
            // Lambda trips verifier on forge
            Minecraft.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.level == null) {
                                return;
                            }

                            if (mc.level.getBlockEntity(packet.pos) instanceof ICraftingBlockEntity blockEntity) {
                                blockEntity.craft(packet.slot);
                            }
                        }
                    }

            );
        }
    }
}

