package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.function.Consumer;

public class SkillLevelUpRewardsConfiguration {
    public static final Codec<SkillLevelUpRewardsConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    Codec.unboundedMap(Codec.STRING, SkillLevelUpRewardsData.CODEC).fieldOf("levelRewards").forGetter(instance -> instance.rewardsDataMap)
            ).apply(codecBuilder, SkillLevelUpRewardsConfiguration::new));


    public record LevelRewards(int level, SkillLevelUpRewardsData data) {}
    public final Map<String, SkillLevelUpRewardsData> rewardsDataMap;

    public SkillLevelUpRewardsConfiguration(Map<String, SkillLevelUpRewardsData> levelRewards) {
        this.rewardsDataMap = levelRewards;
    }

    public boolean hasRewardForLevel(int level) {
        return rewardsDataMap.containsKey(String.valueOf(level));
    }

    public Optional<SkillLevelUpRewardsData> tryGetRewardForLevel(int level) {
        return hasRewardForLevel(level) ? Optional.of(rewardsDataMap.get(String.valueOf(level))) : Optional.empty();
    }

    public Component getDescriptionForLevel(int level) {
        return tryGetRewardForLevel(level).map(SkillLevelUpRewardsData::getDescription).orElse(Component.empty());
    }

    public static class Builder {
        private final Map<String, SkillLevelUpRewardsData.Builder> rewardsDataMap = new HashMap<>();

        public static Builder create() {
            return new Builder();
        }

        public Builder addRewardForLevel(int level, Consumer<SkillLevelUpRewardsData.Builder> rewardDataBuilder) {
            rewardDataBuilder.accept(rewardsDataMap.computeIfAbsent(String.valueOf(level), k -> new SkillLevelUpRewardsData.Builder()));
            return this;
        }

        public SkillLevelUpRewardsConfiguration build(HolderGetter<ConfiguredPlayerAbility<?, ?>> lookup) {
            Map<String, SkillLevelUpRewardsData> builtRewardsDataMap = new HashMap<>();
            rewardsDataMap.forEach((level, builder) -> builtRewardsDataMap.put(level, builder.build(lookup)));
            return new SkillLevelUpRewardsConfiguration(builtRewardsDataMap);
        }
    }
}
