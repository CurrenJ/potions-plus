package grill24.potionsplus.core.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Common-visible registration hook for the (still fully NeoForge-side, Phase 4 bucket)
 * {@code core.potion.neoforge.Potions} hub - set once during that class's static init, so
 * {@link PotionBuilder} can register new potions without common depending on the DeferredRegister
 * itself. See docs/multi-loader-expansion.md Phase 4.
 */
public class Potions {
    public static BiFunction<String, Supplier<Potion>, Holder<Potion>> REGISTER;
}
