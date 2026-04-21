package grill24.potionsplus.utility.registration;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.data.RecipeProvider;
import grill24.potionsplus.event.runtimeresource.GenerateRuntimeResourceInjectionsCacheEvent;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractRegistererBuilder<T, B extends AbstractRegistererBuilder<T, B>> implements IRegisterer<T>, IModelGenerator<T>, IRecipeGenerator<T>, ILootGenerator<T>, IRuntimeModelGenerator<T> {
    protected Holder<T> holder;

    protected String name;
    protected Supplier<T> factory;

    protected IModelGenerator<T> modelGenerator;
    protected List<IRecipeGenerator<T>> recipeGenerators;
    protected ILootGenerator<T> lootGenerator;
    protected IRuntimeModelGenerator<T> runtimeModelGenerator;

    public Holder<T> getHolder() {
        return this.holder;
    }

    public T getValue() {
        return this.holder.value();
    }

    public B name(String name) {
        this.name = name;
        return self();
    }

    public B modelGenerator(Function<Supplier<Holder<T>>, IModelGenerator<T>> modelGenerator) {
        if (modelGenerator == null || FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            this.modelGenerator = null;
            return self();
        }

        this.modelGenerator = modelGenerator.apply(this::getHolder);
        return self();
    }

    public B recipeGenerator(Function<Supplier<Holder<T>>, IRecipeGenerator<T>> recipeGenerator) {
        // Clear all previous recipe generators if the new one is null
        if (recipeGenerator == null) {
            this.recipeGenerators = null;
            return self();
        }

        // Add the new recipe generator to the list
        if (this.recipeGenerators == null) {
            this.recipeGenerators = new ArrayList<>();
        }
        this.recipeGenerators.add(recipeGenerator.apply(this::getHolder));
        return self();
    }

    public B lootGenerator(Function<Supplier<Holder<T>>, ILootGenerator<T>> lootGenerator) {
        if (lootGenerator == null) {
            this.lootGenerator = null;
            return self();
        }

        this.lootGenerator = lootGenerator.apply(this::getHolder);
        return self();
    }

    public B runtimeModelGenerator(Function<Supplier<Holder<T>>, IRuntimeModelGenerator<T>> runtimeModelGenerator) {
        if (runtimeModelGenerator == null) {
            this.runtimeModelGenerator = null;
            return self();
        }

        this.runtimeModelGenerator = runtimeModelGenerator.apply(this::getHolder);
        return self();
    }

    public boolean hasRecipeGenerator() {
        return this.recipeGenerators != null;
    }

    public boolean hasModelGenerator() {
        return this.modelGenerator != null;
    }

    public boolean hasLootGenerator() {
        return this.lootGenerator != null;
    }

    public boolean hasRuntimeModelGenerator() {
        return this.runtimeModelGenerator != null;
    }

    public IModelGenerator<T> getModelGenerator() {
        return this.modelGenerator;
    }

    public List<IRecipeGenerator<T>> getRecipeGenerators() {
        return this.recipeGenerators;
    }

    public ILootGenerator<T> getLootGenerator() {
        return this.lootGenerator;
    }

    public IRuntimeModelGenerator<T> getRuntimeModelGenerator() {
        return this.runtimeModelGenerator;
    }

    @Override
    public void register(BiFunction<String, Supplier<T>, Holder<T>> register) {
        if (this.factory == null) {
            PotionsPlus.LOGGER.warn("Item factory must be set before registration. | " + name + " will not be registered an Item.");
            return;
        }

        if (this.name == null) {
            PotionsPlus.LOGGER.warn("Item name must be set before registration. | " + name + " will not be registered an Item.");
            return;
        }

        this.holder = register.apply(this.name, this.factory);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        if (this.modelGenerator != null) {
            this.modelGenerator.generate(blockModelGenerators, itemModelGenerators);
        } else {
            PotionsPlus.LOGGER.warn("Tried to generate model for " + this.name + " but no model generator was set.");
        }
    }

    @Override
    public void generate(RecipeProvider provider, RecipeOutput output) {
        if (this.recipeGenerators != null) {
            for (IRecipeGenerator<T> recipeGenerator : this.recipeGenerators) {
                recipeGenerator.generate(provider, output);
            }
        } else {
            PotionsPlus.LOGGER.warn("Tried to generate recipe for " + this.name + " but no recipe generator was set.");
        }
    }

    @Override
    public void tryGenerate(ContextKeySet paramSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        if (this.lootGenerator != null) {
            this.lootGenerator.tryGenerate(paramSet, provider, consumer);
        } else {
            PotionsPlus.LOGGER.warn("Tried to generate loot table for " + this.name + " but no loot generator was set.");
        }
    }

    @Override
    public ContextKeySet getParamSet() {
        if (this.lootGenerator != null) {
            return this.lootGenerator.getParamSet();
        }
        return LootContextParamSets.EMPTY;
    }

    @Override
    public void generateClient(final GenerateRuntimeResourceInjectionsCacheEvent event) {
        if (this.runtimeModelGenerator != null) {
            this.runtimeModelGenerator.generateClient(event);
        }
    }

    @Override
    public void generateCommon() {
        if (this.runtimeModelGenerator != null) {
            this.runtimeModelGenerator.generateCommon();
        }
    }

    protected abstract B self();
}
