package grill24.potionsplus.core.seededrecipe;

import com.google.common.collect.Sets;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;
import java.util.stream.Collectors;

public class SeededPotionRecipes {
    private final RandomSource random;
    private final List<RecipeHolder<BrewingCauldronRecipe>> recipes;

    public static TagKey<Item>[] POTION_INGREDIENT_TAGS = new TagKey[]{Tags.Items.COMMON_INGREDIENTS, Tags.Items.RARE_INGREDIENTS, Tags.Items.TIER_2_POTION_INGREDIENTS, Tags.Items.TIER_3_POTION_INGREDIENTS};

    public SeededPotionRecipes() {
        this.random = RandomSource.create(0);
        this.recipes = new ArrayList<>();
    }

    public SeededPotionRecipes(MinecraftServer server) {
        long seed = server.getWorldData().worldGenOptions().seed();
        this.random = RandomSource.create(seed);
        this.recipes = new ArrayList<>();

        SeededIngredientsLootTables.initializeLootTables(server.overworld(), seed);
        generateRecipes();
        SavedData.instance.setSeededPotionRecipesFromSavedData(recipes);
    }

    private void generateRecipes() {
        Set<PpMultiIngredient> allRecipeInputs = new HashSet<>();
        generateRecipesFromGenerationData(allRecipeInputs, Potions.getAllPotionAmpDurMatrices());
        generateDurationAndAmplificationUpgradeRecipes(allRecipeInputs);
    }

    public void generateDurationAndAmplificationUpgradeRecipes(Set<PpMultiIngredient> usedRecipeInputs) {
        RandomSource randomSource = RandomSource.create(PotionsPlus.worldSeed);

        // Sample half of the ingredients in the tag
        int durSize = (int) Math.ceil(BuiltInRegistries.ITEM.getTag(Tags.Items.POTION_DURATION_UP_INGREDIENTS).get().size() / 2F);
        List<PpIngredient> durationSamples = BuiltInRegistries.ITEM.getTag(Tags.Items.POTION_DURATION_UP_INGREDIENTS).get().stream().map(ItemStack::new).map(PpIngredient::of).collect(Collectors.toCollection(ArrayList::new));
        for(int d = 0; d < durSize && !durationSamples.isEmpty(); d++) {
            int randomIndex = randomSource.nextInt(0, durationSamples.size());
            durationSamples.remove(randomIndex);
        }

        // Sample half of the ingredients in the tag
        int ampSize = (int) Math.ceil(BuiltInRegistries.ITEM.getTag(Tags.Items.POTION_AMPLIFIER_UP_INGREDIENTS).get().size() / 2F);
        List<PpIngredient> amplifierSamples = BuiltInRegistries.ITEM.getTag(Tags.Items.POTION_AMPLIFIER_UP_INGREDIENTS).get().stream().map(ItemStack::new).map(PpIngredient::of).collect(Collectors.toCollection(ArrayList::new));
        for(int a = 0; a < ampSize && !amplifierSamples.isEmpty(); a++) {
            int randomIndex = randomSource.nextInt(0, amplifierSamples.size());
            amplifierSamples.remove(randomIndex);
        }

        // Builds sets to figure out which ingredients are only duration, only amplifier, or both
        Set<PpIngredient> durationTagItems = new HashSet<>(durationSamples);
        Set<PpIngredient> amplifierTagItems = new HashSet<>(amplifierSamples);
        Set<PpIngredient> durationAndAmplifierTagItems = Sets.intersection(durationTagItems, amplifierTagItems);
        durationTagItems = Sets.difference(durationTagItems, durationAndAmplifierTagItems);
        amplifierTagItems = Sets.difference(amplifierTagItems, durationAndAmplifierTagItems);



        // Only duration upgrades
        List<RecipeHolder<BrewingCauldronRecipe>> durationUpgradeRecipes = new ArrayList<>();
        for (PpIngredient ingredient : durationTagItems) {
            durationUpgradeRecipes.add(
                    new BrewingCauldronRecipeBuilder()
                    .result(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION))
                    .ingredients(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION), ingredient.getItemStack())
                    .processingTime(30)
                    .durationToAdd(randomSource.nextInt(100, 1800))
                    .potionMatchingCriteria(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT)
                    .build()
            );
        }
        Recipes.DURATION_UPGRADE_ANALYSIS.compute(durationUpgradeRecipes);

        if (PotionsPlus.Debug.DEBUG) {
            PotionsPlus.LOGGER.info("[SPR] Injected {} seeded potion duration upgrade recipes:", durationUpgradeRecipes.size());
        }

        // Only amplifier upgrades
        List<RecipeHolder<BrewingCauldronRecipe>> amplifierUpgradeRecipes = new ArrayList<>();
        for (PpIngredient ingredient : amplifierTagItems) {
            amplifierUpgradeRecipes.add(
                    new BrewingCauldronRecipeBuilder()
                            .result(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION))
                            .ingredients(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION), ingredient.getItemStack())
                            .processingTime(30)
                            .amplifierToAdd(1)
                            .potionMatchingCriteria(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT)
                            .build()
            );
        }
        Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.compute(amplifierUpgradeRecipes);

        if (PotionsPlus.Debug.DEBUG) {
            PotionsPlus.LOGGER.info("[SPR] Injected {} seeded potion amplifier upgrade recipes:", amplifierUpgradeRecipes.size());
        }

        // Duration and amplifier upgrades
        List<RecipeHolder<BrewingCauldronRecipe>> bothUpgradeRecipes = new ArrayList<>();
        for (PpIngredient ingredient : durationAndAmplifierTagItems) {
            bothUpgradeRecipes.add(
                    new BrewingCauldronRecipeBuilder()
                            .result(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION))
                            .ingredients(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION), ingredient.getItemStack())
                            .processingTime(30)
                            .durationToAdd(randomSource.nextInt(400, 1200))
                            .amplifierToAdd(1)
                            .potionMatchingCriteria(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT)
                            .build()
            );
        }

        // Add all new recipes to the list
        recipes.addAll(durationUpgradeRecipes);
        recipes.addAll(amplifierUpgradeRecipes);
        recipes.addAll(bothUpgradeRecipes);
    }

    private void generateRecipesFromGenerationData(Set<PpMultiIngredient> usedRecipeInputs, PotionBuilder.PotionsPlusPotionGenerationData... potions) {
        int newlyGeneratedRecipes = 0;
        for (PotionBuilder.PotionsPlusPotionGenerationData potionsAmpDurMatrix : potions) {
            // Generate all recipes
            List<RecipeHolder<BrewingCauldronRecipe>> allGeneratedRecipes = potionsAmpDurMatrix.generateRecipes(usedRecipeInputs, random);
            // Take out the recipes that we've loaded from saved data
            List<RecipeHolder<BrewingCauldronRecipe>> newRecipesToAdd = allGeneratedRecipes.stream().filter(recipe -> !SavedData.instance.itemsWithRecipesInSavedData.contains(PpIngredient.of(recipe.value().getResultItemWithTransformations(recipe.value().getIngredientsAsItemStacks())))).toList();
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

    public List<RecipeHolder<BrewingCauldronRecipe>> getRecipes() {
        return recipes;
    }
}
