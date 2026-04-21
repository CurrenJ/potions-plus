package grill24.potionsplus.skill.source;

import grill24.potionsplus.extension.IStateTestingPredicateExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

import static grill24.potionsplus.utility.Utility.ppId;

public class BreakBlockSource extends SkillPointSource<BreakBlockSource.EvaluationData, BreakBlockSourceConfiguration> {
    public record EvaluationData(BlockState state, float experienceOrbsDropped) {
    }

    public static final Identifier ID = ppId("break_block");

    public BreakBlockSource() {
        super(BreakBlockSourceConfiguration.CODEC);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(BreakBlockSourceConfiguration config, EvaluationData evaluationData) {
        return config.getBlockSkillPoints().stream()
                .filter(blockSkillPoints -> {
                    IStateTestingPredicateExtension extension = (IStateTestingPredicateExtension) blockSkillPoints.blockStatePredicate;
                    return extension.potions_plus$test(evaluationData.state);
                })
                .map(blockSkillPoints -> {
                    if (blockSkillPoints.useXpDroppedAsPoints) {
                        return evaluationData.experienceOrbsDropped();
                    } else {
                        return blockSkillPoints.points;
                    }
                }).findFirst().orElse(0F);
    }
}
