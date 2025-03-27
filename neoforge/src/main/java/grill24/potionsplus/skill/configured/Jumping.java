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

public class Jumping {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.JUMPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_JUMPING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.RABBIT_FOOT)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.JUMP),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 25),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(7, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(11, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(13, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_UNLOCK))
                                .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(17, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(21, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.DOUBLE_JUMP_COUNT_INCREASE))
                                .addRewardForLevel(25, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_INCREASE))
                                .addRewardForLevel(27, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(30, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(31, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_INCREASE))
                                .addRewardForLevel(36, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.DOUBLE_JUMP_COUNT_INCREASE))
                                .addRewardForLevel(38, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(42, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(44, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(49, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_INCREASE))
                                .addRewardForLevel(58, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(62, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.DOUBLE_JUMP_COUNT_INCREASE))
                                .addRewardForLevel(64, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_INCREASE))
                                .addRewardForLevel(68, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(72, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(77, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_INCREASE))
                                .addRewardForLevel(82, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(85, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAVED_BY_THE_BOUNCE_INCREASE))
                                .addRewardForLevel(91, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .addRewardForLevel(96, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SAFE_FALL_DISTANCE_INCREASE))
                                .addRewardForLevel(100, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_JUMP_HEIGHT_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
