package grill24.potionsplus.core;

import grill24.potionsplus.data.AdvancementProvider;
import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.configured.*;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.*;
import java.util.stream.Stream;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Data Gen Class. ConfiguredSkills are registered dynamically from datapack.
 */
public class ConfiguredSkills {
    public static final ResourceKey<ConfiguredSkill<?, ?>> MINING_CONFIGURED_KEY = register("mining");
    public static final ResourceKey<ConfiguredSkill<?, ?>> WOODCUTTING_CONFIGURED_KEY = register("woodcutting");

    public static final ResourceKey<ConfiguredSkill<?, ?>> SWORDSMANSHIP = register("swordsmanship");
    public static final ResourceKey<ConfiguredSkill<?, ?>> ARCHERY = register("archery");
    public static final ResourceKey<ConfiguredSkill<?, ?>> CHOPPING = register("chopping");

    public static final ResourceKey<ConfiguredSkill<?, ?>> WALKING = register("walking");
    public static final ResourceKey<ConfiguredSkill<?, ?>> SPRINTING = register("sprinting");
    public static final ResourceKey<ConfiguredSkill<?, ?>> SNEAKING = register("sneaking");
    public static final ResourceKey<ConfiguredSkill<?, ?>> JUMPING = register("jumping");

    // Data Gen
    public static void generate(BootstrapContext<ConfiguredSkill<?, ?>> context) {
        HolderGetter<ConfiguredSkillPointSource<?, ?>> sourceLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE);
        HolderGetter<ConfiguredGrantableReward<?, ?>> rewardLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);

        // Join arrays into list
        List<Milestone> allOreHats = Stream.of(
                AdvancementProvider.MINE_COPPER_ORES,
                AdvancementProvider.MINE_COAL_ORES,
                AdvancementProvider.MINE_IRON_ORES,
                AdvancementProvider.MINE_GOLD_ORES,
                AdvancementProvider.MINE_DIAMOND_ORES,
                AdvancementProvider.MINE_EMERALD_ORES
        ).flatMap(Stream::of).map(Milestone::new).toList();
        // Mining
        Mining.generate(context, sourceLookup, allOreHats, rewardLookup);

        // Woodcutting
        Woodcutting.generate(context, sourceLookup, rewardLookup);

        // Walking
        Walking.generate(context, sourceLookup, rewardLookup);

        // Sprinting
        Sprinting.generate(context, sourceLookup, rewardLookup);

        // Sneaking
        Sneaking.generate(context, sourceLookup, rewardLookup);

        // Jumping
        Jumping.generate(context, sourceLookup, rewardLookup);

        // Swordsmanship
        Swordsmanship.generate(context, sourceLookup, rewardLookup);

        // Axes
        Axes.generate(context, sourceLookup, rewardLookup);

        // Archery
        Archery.generate(context, sourceLookup, rewardLookup);
    }

    private static ResourceKey<ConfiguredSkill<?, ?>> register(String name) {
        return ResourceKey.create(PotionsPlusRegistries.CONFIGURED_SKILL, ppId(name));
    }

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
        for(int i = 25; i < 50; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.INTERMEDIATE_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 50; i < 75; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.ADVANCED_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 75; i < 90; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.EXPERT_SKILL_LOOT_EDIBLE.getKey()));
        }
        for(int i = 90; i <= 100; i++) {
            builder.addRewardForLevel(i, b -> b.addReward(rewardLookup, ConfiguredGrantableRewards.MASTER_SKILL_LOOT_EDIBLE.getKey()));
        }

        for(int i = 1; i <= 20; i++) {
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
}
