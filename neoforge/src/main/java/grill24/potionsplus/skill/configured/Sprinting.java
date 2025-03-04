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

public class Sprinting {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.SPRINTING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SPRINTING,
                        new TreeMap<>(Map.of(
                                5, new ItemStack(Items.SUGAR)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SPRINT),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(17, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE))
                                .addRewardForLevel(33, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE))
                                .addRewardForLevel(49, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE))
                                .addRewardForLevel(67, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE))
                                .addRewardForLevel(85, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE))
                                .addRewardForLevel(100, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SPRINT_SPEED_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
