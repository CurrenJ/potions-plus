package grill24.potionsplus.core.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

/**
 * Loader-agnostic stub for the ore item statics. The neoforge registration logic lives in
 * {@code core.neoforge.items.OreItems}, which populates these fields at the end of its
 * {@code init}. See docs/multi-loader-expansion.md Phase 4.
 */
public class OreItems {
    public static Holder<Item> NETHERITE_REMNANT;
    public static Holder<Item> SULFUR_SHARD, SULFURIC_ACID;
}
