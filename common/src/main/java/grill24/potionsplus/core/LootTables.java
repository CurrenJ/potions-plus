package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import static grill24.potionsplus.utility.Utility.ppId;

public class LootTables {
    public static final ResourceKey<LootTable> ARID_CAVE_SUSPICIOUS_SAND = ResourceKey.create(Registries.LOOT_TABLE, ppId("arid_cave_suspicious_sand"));
    public static final ResourceKey<LootTable> ALL_POTIONS = ResourceKey.create(Registries.LOOT_TABLE, ppId("all_potions"));

    public static final ResourceKey<LootTable> BASIC_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("basic_skill_rewards"));
    public static final ResourceKey<LootTable> INTERMEDIATE_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("intermediate_skill_rewards"));
    public static final ResourceKey<LootTable> ADVANCED_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("advanced_skill_rewards"));
    public static final ResourceKey<LootTable> EXPERT_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("expert_skill_rewards"));
    public static final ResourceKey<LootTable> MASTER_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("master_skill_rewards"));
    public static final ResourceKey<LootTable> GEMS_AND_ORES_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("gems_and_ores"));

    // Ore hat loot tables (arrays of 4, matching the 4 hat milestones: 64, 128, 256, 512)
    public static final ResourceKey<LootTable>[] COPPER_ORE_HATS = createHatArray("copper_ore_hats");
    public static final ResourceKey<LootTable>[] COAL_ORE_HATS = createHatArray("coal_ore_hats");
    public static final ResourceKey<LootTable>[] IRON_ORE_HATS = createHatArray("iron_ore_hats");
    public static final ResourceKey<LootTable>[] GOLD_ORE_HATS = createHatArray("gold_ore_hats");
    public static final ResourceKey<LootTable>[] DIAMOND_ORE_HATS = createHatArray("diamond_ore_hats");
    public static final ResourceKey<LootTable>[] EMERALD_ORE_HATS = createHatArray("emerald_ore_hats");

    // Ore bonus drop loot tables
    public static final ResourceKey<LootTable> IRON_ORE_GOLD_NUGGET_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("iron_ore_gold_nugget_bonus_drops"));
    public static final ResourceKey<LootTable> COPPER_ORE_IRON_NUGGET_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("copper_ore_iron_nugget_bonus_drops"));
    public static final ResourceKey<LootTable> DIAMOND_ORE_EMERALD_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("diamond_ore_emerald_bonus_drops"));
    public static final ResourceKey<LootTable> DIAMOND_ORE_LAPIS_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("diamond_ore_lapis_bonus_drops"));

    // Mob bonus drop loot tables
    public static final ResourceKey<LootTable> CREEPER_SAND_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("creeper_sand_bonus_drops"));
    public static final ResourceKey<LootTable> SKELETON_BONE_MEAL_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("skeleton_bone_meal_bonus_drops"));
    public static final ResourceKey<LootTable> SKELETON_BONE_BLOCK_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("skeleton_bone_block_bonus_drops"));

    // Crop bonus drop loot tables
    public static final ResourceKey<LootTable> WHEAT_ADDITIONAL_SEEDS_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("wheat_additional_seeds_bonus_drops"));
    public static final ResourceKey<LootTable> CARROT_GOLDEN_CARROT_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("carrot_golden_carrot_bonus_drops"));
    public static final ResourceKey<LootTable> POTATO_POISONOUS_POTATO_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("potato_poisonous_potato_bonus_drops"));
    public static final ResourceKey<LootTable> BEETROOT_SUGAR_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("beetroot_sugar_bonus_drops"));
    public static final ResourceKey<LootTable> NETHER_WART_BLAZE_POWDER_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("nether_wart_blaze_powder_bonus_drops"));
    public static final ResourceKey<LootTable> COCOA_COOKIE_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("cocoa_cookie_bonus_drops"));

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
