package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

import java.util.List;

public class BreakBlockSourceConfiguration extends SkillPointSourceConfiguration {
    public static final Codec<BreakBlockSourceConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    BreakBlockSourceConfiguration.BlockSkillPoints.CODEC.listOf().fieldOf("pointsPerBlock").forGetter(BreakBlockSourceConfiguration::getBlockSkillPoints)
            ).apply(codecBuilder, BreakBlockSourceConfiguration::new));

    public static class BlockSkillPoints {
        public static final Codec<BlockSkillPoints> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        StateTestingPredicate.CODEC.fieldOf("blockStatePredicate").forGetter(instance -> instance.blockStatePredicate),
                        Codec.BOOL.optionalFieldOf("useXpDroppedAsPoints", false).forGetter(instance -> instance.useXpDroppedAsPoints),
                        Codec.FLOAT.optionalFieldOf("points", 0F).forGetter(instance -> instance.points)
                ).apply(codecBuilder, BlockSkillPoints::new));

        public final StateTestingPredicate blockStatePredicate;
        public final boolean useXpDroppedAsPoints;
        public final float points;

        public BlockSkillPoints(BlockPredicate blockPredicate, boolean useXpDroppedAsPoints, float points) {
            if (blockPredicate instanceof StateTestingPredicate stateTestingPredicate) {
                this.blockStatePredicate = stateTestingPredicate;
                this.useXpDroppedAsPoints = useXpDroppedAsPoints;
                this.points = points;
            } else {
                throw new IllegalArgumentException("BlockPredicate must be a StateTestingPredicate");
            }
        }
    }

    private final List<BlockSkillPoints> blockSkillPoints;

    public BreakBlockSourceConfiguration(List<BlockSkillPoints> blockSkillPoints) {
        this.blockSkillPoints = blockSkillPoints;
    }

    public List<BlockSkillPoints> getBlockSkillPoints() {
        return blockSkillPoints;
    }
}
