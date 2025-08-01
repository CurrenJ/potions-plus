package grill24.potionsplus.skill.source;

import grill24.potionsplus.advancement.AwardStatTrigger;
import grill24.potionsplus.core.SkillPointSources;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.StatAwardEvent;
import oshi.util.tuples.Pair;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class IncrementStatSource extends SkillPointSource<Pair<String, Integer>, IncrementStatSourceConfiguration> {
    public static final ResourceLocation ID = ppId("increment_stat");

    public IncrementStatSource() {
        super(IncrementStatSourceConfiguration.CODEC);
    }

    @SubscribeEvent
    public static void onAwardStat(final StatAwardEvent event) {
        // Get current stat value
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            int currentStatValue = serverPlayer.getStats().getValue(event.getStat());
            int increment = event.getValue() - currentStatValue;

            // Trigger skill point source(s)
            SkillsData.triggerSkillPointSource(event.getEntity(), SkillPointSources.INCREMENT_STAT.value(), new Pair<>(event.getStat().getName(), increment));

            // Trigger advancement
            AwardStatTrigger.INSTANCE.trigger(serverPlayer, event.getStat().getName(), event.getValue());
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(IncrementStatSourceConfiguration config, Pair<String, Integer> statAndIncrement) {
        return config.getStatName().equals(statAndIncrement.getA()) ? config.getPointsPerIncrement() * statAndIncrement.getB() : 0;
    }
}
