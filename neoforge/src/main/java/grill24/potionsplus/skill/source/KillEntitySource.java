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
import oshi.util.tuples.Pair;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class KillEntitySource extends SkillPointSource<Pair<ServerPlayer, LootContext>, KillEntitySourceConfiguration> {
    public static final ResourceLocation ID = ppId("kill_entity");

    public KillEntitySource() {
        super(KillEntitySourceConfiguration.CODEC);
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(final LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            Entity entity = event.getEntity();
            Pair<ServerPlayer, LootContext> evaluationData = new Pair<>(serverPlayer, EntityPredicate.createContext(serverPlayer, entity));

            SkillsData.triggerSkillPointSource((ServerPlayer) event.getSource().getEntity(), SkillPointSources.KILL_ENTITY.value(), evaluationData);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(KillEntitySourceConfiguration config, Pair<ServerPlayer, LootContext> evaluationData) {
        return config.getEntitySkillPoints().stream()
                .filter(entitySkillPoints -> {
                    Entity entity = evaluationData.getB().getParamOrNull(LootContextParams.THIS_ENTITY);
                    ServerPlayer player = evaluationData.getA();
                    return entitySkillPoints.entityPredicate().matches(player, entity) && entitySkillPoints.playerPredicate().matches(player, player);
                })
                .mapToInt(KillEntitySourceConfiguration.EntitySkillPoints::points)
                .findFirst().orElse(0);
    }
}
