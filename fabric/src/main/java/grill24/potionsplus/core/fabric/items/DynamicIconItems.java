package grill24.potionsplus.core.fabric.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Registers the two dynamic-icon items directly (no DSL - matching {@code BrewingItems}/
 * {@code OreItems}/{@code WreathItem}). Item-model datagen (per-icon predicate overrides) is
 * NeoForge-only (Decision 5); the generated models reach this jar via {@code commonDatagen}
 * (Phase 10), so only the {@code Item} instances themselves need to exist here.
 */
public class DynamicIconItems {
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        Holder<Item> potionEffectIcon = register.apply("potion_effect_icon", () -> new Item(new Item.Properties()));
        Holder<Item> genericIcon = register.apply("generic_icon", () -> new Item(new Item.Properties()));

        // Populate common stubs
        grill24.potionsplus.core.items.DynamicIconItems.POTION_EFFECT_ICON = potionEffectIcon;
        grill24.potionsplus.core.items.DynamicIconItems.GENERIC_ICON = genericIcon;
    }
}
