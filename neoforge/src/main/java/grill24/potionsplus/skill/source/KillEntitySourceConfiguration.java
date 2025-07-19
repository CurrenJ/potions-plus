package grill24.potionsplus.skill.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;

import java.util.List;

public class KillEntitySourceConfiguration extends SkillPointSourceConfiguration {
    public static final Codec<KillEntitySourceConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    EntitySkillPoints.CODEC.listOf().fieldOf("pointsPerEntity").forGetter(KillEntitySourceConfiguration::getEntitySkillPoints),
                    EntityPredicate.CODEC.optionalFieldOf("playerEntityPredicate", EntityPredicate.Builder.entity().build()).forGetter(KillEntitySourceConfiguration::getPlayerEntityPredicate)
            ).apply(codecBuilder, KillEntitySourceConfiguration::new));

    public record EntitySkillPoints(EntityPredicate entityPredicate, int points) {
        public static final Codec<EntitySkillPoints> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        EntityPredicate.CODEC.optionalFieldOf("killedEntityPredicate", EntityPredicate.Builder.entity().build()).forGetter(EntitySkillPoints::entityPredicate),
                        Codec.INT.fieldOf("points").forGetter(EntitySkillPoints::points)
                ).apply(codecBuilder, EntitySkillPoints::new));
    }

    private final List<EntitySkillPoints> entitySkillPoints;
    private final EntityPredicate playerEntityPredicate;

    public KillEntitySourceConfiguration(List<EntitySkillPoints> entitySkillPoints, EntityPredicate playerEntityPredicate) {
        this.entitySkillPoints = entitySkillPoints;
        this.playerEntityPredicate = playerEntityPredicate;
    }

    public List<EntitySkillPoints> getEntitySkillPoints() {
        return entitySkillPoints;
    }

    public EntityPredicate getPlayerEntityPredicate() {
        return playerEntityPredicate;
    }
}
