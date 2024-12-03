package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import static grill24.potionsplus.utility.Utility.ppId;

public class LootTables {
    public static final ResourceKey<LootTable> ARID_CAVE_SUSPICIOUS_SAND = ResourceKey.create(Registries.LOOT_TABLE, ppId("arid_cave_suspicious_sand"));

    public static final ResourceKey<LootTable> BASIC_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("basic_skill_rewards"));
    public static final ResourceKey<LootTable> INTERMEDIATE_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("intermediate_skill_rewards"));
    public static final ResourceKey<LootTable> ADVANCED_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("advanced_skill_rewards"));
    public static final ResourceKey<LootTable> EXPERT_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("expert_skill_rewards"));
    public static final ResourceKey<LootTable> MASTER_SKILL_REWARDS = ResourceKey.create(Registries.LOOT_TABLE, ppId("master_skill_rewards"));

    public static final ResourceKey<LootTable> IRON_ORE_GOLD_NUGGET_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("iron_ore_gold_nugget_bonus_drops"));
    public static final ResourceKey<LootTable> COPPER_ORE_IRON_NUGGET_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("copper_ore_iron_nugget_bonus_drops"));
    public static final ResourceKey<LootTable> DIAMOND_ORE_EMERALD_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("diamond_ore_emerald_bonus_drops"));

    public static final ResourceKey<LootTable> CREEPER_SAND_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("creeper_sand_bonus_drops"));
    public static final ResourceKey<LootTable> SKELETON_BONE_MEAL_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("skeleton_bone_meal_bonus_drops"));
    public static final ResourceKey<LootTable> SKELETON_BONE_BLOCK_BONUS_DROPS = ResourceKey.create(Registries.LOOT_TABLE, ppId("skeleton_bone_block_bonus_drops"));
}
