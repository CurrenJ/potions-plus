package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.utility.Utility;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

// Generate seed-instanced recipes for a given potion type
public class PotionUpgradeIngredients {
    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>> BASE_POTION_INGREDIENT_COUNT_DISTRIBUTION = WeightedRandomList.create(
            WeightedEntry.wrap(1, 2),
            WeightedEntry.wrap(2, 6),
            WeightedEntry.wrap(3, 1)
    );

    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>> TIER_1_INGREDIENT_COUNT_DISTRIBUTION = WeightedRandomList.create(
            WeightedEntry.wrap(2, 4),
            WeightedEntry.wrap(3, 1)
    );

    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>> TIER_2_INGREDIENT_COUNT_DISTRIBUTION = WeightedRandomList.create(
            WeightedEntry.wrap(1, 1),
            WeightedEntry.wrap(2, 4),
            WeightedEntry.wrap(3, 2)
    );

    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>> TIER_3_INGREDIENT_COUNT_DISTRIBUTION = WeightedRandomList.create(
            WeightedEntry.wrap(1, 1),
            WeightedEntry.wrap(2, 8),
            WeightedEntry.wrap(3, 4)
    );
    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>>[] ingredientCountDistributions = new WeightedRandomList[]{BASE_POTION_INGREDIENT_COUNT_DISTRIBUTION, TIER_1_INGREDIENT_COUNT_DISTRIBUTION, TIER_2_INGREDIENT_COUNT_DISTRIBUTION, TIER_3_INGREDIENT_COUNT_DISTRIBUTION};

    private final Ingredient[][] upgradeAmpUpIngredients;
    private final Ingredient[][] upgradeDurUpIngredients;
    private Ingredient[] basePotionIngredients;
    private MobEffect effect;
    private Potion basePotion;

    /*
        * @param basePotion The base potion to upgrade
        * @param basePotionIngredientTags The tag key for the base-level (from awkward potion) potion ingredients
        * @param potionUpgradeTierTags The tag keys for the upgrade ingredients by tier
        * @param additionalTags Additional tags to sample from. If additional tags are present, items sampled from the upgrade ingredients and base ingredients must be present in at least one additional tag.
        * @param random The random instance to use for sampling
        * @param allBasePotionIngredients A set of all base potion ingredients generated so far. Used to ensure uniqueness.
        * @param allUpgradeIngredients A set of all upgrade ingredients generated so far. Used to ensure uniqueness.
     */
    public PotionUpgradeIngredients(Potion basePotion, int maxAmp, int maxDur, TagKey<Item>[] tieredIngredientTags, TagKey<Item>[] additionalTags, Random random, Set<PpIngredient> allRecipes) {
        this.basePotion = basePotion;
        this.effect = basePotion.getEffects().get(0).getEffect();
        this.upgradeAmpUpIngredients = new Ingredient[tieredIngredientTags.length][];
        this.upgradeDurUpIngredients = new Ingredient[tieredIngredientTags.length][];

        for (int t = 0; t < tieredIngredientTags.length; t++) {
            final int tier = t;
            if(t == 0) {
                sampleUniqueIngredientsFromTag(tieredIngredientTags[tier], additionalTags, random, ingredientCountDistributions[tier].getRandom(random).get().getData(), allRecipes, this::setBasePotionIngredients);
            }
            else {
                if(t < maxAmp)
                    sampleUniqueIngredientsFromTag(tieredIngredientTags[tier], additionalTags, random, ingredientCountDistributions[tier].getRandom(random).get().getData(), allRecipes, (ingredients) -> setUpgradeAmpUpIngredients(tier - 1, ingredients));
                if(t < maxDur)
                    sampleUniqueIngredientsFromTag(tieredIngredientTags[tier], additionalTags, random, ingredientCountDistributions[tier].getRandom(random).get().getData(), allRecipes, (ingredients) -> setUpgradeDurUpIngredients(tier - 1, ingredients));
            }
        }
    }

    private void sampleUniqueIngredientsFromTag(TagKey<Item> tagKey, TagKey<Item>[] additionalTags, Random random, int count, Set<PpIngredient> allPreviouslyGeneratedIngredients, Consumer<Ingredient[]> setter) {
        for (int i = 0; i < count; i++) {
            Ingredient[] ingredients;
            PpMultiIngredient items;

            int iterations = 0;
            final int MAX_ITERATIONS = 100;
            do {
                ingredients = Utility.sampleIngredientsFromTag(tagKey, additionalTags, random, count);
                items = PpMultiIngredient.of(ingredients);

                iterations++;
                if (iterations > 1) {
                    System.out.println("[BCR] Regenerating ingredients for recipe from " + tagKey.location() + " due to collision: " + items);
                }
            } while (allPreviouslyGeneratedIngredients.contains(items) && iterations < MAX_ITERATIONS);

            setter.accept(ingredients);
            allPreviouslyGeneratedIngredients.add(items);
            // We need a List<Item> to check for uniqueness. We can't use Ingredient[] because it doesn't override equals/hashCode. Would like to use a mixin to add these methods, but don't want to have side-effects elsewhere in the game code.
            // Maybe make an encapsulating class that has an Ingredient[] and overrides equals/hashCode?

            if (iterations >= MAX_ITERATIONS) {
                throw new IllegalStateException("Could not generate unique ingredients for recipe from tag " + tagKey.location() + ". Please check the tag contents.");
            }
        }
    }

    public void setUpgradeAmpUpIngredients(int a, Ingredient[] ingredients) {
        this.upgradeAmpUpIngredients[a] = ingredients;
    }

    public void setUpgradeDurUpIngredients(int d, Ingredient[] ingredients) {
        this.upgradeDurUpIngredients[d] = ingredients;
    }

    public void setBasePotionIngredients(Ingredient[] ingredients) {
        this.basePotionIngredients = ingredients;
    }

    public Potion getBasePotion() {
        return basePotion;
    }

    public MobEffect getEffect() {
        return effect;
    }

    public Ingredient[] getUpgradeAmpUpIngredients(int a) {
        if (a < upgradeAmpUpIngredients.length)
            return upgradeAmpUpIngredients[a];
        return new Ingredient[0];
    }

    public Ingredient[] getUpgradeDurUpIngredients(int d) {
        if (d < upgradeDurUpIngredients.length)
            return upgradeDurUpIngredients[d];
        return new Ingredient[0];
    }

    public Ingredient[] getBasePotionIngredients() {
        return basePotionIngredients;
    }
}
