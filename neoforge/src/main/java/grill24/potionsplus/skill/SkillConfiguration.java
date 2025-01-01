package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import grill24.potionsplus.utility.EnumCodec;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.Map;

public class SkillConfiguration {
    public static final Codec<SkillConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    SkillConfigurationData.CODEC.fieldOf("baseConfig").forGetter(SkillConfiguration::getData)
            ).apply(codecBuilder, SkillConfiguration::new));

    public record SkillConfigurationData(String translationKey, List<Holder<ConfiguredSkillPointSource<?, ?>>> skillPointSources, PointsLevelingScale pointsLevelingScale, SkillLevelUpRewardsConfiguration rewardsConfiguration) {
        public static final Codec<SkillConfigurationData> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        Codec.STRING.optionalFieldOf("translationKey", "").forGetter(SkillConfigurationData::translationKey),
                        ConfiguredSkillPointSource.CODEC.listOf().fieldOf("skillPointSources").forGetter(SkillConfigurationData::skillPointSources),
                        PointsLevelingScale.CODEC.optionalFieldOf("pointsLevelingScale", new PointsLevelingScale()).forGetter(SkillConfigurationData::pointsLevelingScale),
                        SkillLevelUpRewardsConfiguration.CODEC.optionalFieldOf("rewardsConfig", new SkillLevelUpRewardsConfiguration(Map.of())).forGetter(SkillConfigurationData::rewardsConfiguration)
                ).apply(codecBuilder, SkillConfigurationData::new));
    }

    public record PointsLevelingScale(int pointsMax, int levelMax, Scale scale, float a, float b, int c, float d) {
        public enum Scale {
            FROM_SKILL,
            LINEAR,
            EXPONENTIAL
        }
        public static final Codec<Scale> SCALE_CODEC = EnumCodec.create(Scale.class, Scale::valueOf);

        public static final Codec<PointsLevelingScale> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        Codec.INT.optionalFieldOf("pointsMax", -1).forGetter(PointsLevelingScale::pointsMax),
                        Codec.INT.optionalFieldOf("levelMax", -1).forGetter(PointsLevelingScale::levelMax),
                        SCALE_CODEC.optionalFieldOf("scale", Scale.LINEAR).forGetter(PointsLevelingScale::scale),
                        Codec.FLOAT.optionalFieldOf("a", 1F).forGetter(PointsLevelingScale::a),
                        Codec.FLOAT.optionalFieldOf("b", 1F).forGetter(PointsLevelingScale::b),
                        Codec.INT.optionalFieldOf("c", 0).forGetter(PointsLevelingScale::c),
                        Codec.FLOAT.optionalFieldOf("d", 0F).forGetter(PointsLevelingScale::d)
                ).apply(codecBuilder, PointsLevelingScale::new));

        public PointsLevelingScale() {
            this(-1, -1, Scale.LINEAR, 1, 1, 0, 0);
        }

        public float getPartialLevel(float totalPoints) {
            // Assuming points are always increasing, we can binary search for the level
            // Total points should be between the level and the next level
            // We want to values: the level (points required < totalPoints) and the next level (points required > totalPoints)
            int left = 0;
            int right = levelMax;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (totalPoints >= getPointsForLevel(mid)) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            int currentLevel = left - 1;
            float partialLevel = (totalPoints - getPointsForLevel(currentLevel)) / (getPointsForLevel(currentLevel + 1) - getPointsForLevel(currentLevel));
            return currentLevel + (partialLevel > 0 ? partialLevel : 0);
        }

        public float getPointsForLevel(int level) {
            if (level == 0) {
                return 0;
            }
            return switch (scale) {
                case LINEAR -> a * level + b;
                case EXPONENTIAL -> (float) Math.pow(Math.pow(level, a) + c + (d * level), b);
                case FROM_SKILL -> level;
                default -> 0;
            };
        }
    }
    private final SkillConfigurationData data;

    public SkillConfiguration(SkillConfigurationData data) {
        this.data = data;
    }

    public SkillConfiguration(List<Holder<ConfiguredSkillPointSource<?, ?>>> skillPointSources, PointsLevelingScale pointsLevelingScale, SkillLevelUpRewardsConfiguration rewardsConfiguration) {
        this.data = new SkillConfigurationData("", skillPointSources, pointsLevelingScale, rewardsConfiguration);
    }

    public SkillConfiguration(String translationKey, List<Holder<ConfiguredSkillPointSource<?, ?>>> skillPointSources, PointsLevelingScale pointsLevelingScale, SkillLevelUpRewardsConfiguration rewardsConfiguration) {
        this.data = new SkillConfigurationData(translationKey, skillPointSources, pointsLevelingScale, rewardsConfiguration);
    }

    public SkillConfigurationData getData() {
        return data;
    }
}
