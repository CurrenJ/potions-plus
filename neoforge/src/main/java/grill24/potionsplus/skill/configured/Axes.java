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

public class Axes {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.CHOPPING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_CHOPPING,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.WOODEN_AXE),
                                20, new ItemStack(Items.STONE_AXE),
                                40, new ItemStack(Items.IRON_AXE),
                                60, new ItemStack(Items.GOLDEN_AXE),
                                80, new ItemStack(Items.DIAMOND_AXE)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.KILL_ENTITY_WITH_AXE),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 15),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_DAMAGE_INCREASE))
                                .addRewardForLevel(11, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.STUN_SHOT_INCREASE))
                                .addRewardForLevel(14, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_SMITE_INCREASE))
                                .addRewardForLevel(19, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_DAMAGE_INCREASE))
                                .addRewardForLevel(23, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_LOOTING_INCREASE))
                                .addRewardForLevel(28, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.STUN_SHOT_INCREASE))
                                .addRewardForLevel(37, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_DAMAGE_INCREASE))
                                .addRewardForLevel(42, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_LOOTING_INCREASE))
                                .addRewardForLevel(45, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.STUN_SHOT_INCREASE))
                                .addRewardForLevel(56, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_DAMAGE_INCREASE))
                                .addRewardForLevel(58, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.AXE_SMITE_INCREASE))
                                .addRewardForLevel(64, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.STUN_SHOT_INCREASE))
                                .addRewardForLevel(73, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_DAMAGE_INCREASE))
                                .addRewardForLevel(87, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_AXE_DAMAGE_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
