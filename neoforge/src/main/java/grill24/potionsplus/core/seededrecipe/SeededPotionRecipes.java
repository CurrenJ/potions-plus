package grill24.potionsplus.core.seededrecipe;

import com.google.common.collect.Sets;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    }

    private void generateRecipes() {
        // Keeps track of unique potion recipe inputs, so we don't clashing recipes.
        Set<PpMultiIngredient> allRecipeInputs = new HashSet<>();

        // Don't want to generate recipes with the same ingredients as what we've loaded from saved data, so add their inputs to the set.
        SavedData.instance.seededPotionRecipes.forEach(recipe -> allRecipeInputs.add(PpMultiIngredient.of(recipe.value().getIngredientsAsItemStacks())));

        // Generate base potion recipes and amplification/duration upgrade recipes.
        generateRecipesFromGenerationData(allRecipeInputs, Potions.getAllPotionAmpDurMatrices());
        generateDurationAndAmplificationUpgradeRecipes(allRecipeInputs);

        // Remove recipes that already exist in saved data; we don't want to overwrite them with new generations.
        // Add recipes from saved data to the list.
        recipes.removeIf(SavedData.instance::isRecipeResultInSavedData);
        int generatedRecipeCount = recipes.size();
        recipes.addAll(SavedData.instance.seededPotionRecipes);

        // Log recipe generation information
        PotionsPlus.LOGGER.info("[SPR] Generated {} new seeded potion recipes.", generatedRecipeCount);
        PotionsPlus.LOGGER.info("[SPR] Loaded {} brewing cauldron potion recipes from saved data.", SavedData.instance.seededPotionRecipes.size());

        // Save the new recipes to saved data
        SavedData.instance.setBrewingCauldronRecipes(recipes.stream().filter(recipeHolder -> recipeHolder.value().isSeededRuntimeRecipe()).toList());
    }

    public void generateDurationAndAmplificationUpgradeRecipes(Set<PpMultiIngredient> usedRecipeInputs) {
        RandomSource randomSource = RandomSource.create(PotionsPlus.worldSeed);

        // ----- Prepare ingredients for duration and amplifier upgrades -----

        // Sample half of the ingredients in the tag
        int durSize = (int) Math.ceil(BuiltInRegistries.ITEM.getOrThrow(Tags.Items.POTION_DURATION_UP_INGREDIENTS).size() / 2F);
        List<PpIngredient> durationSamples = BuiltInRegistries.ITEM.getOrThrow(Tags.Items.POTION_DURATION_UP_INGREDIENTS).stream().map(ItemStack::new).map(PpIngredient::of).collect(Collectors.toCollection(ArrayList::new));
        for (int d = 0; d < durSize && !durationSamples.isEmpty(); d++) {
            int randomIndex = randomSource.nextInt(0, durationSamples.size());
            durationSamples.remove(randomIndex);
        }

        // Sample half of the ingredients in the tag
        int ampSize = (int) Math.ceil(BuiltInRegistries.ITEM.getOrThrow(Tags.Items.POTION_AMPLIFIER_UP_INGREDIENTS).size() / 2F);
        List<PpIngredient> amplifierSamples = BuiltInRegistries.ITEM.getOrThrow(Tags.Items.POTION_AMPLIFIER_UP_INGREDIENTS).stream().map(ItemStack::new).map(PpIngredient::of).collect(Collectors.toCollection(ArrayList::new));
        for (int a = 0; a < ampSize && !amplifierSamples.isEmpty(); a++) {
            int randomIndex = randomSource.nextInt(0, amplifierSamples.size());
            amplifierSamples.remove(randomIndex);
        }

        // Builds sets to figure out which ingredients are only duration, only amplifier, or both
        Set<PpIngredient> durationTagItems = new HashSet<>(durationSamples);
        Set<PpIngredient> amplifierTagItems = new HashSet<>(amplifierSamples);
        Set<PpIngredient> durationAndAmplifierTagItems = Sets.intersection(durationTagItems, amplifierTagItems);
        durationTagItems = Sets.difference(durationTagItems, durationAndAmplifierTagItems);
        amplifierTagItems = Sets.difference(amplifierTagItems, durationAndAmplifierTagItems);

        // ----- Generate recipes -----

        // Only duration upgrades
        List<RecipeHolder<BrewingCauldronRecipe>> durationUpgradeRecipes = new ArrayList<>();
        for (PpIngredient ingredient : durationTagItems) {
            durationUpgradeRecipes.add(
                    new BrewingCauldronRecipeBuilder()
                            .result(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION))
                            .ingredients(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION), ingredient.getItemStack())
                            .processingTime(30)
                            .durationToAdd(randomSource.nextInt(100, 1800))
                            .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER, BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT))
                            .isSeededRuntimeRecipe()
                            .build()
            );
        }
        Recipes.DURATION_UPGRADE_ANALYSIS.compute(durationUpgradeRecipes);

        // Only amplifier upgrades
        List<RecipeHolder<BrewingCauldronRecipe>> amplifierUpgradeRecipes = new ArrayList<>();
        for (PpIngredient ingredient : amplifierTagItems) {
            amplifierUpgradeRecipes.add(
                    new BrewingCauldronRecipeBuilder()
                            .result(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION))
                            .ingredients(PUtil.createPotionItemStack(Potions.ANY_POTION, PUtil.PotionType.POTION), ingredient.getItemStack())
                            .processingTime(30)
                            .amplifierToAdd(1)
                            .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER, BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT))
                            .isSeededRuntimeRecipe()
                            .build()
            );
        }
        Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.compute(amplifierUpgradeRecipes);

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
                            .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER, BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT))
                            .isSeededRuntimeRecipe()
                            .build()
            );
        }

        // Add all new recipes to the list
        recipes.addAll(durationUpgradeRecipes);
        recipes.addAll(amplifierUpgradeRecipes);
        recipes.addAll(bothUpgradeRecipes);
    }

    private void generateRecipesFromGenerationData(Set<PpMultiIngredient> usedRecipeInputs, PotionBuilder.PotionsPlusPotionGenerationData... potions) {
        for (PotionBuilder.PotionsPlusPotionGenerationData potionsAmpDurMatrix : potions) {
            // Generate all recipes
            List<RecipeHolder<BrewingCauldronRecipe>> generatedRecipes = potionsAmpDurMatrix.generateRecipes(usedRecipeInputs, random);

            if (Debug.DEBUG && Debug.DEBUG_POTION_RECIPE_GENERATION) {
                for (RecipeHolder<BrewingCauldronRecipe> recipe : generatedRecipes) {
                    if (!SavedData.instance.isRecipeResultInSavedData(recipe)) {
                        PotionsPlus.LOGGER.info("[SPR] Generated new recipe: {}", recipe);
                    }
                }
            }
            recipes.addAll(generatedRecipes);
        }
    }

    public List<RecipeHolder<BrewingCauldronRecipe>> getRecipes() {
        return recipes;
    }
}
