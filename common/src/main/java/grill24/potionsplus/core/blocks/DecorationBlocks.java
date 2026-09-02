package grill24.potionsplus.core.blocks;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

/**
 * Loader-agnostic stub for the decoration block statics. The neoforge registration logic lives in
 * {@code core.neoforge.blocks.DecorationBlocks}, which populates these fields at the end of its
 * {@code init}. See docs/multi-loader-expansion.md Phase 4.
 */
public class DecorationBlocks {
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE, GROWING_MOSSY_STONE_BRICKS;
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE_SLAB, GROWING_MOSSY_COBBLESTONE_STAIRS;
    public static Holder<Block> GROWING_MOSSY_STONE_BRICK_SLAB, GROWING_MOSSY_STONE_BRICK_STAIRS;
}
