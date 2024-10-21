package grill24.potionsplus.behaviour;

import grill24.potionsplus.block.OreFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class OreBehaviour {
//    public static void doOreInteractions(PlayerInteractEvent.RightClickBlock event, BlockPos pos) {
//        if(event.getItemStack().is(Items.BONE_MEAL)) {
//            // Get ore flower for block
//            grill24.potionsplus.core.Blocks.ORE_FLOWER_BLOCKS.forEach(
//                (block) -> {
//                    BlockState state = event.getLevel().getBlockState(pos);
//                    if (block.mayPlaceOn(state)) {
//                        // Check if bonemeal is successful
//                        if (event.getLevel().getRandom().nextFloat() < block.getGenerationChance()) {
//                            // Perform bonemeal
//                            block.performBonemeal(event.getLevel(), event.getLevel().getRandom(), pos, state);
//                            event.getLevel().setBlockAndUpdate(pos, block.defaultBlockState());
//                            // Consume bonemeal
//                            if (!event.getEntity().isCreative()) {
//                                event.getItemStack().shrink(1);
//                            }
//                            event.getEntity().swing(event.getHand());
//                        }
//                    }
//                }
//            );
//        }
//    }
}