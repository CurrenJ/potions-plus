package grill24.potionsplus.core.fabric.items;

import grill24.potionsplus.core.fabric.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OreItems {
    public static Holder<Item> NETHERITE_REMNANT;
    public static Holder<Item> SULFUR_SHARD, SULFURIC_ACID;

    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        NETHERITE_REMNANT = register.apply("netherite_remnant",
                () -> new Item(Items.properties().fireResistant().rarity(Rarity.UNCOMMON)));
        SULFUR_SHARD = register.apply("sulfur_shard", () -> new Item(Items.properties()));
        SULFURIC_ACID = register.apply("sulfuric_acid", () -> new Item(Items.properties()));

        // Populate common stubs
        grill24.potionsplus.core.items.OreItems.NETHERITE_REMNANT = NETHERITE_REMNANT;
        grill24.potionsplus.core.items.OreItems.SULFUR_SHARD = SULFUR_SHARD;
        grill24.potionsplus.core.items.OreItems.SULFURIC_ACID = SULFURIC_ACID;
    }
}
