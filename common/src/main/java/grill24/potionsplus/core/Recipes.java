package grill24.potionsplus.core;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.seededrecipe.IRuntimeRecipeProvider;
import grill24.potionsplus.core.seededrecipe.SanguineAltarRecipes;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipeAnalysis;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeDisplay;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.alchemy.PotionContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Recipes {
    /** Populated by neoforge Recipes at class-load time. DeferredHolder implements Supplier. */
    public static Supplier<RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE;
    public static Supplier<RecipeType<ClotheslineRecipe>> CLOTHESLINE_RECIPE;
    public static Supplier<RecipeType<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE;
    public static Supplier<RecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER;
    public static Supplier<RecipeDisplay.Type<BrewingCauldronRecipeDisplay>> BREWING_CAULDRON_RECIPE_DISPLAY;
    public static Supplier<RecipeSerializer<ClotheslineRecipe>> CLOTHESLINE_RECIPE_SERIALIZER;
    public static Supplier<RecipeSerializer<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE_SERIALIZER;

    /** Populated by neoforge Recipes at class-load time, so datagen can build advancement criteria without depending on DeferredHolder. */
    public static ResourceKey<RecipeType<?>> BREWING_CAULDRON_RECIPE_KEY;
    public static ResourceKey<RecipeType<?>> CLOTHESLINE_RECIPE_KEY;
    public static ResourceKey<RecipeType<?>> SANGUINE_ALTAR_RECIPE_KEY;

    public static final List<Pair<RecipeType<?>, IRuntimeRecipeProvider>> RECIPE_INJECTION_FUNCTIONS = new ArrayList<>();

    public static SeededPotionRecipes seededPotionRecipes = new SeededPotionRecipes();

    public static RecipeMap recipes;

    public static final BrewingCauldronRecipeAnalysis DURATION_UPGRADE_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final BrewingCauldronRecipeAnalysis AMPLIFICATION_UPGRADE_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final BrewingCauldronRecipeAnalysis ALL_SEEDED_POTION_RECIPES_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final BrewingCauldronRecipeAnalysis ALL_BCR_RECIPES_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final RecipeAnalysis<SanguineAltarRecipe> SANGUINE_ALTAR_ANALYSIS = new RecipeAnalysis<>();

    public static void registerRecipeInjectionFunctions() {
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(BREWING_CAULDRON_RECIPE.get(), Recipes::generateRuntimeBrewingCauldronRecipes));
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(SANGUINE_ALTAR_RECIPE.get(), (server) -> SanguineAltarRecipes.generateAllSanguineAltarRecipes(ModState.worldSeed)));
    }

    public static void postProcessRecipes(RecipeMap recipeMap) {
        recipes = recipeMap;

        List<RecipeHolder<SanguineAltarRecipe>> sanguineAltarRecipes = recipeMap.byType(SANGUINE_ALTAR_RECIPE.get()).stream().toList();
        SANGUINE_ALTAR_ANALYSIS.compute(sanguineAltarRecipes);
        SanguineAltarBlockEntity.computeRecipeMap(SANGUINE_ALTAR_ANALYSIS.getRecipes());

        List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronRecipes = recipeMap.byType(BREWING_CAULDRON_RECIPE.get()).stream().toList();
        DURATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(r -> r.value().isDurationUpgrade()).toList());
        AMPLIFICATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(r -> r.value().isAmplifierUpgrade()).toList());
        ALL_SEEDED_POTION_RECIPES_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(r -> r.value().isSeededRuntimeRecipe()).toList());
        ALL_BCR_RECIPES_ANALYSIS.compute(brewingCauldronRecipes);

        AbyssalTroveBlockEntity.computeAbyssalTroveIngredients();
    }

    private static List<RecipeHolder<?>> generateRuntimeBrewingCauldronRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> result = new ArrayList<>();
        result.addAll(getVanillaBrewingRecipes(server));
        seededPotionRecipes = new SeededPotionRecipes(server);
        result.addAll(seededPotionRecipes.getRecipes());
        return result;
    }

    private static List<RecipeHolder<?>> getVanillaBrewingRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> vanillaBrewingRecipes = new ArrayList<>();
        List<ItemStack> INGREDIENTS = BuiltInRegistries.ITEM.stream().map(ItemStack::new).filter((item) -> server.potionBrewing().isIngredient(item)).toList();
        for (PotionContainer inputPotionContainer : PotionContainer.values()) {
            List<ItemStack> POTIONS = BuiltInRegistries.POTION.registryKeySet().stream().map(BuiltInRegistries.POTION::getOrThrow).map(inputPotionContainer::create).toList();
            POTIONS.forEach(potion -> {
                INGREDIENTS.forEach(ingredient -> {
                    ItemStack output = server.potionBrewing().mix(ingredient, potion);
                    if (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, potion)) {
                        RecipeHolder<BrewingCauldronRecipe> recipe = new BrewingCauldronRecipeBuilder()
                                .result(output)
                                .ingredients(potion, ingredient)
                                .processingTime(100)
                                .potionMatchingCriteria(EffectComparison.MatchCriteria.EXACT_MATCH)
                                .canShowInJei(false)
                                .build("minecraft");
                        vanillaBrewingRecipes.add(recipe);
                    }
                });
            });
        }
        return vanillaBrewingRecipes;
    }
}
