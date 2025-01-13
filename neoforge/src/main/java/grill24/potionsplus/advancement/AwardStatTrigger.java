package grill24.potionsplus.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Advancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class AwardStatTrigger extends SimpleCriterionTrigger<AwardStatTrigger.TriggerInstance> {
    public static final ResourceLocation ID = ppId("award_stat");
    public static final AwardStatTrigger INSTANCE = new AwardStatTrigger();

    private AwardStatTrigger() {}

    public void trigger(ServerPlayer player, String statId, int totalStat) {
        trigger(player, triggerInstance -> triggerInstance.test(statId, totalStat));
    }

    @Override
    public Codec<AwardStatTrigger.TriggerInstance> codec() {
        return AwardStatTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String statId, int totalStats) implements SimpleInstance {
        public static final Codec<AwardStatTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(triggerInstance -> triggerInstance.player),
                Codec.STRING.fieldOf("statId").forGetter(triggerInstance -> triggerInstance.statId),
                Codec.INT.fieldOf("totalStats").forGetter(triggerInstance -> triggerInstance.totalStats)
        ).apply(instance, TriggerInstance::new));

        public static Criterion<AwardStatTrigger.TriggerInstance> create(String statId, int totalStats) {
            return Advancements.AWARD_STAT_TRIGGER.value().createCriterion(new AwardStatTrigger.TriggerInstance(Optional.empty(), statId, totalStats));
        }

        public boolean test(String statId, int totalStat) {
            return this.statId.equals(statId) && totalStat >= this.totalStats;
        }
    }
}
