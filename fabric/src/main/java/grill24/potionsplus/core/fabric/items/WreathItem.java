package grill24.potionsplus.core.fabric.items;

import grill24.potionsplus.core.ArmorMaterials;
import grill24.potionsplus.core.fabric.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class WreathItem {
    public static Holder<Item> WREATH;

    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        // ArmorItem's ctor eagerly derefs the armor material holder, so
        // core.ArmorMaterials.init() must have run (the entrypoint does this before Items.init()).
        WREATH = register.apply("wreath",
                () -> new ArmorItem(ArmorMaterials.WREATH, ArmorItem.Type.HELMET, Items.properties()));

        // Populate common stubs
        grill24.potionsplus.core.items.WreathItem.WREATH = WREATH;
    }
}
