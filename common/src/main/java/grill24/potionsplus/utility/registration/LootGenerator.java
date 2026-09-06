package grill24.potionsplus.utility.registration;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.function.BiConsumer;

public abstract class LootGenerator<T> implements ILootGenerator<T> {
    private LootContextParamSet paramSet;

    public LootGenerator(LootContextParamSet paramSet) {
        this.paramSet = paramSet;
    }

    @Override
    public LootContextParamSet getParamSet() {
        return this.paramSet;
    }

    @Override
    public final void tryGenerate(LootContextParamSet paramSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        if (this.paramSet.equals(paramSet)) {
            generate(provider, consumer);
        }
    }

    protected abstract void generate(LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer);
}
