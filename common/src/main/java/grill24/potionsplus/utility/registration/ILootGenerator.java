package grill24.potionsplus.utility.registration;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.function.BiConsumer;

public interface ILootGenerator<T> extends IDataGenerator<T> {
    void tryGenerate(LootContextParamSet lootContextParamSet, LootTableSubProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer);
    LootContextParamSet getParamSet();
}
