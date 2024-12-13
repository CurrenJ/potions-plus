package grill24.potionsplus.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Advancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class CreatePotionsPlusBlockTrigger extends SimpleCriterionTrigger<CreatePotionsPlusBlockTrigger.TriggerInstance> {
    public static final ResourceLocation ID = ppId("create_pp_block");
    public static final CreatePotionsPlusBlockTrigger INSTANCE = new CreatePotionsPlusBlockTrigger();

    private CreatePotionsPlusBlockTrigger() {}

    public void trigger(ServerPlayer player, BlockState blockState) {
        trigger(player, triggerInstance -> triggerInstance.test(player, blockState));
    }

    @Override
    public Codec<CreatePotionsPlusBlockTrigger.TriggerInstance> codec() {
        return CreatePotionsPlusBlockTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, BlockState block) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<CreatePotionsPlusBlockTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(triggerInstance -> triggerInstance.player),
                BlockState.CODEC.fieldOf("block").forGetter(triggerInstance -> triggerInstance.block)
        ).apply(instance, TriggerInstance::new));

        public static Criterion<CreatePotionsPlusBlockTrigger.TriggerInstance> create(BlockState blockState) {
            return Advancements.BREWING_CAULDRON_CREATION.value().createCriterion(new CreatePotionsPlusBlockTrigger.TriggerInstance(Optional.empty(), blockState));
        }

        public boolean test(ServerPlayer player, @Nonnull BlockState blockState) {
            return blockState.is(block().getBlock());
        }
    }
}
