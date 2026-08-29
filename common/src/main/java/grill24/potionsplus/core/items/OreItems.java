package grill24.potionsplus.core.items;


import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OreItems {
    public static Holder<Item> SULFUR_SHARD, SULFURIC_ACID;

    /**
     * Force static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        SULFUR_SHARD = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("sulfur_shard")).getHolder();
        SULFURIC_ACID = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("sulfuric_acid")).getHolder();
    }
}
