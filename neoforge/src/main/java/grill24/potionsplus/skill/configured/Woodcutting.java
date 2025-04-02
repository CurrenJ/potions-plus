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

public class Woodcutting {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.WOODCUTTING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WOODCUTTING,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.OAK_PLANKS),
                                15, new ItemStack(Items.OAK_LOG),
                                30, new ItemStack(Items.SPRUCE_LOG),
                                45, new ItemStack(Items.BIRCH_LOG),
                                60, new ItemStack(Items.JUNGLE_LOG),
                                75, new ItemStack(Items.ACACIA_LOG)
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
