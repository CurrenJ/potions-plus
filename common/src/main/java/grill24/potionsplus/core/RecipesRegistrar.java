package grill24.potionsplus.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.seededrecipe.IRuntimeRecipeProvider;
import grill24.potionsplus.core.seededrecipe.SanguineAltarRecipes;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipeAnalysis;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.alchemy.PotionContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns runtime recipe injection (seeded brewing cauldron / sanguine altar recipes) and the recipe
 * analysis statics consumed by tooltips, block entities and mixins. Ported from
 * {@code core.neoforge.RecipesRegistrar} to common/ - see docs/multi-loader-expansion.md Phase 9/11a
 * progress log ("RecipesRegistrar DeferredRegister/SeededPotionRecipes/SanguineAltarRecipes/
 * postProcessRecipes" entry).
 *
 * <p>The DeferredRegister-shaped RECIPE_TYPES/RECIPE_SERIALIZERS half of the original class stayed
 * per-loader (already existed at {@code core.fabric.Recipes}/{@code core.forge.Recipes}, and now
 * {@code core.neoforge.Recipes} too), flushing this module's sibling {@link Recipes}
 * loader-agnostic type/serializer definitions - see those classes.
 *
 * <p>{@link #postProcessRecipes} intentionally omits the {@code SanguineAltarBlockEntity
 * .computeRecipeMap} and {@code AbyssalTroveBlockEntity.computeAbyssalTroveIngredients} calls the
 * NeoForge original made: both block entities are still neoforge-only (sanguine altar needs its
 * networking packets ported; abyssal trove's Block class - and herbalist's lectern's - turned out to
 * still be neoforge-only too, with fabric/forge never having registered those two Blocks in the
 * first place - a deeper prerequisite than this session's scope), so those follow-up calls stay in
 * NeoForge's {@code ServerLifecycleListeners} after it calls this method. Fabric/Forge don't have
 * those block entities yet, so there is nothing for them to call.
 */
public class RecipesRegistrar {
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
            // NeoForge patches a ItemStack-taking isInput(ItemStack) directly onto vanilla's
            // PotionBrewing class (verified via javap: absent on the plain vanilla jar, present with
            // an extra constructor/field on NeoForge's) - not portable. isBrewablePotion(Holder<Potion>)
            // is vanilla (present on both jars) and captures the same intent (skip potions the
            // brewing config doesn't consider brewable) filtered before building the container stack.
            List<ItemStack> POTIONS = BuiltInRegistries.POTION.holders().filter(server.potionBrewing()::isBrewablePotion).map(inputPotionContainer::create).toList();
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

    /**
     * Recomputes the recipe analysis statics (duration/amplifier upgrades, all seeded potion
     * recipes, all brewing cauldron recipes) plus the abyssal trove's derived ingredient set. Does
     * NOT recompute the sanguine altar analysis's block-entity-facing side effect - see the class
     * javadoc.
     */
    @SuppressWarnings("unchecked")
    public static void postProcessRecipes(RecipeManager recipeManager) {
        RecipeType<SanguineAltarRecipe> sanguineAltarRecipeType = (RecipeType<SanguineAltarRecipe>) (RecipeType<?>) Recipes.SANGUINE_ALTAR_RECIPE.value();
        List<RecipeHolder<SanguineAltarRecipe>> sanguineAltarRecipes = recipeManager.getAllRecipesFor(sanguineAltarRecipeType);
        SANGUINE_ALTAR_ANALYSIS.compute(sanguineAltarRecipes);

        RecipeType<BrewingCauldronRecipe> brewingCauldronRecipeType = (RecipeType<BrewingCauldronRecipe>) (RecipeType<?>) Recipes.BREWING_CAULDRON_RECIPE.value();
        List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronRecipes = recipeManager.getAllRecipesFor(brewingCauldronRecipeType);
        DURATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isDurationUpgrade()).toList());
        AMPLIFICATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isAmplifierUpgrade()).toList());
        ALL_SEEDED_POTION_RECIPES_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isSeededRuntimeRecipe()).toList());
        ALL_BCR_RECIPES_ANALYSIS.compute(brewingCauldronRecipes);
    }
}
