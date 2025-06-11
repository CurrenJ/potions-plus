package grill24.potionsplus.skill.source;

import grill24.potionsplus.core.SkillPointSources;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import oshi.util.tuples.Pair;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class KillEntitySource extends SkillPointSource<KillEntitySource.EvaluationData, KillEntitySourceConfiguration> {
    public record EvaluationData(ServerPlayer player, LootContext context, float defaultXpToAward) {
    }

    public static final ResourceLocation ID = ppId("kill_entity");

    public KillEntitySource() {
        super(KillEntitySourceConfiguration.CODEC);
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(final LivingDeathEvent event) {
//        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
//            Entity entity = event.getEntity();
//            Pair<ServerPlayer, LootContext> evaluationData = new Pair<>(serverPlayer, EntityPredicate.createContext(serverPlayer, entity));
//            EvaluationData evaluationData = new EvaluationData(serverPlayer, EntityPredicate.createContext(serverPlayer, entity), event.getExperienceToDrop());
//
//            SkillsData.triggerSkillPointSource((ServerPlayer) event.getSource().getEntity(), SkillPointSources.KILL_ENTITY.value(), evaluationData);
//        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(final LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof ServerPlayer serverPlayer) {
            Entity entity = event.getEntity();
            EvaluationData evaluationData = new EvaluationData(serverPlayer, EntityPredicate.createContext(serverPlayer, entity), event.getDroppedExperience());

            SkillsData.triggerSkillPointSource(serverPlayer, SkillPointSources.KILL_ENTITY.value(), evaluationData);
        }
    }

    @Override
    public ResourceLocation getId() {
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
