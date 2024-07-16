package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;

public class SeededPotionRecipeGenerator {
    private List<LootPoolSupplier> tieredIngredientPools;

    public SeededPotionRecipeGenerator(SeededPotionRecipeGenerator seededPotionRecipeGenerator) {
        this.tieredIngredientPools = new ArrayList<>(seededPotionRecipeGenerator.tieredIngredientPools);
    }

    public SeededPotionRecipeGenerator() {
        this.tieredIngredientPools = new ArrayList<>();
    }

    public SeededPotionRecipeGenerator withTieredIngredientPools(LootPoolSupplier... tieredIngredientPools) {
        this.tieredIngredientPools.clear();
        Collections.addAll(this.tieredIngredientPools, tieredIngredientPools);
        return this;
    }

    public SeededPotionRecipeGenerator withTieredIngredientPool(int tier, LootPoolSupplier pool) {
        ensureCapacity(this.tieredIngredientPools, tier, SeededIngredientsLootTables.EMPTY);

        this.tieredIngredientPools.set(tier, pool);
        return this;
    }

    @SafeVarargs
    public final SeededPotionRecipeGenerator addItemsInTagsToTierPool(int tier, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, TagKey<Item>... tags) {
        ensureCapacity(this.tieredIngredientPools, tier, SeededIngredientsLootTables.EMPTY);

        final LootPoolSupplier existingPool = tieredIngredientPools.get(tier);
        this.tieredIngredientPools.set(tier, () -> {
            LootPool.Builder pool = existingPool.getLootPool();
            SeededIngredientsLootTables.addItemsInTags(pool, weightingMode, weight, tags);
            return pool;
        });
        return this;
    }

    public SeededPotionRecipeGenerator addItemsToTierPool(int tier, SeededIngredientsLootTables.WeightingMode weightingMode, int weight, ItemLike... items) {
        ensureCapacity(this.tieredIngredientPools, tier, SeededIngredientsLootTables.EMPTY);

        this.tieredIngredientPools.set(tier, () -> {
            LootPool.Builder pool = tieredIngredientPools.get(tier).getLootPool();
            SeededIngredientsLootTables.addItems(pool, weightingMode, weight, items);
            return pool;
        });
        return this;
    }

    public SeededPotionRecipeGenerator clearTierPool(int tier) {
        ensureCapacity(this.tieredIngredientPools, tier, SeededIngredientsLootTables.EMPTY);

        this.tieredIngredientPools.set(tier, SeededIngredientsLootTables.EMPTY);
        return this;
    }

    public SeededPotionRecipeGenerator clearAllTierPools() {
        this.tieredIngredientPools.clear();
        return this;
    }

    private static <T> void ensureCapacity(List<T> list, int index, T defaultValue) {
        while (list.size() <= index) {
            list.add(defaultValue);
        }
    }

    public List<BrewingCauldronRecipe> generateRecipes(PotionBuilder.PotionsAmpDurMatrix potionAmpDurMatrix, Set<PpIngredient> allRecipes, Random random) {
        Potion basePotion = potionAmpDurMatrix.get(0, 0);
        IPotionUpgradeIngredients potionUpgradeIngredients = new PotionUpgradeIngredients(
                basePotion,
                potionAmpDurMatrix.getAmplificationLevels(),
                potionAmpDurMatrix.getDurationLevels(),
                tieredIngredientPools.stream().map((pool) -> LootTable.lootTable().withPool(pool.getLootPool()).build()).toArray(LootTable[]::new),
                random,
                allRecipes);

        List<BrewingCauldronRecipe> recipesToAdd = brewingCauldronPotionUpgrades(0.1F, 100, "has_potion", potionAmpDurMatrix, potionUpgradeIngredients);
        for (BrewingCauldronRecipe recipe : recipesToAdd) {
            System.out.println(recipe.toString());
        }
        return recipesToAdd;
    }

    private static List<BrewingCauldronRecipe> brewingCauldronPotionUpgrades(float experience, int baseProcessingTime, String advancementNameIngredient, PotionBuilder.PotionsAmpDurMatrix potions, IPotionUpgradeIngredients potionUpgradeIngredients) {
        // Iterate through all potions
        List<BrewingCauldronRecipe> allRecipes = new ArrayList<>();
        for (int a = 0; a < potions.getAmplificationLevels(); a++) {
            for (int d = 0; d < potions.getDurationLevels(); d++) {
                Potion toCraft = potions.get(a, d);
                if (a > 0) {
                    Potion ampTierBelow = potions.get(a - 1, d);
                    Ingredient[] ingredients = potionUpgradeIngredients.getUpgradeAmpUpIngredients(a - 1);
                    if (ingredients == null) {
                        PotionsPlus.LOGGER.error("[BCR] Ingredients for amplification upgrade are null: " + ampTierBelow.getRegistryName());
                    } else {
                        allRecipes.addAll(PUtil.brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, ampTierBelow, toCraft, a, ingredients));
                    }
                }
                if (d > 0) {
                    Potion durTierBelow = potions.get(a, d - 1);
                    Ingredient[] ingredients = potionUpgradeIngredients.getUpgradeDurUpIngredients(d - 1);
                    if (ingredients == null) {
                        PotionsPlus.LOGGER.error("[BCR] Ingredients for duration upgrade are null: " + durTierBelow.getRegistryName());
                    } else {
                        allRecipes.addAll(PUtil.brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, durTierBelow, toCraft, d, ingredients));
                    }
                }
                else if (a == 0 && d == 0) {
                    Ingredient[] ingredients = potionUpgradeIngredients.getBasePotionIngredients();
                    if (ingredients == null) {
                        PotionsPlus.LOGGER.error("[BCR] Ingredients for base potion are null: " + toCraft.getRegistryName());
                    } else {
                        allRecipes.addAll(PUtil.brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, Potions.AWKWARD, toCraft, 0, ingredients));
                    }
                }
            }
        }
        return allRecipes;
    }
}
