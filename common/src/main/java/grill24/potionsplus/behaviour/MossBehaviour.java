package grill24.potionsplus.behaviour;

import grill24.potionsplus.block.GrowableMossyBlock;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.items.BrewingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

/**
 * Loader-agnostic moss shear/mossify/bonemeal interactions. Callers pass the
 * live item stack from the player's hand and apply their own event-cancellation
 * based on the returned boolean.
 */
public class MossBehaviour {
    public static boolean doMossInteractions(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand) {
        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE)) return true;
        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB, 0.5F)) return true;
        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS)) return true;

        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS)) return true;
        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS)) return true;
        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB, 0.5F)) return true;
        if (tryShearMossyBlock(level, pos, itemStack, player, hand, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS)) return true;

        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE)) return true;
        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB)) return true;
        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS)) return true;

        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS)) return true;
        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB)) return true;
        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS)) return true;
        if (tryMossifyBlock(level, pos, itemStack, player, hand, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS)) return true;

        // Handle bone meal to create growable mossy blocks
        return tryBoneMealStoneBlock(level, pos, itemStack, player, hand);
    }

    private static boolean tryBoneMealStoneBlock(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand) {
        if (!itemStack.is(Items.BONE_MEAL)) {
            return false;
        }

        if (GrowableMossyBlock.shouldConvertStoneBlock(level, pos, Blocks.COBBLESTONE, DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value())) {
            level.setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value().defaultBlockState());
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BONE_MEAL_USE);
            return true;
        }

        if (GrowableMossyBlock.shouldConvertStoneBlock(level, pos, Blocks.COBBLESTONE_SLAB, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value())) {
            level.setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value().defaultBlockState()
                    .setValue(SlabBlock.TYPE, level.getBlockState(pos).getValue(SlabBlock.TYPE))
                    .setValue(SlabBlock.WATERLOGGED, level.getBlockState(pos).getValue(SlabBlock.WATERLOGGED)));
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BONE_MEAL_USE);
            return true;
        }

        if (GrowableMossyBlock.shouldConvertStoneBlock(level, pos, Blocks.COBBLESTONE_STAIRS, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value())) {
            level.setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value().defaultBlockState()
                    .setValue(StairBlock.FACING, level.getBlockState(pos).getValue(StairBlock.FACING))
                    .setValue(StairBlock.HALF, level.getBlockState(pos).getValue(StairBlock.HALF))
                    .setValue(StairBlock.SHAPE, level.getBlockState(pos).getValue(StairBlock.SHAPE))
                    .setValue(StairBlock.WATERLOGGED, level.getBlockState(pos).getValue(StairBlock.WATERLOGGED)));
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BONE_MEAL_USE);
            return true;
        }

        if (GrowableMossyBlock.shouldConvertStoneBlock(level, pos, Blocks.STONE_BRICKS, DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value())) {
            level.setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value().defaultBlockState());
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BONE_MEAL_USE);
            return true;
        }

        if (GrowableMossyBlock.shouldConvertStoneBlock(level, pos, Blocks.STONE_BRICK_SLAB, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value())) {
            level.setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value().defaultBlockState()
                    .setValue(SlabBlock.TYPE, level.getBlockState(pos).getValue(SlabBlock.TYPE))
                    .setValue(SlabBlock.WATERLOGGED, level.getBlockState(pos).getValue(SlabBlock.WATERLOGGED)));
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BONE_MEAL_USE);
            return true;
        }

        if (GrowableMossyBlock.shouldConvertStoneBlock(level, pos, Blocks.STONE_BRICK_STAIRS, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value())) {
            level.setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value().defaultBlockState()
                    .setValue(StairBlock.FACING, level.getBlockState(pos).getValue(StairBlock.FACING))
                    .setValue(StairBlock.HALF, level.getBlockState(pos).getValue(StairBlock.HALF))
                    .setValue(StairBlock.SHAPE, level.getBlockState(pos).getValue(StairBlock.SHAPE))
                    .setValue(StairBlock.WATERLOGGED, level.getBlockState(pos).getValue(StairBlock.WATERLOGGED)));
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BONE_MEAL_USE);
            return true;
        }

        return false;
    }

    private static boolean tryMossifyBlock(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand, Block nonMossyBlock, Block mossyBlock) {
        if (level.getBlockState(pos).is(nonMossyBlock) && itemStack.is(BrewingItems.MOSS.value())) {
            level.setBlockAndUpdate(pos, mossyBlock.defaultBlockState());
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.GROWING_PLANT_CROP);
            return true;
        }
        return false;
    }

    private static boolean tryShearMossyBlock(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand, Block mossyBlock, Block nonMossyBlock, float dropChance) {
        if (level.getBlockState(pos).is(mossyBlock) && itemStack.is(Items.SHEARS)) {
            for (int i = 0; i < dropChance; i++) {
                boolean dropped;
                if (dropChance - i < 1) {
                    dropped = level.getRandom().nextFloat() < dropChance - i;
                } else {
                    dropped = true;
                }

                if (dropped) {
                    Block.popResource(level, pos, new ItemStack(BrewingItems.MOSS.value(), 1));
                    itemStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(itemStack));
                }
            }
            level.setBlockAndUpdate(pos, nonMossyBlock.defaultBlockState());
            finishInteraction(level, pos, itemStack, player, hand, SoundEvents.BEEHIVE_SHEAR);
            return true;
        }
        return false;
    }

    public static boolean tryShearMossyBlock(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand, Block mossyBlock, Block nonMossyBlock) {
        return tryShearMossyBlock(level, pos, itemStack, player, hand, mossyBlock, nonMossyBlock, 1.0F);
    }

    private static void finishInteraction(Level level, BlockPos pos, ItemStack itemStack, Player player, InteractionHand hand, net.minecraft.sounds.SoundEvent sound) {
        if (!player.isCreative()) {
            itemStack.shrink(1);
        }
        player.swing(hand);
        level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
