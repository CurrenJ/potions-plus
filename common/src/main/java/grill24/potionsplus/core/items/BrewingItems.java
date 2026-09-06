package grill24.potionsplus.core.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

/**
 * Loader-agnostic stub for the brewing item statics. The neoforge registration logic lives in
 * {@code core.neoforge.items.BrewingItems}, which populates these fields at the end of its
 * {@code init}. See docs/multi-loader-expansion.md Phase 4.
 */
public class BrewingItems {
    public static Holder<Item> LUNAR_BERRIES, MOSS, SALT, WORMROOT, ROTTEN_WORMROOT;
}
