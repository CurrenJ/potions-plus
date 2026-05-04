package grill24.potionsplus.skill.source;

import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import static grill24.potionsplus.utility.Utility.ppId;

public class KillEntitySource extends SkillPointSource<KillEntitySource.EvaluationData, KillEntitySourceConfiguration> {
    public record EvaluationData(ServerPlayer player, LootContext context, float defaultXpToAward) {
    }

    public static final Identifier ID = ppId("kill_entity");

    public KillEntitySource() {
        super(KillEntitySourceConfiguration.CODEC);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(KillEntitySourceConfiguration config, EvaluationData evaluationData) {
        Entity killedEntity = evaluationData.context().getOptionalParameter(LootContextParams.THIS_ENTITY);
        ServerPlayer player = evaluationData.player();
        if (config.getPlayerEntityPredicate().matches(player, player)) {
            return (float) config.getEntitySkillPoints().stream()
                    .filter(entitySkillPoints -> {
                        Entity entity = evaluationData.context().getOptionalParameter(LootContextParams.THIS_ENTITY);
                        return entitySkillPoints.entityPredicate().matches(player, entity);
                    })
                    .mapToDouble(KillEntitySourceConfiguration.EntitySkillPoints::points)
                    .findFirst().orElse(evaluationData.defaultXpToAward());
        }
        return 0;
    }
}
