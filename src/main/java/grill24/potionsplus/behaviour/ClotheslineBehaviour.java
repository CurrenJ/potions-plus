package grill24.potionsplus.behaviour;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.ClotheslinePart;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.network.PotionsPlusPacketHandler;
import grill24.potionsplus.network.ServerboundConstructClotheslinePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ClotheslineBehaviour {
    private static final Item INTERACTION_ITEM = Items.STRING;
    private static final Block INTERACTION_BLOCK = Blocks.OAK_FENCE;

    private static boolean firstBlockClicked = false;
    private static BlockPos firstBlockPos = BlockPos.ZERO;

    public static void doClotheslineInteractions(PlayerInteractEvent.RightClickBlock event) {

        BlockPos pos = event.getPos();
        Block block = event.getWorld().getBlockState(pos).getBlock();
        Item item = event.getItemStack().getItem();
        if (block == INTERACTION_BLOCK && item == INTERACTION_ITEM) {
            event.setCanceled(true);
            if(!event.getWorld().isClientSide)
                return;
            event.getPlayer().swing(event.getHand());

            if(!firstBlockClicked) {
                firstBlockPos = pos;
                firstBlockClicked = true;

                spawnParticles(event.getWorld(), firstBlockPos);
            } else {
                // Check if the second block is in the same line as the first block
                if (firstBlockPos.getX() == pos.getX() || firstBlockPos.getZ() == pos.getZ()) {
                    spawnParticles(event.getWorld(), pos);
                    firstBlockClicked = false;

                    PotionsPlusPacketHandler.CHANNEL.sendToServer(new ServerboundConstructClotheslinePacket(firstBlockPos, pos));
                } else {
                    firstBlockPos = pos;
                    spawnParticles(event.getWorld(), firstBlockPos);
                }
            }
        }
    }

    public static void replaceWithClothelines(Level level, BlockPos pos, BlockPos otherPos) {
        // Replace left and right blocks with clotheslines
        Direction direction = pos.getX() == otherPos.getX() ? Direction.EAST : Direction.NORTH;

        // Lower coordinates are always the left block
        int distance = direction == Direction.EAST ? Math.abs(pos.getZ() - otherPos.getZ()) : Math.abs(pos.getX() - otherPos.getX());
        if(distance < 2) return;
        ClotheslinePart part = direction == Direction.NORTH ?
                (pos.getX() < otherPos.getX() ? ClotheslinePart.LEFT : ClotheslinePart.RIGHT) :
                (pos.getZ() < otherPos.getZ() ? ClotheslinePart.LEFT : ClotheslinePart.RIGHT);
        level.setBlockAndUpdate(pos, grill24.potionsplus.core.Blocks.CLOTHESLINE.get().defaultBlockState().setValue(ClotheslineBlock.FACING, direction).setValue(ClotheslineBlock.PART, part).setValue(ClotheslineBlock.DISTANCE, distance));
        level.setBlockAndUpdate(otherPos, grill24.potionsplus.core.Blocks.CLOTHESLINE.get().defaultBlockState().setValue(ClotheslineBlock.FACING, direction).setValue(ClotheslineBlock.PART, part == ClotheslinePart.LEFT ? ClotheslinePart.RIGHT : ClotheslinePart.LEFT).setValue(ClotheslineBlock.DISTANCE, distance));
    }

    private static void spawnParticles(Level level, BlockPos blockPos) {
        Vec3 pos = Vec3.atCenterOf(blockPos);
        for (int i = 0; i < 5; i++) {
            level.addParticle(Particles.END_ROD_RAIN.get(),
                    pos.x + level.random.nextGaussian(0, 0.1),
                    pos.y + 0.5 + level.random.nextGaussian(0, 0.1),
                    pos.z + level.random.nextGaussian(0, 0.1),
                    0, 0, 0);
        }
    }
}
