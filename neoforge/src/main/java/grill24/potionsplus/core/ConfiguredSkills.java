package grill24.potionsplus.core;

import grill24.potionsplus.data.AdvancementProvider;
import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.stream.Stream;

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
        HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

        // Join arrays into list
        List<Milestone> allOreHats = Stream.of(
                AdvancementProvider.MINE_COPPER_ORES,
                AdvancementProvider.MINE_COAL_ORES,
                AdvancementProvider.MINE_IRON_ORES,
                AdvancementProvider.MINE_GOLD_ORES,
                AdvancementProvider.MINE_DIAMOND_ORES,
                AdvancementProvider.MINE_EMERALD_ORES
        ).flatMap(Stream::of).map(Milestone::new).toList();

        // Mining
        context.register(MINING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration(
                Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING,
                new TreeMap<>(Map.of(
                        0, new ItemStack(Items.COAL_ORE),
                        15, new ItemStack(Items.COPPER_ORE),
                        30, new ItemStack(Items.IRON_ORE),
                        45, new ItemStack(Items.GOLD_ORE),
                        60, new ItemStack(Items.DIAMOND_ORE),
                        75, new ItemStack(Items.EMERALD_ORE),
                        100, new ItemStack(Items.ANCIENT_DEBRIS)
                )),
                tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_ORE),
                new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                allOreHats,
                createDefaultRewards(context)
                        .addRewardForLevel(5, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SIMPLE_DUNGEON_LOOT_EDIBLE[0].getKey())
                        )
                        .addRewardForLevel(8, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_INCREASE)
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_COPPER_ORE_ADDITIONAL_LOOT)
                        )
                        .addRewardForLevel(10, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ABANDONED_MINESHAFT_LOOT_EDIBLE[0].getKey())
                        )
                        .addRewardForLevel(11, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_1.getKey())
                        )
                        .addRewardForLevel(12, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SUBMERGED_PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(13, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SUBMERGED_PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(15, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.STRONGHOLD_LIBRARY_LOOT_EDIBLE[0].getKey())
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.COPPER_ORE)))
                        )
                        .addRewardForLevel(16, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_IRON_ORE_ADDITIONAL_LOOT)
                        )
                        .addRewardForLevel(17, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_2)
                        )
                        .addRewardForLevel(18, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_PICKAXE_EFFICIENCY_2)
                        )
                        .addRewardForLevel(20, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.DESERT_PYRAMID_LOOT_EDIBLE[0])
                        )
                        .addRewardForLevel(21, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SUBMERGED_PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(23, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_PICKAXE_LOOT_1)
                        )
                        .addRewardForLevel(24, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_FORTUNE_INCREASE)
                        )
                        .addRewardForLevel(25, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.JUNGLE_TEMPLE_LOOT_EDIBLE[0])
                        )
                        .addRewardForLevel(27, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(30, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_ORE)))
                        )
                        .addRewardForLevel(41, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(45, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLD_ORE)))
                        )
                        .addRewardForLevel(60, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_ORE)))
                        )
                        .addRewardForLevel(64, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(80, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.EMERALD_ORE)))
                        )
                        .addRewardForLevel(81, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .addRewardForLevel(90, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.ANCIENT_DEBRIS)))
                        )
                        .build(rewardLookup)
        )));

        // Woodcutting
        context.register(WOODCUTTING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WOODCUTTING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.OAK_LOG),
                                10, new ItemStack(Items.SPRUCE_LOG),
                                15, new ItemStack(Items.BIRCH_LOG),
                                20, new ItemStack(Items.JUNGLE_LOG),
                                25, new ItemStack(Items.ACACIA_LOG)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_LOG),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_AXE)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_AXE)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_AXE)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_AXE)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_AXE)))
                                )
                                .build(rewardLookup)
                )
        ));

        // Walking
        context.register(WALKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WALKING,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.LEATHER_BOOTS),
                                20, new ItemStack(Items.CHAINMAIL_BOOTS),
                                40, new ItemStack(Items.IRON_BOOTS),
                                60, new ItemStack(Items.GOLDEN_BOOTS),
                                80, new ItemStack(Items.DIAMOND_BOOTS),
                                100, new ItemStack(Items.NETHERITE_BOOTS)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.WALK),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(6, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(7, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.CHAINMAIL_BOOTS)))
                                )
                                .addRewardForLevel(30, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(40, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_BOOTS)))
                                )
                                .addRewardForLevel(50, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(60, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_BOOTS)))
                                )
                                .addRewardForLevel(80, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_BOOTS)))
                                )
                                .addRewardForLevel(85, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(100, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.NETHERITE_BOOTS)))
                                )
                                .build(rewardLookup)
                )
        ));

        // Sprinting
        context.register(SPRINTING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SPRINTING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.SUGAR),
                                10, new ItemStack(Items.SUGAR),
                                15, new ItemStack(Items.SUGAR),
                                20, new ItemStack(Items.SUGAR),
                                25, new ItemStack(Items.SUGAR)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SPRINT),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.SUGAR)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE)
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE)
                                )
                                .build(rewardLookup)
                )
        ));

        // Sneaking
        context.register(SNEAKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SNEAKING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.LEATHER_BOOTS),
                                10, new ItemStack(Items.LEATHER_BOOTS),
                                15, new ItemStack(Items.LEATHER_BOOTS),
                                20, new ItemStack(Items.LEATHER_BOOTS),
                                25, new ItemStack(Items.LEATHER_BOOTS)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SNEAK),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 50),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(10, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .build(rewardLookup)
                )
        ));

        // Jumping
        context.register(JUMPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_JUMPING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.RABBIT_FOOT),
                                10, new ItemStack(Items.RABBIT_FOOT),
                                15, new ItemStack(Items.RABBIT_FOOT),
                                20, new ItemStack(Items.RABBIT_FOOT),
                                25, new ItemStack(Items.RABBIT_FOOT)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.JUMP),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(10, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .build(rewardLookup)
                )
        ));

        context.register(SWORDSMANSHIP, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SWORDSMANSHIP,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.WOODEN_SWORD),
                                10, new ItemStack(Items.STONE_SWORD),
                                15, new ItemStack(Items.IRON_SWORD),
                                20, new ItemStack(Items.GOLDEN_SWORD),
                                25, new ItemStack(Items.DIAMOND_SWORD)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_SWORD),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_SWORD)))
                                )
                                .addRewardForLevel(6, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SKELETON_BONE_MEAL_ADDITIONAL_LOOT)
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_SWORD)))
                                )
                                .addRewardForLevel(14, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT)
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_SWORD)))
                                )
                                .addRewardForLevel(18, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.CREEPER_SAND_ADDITIONAL_LOOT)
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_SWORD)))
                                )
                                .addRewardForLevel(23, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_LOOTING_INCREASE)
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_SWORD)))
                                )
                                .build(rewardLookup)
                )
        ));

        context.register(CHOPPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_CHOPPING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.IRON_AXE),
                                10, new ItemStack(Items.IRON_AXE),
                                15, new ItemStack(Items.IRON_AXE),
                                20, new ItemStack(Items.IRON_AXE),
                                25, new ItemStack(Items.IRON_AXE)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_AXE),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_AXE)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_AXE)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_AXE)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_AXE)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_AXE)))
                                )
                                .build(rewardLookup)
                )
        ));

        context.register(ARCHERY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_ARCHERY,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.BOW),
                                10, new ItemStack(Items.BOW),
                                15, new ItemStack(Items.BOW),
                                20, new ItemStack(Items.BOW),
                                25, new ItemStack(Items.BOW)
                        )),
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_BOW),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        List.of(),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE)
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .build(rewardLookup)
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

    private static SkillLevelUpRewardsConfiguration.Builder createDefaultRewards(BootstrapContext<ConfiguredSkill<?, ?>> context) {
        HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);
        SkillLevelUpRewardsConfiguration.Builder builder = SkillLevelUpRewardsConfiguration.Builder.create();

        for (int i = 1; i < 25; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.BASIC_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 25; i < 50; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.INTERMEDIATE_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 50; i < 75; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.ADVANCED_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 75; i < 90; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EXPERT_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 90; i <= 100; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.MASTER_SKILL_LOOT_EDIBLE.getKey()));
        }

        for(int i = 1; i <= 20; i++) {
            builder.addRewardForLevel(i * 5, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EDIBLE_GEMS_AND_ORES_WHEEL.getKey()));
        }

        for (int i = 1; i <= 4; i++) {
            builder.addRewardForLevel(i * 25, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EDIBLE_WHEEL_END_CITY.getKey()));
        }

        for (int i = 1; i <= 16; i++) {
            builder.addRewardForLevel(i * 6, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EDIBLE_UNKNOWN_POTION_INGREDIENT.getKey()));
        }

        return builder;
    }
}
