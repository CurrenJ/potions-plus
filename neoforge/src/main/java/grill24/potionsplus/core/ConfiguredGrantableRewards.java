package grill24.potionsplus.core;

import grill24.potionsplus.core.items.SkillLootItems;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.reward.*;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import oshi.util.tuples.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfiguredGrantableRewards {
    public static final AdvancementReward.AdvancementRewardBuilder SIMPLE_DUNGEON_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("simple_dungeon_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.SIMPLE_DUNGEON).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_5));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] SIMPLE_DUNGEON_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("simple_dungeon_loot_edible_" + i,
            new Pair<>(new ItemStack(SkillLootItems.MOSSASHIMI.getValue()), SIMPLE_DUNGEON_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder ABANDONED_MINESHAFT_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("abandoned_mineshaft_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.ABANDONED_MINESHAFT).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_10));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] ABANDONED_MINESHAFT_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("abandoned_mineshaft_loot_edible_" + i,
            new Pair<>(new ItemStack(SkillLootItems.STONE_FRUIT.getValue()), ABANDONED_MINESHAFT_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder STRONGHOLD_LIBRARY_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("stronghold_library_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.STRONGHOLD_LIBRARY).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_15));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] STRONGHOLD_LIBRARY_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("stronghold_library_loot_edible_" + i,
            new Pair<>(new ItemStack(SkillLootItems.CHOCOLATE_BOOK.getValue()), STRONGHOLD_LIBRARY_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder DESERT_PYRAMID_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("desert_pyramid_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.DESERT_PYRAMID).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_20));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] DESERT_PYRAMID_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("desert_pyramid_loot_edible_" + i,
            new Pair<>(new ItemStack(SkillLootItems.PYRAMIDS_OF_SALT.getValue()), DESERT_PYRAMID_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder JUNGLE_TEMPLE_LOOT = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("jungle_temple_loot",
                    AdvancementRewards.Builder.loot(BuiltInLootTables.JUNGLE_TEMPLE).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_25));

    public static final EdibleChoiceReward.ChoiceRewardBuilder[] JUNGLE_TEMPLE_LOOT_EDIBLE = register((i) -> new EdibleChoiceReward.ChoiceRewardBuilder("jungle_temple_loot_edible_" + i,
            new Pair<>(new ItemStack(SkillLootItems.ROASTED_BAMBOO.getValue()), JUNGLE_TEMPLE_LOOT.getKey())
    ), 25);

    public static final AdvancementReward.AdvancementRewardBuilder BASIC_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("basic_skill",
                    AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC));

    public static final EdibleChoiceReward.ChoiceRewardBuilder BASIC_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("basic_skill_loot_edible",
            new Pair<>(new ItemStack(SkillLootItems.BASIC_LOOT.getValue()), BASIC_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder INTERMEDIATE_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("intermediate_skill",
                    AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE));

    public static final EdibleChoiceReward.ChoiceRewardBuilder INTERMEDIATE_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("intermediate_skill_loot_edible",
            new Pair<>(new ItemStack(SkillLootItems.INTERMEDIATE_LOOT.getValue()), INTERMEDIATE_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder ADVANCED_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("advanced_skill",
                    AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED));

    public static final EdibleChoiceReward.ChoiceRewardBuilder ADVANCED_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("advanced_skill_loot_edible",
            new Pair<>(new ItemStack(SkillLootItems.ADVANCED_LOOT.getValue()), ADVANCED_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder EXPERT_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("expert_skill",
                    AdvancementRewards.Builder.loot(LootTables.EXPERT_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_EXPERT));

    public static final EdibleChoiceReward.ChoiceRewardBuilder EXPERT_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("expert_skill_loot_edible",
            new Pair<>(new ItemStack(SkillLootItems.SPARKLING_SQUASH.getValue()), EXPERT_SKILL_REWARDS.getKey())
    ));

    public static final AdvancementReward.AdvancementRewardBuilder MASTER_SKILL_REWARDS = register(() ->
            new AdvancementReward.AdvancementRewardBuilder("master_skill",
                    AdvancementRewards.Builder.loot(LootTables.MASTER_SKILL_REWARDS).build())
                    .translation(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_MASTER));

    public static final EdibleChoiceReward.ChoiceRewardBuilder MASTER_SKILL_LOOT_EDIBLE = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("master_skill_loot_edible",
            new Pair<>(new ItemStack(SkillLootItems.MASTER_LOOT.getValue()), MASTER_SKILL_REWARDS.getKey())
    ));

    public static final AnimatedItemReward.AnimatedItemRewardBuilder ANIMATED_ITEMS = register(() -> new AnimatedItemReward.AnimatedItemRewardBuilder(new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Items.STONE_PICKAXE), new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.NETHERITE_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.WOODEN_AXE), new ItemStack(Items.STONE_AXE), new ItemStack(Items.IRON_AXE), new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.NETHERITE_AXE), new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Items.STONE_SHOVEL), new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.DIAMOND_SHOVEL), new ItemStack(Items.NETHERITE_SHOVEL), new ItemStack(Items.GOLDEN_SHOVEL), new ItemStack(Items.WOODEN_HOE), new ItemStack(Items.STONE_HOE), new ItemStack(Items.IRON_HOE), new ItemStack(Items.DIAMOND_HOE), new ItemStack(Items.NETHERITE_HOE), new ItemStack(Items.GOLDEN_HOE), new ItemStack(Items.WOODEN_SWORD), new ItemStack(Items.STONE_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_SWORD), new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.BOW), new ItemStack(Items.CROSSBOW), new ItemStack(Items.TRIDENT), new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.CHAINMAIL_BOOTS), new ItemStack(Items.CHAINMAIL_LEGGINGS), new ItemStack(Items.CHAINMAIL_CHESTPLATE), new ItemStack(Items.CHAINMAIL_HELMET), new ItemStack(Items.IRON_BOOTS), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_HELMET), new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.NETHERITE_BOOTS), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.GOLDEN_BOOTS), new ItemStack(Items.GOLDEN_LEGGINGS), new ItemStack(Items.GOLDEN_CHESTPLATE), new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.SHIELD), new ItemStack(Items.ELYTRA), new ItemStack(Items.SUGAR), new ItemStack(Items.COPPER_ORE), new ItemStack(Items.COAL_ORE), new ItemStack(Items.IRON_ORE), new ItemStack(Items.GOLD_ORE), new ItemStack(Items.DIAMOND_ORE), new ItemStack(Items.EMERALD_ORE), new ItemStack(Items.ANCIENT_DEBRIS)));

    public static final IncreaseAbilityStrengthReward.Builder SMALL_PICKAXE_EFFICIENCY_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("small_pickaxe_efficiency_increase")
            .ability(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_MODIFIER.getKey())
            .strength(0.05F));
    public static final IncreaseAbilityStrengthReward.Builder MEDIUM_PICKAXE_EFFICIENCY_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("medium_pickaxe_efficiency_increase")
            .ability(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_MODIFIER.getKey())
            .strength(0.1F));
    public static final IncreaseAbilityStrengthReward.Builder SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("small_submerged_pickaxe_efficiency_increase")
            .ability(ConfiguredPlayerAbilities.SUBMERGED_PICKAXE_EFFICIENCY_MODIFIER.getKey())
            .strength(0.05F));
    public static final IncreaseAbilityStrengthReward.Builder PICKAXE_FORTUNE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("pickaxe_fortune_increase")
            .ability(ConfiguredPlayerAbilities.PICKAXE_FORTUNE_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder PICKAXE_UNBREAKING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("pickaxe_unbreaking_increase")
            .ability(ConfiguredPlayerAbilities.PICKAXE_UNBREAKING_MODIFIER.getKey())
            .strength(1.0F));
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_COPPER_ORE_ADDITIONAL_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.COPPER_ORE_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_IRON_ORE_ADDITIONAL_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.IRON_ORE_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_DIAMOND_ORE_ADDITIONAL_EMERALDS_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.DIAMOND_ORE_ADDITIONAL_LOOT_EMERALDS.getKey());
    public static final AbilityReward.AbilityRewardBuilder PICKAXE_DIAMOND_ORE_ADDITIONAL_LAPIS_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.DIAMOND_ORE_ADDITIONAL_LOOT_LAPIS.getKey());

    // Crop additional loot rewards for farming skill
    public static final AbilityReward.AbilityRewardBuilder WHEAT_ADDITIONAL_SEEDS_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.WHEAT_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder CARROT_GOLDEN_CARROT_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.CARROT_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder POTATO_POISONOUS_POTATO_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.POTATO_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder BEETROOT_SUGAR_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.BEETROOT_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder NETHER_WART_BLAZE_POWDER_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.NETHER_WART_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder COCOA_BROWN_DYE_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.COCOA_ADDITIONAL_LOOT.getKey());

    public static final IncreaseAbilityStrengthReward.Builder SMALL_AXE_EFFICIENCY_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("axe_efficiency_increase")
            .ability(ConfiguredPlayerAbilities.AXE_EFFICIENCY_MODIFIER.getKey())
            .strength(0.05F));
    public static final IncreaseAbilityStrengthReward.Builder SMALL_SHOVEL_EFFICIENCY_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("shovel_efficiency_increase")
            .ability(ConfiguredPlayerAbilities.SHOVEL_EFFICIENCY_MODIFIER.getKey())
            .strength(0.05F));
    public static final IncreaseAbilityStrengthReward.Builder SMALL_HOE_EFFICIENCY_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("hoe_efficiency_increase")
            .ability(ConfiguredPlayerAbilities.HOE_EFFICIENCY_MODIFIER.getKey())
            .strength(0.05F));
    public static final IncreaseAbilityStrengthReward.Builder HOE_FORTUNE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("hoe_fortune_increase")
            .ability(ConfiguredPlayerAbilities.HOE_FORTUNE_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder HOE_UNBREAKING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("hoe_unbreaking_increase")
            .ability(ConfiguredPlayerAbilities.HOE_UNBREAKING_MODIFIER.getKey())
            .strength(1.0F));

    public static final IncreaseAbilityStrengthReward.Builder SWORD_SHARPNESS_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("sword_sharpness_increase")
            .ability(ConfiguredPlayerAbilities.SWORD_SHARPNESS_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder SWORD_LOOTING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("sword_looting_increase")
            .ability(ConfiguredPlayerAbilities.SWORD_LOOTING_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder SWORD_UNBREAKING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("sword_unbreaking_increase")
            .ability(ConfiguredPlayerAbilities.SWORD_UNBREAKING_MODIFIER.getKey())
            .strength(1.0F));
    public static final AbilityReward.AbilityRewardBuilder CREEPER_SAND_ADDITIONAL_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.CREEPER_SAND_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder SKELETON_BONE_MEAL_ADDITIONAL_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.SKELETON_BONE_MEAL_ADDITIONAL_LOOT.getKey());
    public static final AbilityReward.AbilityRewardBuilder SKELETON_BONE_BLOCK_ADDITIONAL_LOOT = registerAbilityReward(ConfiguredPlayerAbilities.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT.getKey());

    public static final IncreaseAbilityStrengthReward.Builder SMALL_AXE_DAMAGE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("axe_damage_increase")
            .ability(ConfiguredPlayerAbilities.AXE_DAMAGE_MODIFIER.getKey())
            .strength(0.25F));
    public static final IncreaseAbilityStrengthReward.Builder AXE_SMITE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("axe_smite_increase")
            .ability(ConfiguredPlayerAbilities.AXE_SMITE_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder AXE_UNBREAKING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("axe_unbreaking_increase")
            .ability(ConfiguredPlayerAbilities.AXE_UNBREAKING_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder AXE_LOOTING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("axe_looting_increase")
            .ability(ConfiguredPlayerAbilities.AXE_LOOTING_MODIFIER.getKey())
            .strength(1.0F));

    public static final IncreaseAbilityStrengthReward.Builder FISHING_ROD_LUCK_OF_THE_SEA_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("fishing_rod_luck_of_the_sea_increase")
            .ability(ConfiguredPlayerAbilities.FISHING_ROD_LUCK_OF_THE_SEA_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder FISHING_ROD_LURE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("fishing_rod_lure_increase")
            .ability(ConfiguredPlayerAbilities.FISHING_ROD_LURE_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder FISHING_ROD_UNBREAKING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("fishing_rod_unbreaking_increase")
            .ability(ConfiguredPlayerAbilities.FISHING_ROD_UNBREAKING_MODIFIER.getKey())
            .strength(1.0F));

    public static final IncreaseAbilityStrengthReward.Builder BOW_POWER_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("bow_power_increase")
            .ability(ConfiguredPlayerAbilities.BOW_POWER_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder BOW_PUNCH_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("bow_punch_increase")
            .ability(ConfiguredPlayerAbilities.BOW_PUNCH_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder BOW_UNBREAKING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("bow_unbreaking_increase")
            .ability(ConfiguredPlayerAbilities.BOW_UNBREAKING_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder BOW_LOOTING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("bow_looting_increase")
            .ability(ConfiguredPlayerAbilities.BOW_LOOTING_MODIFIER.getKey())
            .strength(1.0F));
    public static final IncreaseAbilityStrengthReward.Builder SMALL_BOW_USE_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("small_bow_use_speed_increase")
            .ability(ConfiguredPlayerAbilities.BOW_USE_SPEED_MODIFIER.getKey())
            .strength(0.05F));
    public static final IncreaseAbilityStrengthReward.Builder MEDIUM_BOW_USE_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("medium_bow_use_speed_increase")
            .ability(ConfiguredPlayerAbilities.BOW_USE_SPEED_MODIFIER.getKey())
            .strength(0.1F));

    public static final IncreaseAbilityStrengthReward.Builder SMALL_MOVEMENT_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("movement_speed_increase")
            .ability(ConfiguredPlayerAbilities.MOVEMENT_SPEED_MODIFIER.getKey())
            .strength(0.01F));
    public static final IncreaseAbilityStrengthReward.Builder SMALL_SPRINT_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("sprint_speed_increase")
            .ability(ConfiguredPlayerAbilities.SPRINT_SPEED_MODIFIER.getKey())
            .strength(0.001F));
    public static final IncreaseAbilityStrengthReward.Builder SMALL_SNEAK_SPEED_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("sneak_speed_increase")
            .ability(ConfiguredPlayerAbilities.SNEAK_SPEED_MODIFIER.getKey())
            .strength(0.01F));

    public static IncreaseAbilityStrengthReward.Builder SMALL_JUMP_HEIGHT_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("jump_height_increase")
            .ability(ConfiguredPlayerAbilities.JUMP_HEIGHT_MODIFIER.getKey())
            .strength(0.02F));
    public static IncreaseAbilityStrengthReward.Builder DOUBLE_JUMP_COUNT_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("double_jump_count_increase")
            .ability(ConfiguredPlayerAbilities.DOUBLE_JUMP.getKey())
            .strength(1.0F));
    public static IncreaseAbilityStrengthReward.Builder SAFE_FALL_DISTANCE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("safe_fall_distance_increase")
            .ability(ConfiguredPlayerAbilities.SAFE_FALL_DISTANCE_MODIFIER.getKey())
            .strength(0.5F));

    public static IncreaseAbilityStrengthReward.Builder SAVED_BY_THE_BOUNCE_UNLOCK = register(() -> new IncreaseAbilityStrengthReward.Builder("saved_by_the_bounce_unlock")
            .ability(ConfiguredPlayerAbilities.SAVED_BY_THE_BOUNCE.getKey())
            .strength(1.0F)
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SAVED_BY_THE_BOUNCE_UNLOCK));
    public static IncreaseAbilityStrengthReward.Builder SAVED_BY_THE_BOUNCE_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("saved_by_the_bounce_increase")
            .ability(ConfiguredPlayerAbilities.SAVED_BY_THE_BOUNCE.getKey())
            .strength(1.0F))
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_SAVED_BY_THE_BOUNCE_INCREASE);

    public static IncreaseAbilityStrengthReward.Builder LAST_BREATH_UNLOCK = register(() -> new IncreaseAbilityStrengthReward.Builder("last_breath_unlock")
            .ability(ConfiguredPlayerAbilities.LAST_BREATH.getKey())
            .strength(1.0F)
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_LAST_BREATH_UNLOCK));
    public static IncreaseAbilityStrengthReward.Builder LAST_BREATH_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("last_breath_increase")
            .ability(ConfiguredPlayerAbilities.LAST_BREATH.getKey())
            .strength(1.0F))
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_LAST_BREATH_INCREASE);

    public static IncreaseAbilityStrengthReward.Builder HOT_POTATO_UNLOCK = register(() -> new IncreaseAbilityStrengthReward.Builder("hot_potato_unlock")
            .ability(ConfiguredPlayerAbilities.HOT_POTATO.getKey())
            .strength(1.0F)
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOT_POTATO_UNLOCK));
    public static IncreaseAbilityStrengthReward.Builder HOT_POTATO_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("hot_potato_increase")
            .ability(ConfiguredPlayerAbilities.HOT_POTATO.getKey())
            .strength(1.0F))
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_HOT_POTATO_INCREASE);

    public static IncreaseAbilityStrengthReward.Builder CHAIN_LIGHTNING_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("chain_lightning_increase")
            .ability(ConfiguredPlayerAbilities.CHAIN_LIGHTNING.getKey())
            .strength(1.0F)
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CHAIN_LIGHTNING_INCREASE));
    public static IncreaseAbilityStrengthReward.Builder CHAIN_LIGHTNING_INCREASE_2X = register(() -> new IncreaseAbilityStrengthReward.Builder("chain_lightning_increase_2x")
            .ability(ConfiguredPlayerAbilities.CHAIN_LIGHTNING.getKey())
            .strength(2.0F)
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_CHAIN_LIGHTNING_INCREASE_2X));

    public static IncreaseAbilityStrengthReward.Builder STUN_SHOT_INCREASE = register(() -> new IncreaseAbilityStrengthReward.Builder("stun_shot_increase")
            .ability(ConfiguredPlayerAbilities.STUN_SHOT.getKey())
            .strength(1.0F)
            .translationKey(Translations.DESCRIPTION_POTIONSPLUS_ABILITY_STUN_SHOT_INCREASE));

    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_LOOT_1 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_loot_1",
            new Pair<>(new ItemStack(SkillLootItems.MOSSASHIMI.getValue()), SIMPLE_DUNGEON_LOOT.getKey()),
            new Pair<>(new ItemStack(SkillLootItems.STONE_FRUIT.getValue()), ABANDONED_MINESHAFT_LOOT.getKey())
    ));

    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_LOOT_2 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_loot_2",
            new Pair<>(new ItemStack(SkillLootItems.SPARKLING_SQUASH.getValue()), STRONGHOLD_LIBRARY_LOOT.getKey()),
            new Pair<>(new ItemStack(SkillLootItems.BLUEB_BERRIES.getValue()), DESERT_PYRAMID_LOOT.getKey())
    ));

    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_PICKAXE_EFFICIENCY_1 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_pickaxe_efficiency_1",
            new Pair<>(new ItemStack(SkillLootItems.SPARKLING_SQUASH.getValue()), SMALL_PICKAXE_EFFICIENCY_INCREASE.getKey()),
            new Pair<>(new ItemStack(SkillLootItems.BLUEB_BERRIES.getValue()), SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE.getKey())
    ));
    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_PICKAXE_EFFICIENCY_2 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_pickaxe_efficiency_2",
            new Pair<>(new ItemStack(SkillLootItems.FORTIFYING_FUDGE.getValue()), SMALL_PICKAXE_EFFICIENCY_INCREASE.getKey()),
            new Pair<>(new ItemStack(SkillLootItems.BLUEB_BERRIES.getValue()), SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE.getKey())
    ));

    public static EdibleChoiceReward.ChoiceRewardBuilder CHOOSE_PICKAXE_LOOT_1 = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("choose_pickaxe_loot_1",
            new Pair<>(new ItemStack(SkillLootItems.SPARKLING_SQUASH.getValue()), PICKAXE_DIAMOND_ORE_ADDITIONAL_EMERALDS_LOOT.getKey()),
            new Pair<>(new ItemStack(SkillLootItems.BLUEB_BERRIES.getValue()), PICKAXE_DIAMOND_ORE_ADDITIONAL_LAPIS_LOOT.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder WHEEL_TREASURES = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("wheel_treasures",
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.LAPIS_LAZULI),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.REDSTONE),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.ANCIENT_DEBRIS)
    ));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_WHEEL_TREASURES = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_wheel_treasures",
            new Pair<>(new ItemStack(SkillLootItems.WHEEL.getValue()), WHEEL_TREASURES.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder WHEEL_END_CITY = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("wheel_end_city",
            BuiltInLootTables.END_CITY_TREASURE, 8).translation(Translations.TOOLTIP_POTIONSPLUS_REWARD_END_CITY_LOOT));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_WHEEL_END_CITY = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_wheel_end_city",
            new Pair<>(new ItemStack(SkillLootItems.WHEEL.getValue()), WHEEL_END_CITY.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder GEMS_AND_ORES_WHEEL = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("gems_and_ores_wheel",
            LootTables.GEMS_AND_ORES_REWARDS, 8).translation(Translations.TOOLTIP_POTIONSPLUS_REWARD_GEMS_AND_ORES_WHEEL));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_GEMS_AND_ORES_WHEEL = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_gems_and_ores_wheel",
            new Pair<>(new ItemStack(SkillLootItems.WHEEL.getValue()), GEMS_AND_ORES_WHEEL.getKey())
    ));

    public static ItemWheelReward.ItemWheelRewardBuilder POTIONS_WHEEL = register(() -> new ItemWheelReward.ItemWheelRewardBuilder("potions_wheel",
            LootTables.ALL_POTIONS, 8));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_POTIONS_WHEEL = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_potions_wheel",
            new Pair<>(new ItemStack(SkillLootItems.WHEEL.getValue()), POTIONS_WHEEL.getKey())
    ));

    public static UnknownPotionIngredientReward.UnknownPotionIngredientRewardBuilder UNKNOWN_POTION_INGREDIENT = register(() -> new UnknownPotionIngredientReward.UnknownPotionIngredientRewardBuilder("unknown_potion_ingredient", 1));
    public static EdibleChoiceReward.ChoiceRewardBuilder EDIBLE_UNKNOWN_POTION_INGREDIENT = register(() -> new EdibleChoiceReward.ChoiceRewardBuilder("edible_unknown_potion_ingredient",
            new Pair<>(new ItemStack(SkillLootItems.MOSSASHIMI.getValue()), UNKNOWN_POTION_INGREDIENT.getKey())
    ));

    public static void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
        for (IRewardBuilder data : rewardBuilders) {
            data.generate(context);
        }
    }

    private static List<IRewardBuilder> rewardBuilders;

    private static AbilityReward.AbilityRewardBuilder registerAbilityReward(ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityKey) {
        AbilityReward.AbilityRewardBuilder data = new AbilityReward.AbilityRewardBuilder(configuredAbilityKey);
        if (rewardBuilders == null) {
            rewardBuilders = new ArrayList<>();
        }
        rewardBuilders.add(data);
        return data;
    }

    private static <T extends IRewardBuilder> T register(Supplier<T> supplier) {
        T data = supplier.get();
        if (rewardBuilders == null) {
            rewardBuilders = new ArrayList<>();
        }
        rewardBuilders.add(data);
        return data;
    }

    private static <T extends IRewardBuilder> T[] register(Function<Integer, T> supplier, int count) {
        List<T> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int finalI = i;
            data.add(register(() -> supplier.apply(finalI)));
        }
        return data.toArray((T[]) Array.newInstance(data.get(0).getClass(), count));
    }

    public interface IRewardBuilder {
        void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context);

        ResourceKey<ConfiguredGrantableReward<?, ?>> getKey();
    }
}
