package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipeBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.*;

public class SeededPotionRecipes {
    public Set<PpIngredient> allUniqueRecipeInputs = new HashSet<>(); // All unique recipe inputs. Used to avoid duplicate recipes. E.x. [1 water bottle, 1 nether wart]
    public Set<PpIngredient> allPotionsPlusIngredientsNoPotions = new HashSet<>(); // 1 item per ingredient.

    public Set<PpIngredient> allPotionBrewingIngredientsNoPotions = new HashSet<>(); // 1 item per ingredient.
    public Map<Integer, Set<PpIngredient>> allPotionsBrewingIngredientsByTierNoPotions = new HashMap<>(); // 1 item per ingredient.

    private final RandomSource random;
    private final List<RecipeHolder<BrewingCauldronRecipe>> recipes;
    private List<RecipeHolder<SanguineAltarRecipe>> sanguineAltarRecipes;

    private final Set<PpIngredient> allRecipeInputs;

    private final Map<PpIngredient, TreeNode<List<BrewingCauldronRecipe>>> outputToInput = new HashMap<>();

    public static TagKey<Item>[] POTION_INGREDIENT_TAGS = new TagKey[]{Tags.Items.BASE_TIER_POTION_INGREDIENTS, Tags.Items.TIER_1_POTION_INGREDIENTS, Tags.Items.TIER_2_POTION_INGREDIENTS, Tags.Items.TIER_3_POTION_INGREDIENTS};

    public SeededPotionRecipes() {
        this.random = RandomSource.create(0);
        this.recipes = new ArrayList<>();
        this.allRecipeInputs = new HashSet<>();
        this.sanguineAltarRecipes = new ArrayList<>();
    }

    public SeededPotionRecipes(MinecraftServer server) {
        long seed = server.getWorldData().worldGenOptions().seed();
        this.random = RandomSource.create(seed);

        this.recipes = new ArrayList<>();
        this.allRecipeInputs = new HashSet<>();
        this.sanguineAltarRecipes = new ArrayList<>();

        SeededIngredientsLootTables.initializeLootTables(server.overworld(), seed);
        generateRecipes();

        SavedData.instance.setSeededPotionRecipes(recipes);
    }

    private void generateRecipes() {
        addAllPotionRecipes(Potions.getAllPotionAmpDurMatrices());
        computeUniqueIngredientsList(recipes);

        addAllSanguineAltarRecipes();
    }

    private void addAllPotionRecipes(PotionBuilder.PotionsAmpDurMatrix... potions) {
        int newlyGeneratedRecipes = 0;
        for (PotionBuilder.PotionsAmpDurMatrix potionsAmpDurMatrix : potions) {
            // Generate all recipes
            List<RecipeHolder<BrewingCauldronRecipe>> allGeneratedRecipes = potionsAmpDurMatrix.generateRecipes(allRecipeInputs, random);
            // Take out the recipes that we've loaded from saved data
            List<RecipeHolder<BrewingCauldronRecipe>> newRecipesToAdd = allGeneratedRecipes.stream().filter(recipe -> !SavedData.instance.itemsWithRecipesInSavedData.contains(PUtil.getNameOrVerbosePotionName(recipe.value().getResultItem()))).toList();
            newlyGeneratedRecipes += newRecipesToAdd.size();

            if (PotionsPlus.Debug.DEBUG && PotionsPlus.Debug.DEBUG_POTION_RECIPE_GENERATION) {
                for (RecipeHolder<BrewingCauldronRecipe> recipe : newRecipesToAdd) {
                    PotionsPlus.LOGGER.info("[SPR] Generated recipe: {}", recipe);
                }
            }
            recipes.addAll(newRecipesToAdd);
        }

        recipes.addAll(SavedData.instance.seededPotionRecipes);
        PotionsPlus.LOGGER.info("[SPR] Generated {} new brewing cauldron potion recipes.", newlyGeneratedRecipes);
        PotionsPlus.LOGGER.info("[SPR] Loaded {} brewing cauldron potion recipes from saved data.", SavedData.instance.seededPotionRecipes.size());
    }

    /**
     * Compute the unique ingredients list for the brewing cauldron
     * Called when recipes are synced
     */
    public void computeUniqueIngredientsList(List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronRecipes) {
        allPotionsBrewingIngredientsByTierNoPotions = new HashMap<>();
        allPotionBrewingIngredientsNoPotions = new HashSet<>();
        allPotionsPlusIngredientsNoPotions = new HashSet<>();
        allUniqueRecipeInputs = new HashSet<>();

        brewingCauldronRecipes.forEach(recipeHolder -> {
            BrewingCauldronRecipe recipe = recipeHolder.value();
            for (ItemStack itemStack : recipe.getIngredientsAsItemStacks()) {
                allUniqueRecipeInputs.add(PpIngredient.of(itemStack));
                if (!PUtil.isPotion(itemStack)) {
                    if (PUtil.isPotion(recipe.getResultItem()) && PUtil.isPotionsPlusPotion(recipe.getResultItem())) {
                        allPotionsBrewingIngredientsByTierNoPotions.computeIfAbsent(recipe.getOutputTier(), k -> new HashSet<>()).add(PpIngredient.of(itemStack));
                        allPotionBrewingIngredientsNoPotions.add(PpIngredient.of(itemStack));
                    }
                    allPotionsPlusIngredientsNoPotions.add(PpIngredient.of(itemStack));
                }
            }
        });
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

    public List<RecipeHolder<BrewingCauldronRecipe>> getRecipes() {
        return recipes;
    }

    // ----- Sangune Altar Recipes -----

    public void addAllSanguineAltarRecipes() {
        sanguineAltarRecipes.clear();

        Random random = new Random(PotionsPlus.worldSeed);
        final int processingTime = 100;
        for (Set<PpIngredient> ingredients : allPotionsBrewingIngredientsByTierNoPotions.values()) {
            List<PpIngredient> tierIngredients = new ArrayList<>(ingredients);
            PpIngredient firstIngredient = null;
            PpIngredient lastIngredient = null;

            while (!tierIngredients.isEmpty()) {
                PpIngredient nextIngredient = tierIngredients.remove(random.nextInt(tierIngredients.size()));
                if (lastIngredient != null) {
                    SanguineAltarRecipeBuilder builder = new SanguineAltarRecipeBuilder().ingredients(lastIngredient).result(nextIngredient.getItemStack()).processingTime(processingTime);
                    sanguineAltarRecipes.add(builder.build());
                } else {
                    firstIngredient = nextIngredient;
                }
                lastIngredient = nextIngredient;
            }

            if (firstIngredient != null) {
                SanguineAltarRecipeBuilder builder = new SanguineAltarRecipeBuilder().ingredients(lastIngredient).result(firstIngredient.getItemStack()).processingTime(processingTime);
                sanguineAltarRecipes.add(builder.build());
            }
        }
    }

    public List<RecipeHolder<SanguineAltarRecipe>> getSanguineAltarRecipes() {
        return sanguineAltarRecipes;
    }
}
