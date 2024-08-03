package grill24.potionsplus.behaviour;

import grill24.potionsplus.core.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class MossBehaviour {
    public static void doMossInteractions(PlayerInteractEvent.RightClickBlock event, BlockPos pos) {
        tryShearMossyBlock(event, pos, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE);
        tryShearMossyBlock(event, pos, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB, 0.5F);
        tryShearMossyBlock(event, pos, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS);

        tryShearMossyBlock(event, pos, Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS);
        tryShearMossyBlock(event, pos, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
        tryShearMossyBlock(event, pos, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB, 0.5F);
        tryShearMossyBlock(event, pos, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS);

        tryMossifyBlock(event, pos, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
        tryMossifyBlock(event, pos, Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB);
        tryMossifyBlock(event, pos, Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS);

        tryMossifyBlock(event, pos, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        tryMossifyBlock(event, pos, Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
        tryMossifyBlock(event, pos, Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
        tryMossifyBlock(event, pos, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
    }

    private static void tryMossifyBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos, Block nonMossyBlock, Block mossyBlock) {
        if (event.getWorld().getBlockState(pos).is(nonMossyBlock)) {
            event.setCanceled(true);
            if (event.getItemStack().is(Items.MOSS.get())) {
                event.getWorld().setBlockAndUpdate(pos, mossyBlock.defaultBlockState());
                if (!event.getPlayer().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getPlayer().swing(event.getHand());
                event.getWorld().playSound(event.getPlayer(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GROWING_PLANT_CROP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
    }

    private static void tryShearMossyBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos, Block mossyBlock, Block nonMossyBlock, float dropChance) {
        if (event.getWorld().getBlockState(pos).is(mossyBlock)) {
            event.setCanceled(true);
            if (event.getItemStack().canPerformAction(net.minecraftforge.common.ToolActions.SHEARS_HARVEST)) {
                for (int i = 0; i < dropChance; i++) {
                    boolean dropped = false;
                    if (dropChance - i < 1) {
                        if (event.getWorld().getRandom().nextFloat() < dropChance - i) {
                            dropped = true;
                        }
                    } else {
                        dropped = true;
                    }

                    if (dropped) {
                        Block.popResource(event.getWorld(), pos, new ItemStack(Items.MOSS.get(), 1));
                        event.getItemStack().hurtAndBreak(1, event.getPlayer(), (p_49571_) -> p_49571_.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
                    }
                }
                event.getWorld().setBlockAndUpdate(pos, nonMossyBlock.defaultBlockState());

                event.getPlayer().swing(event.getHand());
                event.getWorld().playSound(event.getPlayer(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
    }

    public static void tryShearMossyBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos, Block mossyBlock, Block nonMossyBlock) {
        tryShearMossyBlock(event, pos, mossyBlock, nonMossyBlock, 1.0F);
    }
}
