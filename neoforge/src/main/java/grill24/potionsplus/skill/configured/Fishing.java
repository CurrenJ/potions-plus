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

public class Fishing {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.FISHING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_FISHING,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.FISHING_ROD)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.CATCH_FISH),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 4, 0),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(8, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_LURE_INCREASE))
                                .addRewardForLevel(14, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_LUCK_OF_THE_SEA_INCREASE))
                                .addRewardForLevel(21, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_UNBREAKING_INCREASE))
                                .addRewardForLevel(34, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_LURE_INCREASE))
                                .addRewardForLevel(38, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_LUCK_OF_THE_SEA_INCREASE))
                                .addRewardForLevel(44, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_UNBREAKING_INCREASE))
                                .addRewardForLevel(49, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_LURE_INCREASE))
                                .addRewardForLevel(56, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_LUCK_OF_THE_SEA_INCREASE))
                                .addRewardForLevel(60, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.FISHING_ROD_UNBREAKING_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
