package grill24.potionsplus.behaviour;

import grill24.potionsplus.core.items.BrewingItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Loader-agnostic core of the wormroot global loot modifier: swaps a broken hanging-roots/rooted-dirt
 * drop for wormroot 1-in-4 times. Each loader's global-loot-modifier implementation wraps this.
 */
public class WormrootLootBehaviour {
    public static void apply(List<ItemStack> generatedLoot, RandomSource random, Block brokenBlock, List<Block> targetBlocks) {
        for (Block target : targetBlocks) {
            if (brokenBlock != target) {
                continue;
            }

            if (random.nextInt(4) == 0) {
                if (target == Blocks.ROOTED_DIRT) {
                    generatedLoot.add(new ItemStack(Items.DIRT, 1));
                }

                generatedLoot.removeIf(stack -> Block.byItem(stack.getItem()) == target);
                generatedLoot.add(new ItemStack(BrewingItems.WORMROOT.value(), 1));
            }

            break;
        }
    }
}
