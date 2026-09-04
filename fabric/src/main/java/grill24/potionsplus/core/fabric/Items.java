package grill24.potionsplus.core.fabric;

import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Items {
    static {
        grill24.potionsplus.core.fabric.items.OreItems.init(Items::register);
        grill24.potionsplus.core.fabric.items.BrewingItems.init(Items::register);
        grill24.potionsplus.core.fabric.items.WreathItem.init(Items::register);
        grill24.potionsplus.core.fabric.items.DynamicIconItems.init(Items::register);
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
     * Register a block item and DON'T generate a model for it. Assumes we generate it elsewhere or
     * have manually created it. (Model generation is datagen-only on 1.21.1; the modelGenerator
     * argument of the 26.1.2 mirror has no cross-loader equivalent here.)
     */
    public static void registerBlockItem(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block);
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            registerItem.apply(name, () -> new BlockItem(block.value(), properties()));
        }
    }

    /**
     * Register a block item and generate an item model using a sprite texture. The texture is used
     * only by the datagen model generator on 1.21.1; retained for signature parity with the
     * NeoForge/Forge hubs.
     */
    public static void registerBlockItemWithTexture(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, ResourceLocation texture) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block);
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            registerItem.apply(name, () -> new BlockItem(block.value(), properties()));
        }
    }

    /**
     * Register a block item whose item model should match the block model. No model is generated on
     * 1.21.1 Fabric (the item model JSON comes from shared/common assets, Phase 10).
     */
    public static void registerBlockItemWithAutoModel(Supplier<Holder<Block>> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block.get());
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block.get() + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            registerItem.apply(name, () -> new BlockItem(block.get().value(), properties()));
        }
    }

    /**
     * Register a block item whose item model should be based on a parent model. No model is
     * generated on 1.21.1 Fabric (the item model JSON comes from shared/common assets, Phase 10).
     */
    public static void registerBlockItemWithParentModel(Supplier<Holder<Block>> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, ResourceLocation parent) {
        Optional<ResourceLocation> id = Utility.getResourceLocation(block.get());
        if (id.isEmpty()) {
            throw new IllegalStateException("Couldn't get resource location for block: " + block.get() + "! Skipping item registration.");
        } else {
            String name = id.get().getPath();
            registerItem.apply(name, () -> new BlockItem(block.get().value(), properties()));
        }
    }
}
