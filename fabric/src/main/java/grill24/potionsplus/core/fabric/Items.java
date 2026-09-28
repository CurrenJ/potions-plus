package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.items.*;
import grill24.potionsplus.utility.Utility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Items {
    static {
        HatItems.init(Items::register);
        OreItems.init(Items::register);
        BrewingItems.init(Items::register);
        DynamicIconItems.init(Items::register);
    }

    public static <T extends Item> Holder<T> register(String name, Supplier<T> supplier) {
        return FabricRegistration.register(BuiltInRegistries.ITEM, name, supplier);
    }

    public static void init() {
        // No-op: forces class loading so the static initializer (above) runs.
    }

    public static Item.Properties properties() {
        return new Item.Properties();
    }

    /**
     * Register a block item and DON'T generate a model for it. Assumes we generate it elsewhere or have manually created it.
     */
    public static void registerBlockItem(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Optional<Identifier> id = Utility.getResourceLocation(block);
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple(name)
                    .itemFactory(prop -> new BlockItem(block.value(), prop.useBlockDescriptionPrefix()))
                    .modelGenerator(null));
        }
    }

    /**
     * Register a block item and generate an item model using a sprite texture.
     */
    public static void registerBlockItemWithTexture(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, Identifier texture) {
        Optional<Identifier> id = Utility.getResourceLocation(block);
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple(name)
                    .itemFactory(prop -> new BlockItem(block.value(), prop.useBlockDescriptionPrefix()))
                    .modelGenerator(holder -> new ItemModelUtility.SimpleItemModelGenerator<>(holder, texture)));
        }
    }
}
