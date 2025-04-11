package grill24.potionsplus.core;

import grill24.potionsplus.core.items.*;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ModInfo.MOD_ID);

    static {
        FishItems.init(ITEMS::register);
        HatItems.init(ITEMS::register);
        SkillLootItems.init(ITEMS::register);
        OreItems.init(ITEMS::register);
        BrewingItems.init(ITEMS::register);
        FilterHopperUpgradeItems.init(ITEMS::register);
        DynamicIconItems.init(ITEMS::register);
    }

    public static Item.Properties properties() {
        return new Item.Properties();
    }

    /**
     * Register a block item and DON'T generate a model for it. Assumes we generate it elsewhere or have manually created it.
     *
     * @param block
     * @param registerItem
     */
    public static void registerBlockItem(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block);
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple(name)
                    .itemFactory(prop -> new BlockItem(block.value(), prop))
                    .modelGenerator(null));
        }
    }

    /**
     * Register a block item and generate an item model using a sprite texture.
     *
     * @param block
     * @param registerItem
     * @param texture
     */
    public static void registerBlockItemWithTexture(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, ResourceLocation texture) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block);
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple(name)
                    .itemFactory(prop -> new BlockItem(block.value(), prop))
                    .modelGenerator(holder -> new ItemModelUtility.SimpleItemModelGenerator<>(holder, texture)));
        }
    }

    /**
     * Register a block item and generate an item model that is the same as the block model.
     * @param block
     * @param registerItem
     */
    public static void registerBlockItemWithAutoModel(Supplier<Holder<Block>> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block.get());
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple(name)
                    .itemFactory(prop -> new BlockItem(block.get().value(), prop))
                    .modelGenerator((holder) -> new ItemModelUtility.SimpleBlockItemModelGenerator<>(holder, block)));
        }
    }

    public static void registerBlockItemWithParentModel(Supplier<Holder<Block>> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, ResourceLocation parent) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block.get());
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource targetLocation for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple(name)
                    .itemFactory(prop -> new BlockItem(block.get().value(), prop))
                    .modelGenerator((holder) -> new ItemModelUtility.SimpleBlockItemModelGenerator<>(holder, block, parent)));
        }
    }
}
