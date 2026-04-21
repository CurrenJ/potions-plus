package grill24.potionsplus.skill.source;

import net.minecraft.resources.Identifier;
import oshi.util.tuples.Pair;

import static grill24.potionsplus.utility.Utility.ppId;

public class IncrementStatSource extends SkillPointSource<Pair<String, Integer>, IncrementStatSourceConfiguration> {
    public static final Identifier ID = ppId("increment_stat");

    public IncrementStatSource() {
        super(IncrementStatSourceConfiguration.CODEC);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(IncrementStatSourceConfiguration config, Pair<String, Integer> statAndIncrement) {
        return config.getStatName().equals(statAndIncrement.getA()) ? config.getPointsPerIncrement() * statAndIncrement.getB() : 0;
    }
}
