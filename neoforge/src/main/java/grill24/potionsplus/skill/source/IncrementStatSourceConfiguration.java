package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.List;

public class IncrementStatSourceConfiguration extends SkillPointSourceConfiguration {
    public static final Codec<IncrementStatSourceConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    Codec.STRING.fieldOf("statName").forGetter(IncrementStatSourceConfiguration::getStatName),
                    Codec.FLOAT.fieldOf("pointsPerIncrement").forGetter(IncrementStatSourceConfiguration::getPointsPerIncrement)
            ).apply(codecBuilder, IncrementStatSourceConfiguration::new));

    private final String statName;
    private final float pointsPerIncrement;

    public IncrementStatSourceConfiguration(Stat<?> stat, float pointsPerIncrement) {
        this.statName = stat.getName();
        this.pointsPerIncrement = pointsPerIncrement;
    }

    private IncrementStatSourceConfiguration(String statName, float pointsPerIncrement) {
        this.statName = statName;
        this.pointsPerIncrement = pointsPerIncrement;
    }

    public String getStatName() {
        return statName;
    }

    public float getPointsPerIncrement() {
        return pointsPerIncrement;
    }
}
