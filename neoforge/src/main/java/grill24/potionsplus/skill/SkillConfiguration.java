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

    public record PointsLevelingScale(int pointsMax, int levelMax, Scale scale, float a, float b, int pointsOffset) {
        public enum Scale {
            FROM_SKILL,
            LINEAR,
            LOG
        }
        public static final Codec<Scale> SCALE_CODEC = EnumCodec.create(Scale.class, Scale::valueOf);

        public static final Codec<PointsLevelingScale> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        Codec.INT.optionalFieldOf("pointsMax", -1).forGetter(PointsLevelingScale::pointsMax),
                        Codec.INT.optionalFieldOf("levelMax", -1).forGetter(PointsLevelingScale::levelMax),
                        SCALE_CODEC.optionalFieldOf("scale", Scale.LINEAR).forGetter(PointsLevelingScale::scale),
                        Codec.FLOAT.optionalFieldOf("a", 1F).forGetter(PointsLevelingScale::a),
                        Codec.FLOAT.optionalFieldOf("b", 1F).forGetter(PointsLevelingScale::b),
                        Codec.INT.optionalFieldOf("pointsOffset", 0).forGetter(PointsLevelingScale::pointsOffset)
                ).apply(codecBuilder, PointsLevelingScale::new));

        public PointsLevelingScale() {
            this(-1, -1, Scale.LINEAR, 1, 1, 0);
        }

        public float getPartialLevel(float points) {
            float pointsAdjusted = Math.max(points - pointsOffset, 0);
            return switch (scale) {
                case LOG -> (float) (Math.log10(a * pointsAdjusted + 1) * b);
                default -> a * pointsAdjusted;
            };
        }

        public PointsLevelingScale(int pointsMax, int levelMax, Scale scale, float a, float b) {
            this(pointsMax, levelMax, scale, a, b, 0);
        }

        public PointsLevelingScale(int levelMax) {
            this(-1, levelMax, Scale.LINEAR, 1, 1, 0);
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
