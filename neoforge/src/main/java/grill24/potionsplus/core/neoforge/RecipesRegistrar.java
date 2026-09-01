package grill24.potionsplus.core.neoforge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.neoforge.ServerLifecycleListeners;
import grill24.potionsplus.core.seededrecipe.IRuntimeRecipeProvider;
import grill24.potionsplus.core.seededrecipe.neoforge.SanguineAltarRecipes;
import grill24.potionsplus.core.seededrecipe.neoforge.SeededPotionRecipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipeAnalysis;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.alchemy.PotionContainer;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the NeoForge {@link DeferredRegister}s that flush {@link Recipes}' loader-agnostic type and
 * serializer definitions, plus runtime recipe injection (needs the access-widened
 * {@code RecipeManager.byType}/{@code byName} fields - NeoForge-only until Phase 9 grows a shared
 * access widener entry). See docs/multi-loader-expansion.md Phase 4.
 */
public class RecipesRegistrar {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.MOD_ID);

    static {
        Recipes.initTypes(RECIPE_TYPES::register);
        Recipes.initSerializers(RECIPE_SERIALIZERS::register);
    }

    private static final List<Pair<RecipeType<?>, IRuntimeRecipeProvider>> RECIPE_INJECTION_FUNCTIONS = new ArrayList<>();

    public static SeededPotionRecipes seededPotionRecipes = new SeededPotionRecipes();

    private static void registerRecipeInjectionFunctions() {
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(Recipes.BREWING_CAULDRON_RECIPE.value(), RecipesRegistrar::generateRuntimeBrewingCauldronRecipes));
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(Recipes.SANGUINE_ALTAR_RECIPE.value(), (server) -> SanguineAltarRecipes.generateAllSanguineAltarRecipes(PotionsPlus.worldSeed)));
    }

    // ----- Computed Info -----
    public static final BrewingCauldronRecipeAnalysis DURATION_UPGRADE_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final BrewingCauldronRecipeAnalysis AMPLIFICATION_UPGRADE_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final BrewingCauldronRecipeAnalysis ALL_SEEDED_POTION_RECIPES_ANALYSIS = new BrewingCauldronRecipeAnalysis();
    public static final BrewingCauldronRecipeAnalysis ALL_BCR_RECIPES_ANALYSIS = new BrewingCauldronRecipeAnalysis();

    public static final RecipeAnalysis<SanguineAltarRecipe> SANGUINE_ALTAR_ANALYSIS = new RecipeAnalysis<>();

    private static List<RecipeHolder<?>> generateRuntimeBrewingCauldronRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> recipes = new ArrayList<>();

        // Add all possible vanilla brewing recipes
        recipes.addAll(getVanillaBrewingRecipes(server));

        // Generated seeded potion recipes and inject them
        seededPotionRecipes = new SeededPotionRecipes(server);
        recipes.addAll(seededPotionRecipes.getRecipes());

        return recipes;
    }

    // ----- Vanilla Brewing Recipes to Brewing Cauldron Recipes -----

    // Below method is for parsing the vanilla brewing recipes and adding them to the runtime recipe list
    private static List<RecipeHolder<?>> getVanillaBrewingRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> vanillaBrewingRecipes = new ArrayList<>();

        // Add all possible vanilla brewing recipes. Don't show them in JEI because too many recipes. Players already have the vanilla brewing stand recipe viewer.
        List<ItemStack> INGREDIENTS = BuiltInRegistries.ITEM.stream().map(ItemStack::new).filter((item) -> server.potionBrewing().isIngredient(item)).toList();
        for (PotionContainer inputPotionContainer : PotionContainer.values()) {
            List<ItemStack> POTIONS = BuiltInRegistries.POTION.holders().map((potionHolder) -> inputPotionContainer.create(potionHolder)).filter((item) -> server.potionBrewing().isInput(item)).toList();
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

    // ----- Injection Stuff -----

    public static int injectRuntimeRecipes(MinecraftServer server) {
        registerRecipeInjectionFunctions();

        int numInjected = 0;
        for (Pair<RecipeType<?>, IRuntimeRecipeProvider> pair : RECIPE_INJECTION_FUNCTIONS) {
            numInjected += injectRuntimeRecipes(server, pair.getFirst(), pair.getSecond().getRuntimeRecipesToInject(server));
            ServerLifecycleListeners.postProcessRecipes(server.getRecipeManager());
        }
        return numInjected;
    }

    private static int injectRuntimeRecipes(MinecraftServer server, RecipeType<?> recipeType, List<RecipeHolder<?>> additionalRecipes) {
        RecipeManager recipeManager = server.getRecipeManager();

        // Grab immutable recipe maps, copy them into mutable ones, and add the additiona recipes.
        Multimap<RecipeType<?>, RecipeHolder<?>> mutableRecipesByType = LinkedListMultimap.create(recipeManager.byType);
        Map<ResourceLocation, RecipeHolder<?>> mutableRecipesByName = new HashMap<>(recipeManager.byName);
        additionalRecipes.forEach(recipe -> {
                    // Quitting and rejoining a single-player world in the same session will retrigger recipe injection and cause duplicate recipes. Avoid by overwriting duplicate recipes.
                    if (mutableRecipesByName.containsKey(recipe.id()) && mutableRecipesByType.containsEntry(recipeType, recipe)) {
                        mutableRecipesByType.remove(recipeType, recipe);
                        mutableRecipesByName.remove(recipe.id());

                        PotionsPlus.LOGGER.warn("Recipe {} already exists in the recipe manager; overwriting.", recipe.id());
                    }

                    mutableRecipesByType.put(recipeType, recipe);
                    mutableRecipesByName.put(recipe.id(), recipe);
                }
        );

        // Then, copy back into recipe manager
        recipeManager.byType = ImmutableMultimap.copyOf(mutableRecipesByType);
        recipeManager.byName = ImmutableMap.copyOf(mutableRecipesByName);

        return additionalRecipes.size();
    }
}
