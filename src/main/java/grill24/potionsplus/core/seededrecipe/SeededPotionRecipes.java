package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.Potions;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class SeededPotionRecipes {

    private final Random random;
    private final List<BrewingCauldronRecipe> recipes;

    private final Map<Potion, PotionUpgradeIngredients> potionEffectUpgradeIngredients;

    private Set<PpIngredients> allBasePotionRecipeInputs;
    private Set<PpIngredients> allUpgradeRecipeInputs;

    private Map<PpIngredients, TreeNode<List<BrewingCauldronRecipe>>> outputToInput = new HashMap<>();

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
        addAllPotionRecipes(
                Potions.EXPLODING_POTIONS,
                Potions.TELEPORTATION_POTIONS,
                Potions.MAGNETIC_POTIONS,
                Potions.LEVITATION_POTIONS,
                Potions.LOOTING_POTIONS,
                Potions.FORTUITOUS_FATE_POTIONS,
                Potions.METAL_DETECTING_POTIONS,
                Potions.GIANT_STEPS_POTIONS,
                Potions.REACH_FOR_THE_STARS_POTIONS);
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

    public void createRecipeTree(List<BrewingCauldronRecipe> recipes) {
        for (BrewingCauldronRecipe recipe : recipes) {
            PpIngredients output = new PpIngredients(recipe.getResultItem());

            TreeNode<List<BrewingCauldronRecipe>> childRecipes = outputToInput.computeIfAbsent(output, k -> new TreeNode<>(new ArrayList<>()));
            childRecipes.getData().add(recipe);
            outputToInput.put(output, childRecipes);
        }

        for (BrewingCauldronRecipe recipe : recipes) {
            PpIngredients output = new PpIngredients(recipe.getResultItem());

            if (outputToInput.containsKey(output)) {
                TreeNode<List<BrewingCauldronRecipe>> outputIngredientNode = outputToInput.computeIfAbsent(output, k -> new TreeNode<>(new ArrayList<>()));

                for (Ingredient ingredient : recipe.getIngredients()) {
                    PpIngredients input = new PpIngredients(ingredient.getItems()[0]);

                    if (outputToInput.containsKey(input)) {
                        TreeNode<List<BrewingCauldronRecipe>> inputChildRecipes = outputToInput.get(input);
                        outputIngredientNode.addChild(inputChildRecipes);
                    }
                }
            }
        }
    }

    public TreeNode<ItemStack> getItemStackTree(PpIngredients output) {
        return getItemStackTree(Recipes.seededPotionRecipes.getRecipeSubTree(output));
    }

    public TreeNode<ItemStack> getItemStackTree(TreeNode<List<BrewingCauldronRecipe>> branch) {
        if (branch == null) {
            return null;
        }

        ItemStack outputStack = branch.getData().get(0).getResultItem();
        TreeNode<ItemStack> itemStackTree = new TreeNode<>(outputStack, null);

        return getItemStackTree(itemStackTree, branch);
    }

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


    public TreeNode<List<BrewingCauldronRecipe>> getRecipeSubTree(PpIngredients output) {
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

    public Set<PpIngredients> getAllBasePotionRecipeInputs() {
        return allBasePotionRecipeInputs;
    }

    public Set<PpIngredients> getAllUpgradeRecipeInputs() {
        return allUpgradeRecipeInputs;
    }
}
