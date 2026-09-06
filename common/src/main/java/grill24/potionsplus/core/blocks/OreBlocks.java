package grill24.potionsplus.core.blocks;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

/**
 * Loader-agnostic stub for the ore block statics. The neoforge registration logic lives in
 * {@code core.neoforge.blocks.OreBlocks}, which populates these fields at the end of its
 * {@code init}. See docs/multi-loader-expansion.md Phase 4.
 */
public class OreBlocks {
    public static Holder<Block> DENSE_DIAMOND_ORE, DEEPSLATE_DENSE_DIAMOND_ORE;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;
    public static Holder<Block> SULFURIC_NETHER_QUARTZ_ORE;
}
