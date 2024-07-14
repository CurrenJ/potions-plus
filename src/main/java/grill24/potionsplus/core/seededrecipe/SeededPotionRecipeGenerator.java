package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SeededPotionRecipeGenerator {
    private TagKey<Item>[] tieredIngredientTags;
    private TagKey<Item>[] additionalTags;

    public SeededPotionRecipeGenerator(SeededPotionRecipeGenerator seededPotionRecipeGenerator) {
        this.tieredIngredientTags = seededPotionRecipeGenerator.tieredIngredientTags;
        this.additionalTags = seededPotionRecipeGenerator.additionalTags;
    }

    public SeededPotionRecipeGenerator() {
        this.tieredIngredientTags = new TagKey[0];
        this.additionalTags = new TagKey[0];
    }

    public SeededPotionRecipeGenerator withTieredIngredientTags(TagKey<Item>[] tieredIngredientTags) {
        this.tieredIngredientTags = tieredIngredientTags;
        return this;
    }

    public SeededPotionRecipeGenerator withAdditionalTags(TagKey<Item>[] additionalTags) {
        this.additionalTags = additionalTags;
        return this;
    }

    public List<BrewingCauldronRecipe> generateRecipes(PotionBuilder.PotionsAmpDurMatrix potionAmpDurMatrix, Set<PpIngredient> allRecipes, Random random) {
        Potion basePotion = potionAmpDurMatrix.get(0, 0);
        PotionUpgradeIngredients potionUpgradeIngredients = new PotionUpgradeIngredients(basePotion, potionAmpDurMatrix.getAmplificationLevels(), potionAmpDurMatrix.getDurationLevels(), tieredIngredientTags, additionalTags, random, allRecipes);

        List<BrewingCauldronRecipe> recipesToAdd = brewingCauldronPotionUpgrades(0.1F, 100, "has_potion", potionAmpDurMatrix, potionUpgradeIngredients);
        for (BrewingCauldronRecipe recipe : recipesToAdd) {
            System.out.println(recipe.toString());
        }
        return recipesToAdd;
    }

    private static List<BrewingCauldronRecipe> brewingCauldronPotionUpgrades(float experience, int baseProcessingTime, String advancementNameIngredient, PotionBuilder.PotionsAmpDurMatrix potions, PotionUpgradeIngredients potionUpgradeIngredients) {
        // Iterate through all potions
        List<BrewingCauldronRecipe> allRecipes = new ArrayList<>();
        for (int a = 0; a < potions.getAmplificationLevels(); a++) {
            for (int d = 0; d < potions.getDurationLevels(); d++) {
                Potion toCraft = potions.get(a, d);
                if (a > 0) {
                    Potion ampTierBelow = potions.get(a - 1, d);
                    Ingredient[] ingredients = potionUpgradeIngredients.getUpgradeAmpUpIngredients(a - 1);
                    allRecipes.addAll(PUtil.brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, ampTierBelow, toCraft, a, ingredients));
                }
                if (d > 0) {
                    Potion durTierBelow = potions.get(a, d - 1);
                    Ingredient[] ingredients = potionUpgradeIngredients.getUpgradeDurUpIngredients(d - 1);
                    allRecipes.addAll(PUtil.brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, durTierBelow, toCraft, d, ingredients));
                }
                if (a > 0 && d > 0) {
                    // THIS WOULD BE BOTH UPGRADED. BUT NOT USING THIS RN. CAN ADD LATER. ADD FIELD TO POTIONUPGRADEINGREDIENTS
//                    Potion bothTiersBelow = potions[a - 1][d - 1].get();
//                    allRecipes.addAll(brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, bothTiersBelow, toCraft, ingredients));
                } else if (a == 0 && d == 0) {
                    allRecipes.addAll(PUtil.brewingCauldronPotionModifierForAllContainers(experience, baseProcessingTime, advancementNameIngredient, Potions.AWKWARD, toCraft, 0, potionUpgradeIngredients.getBasePotionIngredients()));
                }
            }
        }
        return allRecipes;
    }
}
