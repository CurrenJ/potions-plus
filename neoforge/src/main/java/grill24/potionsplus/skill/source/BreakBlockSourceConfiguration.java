package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.List;

public class BreakBlockSourceConfiguration extends SkillPointSourceConfiguration {
    public static final Codec<BreakBlockSourceConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    BreakBlockSourceConfiguration.BlockSkillPoints.CODEC.listOf().fieldOf("pointsPerBlock").forGetter(BreakBlockSourceConfiguration::getBlockSkillPoints)
            ).apply(codecBuilder, BreakBlockSourceConfiguration::new));

    public record BlockSkillPoints(BlockPredicate blockPredicate, float points) {
        public static final Codec<BlockSkillPoints> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        BlockPredicate.CODEC.fieldOf("entityPredicate").forGetter(BlockSkillPoints::blockPredicate),
                        Codec.FLOAT.fieldOf("points").forGetter(BlockSkillPoints::points)
                ).apply(codecBuilder, BlockSkillPoints::new));
    }

    private final List<BlockSkillPoints> blockSkillPoints;
    public BreakBlockSourceConfiguration(List<BlockSkillPoints> blockSkillPoints) {
        this.blockSkillPoints = blockSkillPoints;
    }

    public List<BlockSkillPoints> getBlockSkillPoints() {
        return blockSkillPoints;
    }
}
