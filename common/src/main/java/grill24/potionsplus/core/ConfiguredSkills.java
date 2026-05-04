package grill24.potionsplus.core;

import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.Milestone;
import grill24.potionsplus.skill.configured.*;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class ConfiguredSkills {
    public static ResourceKey<ConfiguredSkill<?, ?>> ARCHERY;
    public static ResourceKey<ConfiguredSkill<?, ?>> CHOPPING;
    public static ResourceKey<ConfiguredSkill<?, ?>> FARMING;
    public static ResourceKey<ConfiguredSkill<?, ?>> JUMPING;
    public static ResourceKey<ConfiguredSkill<?, ?>> MINING;
    public static ResourceKey<ConfiguredSkill<?, ?>> SNEAKING;
    public static ResourceKey<ConfiguredSkill<?, ?>> SPRINTING;
    public static ResourceKey<ConfiguredSkill<?, ?>> SWORDSMANSHIP;
    public static ResourceKey<ConfiguredSkill<?, ?>> WALKING;
    public static ResourceKey<ConfiguredSkill<?, ?>> WOODCUTTING;

    @SafeVarargs
    public static List<Holder<ConfiguredSkillPointSource<?, ?>>> tryBuildValidSourceList(HolderGetter<ConfiguredSkillPointSource<?, ?>> lookup, ResourceKey<ConfiguredSkillPointSource<?, ?>>... keys) {
        List<Holder<ConfiguredSkillPointSource<?, ?>>> validSources = new ArrayList<>();
        for (ResourceKey<ConfiguredSkillPointSource<?, ?>> key : keys) {
            Optional<Holder.Reference<ConfiguredSkillPointSource<?, ?>>> optional = lookup.get(key);
            optional.ifPresent(validSources::add);
        }
        return validSources;
    }

    public static SkillLevelUpRewardsConfiguration.Builder createDefaultRewards(BootstrapContext<ConfiguredSkill<?, ?>> context) {
        HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);
        SkillLevelUpRewardsConfiguration.Builder builder = SkillLevelUpRewardsConfiguration.Builder.create();

        for (int i = 1; i < 25; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.BASIC_SKILL_LOOT_EDIBLE.getKey()));
        }
        for (int i = 25; i < 50; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.INTERMEDIATE_SKILL_LOOT_EDIBLE.getKey()));
        }
        for (int i = 50; i < 75; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.ADVANCED_SKILL_LOOT_EDIBLE.getKey()));
        }
        for (int i = 75; i < 90; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EXPERT_SKILL_LOOT_EDIBLE.getKey()));
        }
        for (int i = 90; i <= 100; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.MASTER_SKILL_LOOT_EDIBLE.getKey()));
        }

        for (int i = 1; i <= 20; i++) {
            builder.addRewardForLevel(i * 5, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EDIBLE_GEMS_AND_ORES_WHEEL.getKey()));
        }

        for (int i = 1; i <= 4; i++) {
            builder.addRewardForLevel(i * 25, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EDIBLE_WHEEL_END_CITY.getKey()));
        }

        for (int i = 1; i <= 16; i++) {
            builder.addRewardForLevel(i * 6, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EDIBLE_UNKNOWN_POTION_INGREDIENT.getKey()));
        }

        return builder;
    }

    public static void generate(BootstrapContext<ConfiguredSkill<?, ?>> context) {
        HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE);
        HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

        List<Milestone> allOreHats = List.of(
                new Milestone(ppId("mine_copper_ores")),
                new Milestone(ppId("mine_coal_ores")),
                new Milestone(ppId("mine_iron_ores")),
                new Milestone(ppId("mine_gold_ores")),
                new Milestone(ppId("mine_diamond_ores")),
                new Milestone(ppId("mine_emerald_ores"))
        );

        Archery.generate(context, sourceLookup, rewardLookup);
        Axes.generate(context, sourceLookup, rewardLookup);
        Farming.generate(context, sourceLookup, rewardLookup);
        Jumping.generate(context, sourceLookup, rewardLookup);
        Mining.generate(context, sourceLookup, allOreHats, rewardLookup);
        Sneaking.generate(context, sourceLookup, rewardLookup);
        Sprinting.generate(context, sourceLookup, rewardLookup);
        Swordsmanship.generate(context, sourceLookup, rewardLookup);
        Walking.generate(context, sourceLookup, rewardLookup);
        Woodcutting.generate(context, sourceLookup, rewardLookup);
    }
}
