package grill24.potionsplus.skill.configured;

import grill24.potionsplus.core.*;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.Milestone;
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

public class Mining {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, List<Milestone> allOreHats, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.MINING_CONFIGURED_KEY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration(
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
                ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_ORE),
                new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 25),
                allOreHats,
                ConfiguredSkills.createDefaultRewards(context)
                        .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(6, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SIMPLE_DUNGEON_LOOT_EDIBLE[0].getKey()))
                        .addRewardForLevel(8, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_COPPER_ORE_ADDITIONAL_LOOT))
                        .addRewardForLevel(9, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.LAST_BREATH))
                        .addRewardForLevel(10, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.ABANDONED_MINESHAFT_LOOT_EDIBLE[0].getKey()))
                        .addRewardForLevel(11, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_1.getKey()))
                        .addRewardForLevel(12, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(14, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.STRONGHOLD_LIBRARY_LOOT_EDIBLE[0].getKey()))
                        .addRewardForLevel(16, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_IRON_ORE_ADDITIONAL_LOOT))
                        .addRewardForLevel(17, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_LOOT_2))
                        .addRewardForLevel(18, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_PICKAXE_EFFICIENCY_2))
                        .addRewardForLevel(19, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.DESERT_PYRAMID_LOOT_EDIBLE[0]))
                        .addRewardForLevel(21, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(22, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.LAST_BREATH_INCREASE))
                        .addRewardForLevel(23, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CHOOSE_PICKAXE_LOOT_1))
                        .addRewardForLevel(24, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_FORTUNE_INCREASE))
                        .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.JUNGLE_TEMPLE_LOOT_EDIBLE[0]))
                        .addRewardForLevel(27, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(30, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_UNBREAKING_INCREASE))
                        .addRewardForLevel(33, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(35, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.LAST_BREATH_INCREASE))
                        .addRewardForLevel(39, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(44, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(53, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(55, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.PICKAXE_UNBREAKING_INCREASE))
                        .addRewardForLevel(59, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.LAST_BREATH_INCREASE))
                        .addRewardForLevel(67, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(72, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(78, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(85, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(93, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE))
                        .addRewardForLevel(100, builder -> builder
                                .addReward(rewardLookup, ConfiguredGrantableRewards.MEDIUM_PICKAXE_EFFICIENCY_INCREASE)
                                .addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SUBMERGED_PICKAXE_EFFICIENCY_INCREASE)
                        )
                        .build(rewardLookup)
        )));
    }
}
