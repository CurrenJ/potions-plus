package grill24.potionsplus.core;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Function;

import static grill24.potionsplus.utility.Utility.ppId;

public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.MOD_ID);

    // Brewing Cauldron Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = RECIPE_TYPES.register("brewing_cauldron_recipe", () -> new RecipeType<>() {
    });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", BrewingCauldronRecipe.Serializer::new);

    // Clothesline Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<ClotheslineRecipe>> CLOTHESLINE_RECIPE = RECIPE_TYPES.register("clothesline_recipe", () -> new RecipeType<>() {
    });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ClotheslineRecipe>> CLOTHESLINE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("clothesline_recipe", ClotheslineRecipe.Serializer::new);

    // Sanguine Altar Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE = RECIPE_TYPES.register("sanguine_altar_recipe", () -> new RecipeType<>() {
    });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("sanguine_altar_recipe", SanguineAltarRecipe.Serializer::new);

    public static final List<Pair<RecipeType<?>, Function<MinecraftServer, List<RecipeHolder<?>>>>> RECIPE_INJECTION_FUNCTIONS = new ArrayList<>();

    public static SeededPotionRecipes seededPotionRecipes = new SeededPotionRecipes();

    private static void registerRecipeInjectionFunctions() {
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(BREWING_CAULDRON_RECIPE.get(), Recipes::getRuntimeBrewingRecipes));
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(SANGUINE_ALTAR_RECIPE.get(), Recipes::getRuntimeSanguineAltarRecipes));
    }

    private static List<RecipeHolder<?>> getRuntimeBrewingRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> recipes = new ArrayList<>();

        // Add all possible vanilla brewing recipes
        recipes.addAll(getVanillaBrewingRecipes(server));

        // Add seed-instanced recipes
        recipes.addAll(seededPotionRecipes.getRecipes());

        return recipes;
    }

    private static List<RecipeHolder<?>> getRuntimeSanguineAltarRecipes(MinecraftServer server) {
        return new ArrayList<>(seededPotionRecipes.getSanguineAltarRecipes());
    }

    // Below method is for parsing the vanilla brewing recipes and adding them to the runtime recipe list
    private static List<RecipeHolder<?>> getVanillaBrewingRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> vanillaBrewingRecipes = new ArrayList<>();

        // Add all possible vanilla brewing recipes
        List<ItemStack> INGREDIENTS = BuiltInRegistries.ITEM.stream().map(ItemStack::new).filter((item) -> server.potionBrewing().isIngredient(item)).toList();
        for (PUtil.PotionType inputPotionContainer : PUtil.PotionType.values()) {
            List<ItemStack> POTIONS = BuiltInRegistries.POTION.holders().map((potionHolder) -> PUtil.createPotionItemStack(potionHolder, inputPotionContainer)).filter((item) -> server.potionBrewing().isInput(item)).toList();
            POTIONS.forEach(potion -> {
                INGREDIENTS.forEach(ingredient -> {
                    ItemStack output = server.potionBrewing().mix(ingredient, potion);

                    // Determine the "tier", as defined by potions plus, of this potion recipe from a vanilla brewing stand recipe
                    int tier = -1;
                    if (!PUtil.getPotion(potion).getEffects().isEmpty()) {
                        if (ingredient.is(Items.GLOWSTONE_DUST) || ingredient.is(Items.REDSTONE)) {
                            // Vanilla potions only have one duration upgrade, so any potion recipe with a redstone or glowstone ingredient is tier 1 (II) recipe
                            tier = 1;
                        } else {
                            // If the potion has an effect and the ingredient is not redstone or glowstone, it is tier 0 (I), aka a base level potion
                            tier = 0;
                        }
                    }

                    if (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, potion)) {
                        vanillaBrewingRecipes.add(getVanillaBrewingRecipeAsBrewingCauldronRecipe(potion, output, ingredient, tier));
                    }
                });
            });
        }

        return vanillaBrewingRecipes;
    }

    private static RecipeHolder<?> getVanillaBrewingRecipeAsBrewingCauldronRecipe(ItemStack input, ItemStack output, ItemStack ingredient, int tier) {
        Ingredient[] ingredients = new Ingredient[]{Ingredient.of(input), Ingredient.of(ingredient)};
        String resourceName = PUtil.getNameOrVerbosePotionName(input);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(ingredient);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(output);

        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(RecipeCategory.BREWING, "", tier, ingredients, output, 0.1F, PUtil.getProcessingTime(100, input, output, 1));
        ResourceLocation id = ppId(resourceName);

        return new RecipeHolder<>(id, recipe);
    }

    public static int injectRuntimeRecipes(MinecraftServer server) {
        registerRecipeInjectionFunctions();

        // Generated seeded potion recipes
        seededPotionRecipes = new SeededPotionRecipes(server);
        int numInjected = 0;
        for (Pair<RecipeType<?>, Function<MinecraftServer, List<RecipeHolder<?>>>> pair : RECIPE_INJECTION_FUNCTIONS) {
            numInjected += injectRuntimeRecipes(server, pair.getFirst(), pair.getSecond().apply(server));
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
