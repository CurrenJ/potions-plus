package grill24.potionsplus.core.seededrecipe;

import cpw.mods.util.Lazy;
import grill24.potionsplus.core.Potions;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

import java.util.*;

public class SeededPotionRecipes {

    private final Random random;
    private final List<BrewingCauldronRecipe> recipes;

    private final Map<Potion, PotionUpgradeIngredients> potionEffectUpgradeIngredients;

    private Set<PpIngredients> allBasePotionRecipeInputs;
    private Set<PpIngredients> allUpgradeRecipeInputs;


    public SeededPotionRecipes(long seed) {
        this.random = new Random(seed);
        this.recipes = new ArrayList<>();
        this.potionEffectUpgradeIngredients = new HashMap<>();

        this.allUpgradeRecipeInputs = new HashSet<>();
        this.allBasePotionRecipeInputs = new HashSet<>();

        generateRecipes();
    }

    private void generateRecipes() {
        addAllPotionRecipes(Potions.EXPLODING_POTIONS, Potions.TELEPORTATION_POTIONS, Potions.MAGNETIC_POTIONS, Potions.LEVITATION_POTIONS);
    }

    private void addAllPotionRecipes(Potions.PotionsAmpDurMatrix... potions) {
        for (Potions.PotionsAmpDurMatrix potionAmpDurMatrix : potions) {
            TagKey<Item>[] potionUpgradeTierTags = new TagKey[]{Tags.Items.TIER_1_POTION_INGREDIENTS, Tags.Items.TIER_2_POTION_INGREDIENTS, Tags.Items.TIER_3_POTION_INGREDIENTS};
            TagKey<Item> basePotionIngredientTags = Tags.Items.BASE_TIER_POTION_INGREDIENTS;
            Potion basePotion = potionAmpDurMatrix.get(0, 0);

            // Generate seeded upgrade ingredients
            PotionUpgradeIngredients potionUpgradeIngredients = new PotionUpgradeIngredients(basePotion, basePotionIngredientTags, potionUpgradeTierTags, random, allBasePotionRecipeInputs, allUpgradeRecipeInputs);
            potionEffectUpgradeIngredients.put(potionUpgradeIngredients.getBasePotion(), potionUpgradeIngredients);

            List<BrewingCauldronRecipe> recipesToAdd = PUtil.brewingCauldronPotionUpgrades(0.1F, 100, "has_potion", potionAmpDurMatrix, potionUpgradeIngredients);
            for (BrewingCauldronRecipe recipe : recipesToAdd) {
                System.out.println(recipe.toString());
            }
            recipes.addAll(recipesToAdd);
        }
    }

    public List<BrewingCauldronRecipe> getRecipes() {
        return recipes;
    }

    public void print() {
        for (BrewingCauldronRecipe recipe : recipes) {
            System.out.println(recipe.toString());
        }
    }
}
