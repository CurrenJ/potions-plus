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

public class Walking {
    public static Holder.@NotNull Reference<ConfiguredSkill<?, ?>> generate(BootstrapContext<ConfiguredSkill<?, ?>> context, HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup, HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup) {
        return context.register(ConfiguredSkills.WALKING, new ConfiguredSkill<>(Skills.GENERIC.get(), new SkillConfiguration
                (Translations.TOOLTIP_POTIONSPLUS_SKILL_WALKING,
                        new TreeMap<>(Map.of(
                                0, new ItemStack(Items.LEATHER_BOOTS),
                                20, new ItemStack(Items.CHAINMAIL_BOOTS),
                                40, new ItemStack(Items.IRON_BOOTS),
                                60, new ItemStack(Items.GOLDEN_BOOTS),
                                80, new ItemStack(Items.DIAMOND_BOOTS),
                                100, new ItemStack(Items.NETHERITE_BOOTS)
                        )),
                        ConfiguredSkills.tryBuildValidSourceList(sourceLookup, ConfiguredSkillPointSources.WALK),
                        new SkillConfiguration.PointsLevelingScale(-1, 100, SkillConfiguration.PointsLevelingScale.Scale.EXPONENTIAL, 2, 1.2F, 0, 100),
                        List.of(),
                        ConfiguredSkills.createDefaultRewards(context)
                                .addRewardForLevel(5, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE))
                                .addRewardForLevel(12, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE))
                                .addRewardForLevel(23, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE))
                                .addRewardForLevel(35, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE))
                                .addRewardForLevel(50, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE))
                                .addRewardForLevel(85, builder -> builder.addReward(rewardLookup, ConfiguredGrantableRewards.SMALL_MOVEMENT_SPEED_INCREASE))
                                .build(rewardLookup)
                )
        ));
    }
}
