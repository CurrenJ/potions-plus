package grill24.potionsplus.core;

import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

        // Mining
        context.register(MINING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration(
                Translations.TOOLTIP_POTIONSPLUS_SKILL_MINING,
                tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_ORE),
                new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                createDefaultRewards(context)
                        .addRewardForLevel(5, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SIMPLE_DUNGEON_LOOT_EDIBLE[0].getKey())
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_BONUS.getKey(0))
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_PICKAXE)))
                        )
                        .addRewardForLevel(7, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(0))
                        )
                        .addRewardForLevel(8, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_COPPER_ORE_ADDITIONAL_LOOT.getKey(0))
                        )
                        .addRewardForLevel(10, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ABANDONED_MINESHAFT_LOOT_EDIBLE[0].getKey())
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_BONUS.getKey(1))
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_PICKAXE)))
                        )
                        .addRewardForLevel(11, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_1.getKey())
                        )
                        .addRewardForLevel(13, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(1))
                        )
                        .addRewardForLevel(15, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.STRONGHOLD_LIBRARY_LOOT_EDIBLE[0].getKey())
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_BONUS.getKey(2))
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_PICKAXE)))
                        )
                        .addRewardForLevel(16, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_IRON_ORE_ADDITIONAL_LOOT.getKey(0))
                        )
                        .addRewardForLevel(17, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_2.getKey())
                        )
                        .addRewardForLevel(18, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_PICKAXE_EFFICIENCY_2.getKey())
                        )
                        .addRewardForLevel(20, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.DESERT_PYRAMID_LOOT_EDIBLE[0].getKey())
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_BONUS.getKey(3))
                                .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_PICKAXE)))
                        )
                        .addRewardForLevel(21, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SUBMERGED_PICKAXE_EFFICIENCY_BONUS.getKey(2))
                        )
                        .addRewardForLevel(23, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_PICKAXE_LOOT_1.getKey())
                        )
                        .addRewardForLevel(24, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_FORTUNE_BONUS.getKey(0))
                        )
                        .addRewardForLevel(25, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.JUNGLE_TEMPLE_LOOT_EDIBLE[0].getKey())
                                .addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_EFFICIENCY_BONUS.getKey(4))
                        )
                        .build(rewardLookup)
        )));

        // Woodcutting
        context.register(WOODCUTTING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WOODCUTTING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_LOG),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_BONUS.getKey(0))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_AXE)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_BONUS.getKey(1))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_AXE)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_BONUS.getKey(2))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_AXE)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_BONUS.getKey(3))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_AXE)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_EFFICIENCY_BONUS.getKey(4))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_AXE)))
                                )
                                .build(rewardLookup)
                )
        ));

        // Walking
        context.register(WALKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WALKING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.WALK),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.MOVEMENT_SPEED_BONUS.getKey(0))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.LEATHER_BOOTS)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.MOVEMENT_SPEED_BONUS.getKey(1))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.CHAINMAIL_BOOTS)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.MOVEMENT_SPEED_BONUS.getKey(2))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_BOOTS)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.MOVEMENT_SPEED_BONUS.getKey(3))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_BOOTS)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.MOVEMENT_SPEED_BONUS.getKey(4))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_BOOTS)))
                                )
                                .build(rewardLookup)
                )
        ));

        // Sprinting
        context.register(SPRINTING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SPRINTING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SPRINT),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SPRINT_SPEED_BONUS.getKey(0))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.SUGAR)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SPRINT_SPEED_BONUS.getKey(1))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SPRINT_SPEED_BONUS.getKey(2))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SPRINT_SPEED_BONUS.getKey(3))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SPRINT_SPEED_BONUS.getKey(4))
                                )
                                .build(rewardLookup)
                )
        ));

        // Sneaking
        context.register(SNEAKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SNEAKING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SNEAK),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 50),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SNEAK_SPEED_BONUS.getKey(0)))
                                .addRewardForLevel(10, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SNEAK_SPEED_BONUS.getKey(1)))
                                .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SNEAK_SPEED_BONUS.getKey(2)))
                                .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SNEAK_SPEED_BONUS.getKey(3)))
                                .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SNEAK_SPEED_BONUS.getKey(4)))
                                .build(rewardLookup)
                )
        ));

        // Jumping
        context.register(JUMPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_JUMPING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.JUMP),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.JUMP_HEIGHT_BONUS.getKey(0)))
                                .addRewardForLevel(10, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.JUMP_HEIGHT_BONUS.getKey(1)))
                                .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.JUMP_HEIGHT_BONUS.getKey(2)))
                                .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.JUMP_HEIGHT_BONUS.getKey(3)))
                                .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.JUMP_HEIGHT_BONUS.getKey(4)))
                                .build(rewardLookup)
                )
        ));

        context.register(SWORDSMANSHIP, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SWORDSMANSHIP,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_SWORD),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_BONUS.getKey(0))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_SWORD)))
                                )
                                .addRewardForLevel(6, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SKELETON_BONE_MEAL_ADDITIONAL_LOOT.getKey(0))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_BONUS.getKey(1))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_SWORD)))
                                )
                                .addRewardForLevel(14, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT.getKey(0))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_BONUS.getKey(2))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_SWORD)))
                                )
                                .addRewardForLevel(18, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.CREEPER_SAND_ADDITIONAL_LOOT.getKey(0))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_BONUS.getKey(3))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_SWORD)))
                                )
                                .addRewardForLevel(23, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_LOOTING_BONUS.getKey(0))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_BONUS.getKey(4))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_SWORD)))
                                )
                                .build(rewardLookup)
                )
        ));

        context.register(CHOPPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_CHOPPING,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_AXE),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_BONUS.getKey(0))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.WOODEN_AXE)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_BONUS.getKey(1))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.STONE_AXE)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_BONUS.getKey(2))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.IRON_AXE)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_BONUS.getKey(3))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.GOLDEN_AXE)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.AXE_DAMAGE_BONUS.getKey(4))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.DIAMOND_AXE)))
                                )
                                .build(rewardLookup)
                )
        ));

        context.register(ARCHERY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_ARCHERY,
                        tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_BOW),
                        new SkillConfiguration.PointsLevelingScale(-1, 25, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_BONUS.getKey(0))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(10, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_BONUS.getKey(1))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(15, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_BONUS.getKey(2))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(20, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_BONUS.getKey(3))
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.ANIMATED_ITEMS.getKey(new ItemStack(Items.BOW)))
                                )
                                .addRewardForLevel(25, builder -> builder
                                        .addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_BONUS.getKey(4))
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

        return builder;
    }
}
