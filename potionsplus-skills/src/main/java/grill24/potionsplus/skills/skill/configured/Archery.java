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

public class Archery {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.ARCHERY, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_ARCHERY,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.BOW)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_BOW),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 10),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_BOW_USE_SPEED_INCREASE))
                                .addRewardForLevel(9, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_PUNCH_INCREASE))
                                .addRewardForLevel(13, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_UNBREAKING_INCREASE))
                                .addRewardForLevel(17, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_BOW_USE_SPEED_INCREASE))
                                .addRewardForLevel(20, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE))
                                .addRewardForLevel(28, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_LOOTING_INCREASE))
                                .addRewardForLevel(32, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.MEDIUM_BOW_USE_SPEED_INCREASE))
                                .addRewardForLevel(42, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_PUNCH_INCREASE))
                                .addRewardForLevel(50, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE))
                                .addRewardForLevel(55, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_BOW_USE_SPEED_INCREASE))
                                .addRewardForLevel(63, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_LOOTING_INCREASE))
                                .addRewardForLevel(75, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE))
                                .addRewardForLevel(88, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_BOW_USE_SPEED_INCREASE))
                                .addRewardForLevel(100, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.BOW_POWER_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
