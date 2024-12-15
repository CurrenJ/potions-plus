package grill24.potionsplus.core;

import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.reward.AnimatedItemRewardData;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Data Gen Class. ConfiguredSkills are registered dynamically from datapack.
 */
public class ConfiguredSkills {
    public static final ResourceKey<ConfiguredSkill<?, ?>> MINING_CONFIGURED_KEY = register("mining");
    public static final ResourceKey<ConfiguredSkill<?, ?>> WOODCUTTING_CONFIGURED_KEY = register("woodcutting");

    public static final ResourceKey<ConfiguredSkill<?, ?>> SWORDSMANSHIP = register("swordsmanship");
    public static final ResourceKey<ConfiguredSkill<?, ?>> ARCHERY = register("archery");
    public static final ResourceKey<ConfiguredSkill<?, ?>> CHOPPING = register("chopping");

    public static final ResourceKey<ConfiguredSkill<?, ?>> WALKING = register("walking");
    public static final ResourceKey<ConfiguredSkill<?, ?>> SPRINTING = register("sprinting");
    public static final ResourceKey<ConfiguredSkill<?, ?>> SNEAKING = register("sneaking");
    public static final ResourceKey<ConfiguredSkill<?, ?>> JUMPING = register("jumping");

    // Data Gen
    public static void generate(BootstrapContext<ConfiguredSkill<?, ?>> context) {
        HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE);
        HolderGetter<ConfiguredPlayerAbility<?, ?>> abilityLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY);

        context.register(MINING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration(
                Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING,
                tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_ORE),
                new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.005F, 20F),
                createDefaultRewards()
                        .addRewardForLevel(5, builder -> builder
                                .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_5)
                                .addAdvancementRewards(AdvancementRewards.Builder.loot(BuiltInLootTables.SIMPLE_DUNGEON).build())
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS[0])
                                .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.WOODEN_PICKAXE)))
                        )
                        .addRewardForLevel(7, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS[0])
                                .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.STONE_PICKAXE)))
                        )
                        .addRewardForLevel(8, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_COPPER_ORE_ADDITIONAL_LOOT_KEYS[0])
                        )
                        .addRewardForLevel(10, builder -> builder
                                .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_10)
                                .addAdvancementRewards(AdvancementRewards.Builder.loot(BuiltInLootTables.ABANDONED_MINESHAFT).build())
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS[1])
                                .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.STONE_PICKAXE)))
                        )
                        .addRewardForLevel(13, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS[1])
                                .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.DIAMOND_PICKAXE)))
                        )
                        .addRewardForLevel(15, builder -> builder
                                .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_15)
                                .addAdvancementRewards(AdvancementRewards.Builder.loot(BuiltInLootTables.STRONGHOLD_LIBRARY).build())
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS[2])
                        )
                        .addRewardForLevel(16, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_IRON_ORE_ADDITIONAL_LOOT_KEYS[0])
                        )
                        .addRewardForLevel(20, builder -> builder
                                .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_20)
                                .addAdvancementRewards(AdvancementRewards.Builder.loot(BuiltInLootTables.DESERT_PYRAMID).build())
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS[3])
                                .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.IRON_PICKAXE)))
                        )
                        .addRewardForLevel(21, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.SUBMERGED_PICKAXE_EFFICIENCY_BONUS_KEYS[2])
                        )
                        .addRewardForLevel(23, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_DIAMOND_ORE_ADDITIONAL_LOOT_KEYS[0])
                        )
                        .addRewardForLevel(24, builder -> builder
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_FORTUNE_BONUS_KEYS[1])
                        )
                        .addRewardForLevel(25, builder -> builder
                                .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING_REWARD_LEVEL_25)
                                .addAdvancementRewards(AdvancementRewards.Builder.loot(BuiltInLootTables.JUNGLE_TEMPLE).build())
                                .addAbility(ConfiguredPlayerAbilities.PICKAXE_EFFICIENCY_BONUS_KEYS[4])
                        )
                        .build(abilityLookup)
        )));

        // Woodcutting
        context.register(WOODCUTTING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WOODCUTTING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_LOG),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.003F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS[0])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.WOODEN_AXE)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS[1])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.STONE_AXE)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS[2])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.IRON_AXE)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS[3])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.GOLDEN_AXE)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_EFFICIENCY_BONUS_KEYS[4])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.DIAMOND_AXE)))
                                )
                                .build(abilityLookup)
                )
        ));

        // Walking
        context.register(WALKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WALKING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.WALK),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.001F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS[0])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.LEATHER_BOOTS)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS[1])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.CHAINMAIL_BOOTS)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS[2])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.IRON_BOOTS)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS[3])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.GOLDEN_BOOTS)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.MOVEMENT_SPEED_BONUS_KEYS[4])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.DIAMOND_BOOTS)))
                                )
                                .build(abilityLookup)
                )
        ));

        // Sprinting
        context.register(SPRINTING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SPRINTING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SPRINT),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.001F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS[0])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.SUGAR)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS[1])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.SUGAR)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS[2])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.SUGAR)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS[3])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.SUGAR)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SPRINT_SPEED_BONUS_KEYS[4])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.SUGAR)))
                                )
                                .build(abilityLookup)
                )
        ));

        // Sneaking
        context.register(SNEAKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SNEAKING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SNEAK),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.003F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder.addAbility(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS[0]))
                                .addRewardForLevel(10, builder -> builder.addAbility(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS[1]))
                                .addRewardForLevel(15, builder -> builder.addAbility(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS[2]))
                                .addRewardForLevel(20, builder -> builder.addAbility(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS[3]))
                                .addRewardForLevel(25, builder -> builder.addAbility(ConfiguredPlayerAbilities.SNEAK_SPEED_BONUS_KEYS[4]))
                                .build(abilityLookup)
                )
        ));

        // Jumping
        context.register(JUMPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_JUMPING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.JUMP),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.0002F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder.addAbility(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS[0]))
                                .addRewardForLevel(10, builder -> builder.addAbility(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS[1]))
                                .addRewardForLevel(15, builder -> builder.addAbility(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS[2]))
                                .addRewardForLevel(20, builder -> builder.addAbility(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS[3]))
                                .addRewardForLevel(25, builder -> builder.addAbility(ConfiguredPlayerAbilities.JUMP_HEIGHT_BONUS_KEYS[4]))
                                .build(abilityLookup)
                )
        ));

        context.register(SWORDSMANSHIP, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SWORDSMANSHIP,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_SWORD),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.003F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS[0])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.WOODEN_SWORD)))
                                )
                                .addRewardForLevel(6, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SKELETON_BONE_MEAL_ADDITIONAL_LOOT_KEYS[0])
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS[1])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.STONE_SWORD)))
                                )
                                .addRewardForLevel(14, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT_KEYS[0])
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS[2])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.IRON_SWORD)))
                                )
                                .addRewardForLevel(18, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.CREEPER_SAND_ADDITIONAL_LOOT_KEYS[0])
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS[3])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.GOLDEN_SWORD)))
                                )
                                .addRewardForLevel(23, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SWORD_LOOTING_BONUS_KEYS[0])
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.SWORD_SHARPNESS_BONUS_KEYS[4])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.DIAMOND_SWORD)))
                                )
                                .build(abilityLookup)
                )
        ));

        context.register(CHOPPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_CHOPPING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_AXE),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.003F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS[0])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.WOODEN_AXE)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS[1])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.STONE_AXE)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS[2])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.IRON_AXE)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS[3])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.GOLDEN_AXE)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.AXE_DAMAGE_BONUS_KEYS[4])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.DIAMOND_AXE)))
                                )
                                .build(abilityLookup)
                )
        ));

        context.register(ARCHERY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_ARCHERY,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_BOW),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.LOG, 0.003F, 20F),
                        createDefaultRewards()
                                .addRewardForLevel(5, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS[0])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS[1])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS[2])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS[3])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addAbility(ConfiguredPlayerAbilities.BOW_POWER_BONUS_KEYS[4])
                                        .addAnimatedItemReward(new AnimatedItemRewardData(new ItemStack(Items.BOW)))
                                )
                                .build(abilityLookup)
                )
        ));
    }

    private static ResourceKey<ConfiguredSkill<?, ?>> register(String name) {
        return ResourceKey.create(PotionsPlusRegistries.CONFIGURED_SKILL, ppId(name));
    }

    @SafeVarargs
    private static List<Holder<ConfiguredSkillPointSource<?, ?>>> tryBuildValidSourceList(HolderGetter<ConfiguredSkillPointSource<?, ?>> lookup, ResourceKey<ConfiguredSkillPointSource<?, ?>>... keys) {
        List<Holder<ConfiguredSkillPointSource<?, ?>>> validSources = new ArrayList<>();
        for (ResourceKey<ConfiguredSkillPointSource<?, ?>> key : keys) {
            Optional<Holder.Reference<ConfiguredSkillPointSource<?, ?>>> optional = lookup.get(key);
            optional.ifPresent(validSources::add);
        }
        return validSources;
    }

    private static SkillLevelUpRewardsConfiguration.Builder createDefaultRewards() {
        return SkillLevelUpRewardsConfiguration.Builder.create()
                .addRewardForLevel(1, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(2, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(3, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(4, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(5, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(6, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(7, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(8, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(9, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(10, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_BASIC)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.BASIC_SKILL_REWARDS).build())
                )
                .addRewardForLevel(11, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(12, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(13, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(14, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(15, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(16, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(17, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(18, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(19, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_INTERMEDIATE)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.INTERMEDIATE_SKILL_REWARDS).build())
                )
                .addRewardForLevel(20, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                )
                .addRewardForLevel(21, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                )
                .addRewardForLevel(22, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                )
                .addRewardForLevel(23, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                )
                .addRewardForLevel(24, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_ADVANCED)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.ADVANCED_SKILL_REWARDS).build())
                )
                .addRewardForLevel(25, builder -> builder
                        .translationKey(Translations.TOOLTIP_POTIONSPLUS_SKILL_REWARD_MASTER)
                        .addAdvancementRewards(AdvancementRewards.Builder.loot(LootTables.MASTER_SKILL_REWARDS).build())
                );
    }
}
