package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class SeededPotionRecipes {
    public Set<PpIngredient> allUniqueRecipeInputs = new HashSet<>(); // All unique recipe inputs. Used to avoid duplicate recipes. E.x. [1 water bottle, 1 nether wart]
    public Set<PpIngredient> allPotionsPlusIngredientsNoPotions = new HashSet<>(); // All ingredients as defined by the tiered tags. 1 item per ingredient.
    public Map<Integer, Set<PpIngredient>> allPotionsPlusIngredientsByTier = new HashMap<>(); // All ingredients as defined by the tiered tags. 1 item per ingredient.

    private final Random random;
    private final List<BrewingCauldronRecipe> recipes;

    private final Map<Potion, PotionUpgradeIngredients> potionEffectUpgradeIngredients;

    private final Set<PpIngredient> allBasePotionRecipeInputs;
    private final Set<PpIngredient> allUpgradeRecipeInputs;

    private final Map<PpIngredient, TreeNode<List<BrewingCauldronRecipe>>> outputToInput = new HashMap<>();

    public static TagKey<Item>[] POTION_INGREDIENT_TAGS = new TagKey[]{Tags.Items.BASE_TIER_POTION_INGREDIENTS, Tags.Items.TIER_1_POTION_INGREDIENTS, Tags.Items.TIER_2_POTION_INGREDIENTS, Tags.Items.TIER_3_POTION_INGREDIENTS};


    public SeededPotionRecipes(long seed) {
        this.random = new Random(seed);
        this.recipes = new ArrayList<>();
        this.potionEffectUpgradeIngredients = new HashMap<>();

        this.allUpgradeRecipeInputs = new HashSet<>();
        this.allBasePotionRecipeInputs = new HashSet<>();

        generateRecipes();
    }

    private void generateRecipes() {
        addAllPotionRecipes(Potions.getAllPotionAmpDurMatrices());
    }

    private void addAllPotionRecipes(PotionBuilder.PotionsAmpDurMatrix... potions) {
        for (PotionBuilder.PotionsAmpDurMatrix potionAmpDurMatrix : potions) {
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

    public void createRecipeTree(List<BrewingCauldronRecipe> recipes) {
        for (BrewingCauldronRecipe recipe : recipes) {
            PpIngredient output = PpIngredient.of(recipe.getResultItem());

            TreeNode<List<BrewingCauldronRecipe>> childRecipes = outputToInput.computeIfAbsent(output, k -> new TreeNode<>(new ArrayList<>()));
            childRecipes.getData().add(recipe);
            outputToInput.put(output, childRecipes);
        }

        for (BrewingCauldronRecipe recipe : recipes) {
            PpIngredient output = PpIngredient.of(recipe.getResultItem());

            if (outputToInput.containsKey(output)) {
                TreeNode<List<BrewingCauldronRecipe>> outputIngredientNode = outputToInput.computeIfAbsent(output, k -> new TreeNode<>(new ArrayList<>()));

                for (Ingredient ingredient : recipe.getIngredients()) {
                    PpIngredient input = PpIngredient.of(ingredient.getItems()[0]);

                    if (outputToInput.containsKey(input)) {
                        TreeNode<List<BrewingCauldronRecipe>> inputChildRecipes = outputToInput.get(input);
                        outputIngredientNode.addChild(inputChildRecipes);
                    }
                }
            }
        }
    }

    @Deprecated
    public TreeNode<ItemStack> getItemStackTree(PpIngredient output) {
        return getItemStackTree(Recipes.seededPotionRecipes.getRecipeSubTree(output));
    }

    @Deprecated
    public TreeNode<ItemStack> getItemStackTree(TreeNode<List<BrewingCauldronRecipe>> branch) {
        if (branch == null) {
            return null;
        }

        ItemStack outputStack = branch.getData().get(0).getResultItem();
        TreeNode<ItemStack> itemStackTree = new TreeNode<>(outputStack, null);

        return getItemStackTree(itemStackTree, branch);
    }

    @Deprecated
    public TreeNode<ItemStack> getItemStackTree(TreeNode<ItemStack> itemStackTree, TreeNode<List<BrewingCauldronRecipe>> branch) {
        if (branch == null) {
            return null;
        }

        // Process each recipe in the branch
        for (BrewingCauldronRecipe recipe : branch.getData()) {
            // Process each ingredient in the recipe
            for (Ingredient ingredient : recipe.getIngredients()) {
                // Create a child node for each ingredient
                ItemStack ingredientStack = ingredient.getItems()[0];
                TreeNode<ItemStack> ingredientNode = new TreeNode<>(ingredientStack, itemStackTree);
                itemStackTree.addChild(ingredientNode);

                // Recursively add children for the ingredient
                for (TreeNode<List<BrewingCauldronRecipe>> child : branch.getChildren()) {
                    ItemStack childOutputStack = child.getData().get(0).getResultItem();
                    if (PUtil.isSameItemOrPotion(childOutputStack, ingredientStack)) {
                        getItemStackTree(ingredientNode, child);
                    }
                }
            }
        }

        return itemStackTree;
    }

    @Deprecated
    public TreeNode<List<BrewingCauldronRecipe>> getRecipeSubTree(PpIngredient output) {
        return outputToInput.get(output);
    }


    public List<BrewingCauldronRecipe> getRecipes() {
        return recipes;
    }

    public void print() {
        for (BrewingCauldronRecipe recipe : recipes) {
            System.out.println(recipe.toString());
        }
    }

    public Set<PpIngredient> getAllBasePotionRecipeInputs() {
        return allBasePotionRecipeInputs;
    }

    public Set<PpIngredient> getAllUpgradeRecipeInputs() {
        return allUpgradeRecipeInputs;
    }
}
