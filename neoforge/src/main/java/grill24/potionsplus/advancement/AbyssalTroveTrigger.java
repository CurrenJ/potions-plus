package grill24.potionsplus.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Advancements;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class AbyssalTroveTrigger extends SimpleCriterionTrigger<AbyssalTroveTrigger.TriggerInstance> {
    public static final ResourceLocation ID = ppId("abyssal_trove_trigger");
    public static final AbyssalTroveTrigger INSTANCE = new AbyssalTroveTrigger();

    private AbyssalTroveTrigger() {}

    public void trigger(ServerPlayer player, float fillPercentage, PpIngredient addedItem) {
        trigger(player, triggerInstance -> triggerInstance.test(fillPercentage, addedItem));
    }

    @Override
    public Codec<AbyssalTroveTrigger.TriggerInstance> codec() {
        return AbyssalTroveTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Float> fillPercentage, Optional<PotionUpgradeIngredients.Rarity> rarity) implements SimpleInstance {
        public static final Codec<AbyssalTroveTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(triggerInstance -> triggerInstance.player),
                Codec.FLOAT.optionalFieldOf("fill_percentage").forGetter(triggerInstance -> triggerInstance.fillPercentage),
                PotionUpgradeIngredients.Rarity.CODEC.optionalFieldOf("rarity").forGetter(triggerInstance -> triggerInstance.rarity)
        ).apply(instance, TriggerInstance::new));

        public static Criterion<AbyssalTroveTrigger.TriggerInstance> create(float fillPercentage, PotionUpgradeIngredients.Rarity rarity) {
            return Advancements.ABYSSAL_TROVE_TRIGGER.value().createCriterion(new AbyssalTroveTrigger.TriggerInstance(Optional.empty(), Optional.of(fillPercentage), Optional.of(rarity)));
        }

        public static Criterion<AbyssalTroveTrigger.TriggerInstance> create(float fillPercentage) {
            return Advancements.ABYSSAL_TROVE_TRIGGER.value().createCriterion(new AbyssalTroveTrigger.TriggerInstance(Optional.empty(), Optional.of(fillPercentage), Optional.empty()));
        }

        public static Criterion<AbyssalTroveTrigger.TriggerInstance> create(PotionUpgradeIngredients.Rarity rarity) {
            return Advancements.ABYSSAL_TROVE_TRIGGER.value().createCriterion(new AbyssalTroveTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(rarity)));
        }

        public boolean test(float fillPercentage, PpIngredient addedItem) {
            boolean matches = true;
            if (fillPercentage().isPresent()) {
                matches &= fillPercentage >= fillPercentage().get();
            }
            if (rarity().isPresent()) {
                matches &= SeededIngredientsLootTables.isRarity(rarity.get(), addedItem);
            }

            return matches;
        }
    }
}
