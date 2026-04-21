package grill24.potionsplus.behaviour;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.ClotheslinePart;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.network.ServerboundConstructClotheslinePacket;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import grill24.potionsplus.platform.PacketNetwork;


import java.util.Optional;

public class ClotheslineBehaviour {
    private static final Item INTERACTION_ITEM = Items.STRING;

    private static boolean firstBlockClicked = false;
    private static BlockPos firstBlockPos = BlockPos.ZERO;

    public static boolean doClotheslineInteractions(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand) {
        BlockState state = level.getBlockState(pos);
        Item item = itemStack.getItem();
        if ((state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS)) && item == INTERACTION_ITEM) {
            if (!level.isClientSide)
                return true;
            player.swing(hand);

            if (!firstBlockClicked) {
                firstBlockPos = pos;
                firstBlockClicked = true;

                spawnParticles(level, firstBlockPos);
            } else {
                // Check if the second block is in the same line as the first block
                if (firstBlockPos.getX() == pos.getX() || firstBlockPos.getZ() == pos.getZ()) {
                    spawnParticles(level, pos);
                    firstBlockClicked = false;

                    PacketNetwork.sendToServer(new ServerboundConstructClotheslinePacket(firstBlockPos, pos));
                } else {
                    firstBlockPos = pos;
                    spawnParticles(level, firstBlockPos);
                }
            }
            return true;
        }
        return false;
    }

    public static void replaceWithClothelines(ServerLevel level, BlockPos pos, BlockPos otherPos) {
        // Replace left and right blocks with clotheslines
        Direction direction = pos.getX() == otherPos.getX() ? Direction.EAST : Direction.NORTH;

        // Lower coordinates are always the left block
        int distance = direction == Direction.EAST ? Math.abs(pos.getZ() - otherPos.getZ()) : Math.abs(pos.getX() - otherPos.getX());
        if (distance < 2) return;
        ClotheslinePart part = direction == Direction.NORTH ?
                (pos.getX() < otherPos.getX() ? ClotheslinePart.LEFT : ClotheslinePart.RIGHT) :
                (pos.getZ() < otherPos.getZ() ? ClotheslinePart.LEFT : ClotheslinePart.RIGHT);

        ItemStack existingFencePostBlockAsItem = new ItemStack(level.getBlockState(pos).getBlock().asItem());

        level.setBlockAndUpdate(pos, BlockEntityBlocks.CLOTHESLINE.value().defaultBlockState().setValue(ClotheslineBlock.FACING, direction).setValue(ClotheslineBlock.PART, part).setValue(ClotheslineBlock.DISTANCE, distance));
        level.setBlockAndUpdate(otherPos, BlockEntityBlocks.CLOTHESLINE.value().defaultBlockState().setValue(ClotheslineBlock.FACING, direction).setValue(ClotheslineBlock.PART, part == ClotheslinePart.LEFT ? ClotheslinePart.RIGHT : ClotheslinePart.LEFT).setValue(ClotheslineBlock.DISTANCE, distance));

        if (!existingFencePostBlockAsItem.isEmpty()) {
            // Drop the existing block item
            Optional<ClotheslineBlockEntity> blockEntity = level.getBlockEntity(part == ClotheslinePart.LEFT ? pos : otherPos, Blocks.CLOTHESLINE_BLOCK_ENTITY.value());
            blockEntity.ifPresent(itemStacks -> itemStacks.setFencePostBlockItem(existingFencePostBlockAsItem));
        }
    }

    private static void spawnParticles(Level level, BlockPos blockPos) {
        Vec3 pos = Vec3.atCenterOf(blockPos);
        for (int i = 0; i < 5; i++) {
            level.addParticle(Particles.END_ROD_RAIN.get(),
                    pos.x + Utility.nextGaussian(0, 0.1, level.random),
                    pos.y + 0.5 + Utility.nextGaussian(0, 0.1, level.random),
                    pos.z + Utility.nextGaussian(0, 0.1, level.random),
                    0, 0, 0);
        }
    }
}
