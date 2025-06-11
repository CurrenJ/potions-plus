package grill24.potionsplus.utility.registration;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public interface ILootGenerator<T> extends IDataGenerator<T> {
    void tryGenerate(ContextKeySet lootContextParamSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer);
    ContextKeySet getParamSet();
}
