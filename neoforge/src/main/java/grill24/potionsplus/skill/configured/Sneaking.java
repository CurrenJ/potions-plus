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

public class Sneaking {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.SNEAKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_SNEAKING,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.LEATHER_BOOTS)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.SNEAK),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 25),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(13, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(27, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(46, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(55, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(69, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(84, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .addRewardForLevel(91, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_SNEAK_SPEED_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
