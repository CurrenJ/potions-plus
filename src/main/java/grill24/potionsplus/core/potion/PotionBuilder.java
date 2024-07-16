package grill24.potionsplus.core.potion;

import grill24.potionsplus.core.seededrecipe.LootPoolSupplier;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipeGenerator;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class PotionBuilder {
    private String name = "";
    private int amplificationLevels = 0;
    private int durationLevels = 0;
    private Function<int[], MobEffectInstance[]> effectFunction = null;
    private SeededPotionRecipeGenerator potionRecipeGenerator = DEFAULT_POTION_RECIPE_GENERATOR;

    public static final DurationFunction DEFAULT_DURATION_FUNCTION = (int durationLevel) -> (durationLevel + 1) * 3600;
    public static final DurationFunction LONGER_DURATION_FUNCTION = (int durationLevel) -> (durationLevel + 1) * 5000;

    private static final List<LootPoolSupplier> DEFAULT_TIERED_INGREDIENT_TAGS = List.of(
            SeededIngredientsLootTables.TIER_0_INGREDIENTS,
            SeededIngredientsLootTables.TIER_1_INGREDIENTS,
            SeededIngredientsLootTables.TIER_2_INGREDIENTS,
            SeededIngredientsLootTables.TIER_3_INGREDIENTS
    );
    public static final SeededPotionRecipeGenerator DEFAULT_POTION_RECIPE_GENERATOR = new SeededPotionRecipeGenerator().withTieredIngredientPools(DEFAULT_TIERED_INGREDIENT_TAGS);

    public PotionBuilder() {}

    public PotionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PotionBuilder maxAmp(int maxAmp) {
        this.amplificationLevels = maxAmp;
        return this;
    }

    public PotionBuilder maxDur(int maxDur) {
        this.durationLevels = maxDur;
        return this;
    }

    public PotionBuilder effects(Function<int[], MobEffectInstance[]> effectFunction) {
        this.effectFunction = effectFunction;
        return this;
    }

    public PotionBuilder effects(MobEffectSupplier... effects) {
        this.effectFunction = (int[] levels) -> {
            MobEffectInstance[] instances = new MobEffectInstance[effects.length];
            for (int i = 0; i < effects.length; i++) {
                instances[i] = new MobEffectInstance(effects[i].get(), DEFAULT_DURATION_FUNCTION.getDurationTicks(levels[1]), levels[0]);
            }
            return instances;
        };
        return this;
    }

    public PotionBuilder effects(DurationFunction durationFunction, MobEffectSupplier... effects) {
        this.effectFunction = (int[] levels) -> {
            MobEffectInstance[] instances = new MobEffectInstance[effects.length];
            for (int i = 0; i < effects.length; i++) {
                instances[i] = new MobEffectInstance(effects[i].get(), durationFunction.getDurationTicks(levels[1]), levels[0]);
            }
            return instances;
        };
        return this;
    }

    public PotionBuilder recipeGenerator(SeededPotionRecipeGenerator potionRecipeGenerator) {
        this.potionRecipeGenerator = potionRecipeGenerator;
        return this;
    }

    public PotionBuilder addTagToTierPool(int tier, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, TagKey<Item>... tags) {
        this.potionRecipeGenerator.addItemsInTagsToTierPool(tier, weightingMode, weight, tags);
        return this;
    }

    public PotionBuilder addItemsToTierPool(int tier, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, Item... items) {
        this.potionRecipeGenerator.addItemsToTierPool(tier, weightingMode, weight, items);
        return this;
    }

    public PotionBuilder clearTierPool(int tier) {
        this.potionRecipeGenerator.clearTierPool(tier);
        return this;
    }

    public PotionBuilder clearAllTierPools() {
        this.potionRecipeGenerator.clearAllTierPools();
        return this;
    }

    public PotionsAmpDurMatrix build() {
        if (amplificationLevels < 1 || durationLevels < 1)
            throw new IllegalArgumentException("Amplification and duration levels must be at least 1");
        else if (name.isBlank())
            throw new IllegalArgumentException("Name must be set");
        else if (effectFunction == null)
            throw new IllegalArgumentException("Effect function must be set");

        return new PotionsAmpDurMatrix(name, amplificationLevels, durationLevels, effectFunction, potionRecipeGenerator);
    }

    public PotionsAmpDurMatrix build(Consumer<PotionsAmpDurMatrix> consumer) {
        PotionsAmpDurMatrix potions = build();
        consumer.accept(potions);

        return potions;
    }

    @FunctionalInterface
    public interface DurationFunction {
        int getDurationTicks(int durationLevel);
    }

    @FunctionalInterface
    public interface MobEffectSupplier {
        MobEffect get();
    }

    public static class PotionsAmpDurMatrix {
        public final RegistryObject<Potion>[][] potions;
        private final SeededPotionRecipeGenerator potionRecipeGenerator;

        public PotionsAmpDurMatrix(String name, int amplificationLevels, int durationLevels, Function<int[], MobEffectInstance[]> effectFunction, SeededPotionRecipeGenerator potionRecipeGenerator) {
            this.potions = registerNewPotion(name, amplificationLevels, durationLevels, effectFunction);
            this.potionRecipeGenerator = potionRecipeGenerator;
        }

        public static RegistryObject<Potion>[][] registerNewPotion(String name, int amplificationLevels, int durationLevels, Function<int[], MobEffectInstance[]> effectFunction) {
            if (amplificationLevels < 1 || durationLevels < 1)
                throw new IllegalArgumentException("Amplification and duration levels must be at least 1");

            RegistryObject<Potion>[][] potions = new RegistryObject[amplificationLevels][durationLevels];
            for (int i = 0; i < amplificationLevels; i++) {

                for (int j = 0; j < durationLevels; j++) {
                    int finalI = i;
                    int finalJ = j;

                    potions[i][j] = Potions.POTIONS.register(name + "_a" + i + "_d" + j, () ->
                            new Potion(name, effectFunction.apply(new int[]{finalI, finalJ}))
                    );
                }
            }

            return potions;
        }

        public Potion get(int a, int d) {
            return potions[a][d].get();
        }

        public int getAmplificationLevels() {
            return potions.length;
        }

        public int getDurationLevels() {
            return potions[0].length;
        }

        public String getEffectName() {
            try {
                return potions[0][0].get().getEffects().get(0).getEffect().getRegistryName().getPath();
            } catch (NullPointerException e) {
                return "";
            }
        }

        public List<BrewingCauldronRecipe> generateRecipes(Set<PpIngredient> allRecipes, Random random) {
            return potionRecipeGenerator.generateRecipes(this, allRecipes, random);
        }
    }
}
