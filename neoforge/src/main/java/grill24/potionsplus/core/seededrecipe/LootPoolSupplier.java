package grill24.potionsplus.core.seededrecipe;

import net.minecraft.world.level.storage.loot.LootPool;

@FunctionalInterface
public interface LootPoolSupplier {
    LootPool.Builder getLootPool();
}
