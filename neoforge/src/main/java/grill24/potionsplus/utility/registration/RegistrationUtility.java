package grill24.potionsplus.utility.registration;

import grill24.potionsplus.data.BlockStateProvider;
import grill24.potionsplus.event.resources.ClientModifyFileResourceStackEvent;
import grill24.potionsplus.event.resources.ClientModifyFileResourcesEvent;
import grill24.potionsplus.utility.Utility;
import grill24.potionsplus.utility.registration.item.ItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

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
    public static List<IRuntimeModelGenerator<?>> RUNTIME_RESOURCE_GENERATORS;

    /**
     * Registers an {@link ItemBuilder} and a corresponding {@link IModelGenerator}.
     * This generates the Item instance, registers it and stores the holder.
     * The model generator (if present) is registered and called when data generation is run.
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
        if (RUNTIME_RESOURCE_GENERATORS == null) {
            RUNTIME_RESOURCE_GENERATORS = new ArrayList<>();
        }

        builder.register(register);
        BUILDERS.add(builder);

        if(builder.hasModelGenerator()) {
            ITEM_MODEL_GENERATORS.add(builder);
        }
        if(builder.hasRecipeGenerator()) {
            RECIPE_GENERATORS.add(builder);
        }
        if(builder.hasLootGenerator()) {
            LOOT_GENERATORS.add(builder);
        }
        if(builder.hasRuntimeModelGenerator()) {
            RUNTIME_RESOURCE_GENERATORS.add(builder);
        }
        return builder;
    }

    public static void generateItemModels(String namespace, BlockStateProvider provider) {
        for (IModelGenerator<?> generator : ITEM_MODEL_GENERATORS) {
            Optional<ResourceLocation> itemId = Utility.getResourceLocation(generator.getHolder());
            if (itemId.isPresent() && itemId.get().getNamespace().equals(namespace)) {
                generator.generate(provider);
            }
        }
    }

    public static void generateRecipes(String namespace, RecipeProvider provider, RecipeOutput recipeOutput) {
        for (IRecipeGenerator<?> generator : RECIPE_GENERATORS) {
            Optional<ResourceLocation> itemId = Utility.getResourceLocation(generator.getHolder());
            if (itemId.isPresent() && itemId.get().getNamespace().equals(namespace)) {
                generator.generate(provider, recipeOutput);
            }
        }
    }

    public static void generateLootTables(String namespace, LootContextParamSet paramSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        for (ILootGenerator<?> generator : LOOT_GENERATORS) {
            Optional<ResourceLocation> itemId = Utility.getResourceLocation(generator.getHolder());
            if (itemId.isPresent() && itemId.get().getNamespace().equals(namespace)) {
                generator.tryGenerate(paramSet, provider, consumer);
            }
        }
    }

    public static void modifyRuntimeResources(final ClientModifyFileResourcesEvent event) {
        for (IRuntimeModelGenerator<?> generator : RUNTIME_RESOURCE_GENERATORS) {
            generator.generate(event);
        }
    }

    public static void modifyRuntimeResources(final ClientModifyFileResourceStackEvent event) {
        for (IRuntimeModelGenerator<?> generator : RUNTIME_RESOURCE_GENERATORS) {
            generator.generate(event);
        }
    }
}
