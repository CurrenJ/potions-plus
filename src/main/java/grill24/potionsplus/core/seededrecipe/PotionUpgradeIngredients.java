package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.utility.Utility;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class PotionUpgradeIngredients {
    // Generate seed-instanced recipes
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
    private static final WeightedRandomList<WeightedEntry.Wrapper<Integer>>[] ingredientCountDistributions = new WeightedRandomList[]{TIER_1_INGREDIENT_COUNT_DISTRIBUTION, TIER_2_INGREDIENT_COUNT_DISTRIBUTION, TIER_3_INGREDIENT_COUNT_DISTRIBUTION};

    private final Ingredient[][] upgradeAmpUpIngredients;
    private final Ingredient[][] upgradeDurUpIngredients;
    private Ingredient[] basePotionIngredients;
    private MobEffect effect;
    private Potion basePotion;

    public PotionUpgradeIngredients(Potion basePotion, TagKey<Item> basePotionIngredientTags, TagKey<Item>[] potionUpgradeTierTags, Random random, Set<PpIngredients> allBasePotionIngredients, Set<PpIngredients> allUpgradeIngredients) {
        this.basePotion = basePotion;
        this.effect = basePotion.getEffects().get(0).getEffect();
        this.upgradeAmpUpIngredients = new Ingredient[potionUpgradeTierTags.length][];
        this.upgradeDurUpIngredients = new Ingredient[potionUpgradeTierTags.length][];

        for (int x = 0; x < potionUpgradeTierTags.length; x++) {
            int finalX = x;
            sampleUniqueIngredientsFromTag(potionUpgradeTierTags[x], random, ingredientCountDistributions[x].getRandom(random).get().getData(), allUpgradeIngredients, (ingredients) -> setUpgradeAmpUpIngredients(finalX, ingredients));
            sampleUniqueIngredientsFromTag(potionUpgradeTierTags[x], random, ingredientCountDistributions[x].getRandom(random).get().getData(), allUpgradeIngredients, (ingredients) -> setUpgradeDurUpIngredients(finalX, ingredients));
        }
        sampleUniqueIngredientsFromTag(basePotionIngredientTags, random, 1, allBasePotionIngredients, this::setBasePotionIngredients);
    }

    private void sampleUniqueIngredientsFromTag(TagKey<Item> tagKey, Random random, int count, Set<PpIngredients> allPreviouslyGeneratedIngredients, Consumer<Ingredient[]> setter) {
        for (int i = 0; i < count; i++) {
            Ingredient[] ingredients;
            PpIngredients items;

            int iterations = 0;
            final int MAX_ITERATIONS = 100;
            do {
                ingredients = Utility.sampleIngredientsFromTag(tagKey, random, count);
                items = new PpIngredients(ingredients);

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
