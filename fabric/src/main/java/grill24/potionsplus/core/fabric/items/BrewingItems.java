package grill24.potionsplus.core.fabric.items;

import grill24.potionsplus.core.fabric.blocks.FlowerBlocks;
import grill24.potionsplus.item.WormrootItem;
import net.minecraft.core.Holder;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BrewingItems {
    public static Holder<Item> LUNAR_BERRIES, MOSS, SALT, WORMROOT, ROTTEN_WORMROOT;

    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        // 1.21.1 ItemNameBlockItem takes a concrete Block, so LUNAR_BERRY_BUSH must already be
        // registered (and bound) - the fabric entrypoint runs Blocks.init() before Items.init().
        LUNAR_BERRIES = register.apply("lunar_berries",
                () -> new ItemNameBlockItem(FlowerBlocks.LUNAR_BERRY_BUSH.value(), new Item.Properties().food(Foods.SWEET_BERRIES)));
        MOSS = register.apply("moss", () -> new Item(new Item.Properties()));
        SALT = register.apply("salt", () -> new Item(new Item.Properties()));
        WORMROOT = register.apply("wormroot", () -> new WormrootItem(new Item.Properties()));
        ROTTEN_WORMROOT = register.apply("rotten_wormroot", () -> new Item(new Item.Properties()));

        // Populate common stubs
        grill24.potionsplus.core.items.BrewingItems.LUNAR_BERRIES = LUNAR_BERRIES;
        grill24.potionsplus.core.items.BrewingItems.MOSS = MOSS;
        grill24.potionsplus.core.items.BrewingItems.SALT = SALT;
        grill24.potionsplus.core.items.BrewingItems.WORMROOT = WORMROOT;
        grill24.potionsplus.core.items.BrewingItems.ROTTEN_WORMROOT = ROTTEN_WORMROOT;
    }
}
