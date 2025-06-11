package grill24.potionsplus.utility.registration;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public abstract class LootGenerator<T> implements ILootGenerator<T> {
    private ContextKeySet paramSet;

    public LootGenerator(ContextKeySet paramSet) {
        this.paramSet = paramSet;
    }

    @Override
    public ContextKeySet getParamSet() {
        return this.paramSet;
    }

    @Override
    public final void tryGenerate(ContextKeySet paramSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        if (this.paramSet.equals(paramSet)) {
            generate(provider, consumer);
        }
    }

    protected abstract void generate(LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer);
}
