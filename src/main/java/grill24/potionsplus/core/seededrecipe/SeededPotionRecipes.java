package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class SeededPotionRecipes {
    public Set<PpIngredient> allUniqueRecipeInputs = new HashSet<>(); // All unique recipe inputs. Used to avoid duplicate recipes. E.x. [1 water bottle, 1 nether wart]
    public Set<PpIngredient> allPotionsPlusIngredientsNoPotions = new HashSet<>(); // 1 item per ingredient.

    public Set<PpIngredient> allPotionBrewingIngredientsNoPotions = new HashSet<>(); // 1 item per ingredient.
    public Map<Integer, Set<PpIngredient>> allPotionsBrewingIngredientsByTierNoPotions = new HashMap<>(); // 1 item per ingredient.

    private final Random random;
    private final List<BrewingCauldronRecipe> recipes;

    private final Set<PpIngredient> allRecipeInputs;

    private final Map<PpIngredient, TreeNode<List<BrewingCauldronRecipe>>> outputToInput = new HashMap<>();

    public static TagKey<Item>[] POTION_INGREDIENT_TAGS = new TagKey[]{Tags.Items.BASE_TIER_POTION_INGREDIENTS, Tags.Items.TIER_1_POTION_INGREDIENTS, Tags.Items.TIER_2_POTION_INGREDIENTS, Tags.Items.TIER_3_POTION_INGREDIENTS};


    public SeededPotionRecipes(MinecraftServer server) {
        this.random = new Random(server.getWorldData().worldGenSettings().seed());
        this.recipes = new ArrayList<>();
        this.allRecipeInputs = new HashSet<>();

        SeededIngredientsLootTables.initializeLootTables(server.overworld(), random);
        generateRecipes();

        SavedData.instance.setSeededPotionRecipes(recipes);
    }

    private void generateRecipes() {
        addAllPotionRecipes(Potions.getAllPotionAmpDurMatrices());
    }

    private void addAllPotionRecipes(PotionBuilder.PotionsAmpDurMatrix... potions) {
        int newlyGeneratedRecipes = 0;
        for (PotionBuilder.PotionsAmpDurMatrix potionsAmpDurMatrix : potions) {
            // Generate all recipes
            List<BrewingCauldronRecipe> allGeneratedRecipes = potionsAmpDurMatrix.generateRecipes(allRecipeInputs, random);
            // Take out the recipes that we've loaded from saved data
            List<BrewingCauldronRecipe> newRecipesToAdd = allGeneratedRecipes.stream().filter(recipe -> !SavedData.instance.itemsWithRecipesInSavedData.contains(PUtil.getNameOrVerbosePotionName(recipe.getResultItem()))).toList();
            newlyGeneratedRecipes += newRecipesToAdd.size();

            if (PotionsPlus.Debug.DEBUG && PotionsPlus.Debug.DEBUG_POTION_RECIPE_GENERATION) {
                for (BrewingCauldronRecipe recipe : newRecipesToAdd) {
                    PotionsPlus.LOGGER.info("[SPR] Generated recipe: {}", recipe);
                }
            }
            recipes.addAll(newRecipesToAdd);
        }

        recipes.addAll(SavedData.instance.seededPotionRecipes);
        PotionsPlus.LOGGER.info("[SPR] Generated {} new brewing cauldron potion recipes.", newlyGeneratedRecipes);
        PotionsPlus.LOGGER.info("[SPR] Loaded {} brewing cauldron potion recipes from saved data.", SavedData.instance.seededPotionRecipes.size());
    }

    @Deprecated
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
}
