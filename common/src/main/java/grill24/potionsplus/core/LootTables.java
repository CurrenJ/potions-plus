package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import static grill24.potionsplus.utility.Utility.ppId;

public class LootTables {
    public static final ResourceKey<LootTable> ALL_POTIONS = ResourceKey.create(Registries.LOOT_TABLE, ppId("all_potions"));

    public static final ResourceKey<LootTable> GEMS_AND_ORES_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("gems_and_ores"));
}
