package grill24.potionsplus.utility.registration;

import grill24.potionsplus.data.RecipeProvider;
import grill24.potionsplus.utility.Utility;
import grill24.potionsplus.utility.registration.item.ItemBuilder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Utility class for registering items and associated data generators.
 * Allows us to register items and their data generators in a single place.
 */
public class RegistrationUtility {
    public static List<AbstractRegistererBuilder<?, ?>> BUILDERS;
    public static List<IModelGenerator<?>> ITEM_MODEL_GENERATORS;
    public static List<IRecipeGenerator<?>> RECIPE_GENERATORS;
    public static List<ILootGenerator<?>> LOOT_GENERATORS;

    /**
     * Registers an {@link ItemBuilder} and a corresponding {@link IModelGenerator}.
     * This generates the Item instance, registers it and stores the holder.
     * The model generator (if present) is registered and called when data generation is run.
     *
     * @param builder The ItemBuilder to register.
     * @return The registered ItemBuilder
     */
    public static <R, T extends R, B extends AbstractRegistererBuilder<T, B>> B register(BiFunction<String, Supplier<T>, Holder<T>> register, B builder) {
        if (BUILDERS == null) {
            BUILDERS = new ArrayList<>();
        }
        if (ITEM_MODEL_GENERATORS == null) {
            ITEM_MODEL_GENERATORS = new ArrayList<>();
        }
        if (RECIPE_GENERATORS == null) {
            RECIPE_GENERATORS = new ArrayList<>();
        }
        if (LOOT_GENERATORS == null) {
            LOOT_GENERATORS = new ArrayList<>();
        }

        builder.register(register);
        BUILDERS.add(builder);

        if (builder.hasModelGenerator()) {
            ITEM_MODEL_GENERATORS.add(builder);
        }
        if (builder.hasRecipeGenerator()) {
            RECIPE_GENERATORS.add(builder);
        }
        if (builder.hasLootGenerator()) {
            LOOT_GENERATORS.add(builder);
        }
        return builder;
    }

    public static void generateItemModels(String namespace, BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        for (IModelGenerator<?> generator : ITEM_MODEL_GENERATORS) {
            Optional<Identifier> itemId = Utility.getResourceLocation(generator.getHolder());
            if (itemId.isPresent() && itemId.get().getNamespace().equals(namespace)) {
                generator.generate(blockModelGenerators, itemModelGenerators);
            }
        }
    }

    public static void generateRecipes(String namespace, RecipeProvider provider) {
        for (IRecipeGenerator<?> generator : RECIPE_GENERATORS) {
            Optional<Identifier> itemId = Utility.getResourceLocation(generator.getHolder());
            if (itemId.isPresent() && itemId.get().getNamespace().equals(namespace)) {
                generator.generate(provider, provider.getOutput());
            }
        }
    }

    public static void generateLootTables(String namespace, ContextKeySet paramSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        for (ILootGenerator<?> generator : LOOT_GENERATORS) {
            Optional<Identifier> itemId = Utility.getResourceLocation(generator.getHolder());
            if (itemId.isPresent() && itemId.get().getNamespace().equals(namespace)) {
                generator.tryGenerate(paramSet, provider, consumer);
            }
        }
    }

    public static void registerBlockItem(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        register(registerItem, grill24.potionsplus.utility.registration.item.SimpleItemBuilder.createSimple(
                block.unwrapKey().orElseThrow().identifier().getPath())
                .itemFactory(prop -> new net.minecraft.world.item.BlockItem(block.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(null));
    }

    public static void registerBlockItemWithTexture(Holder<Block> block, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem, Identifier texture) {
        register(registerItem, grill24.potionsplus.utility.registration.item.SimpleItemBuilder.createSimple(
                block.unwrapKey().orElseThrow().identifier().getPath())
                .itemFactory(prop -> new net.minecraft.world.item.BlockItem(block.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(holder -> new grill24.potionsplus.utility.registration.item.ItemModelUtility.SimpleItemModelGenerator<>(holder, texture)));
    }

    public static void registerBlockItemWithAutoModel(Supplier<Holder<Block>> blockSupplier, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Holder<Block> block = blockSupplier.get();
        register(registerItem, grill24.potionsplus.utility.registration.item.SimpleItemBuilder.createSimple(
                block.unwrapKey().orElseThrow().identifier().getPath())
                .itemFactory(prop -> new net.minecraft.world.item.BlockItem(block.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(null));
    }
}
