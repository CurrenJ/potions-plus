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

public class Swordsmanship {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.SWORDSMANSHIP, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SWORDSMANSHIP,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.WOODEN_SWORD),
                                10, new ItemStack(Items.STONE_SWORD),
                                15, new ItemStack(Items.IRON_SWORD),
                                20, new ItemStack(Items.GOLDEN_SWORD),
                                25, new ItemStack(Items.DIAMOND_SWORD)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_SWORD),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 15),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(12, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SKELETON_BONE_MEAL_ADDITIONAL_LOOT))
                                .addRewardForLevel(24, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE))
                                .addRewardForLevel(32, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_UNBREAKING_INCREASE))
                                .addRewardForLevel(33, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SKELETON_BONE_BLOCK_ADDITIONAL_LOOT))
                                .addRewardForLevel(35, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.CREEPER_SAND_ADDITIONAL_LOOT))
                                .addRewardForLevel(45, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE))
                                .addRewardForLevel(61, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE))
                                .addRewardForLevel(54, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_UNBREAKING_INCREASE))
                                .addRewardForLevel(73, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE))
                                .addRewardForLevel(88, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_LOOTING_INCREASE))
                                .addRewardForLevel(100, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SWORD_SHARPNESS_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
