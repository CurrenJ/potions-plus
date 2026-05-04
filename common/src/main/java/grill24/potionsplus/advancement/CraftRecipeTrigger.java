package grill24.potionsplus.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Advancements;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class CraftRecipeTrigger extends SimpleCriterionTrigger<CraftRecipeTrigger.TriggerInstance> {
    public static final Identifier ID = ppId("craft_recipe");
    public static final CraftRecipeTrigger INSTANCE = new CraftRecipeTrigger();

    private CraftRecipeTrigger() {
    }

    public void trigger(ServerPlayer player, RecipeType<?> recipeType, PpIngredient recipeResult) {
        trigger(player, triggerInstance -> triggerInstance.test(recipeType, recipeResult));
    }

    @Override
    public Codec<CraftRecipeTrigger.TriggerInstance> codec() {
        return CraftRecipeTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<ResourceKey<RecipeType<?>>> recipeType, Optional<PpIngredient> recipeResult,
                                  Optional<List<BrewingCauldronRecipe.PotionMatchingCriteria>> potionMatchingCriteria) implements SimpleInstance {
        public static final Codec<CraftRecipeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(triggerInstance -> triggerInstance.player),
                ResourceKey.codec(BuiltInRegistries.RECIPE_TYPE.key()).optionalFieldOf("recipe").forGetter(triggerInstance -> triggerInstance.recipeType),
                PpIngredient.CODEC.optionalFieldOf("recipe_id").forGetter(triggerInstance -> triggerInstance.recipeResult),
                BrewingCauldronRecipe.PotionMatchingCriteria.CODEC.listOf().optionalFieldOf("potion_matching_criteria").forGetter(triggerInstance -> triggerInstance.potionMatchingCriteria)
        ).apply(instance, TriggerInstance::new));

        // In the create method, we need to pass in the recipe type and the recipe id
        public static Criterion<CraftRecipeTrigger.TriggerInstance> create(ResourceKey<RecipeType<?>> recipeType, PpIngredient recipeResult, List<BrewingCauldronRecipe.PotionMatchingCriteria> potionMatchingCriteria) {
            return new Criterion<CraftRecipeTrigger.TriggerInstance>(Advancements.CRAFT_RECIPE, new CraftRecipeTrigger.TriggerInstance(Optional.empty(), Optional.of(recipeType), Optional.of(recipeResult), Optional.of(potionMatchingCriteria)));
        }

        public static Criterion<CraftRecipeTrigger.TriggerInstance> create(ResourceKey<RecipeType<?>> recipeType, PpIngredient recipeResult) {
            return new Criterion<CraftRecipeTrigger.TriggerInstance>(Advancements.CRAFT_RECIPE, new CraftRecipeTrigger.TriggerInstance(Optional.empty(), Optional.of(recipeType), Optional.of(recipeResult), Optional.empty()));
        }

        public static Criterion<CraftRecipeTrigger.TriggerInstance> create(ResourceKey<RecipeType<?>> recipeType) {
            return new Criterion<CraftRecipeTrigger.TriggerInstance>(Advancements.CRAFT_RECIPE, new CraftRecipeTrigger.TriggerInstance(Optional.empty(), Optional.of(recipeType), Optional.empty(), Optional.empty()));
        }

        public boolean test(RecipeType<?> recipeType, PpIngredient result) {
            boolean matches = true;
            if (recipeType().isPresent()) {
                matches &= recipeType.equals(BuiltInRegistries.RECIPE_TYPE.getOrThrow(recipeType().get()).value());
            }
            if (recipeResult().isPresent()) {
                List<BrewingCauldronRecipe.PotionMatchingCriteria> criteria = potionMatchingCriteria().orElse(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH));
                matches &= PUtil.isSameItemOrPotion(result.getItemStack(), recipeResult().get().getItemStack(), criteria);
            }

            return matches;
        }
    }
}
