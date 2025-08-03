package grill24.potionsplus.skill.configured;

import grill24.potionsplus.core.*;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillConfiguration;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Farming {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.FARMING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration(
                Translations.TOOLTIP_POTIONSPLUS_SKILL_FARMING,
                new TreeMap<>(Map.of(
                        0, new ItemStack(Items.WOODEN_HOE),
                        15, new ItemStack(Items.STONE_HOE),
                        30, new ItemStack(Items.IRON_HOE),
                        45, new ItemStack(Items.GOLDEN_HOE),
                        60, new ItemStack(Items.DIAMOND_HOE),
                        75, new ItemStack(Items.NETHERITE_HOE)
                )),
                ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.HARVEST_CROPS),
                new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 15),
                List.of(),
                ConfiguredSkills.createDefaultRewards(context)
                        .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(8, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SIMPLE_DUNGEON_LOOT_EDIBLE[0].getKey()))
                        .addRewardForLevel(10, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.WHEAT_ADDITIONAL_SEEDS_LOOT.getKey()))
                        .addRewardForLevel(12, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BASIC_SKILL_LOOT_EDIBLE.getKey()))
                        .addRewardForLevel(16, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.NETHER_WART_BLAZE_POWDER_LOOT.getKey()))
                        .addRewardForLevel(18, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SIMPLE_DUNGEON_LOOT_EDIBLE[1].getKey()))
                        .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.HOE_FORTUNE_INCREASE))
                        .addRewardForLevel(22, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(24, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.POTATO_POISONOUS_POTATO_LOOT.getKey()))
                        .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.INTERMEDIATE_SKILL_LOOT_EDIBLE.getKey()))
                        .addRewardForLevel(28, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BEETROOT_SUGAR_LOOT.getKey()))
                        .addRewardForLevel(30, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(32, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.HOE_UNBREAKING_INCREASE))
                        .addRewardForLevel(35, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(38, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CARROT_GOLDEN_CARROT_LOOT.getKey()))
                        .addRewardForLevel(40, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_1.getKey()))
                        .addRewardForLevel(42, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.COCOA_BROWN_DYE_LOOT.getKey()))
                        .addRewardForLevel(45, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.ADVANCED_SKILL_LOOT_EDIBLE.getKey()))
                        .addRewardForLevel(48, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.HOE_FORTUNE_INCREASE))
                        .addRewardForLevel(55, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(60, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.HOE_UNBREAKING_INCREASE))
                        .addRewardForLevel(65, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.EXPERT_SKILL_LOOT_EDIBLE.getKey()))
                        .addRewardForLevel(75, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_2))
                        .addRewardForLevel(85, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(100, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_HOE_EFFICIENCY_INCREASE)
                                .addReward(rewardLookup, ConfiguredGrantableRewards.MASTER_SKILL_LOOT_EDIBLE.getKey())
                        )
                        .build(rewardLookup)
        )));
    }
}