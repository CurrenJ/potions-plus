package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import static grill24.potionsplus.utility.Utility.ppId;

public class LootTables {
    public static final ResourceKey<LootTable> ARID_CAVE_SUSPICIOUS_SAND = ResourceKey.create(Registries.LOOT_TABLE, ppId("arid_cave_suspicious_sand"));
    public static final ResourceKey<LootTable> ALL_POTIONS = ResourceKey.create(Registries.LOOT_TABLE, ppId("all_potions"));

    public static final ResourceKey<LootTable> GEMS_AND_ORES_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("gems_and_ores"));

    // Ore hat loot tables (arrays of 4, matching the 4 hat milestones: 64, 128, 256, 512)
    public static final ResourceKey<LootTable>[] COPPER_ORE_HATS = createHatArray("copper_ore_hats");
    public static final ResourceKey<LootTable>[] COAL_ORE_HATS = createHatArray("coal_ore_hats");
    public static final ResourceKey<LootTable>[] IRON_ORE_HATS = createHatArray("iron_ore_hats");
    public static final ResourceKey<LootTable>[] GOLD_ORE_HATS = createHatArray("gold_ore_hats");
    public static final ResourceKey<LootTable>[] DIAMOND_ORE_HATS = createHatArray("diamond_ore_hats");
    public static final ResourceKey<LootTable>[] EMERALD_ORE_HATS = createHatArray("emerald_ore_hats");

    @SuppressWarnings("unchecked")
    private static ResourceKey<LootTable>[] createHatArray(String baseName) {
        return new ResourceKey[]{
                ResourceKey.create(Registries.LOOT_TABLE, ppId(baseName + "_1")),
                ResourceKey.create(Registries.LOOT_TABLE, ppId(baseName + "_2")),
                ResourceKey.create(Registries.LOOT_TABLE, ppId(baseName + "_3")),
                ResourceKey.create(Registries.LOOT_TABLE, ppId(baseName + "_4")),
        };
    }
}
