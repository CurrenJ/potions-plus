package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class PotionUpgradeIngredients implements IPotionUpgradeIngredients {
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
    private final MobEffect effect;
    private final Potion basePotion;

    /*
     * @param basePotion The base potion to upgrade
     * @param basePotionIngredientTags The tag key for the base-level (from awkward potion) potion ingredients
     * @param potionUpgradeTierTags The tag keys for the upgrade ingredients by tier
     * @param additionalTags Additional tags to sample from. If additional tags are present, items sampled from the upgrade ingredients and base ingredients must be present in at least one additional tag.
     * @param random The random instance to use for sampling
     * @param allBasePotionIngredients A set of all base potion ingredients generated so far. Used to ensure uniqueness.
     * @param allUpgradeIngredients A set of all upgrade ingredients generated so far. Used to ensure uniqueness.
     */
    public PotionUpgradeIngredients(Potion basePotion, int maxAmp, int maxDur, LootTable[] tieredIngredients, Random random, Set<PpIngredient> allRecipes) {
        this.basePotion = basePotion;
        this.effect = basePotion.getEffects().get(0).getEffect();
        this.upgradeAmpUpIngredients = new Ingredient[tieredIngredients.length][];
        this.upgradeDurUpIngredients = new Ingredient[tieredIngredients.length][];

        for (int t = 0; t < tieredIngredients.length; t++) {
            final int tier = t;
            if (t == 0) {
                sampleUniqueIngredientsFromLootTable(tieredIngredients[tier], ingredientCountDistributions[tier].getRandom(random).get().getData(), allRecipes, this::setBasePotionIngredients);
            } else {
                if (t < maxAmp)
                    sampleUniqueIngredientsFromLootTable(tieredIngredients[tier], ingredientCountDistributions[tier].getRandom(random).get().getData(), allRecipes, (ingredients) -> setUpgradeAmpUpIngredients(tier - 1, ingredients));
                if (t < maxDur)
                    sampleUniqueIngredientsFromLootTable(tieredIngredients[tier], ingredientCountDistributions[tier].getRandom(random).get().getData(), allRecipes, (ingredients) -> setUpgradeDurUpIngredients(tier - 1, ingredients));
            }
        }
    }

    private static void sampleUniqueIngredientsFromLootTable(LootTable lootTable, int count, Set<PpIngredient> allPreviouslyGeneratedIngredients, Consumer<Ingredient[]> consumer) {
        Ingredient[] ingredients;
        PpMultiIngredient ppMultiIngredient;

        int iterations = 0;
        final int MAX_ITERATIONS = 100;
        do {
            ingredients = SeededIngredientsLootTables.sampleIngredients(lootTable, count);
            ppMultiIngredient = PpMultiIngredient.of(ingredients);

            iterations++;
            if (iterations > 1) {
                System.out.println("[BCR] Regenerating ingredients for recipe due to collision: " + ppMultiIngredient);
            }
        } while (allPreviouslyGeneratedIngredients.contains(ppMultiIngredient) && iterations < MAX_ITERATIONS);

        if (iterations >= MAX_ITERATIONS) {
            throw new IllegalStateException("Could not generate unique ingredients for recipe from tag " + lootTable.getLootTableId() + ". Please check the tag contents.");
        }

        consumer.accept(ingredients);
        allPreviouslyGeneratedIngredients.add(ppMultiIngredient);
    }

    private void setUpgradeAmpUpIngredients(int a, Ingredient[] ingredients) {
        this.upgradeAmpUpIngredients[a] = ingredients;
    }

    private void setUpgradeDurUpIngredients(int d, Ingredient[] ingredients) {
        this.upgradeDurUpIngredients[d] = ingredients;
    }

    private void setBasePotionIngredients(Ingredient[] ingredients) {
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
