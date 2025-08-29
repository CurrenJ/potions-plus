package grill24.potionsplus.behaviour;

import grill24.potionsplus.block.GrowableMossyBlock;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.items.BrewingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

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

        // Handle bone meal to create growable mossy blocks
        tryBoneMealStoneBlock(event, pos);
    }

    private static void tryBoneMealStoneBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos) {
        if (event.getItemStack().is(Items.BONE_MEAL)) {
            // Check for cobblestone to growing mossy cobblestone conversion
            if (GrowableMossyBlock.shouldConvertStoneBlock(event.getLevel(), pos, Blocks.COBBLESTONE, DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value().defaultBlockState());
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return;
            }

            // Check for cobblestone slab to growing mossy cobblestone slab conversion
            if (GrowableMossyBlock.shouldConvertStoneBlock(event.getLevel(), pos, Blocks.COBBLESTONE_SLAB, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB.value().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.SlabBlock.TYPE))
                        .setValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED)));
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return;
            }

            // Check for cobblestone stairs to growing mossy cobblestone stairs conversion
            if (GrowableMossyBlock.shouldConvertStoneBlock(event.getLevel(), pos, Blocks.COBBLESTONE_STAIRS, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS.value().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.FACING))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.HALF))
                        .setValue(net.minecraft.world.level.block.StairBlock.SHAPE, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.SHAPE))
                        .setValue(net.minecraft.world.level.block.StairBlock.WATERLOGGED, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.WATERLOGGED)));
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return;
            }
            
            // Check for stone bricks to growing mossy stone bricks conversion
            if (GrowableMossyBlock.shouldConvertStoneBlock(event.getLevel(), pos, Blocks.STONE_BRICKS, DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value().defaultBlockState());
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return;
            }

            // Check for stone brick slab to growing mossy stone brick slab conversion
            if (GrowableMossyBlock.shouldConvertStoneBlock(event.getLevel(), pos, Blocks.STONE_BRICK_SLAB, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB.value().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.SlabBlock.TYPE))
                        .setValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED)));
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return;
            }

            // Check for stone brick stairs to growing mossy stone brick stairs conversion
            if (GrowableMossyBlock.shouldConvertStoneBlock(event.getLevel(), pos, Blocks.STONE_BRICK_STAIRS, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS.value().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.StairBlock.FACING, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.FACING))
                        .setValue(net.minecraft.world.level.block.StairBlock.HALF, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.HALF))
                        .setValue(net.minecraft.world.level.block.StairBlock.SHAPE, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.SHAPE))
                        .setValue(net.minecraft.world.level.block.StairBlock.WATERLOGGED, event.getLevel().getBlockState(pos).getValue(net.minecraft.world.level.block.StairBlock.WATERLOGGED)));
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
    }

    private static void tryMossifyBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos, Block nonMossyBlock, Block mossyBlock) {
        if (event.getLevel().getBlockState(pos).is(nonMossyBlock)) {
            if (event.getItemStack().is(BrewingItems.MOSS.value())) {
                event.setCanceled(true);
                event.getLevel().setBlockAndUpdate(pos, mossyBlock.defaultBlockState());
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GROWING_PLANT_CROP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
    }

    private static void tryShearMossyBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos, Block mossyBlock, Block nonMossyBlock, float dropChance) {
        if (event.getLevel().getBlockState(pos).is(mossyBlock)) {
            if (event.getItemStack().canPerformAction(ItemAbilities.SHEARS_HARVEST)) {
                event.setCanceled(true);
                for (int i = 0; i < dropChance; i++) {
                    boolean dropped = false;
                    if (dropChance - i < 1) {
                        if (event.getLevel().getRandom().nextFloat() < dropChance - i) {
                            dropped = true;
                        }
                    } else {
                        dropped = true;
                    }

                    if (dropped) {
                        Block.popResource(event.getLevel(), pos, new ItemStack(BrewingItems.MOSS.value(), 1));
                        event.getItemStack().hurtAndBreak(1, event.getEntity(), event.getEntity().getEquipmentSlotForItem(event.getItemStack()));
                    }
                }
                event.getLevel().setBlockAndUpdate(pos, nonMossyBlock.defaultBlockState());

                event.getEntity().swing(event.getHand());
                event.getLevel().playSound(event.getEntity(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
    }

    public static void tryShearMossyBlock(PlayerInteractEvent.RightClickBlock event, BlockPos pos, Block mossyBlock, Block nonMossyBlock) {
        tryShearMossyBlock(event, pos, mossyBlock, nonMossyBlock, 1.0F);
    }
}
