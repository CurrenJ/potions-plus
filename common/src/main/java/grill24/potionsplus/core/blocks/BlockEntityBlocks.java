package grill24.potionsplus.core.blocks;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

/**
 * Loader-agnostic stub for the block-entity block statics. The neoforge registration logic lives in
 * {@code core.neoforge.blocks.BlockEntityBlocks}, which populates these fields at the end of its
 * {@code init}. See docs/multi-loader-expansion.md Phase 4.
 */
public class BlockEntityBlocks {
    public static Holder<Block> BREWING_CAULDRON;
    public static Holder<Block> HERBALISTS_LECTERN;
    public static Holder<Block> SANGUINE_ALTAR;
    public static Holder<Block> ABYSSAL_TROVE;
    public static Holder<Block> PRECISION_DISPENSER;
    public static Holder<Block> CLOTHESLINE;
    public static Holder<Block> POTION_BEACON;
}
