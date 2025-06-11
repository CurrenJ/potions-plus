package grill24.potionsplus.core.items;

import grill24.potionsplus.item.WormrootItem;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BrewingItems {
    public static Holder<Item> LUNAR_BERRIES, MOSS, SALT, WORMROOT, ROTTEN_WORMROOT;

    /**
     * Force static fields to be initialized
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        LUNAR_BERRIES = RegistrationUtility.register(register, SimpleItemBuilder.create("lunar_berries")
                        .itemFactory(prop -> new Item(prop.food(Foods.SWEET_BERRIES).useItemDescriptionPrefix()))).getHolder();
        MOSS = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("moss")).getHolder();
        SALT = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("salt")).getHolder();
        WORMROOT = RegistrationUtility.register(register, SimpleItemBuilder.create("wormroot").itemFactory(WormrootItem::new)).getHolder();
        ROTTEN_WORMROOT = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("rotten_wormroot")).getHolder();
    }
}
