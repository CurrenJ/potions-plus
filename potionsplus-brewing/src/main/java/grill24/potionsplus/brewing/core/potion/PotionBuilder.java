package grill24.potionsplus.core.potion;

import grill24.potionsplus.core.seededrecipe.ISeededPotionRecipeBuilder;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpMultiIngredient;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipeBuilder;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PotionBuilder {
    private String name = "";
    private Supplier<MobEffectInstance> effectSupplier = null;
    private ISeededPotionRecipeBuilder potionRecipeGenerator;

    public static final int DEFAULT_DURATION = 1200; // 3 minutes

    public PotionBuilder() {
        this.potionRecipeGenerator = SeededPotionRecipeBuilder.defaultPools();
    }

    public PotionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PotionBuilder effects(Supplier<MobEffectInstance> effectSupplier) {
        this.effectSupplier = effectSupplier;
        return this;
    }

    public final PotionBuilder effect(Holder<MobEffect> effect, int duration) {
        this.effectSupplier = () -> new MobEffectInstance(effect, duration);
        return this;
    }

    public final PotionBuilder effect(Holder<MobEffect> effect) {
        this.effectSupplier = () -> new MobEffectInstance(effect, DEFAULT_DURATION);
        return this;
    }

    public PotionBuilder recipeGenerator(SeededPotionRecipeBuilder potionRecipeGenerator) {
        this.potionRecipeGenerator = potionRecipeGenerator;
        return this;
    }

    public PotionBuilder withRaritySamplingConfigs(Map<PotionUpgradeIngredients.Rarity, PotionUpgradeIngredients.IngredientSamplingConfig> tieredIngredientPools) {
        this.potionRecipeGenerator.withRaritySamplingConfigs(tieredIngredientPools);
        return this;
    }

    public PotionBuilder addItemsToRarityPool(PotionUpgradeIngredients.Rarity rarity, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, ItemLike... items) {
        this.potionRecipeGenerator.addItemsToRaritySamplingPool(rarity, weightingMode, weight, items);
        return this;
    }

    public PotionBuilder clearRaritySamplingConfig(PotionUpgradeIngredients.Rarity rarity) {
        this.potionRecipeGenerator.clearRaritySamplingConfig(rarity);
        return this;
    }

    public PotionBuilder withRarityCount(PotionUpgradeIngredients.Rarity rarity, int count) {
        this.potionRecipeGenerator.withRarityCount(rarity, count);
        return this;
    }

    public PotionsPlusPotionGenerationData build() {
        if (name.isBlank())
            throw new IllegalArgumentException("Name must be set");
        else if (effectSupplier == null)
            throw new IllegalArgumentException("Effect function must be set");

        return new PotionsPlusPotionGenerationData(name, effectSupplier, potionRecipeGenerator);
    }

    public PotionsPlusPotionGenerationData build(Consumer<PotionsPlusPotionGenerationData> consumer) {
        PotionsPlusPotionGenerationData potions = build();
        consumer.accept(potions);

        return potions;
    }

    @FunctionalInterface
    public interface DurationFunction {
        int getDurationTicks(int durationLevel);
    }

    public static class PotionsPlusPotionGenerationData {
        public final Holder<Potion> potion;
        private final ISeededPotionRecipeBuilder potionRecipeGenerator;

        public PotionsPlusPotionGenerationData(String name, Supplier<MobEffectInstance> effectSupplier, ISeededPotionRecipeBuilder potionRecipeGenerator) {
            this.potion = registerNewPotion(name, effectSupplier);
            this.potionRecipeGenerator = potionRecipeGenerator;
        }

        public static Holder<Potion> registerNewPotion(String name, Supplier<MobEffectInstance> effectSupplier) {
            return Potions.POTIONS.register(name, () -> new Potion(name, effectSupplier.get()));
        }

        public List<RecipeHolder<BrewingCauldronRecipe>> generateRecipes(Set<PpMultiIngredient> usedRecipeInputs, RandomSource random) {
            return potionRecipeGenerator.generateRecipes(this, usedRecipeInputs, random);
        }

        public String getName() {
            return potion.getKey().location().getPath();
        }
    }
}
