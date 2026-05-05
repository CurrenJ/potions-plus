package grill24.potionsplus.skill.configured;

import grill24.potionsplus.core.*;
import grill24.potionsplus.utility.PUtil;
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

public class Woodcutting {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.WOODCUTTING, new ConfiguredSkill<>(Skills.GENERIC.value(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WOODCUTTING,
                        new TreeMap<>(Map.of(
                                0, PUtil.safeStack(Items.OAK_PLANKS),
                                15, PUtil.safeStack(Items.OAK_LOG),
                                30, PUtil.safeStack(Items.SPRUCE_LOG),
                                45, PUtil.safeStack(Items.BIRCH_LOG),
                                60, PUtil.safeStack(Items.JUNGLE_LOG),
                                75, PUtil.safeStack(Items.ACACIA_LOG)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.MINE_LOG),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(15, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(22, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_UNBREAKING_INCREASE))
                                .addRewardForLevel(24, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(32, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(36, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_UNBREAKING_INCREASE))
                                .addRewardForLevel(48, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(56, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(61, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_UNBREAKING_INCREASE))
                                .addRewardForLevel(71, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(84, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .addRewardForLevel(93, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_EFFICIENCY_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
