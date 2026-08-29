package grill24.potionsplus.core;

import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import static grill24.potionsplus.utility.Utility.ppId;

public class LootTables {
    public static final ResourceKey<LootTable> ARID_CAVE_SUSPICIOUS_SAND = ResourceKey.create(Registries.LOOT_TABLE, ppId("arid_cave_suspicious_sand"));
    public static final ResourceKey<LootTable> ALL_POTIONS = ResourceKey.create(Registries.LOOT_TABLE, ppId("all_potions"));

    public static final ResourceKey<LootTable>[] COPPER_ORE_HATS = Utility.enumerateLootTableKeys(ppId("copper_ore_hat"), HatItems.BLOCK_HAT_MODELS.length);
    public static final ResourceKey<LootTable>[] COAL_ORE_HATS = Utility.enumerateLootTableKeys(ppId("coal_ore_hat"), HatItems.BLOCK_HAT_MODELS.length);
    public static final ResourceKey<LootTable>[] IRON_ORE_HATS = Utility.enumerateLootTableKeys(ppId("iron_ore_hat"), HatItems.BLOCK_HAT_MODELS.length);
    public static final ResourceKey<LootTable>[] GOLD_ORE_HATS = Utility.enumerateLootTableKeys(ppId("gold_ore_hat"), HatItems.BLOCK_HAT_MODELS.length);
    public static final ResourceKey<LootTable>[] DIAMOND_ORE_HATS = Utility.enumerateLootTableKeys(ppId("diamond_ore_hat"), HatItems.BLOCK_HAT_MODELS.length);
    public static final ResourceKey<LootTable>[] EMERALD_ORE_HATS = Utility.enumerateLootTableKeys(ppId("emerald_ore_hat"), HatItems.BLOCK_HAT_MODELS.length);
}
