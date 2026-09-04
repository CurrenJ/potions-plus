package grill24.potionsplus.core.neoforge.items;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.item.WormrootItem;
import grill24.potionsplus.utility.registration.item.neoforge.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.neoforge.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BrewingItems {
    public static Holder<Item> LUNAR_BERRIES, MOSS, SALT, WORMROOT, ROTTEN_WORMROOT;

    /**
     * Force static fields to be initialized
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        LUNAR_BERRIES = RegistrationUtility.register(register, SimpleItemBuilder.create("lunar_berries")
                        .itemFactory(prop -> new ItemNameBlockItem(
                                FlowerBlocks.LUNAR_BERRY_BUSH.value(), prop.food(Foods.SWEET_BERRIES)))).getHolder();
        MOSS = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("moss")).getHolder();
        SALT = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("salt")).getHolder();
        WORMROOT = RegistrationUtility.register(register, SimpleItemBuilder.create("wormroot").itemFactory(WormrootItem::new)).getHolder();
        ROTTEN_WORMROOT = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("rotten_wormroot")).getHolder();

        // Populate common stubs
        grill24.potionsplus.core.items.BrewingItems.LUNAR_BERRIES = LUNAR_BERRIES;
        grill24.potionsplus.core.items.BrewingItems.MOSS = MOSS;
        grill24.potionsplus.core.items.BrewingItems.SALT = SALT;
        grill24.potionsplus.core.items.BrewingItems.WORMROOT = WORMROOT;
        grill24.potionsplus.core.items.BrewingItems.ROTTEN_WORMROOT = ROTTEN_WORMROOT;
    }
}
