package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.List;

public class KillEntitySourceConfiguration extends SkillPointSourceConfiguration {
    public static final Codec<KillEntitySourceConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    EntitySkillPoints.CODEC.listOf().fieldOf("pointsPerEntity").forGetter(KillEntitySourceConfiguration::getEntitySkillPoints)
            ).apply(codecBuilder, KillEntitySourceConfiguration::new));

    public record EntitySkillPoints(EntityPredicate entityPredicate, EntityPredicate playerPredicate, int points) {
        public static final Codec<EntitySkillPoints> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        EntityPredicate.CODEC.optionalFieldOf("killedEntityPredicate", EntityPredicate.Builder.entity().build()).forGetter(EntitySkillPoints::entityPredicate),
                        EntityPredicate.CODEC.optionalFieldOf("playerEntityPredicate", EntityPredicate.Builder.entity().build()).forGetter(EntitySkillPoints::playerPredicate),
                        Codec.INT.fieldOf("points").forGetter(EntitySkillPoints::points)
                ).apply(codecBuilder, EntitySkillPoints::new));
    }

    private final List<EntitySkillPoints> entitySkillPoints;
    public KillEntitySourceConfiguration(List<EntitySkillPoints> entitySkillPoints) {
        this.entitySkillPoints = entitySkillPoints;
    }

    public List<EntitySkillPoints> getEntitySkillPoints() {
        return entitySkillPoints;
    }
}
