package grill24.potionsplus.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializer;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipeBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeSerializer;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, ModInfo.MOD_ID);

    // Brewing Cauldron Recipe
    public static final RegistryObject<RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = RECIPE_TYPES.register("brewing_cauldron_recipe", () -> new RecipeType<>() {});
    public static final RegistryObject<BrewingCauldronRecipeSerializer> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", () -> new BrewingCauldronRecipeSerializer(BrewingCauldronRecipeBuilder::new, 200));

    // Clothesline Recipe
    public static final RegistryObject<RecipeType<ClotheslineRecipe>> CLOTHESLINE_RECIPE = RECIPE_TYPES.register("clothesline_recipe", () -> new RecipeType<>() {});
    public static final RegistryObject<ShapelessProcessingRecipeSerializer<ClotheslineRecipe, ClotheslineRecipeBuilder>> CLOTHESLINE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("clothesline_recipe", () -> new ShapelessProcessingRecipeSerializer<>(ClotheslineRecipeBuilder::new, 200));

    // Sanguine Altar Recipe
    public static final RegistryObject<RecipeType<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE = RECIPE_TYPES.register("sanguine_altar_recipe", () -> new RecipeType<>() {});
    public static final RegistryObject<ShapelessProcessingRecipeSerializer<SanguineAltarRecipe, SanguineAltarRecipeBuilder>> SANGUINE_ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("sanguine_altar_recipe", () -> new ShapelessProcessingRecipeSerializer<>(SanguineAltarRecipeBuilder::new, 200));

    public static final List<Pair<RecipeType<?>, Function<MinecraftServer, Map<ResourceLocation, Recipe<?>>>>> RECIPE_INJECTION_FUNCTIONS = new ArrayList<>();

    public static SeededPotionRecipes seededPotionRecipes;

    private static void registerRecipeInjectionFunctions() {
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(BREWING_CAULDRON_RECIPE.get(), Recipes::getRuntimeBrewingRecipes));
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(SANGUINE_ALTAR_RECIPE.get(), Recipes::getRuntimeSanguineAltarRecipes));
    }

    private static Map<ResourceLocation, Recipe<?>> getRuntimeBrewingRecipes(MinecraftServer server) {
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        // Add all possible vanilla brewing recipes
        addVanillaBrewingRecipes(recipes);

        // Add seed-instanced recipes
        addRecipes(recipes, seededPotionRecipes.getRecipes());

        return recipes;
    }

    private static Map<ResourceLocation, Recipe<?>> getRuntimeSanguineAltarRecipes(MinecraftServer server) {
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        addRecipes(recipes, seededPotionRecipes.getSanguineAltarRecipes());

        return recipes;
    }

    // Below method is for parsing the vanilla brewing recipes and adding them to the runtime recipe list
    private static void addVanillaBrewingRecipes(Map<ResourceLocation, Recipe<?>> recipes) {
        // Add all possible vanilla brewing recipes
        for (PUtil.PotionType inputPotionContainer : PUtil.PotionType.values()) {
            ForgeRegistries.POTIONS.getValues().forEach(potion -> {
                ForgeRegistries.ITEMS.getValues().forEach(item -> {
                    ItemStack input = PUtil.createPotionItemStack(potion, inputPotionContainer);
                    ItemStack ingredient = new ItemStack(item);
                    ItemStack output = BrewingRecipeRegistry.getOutput(input, ingredient);

                    // Determine the "tier", as defined by potions plus, of this potion recipe from a vanilla brewing stand recipe
                    int tier = -1;
                    if (!potion.getEffects().isEmpty()) {
                        if (ingredient.is(Items.GLOWSTONE_DUST) || ingredient.is(Items.REDSTONE)) {
                            // Vanilla potions only have one duration upgrade, so any potion recipe with a redstone or glowstone ingredient is tier 1 (II) recipe
                            tier = 1;
                        } else {
                            // If the potion has an effect and the ingredient is not redstone or glowstone, it is tier 0 (I), aka a base level potion
                            tier = 0;
                        }
                    }

                    if (!output.isEmpty()) {
                        addVanillaBrewingRecipes(recipes, input, output, ingredient, tier);
                    }
                });
            });
        }
    }

    private static void addVanillaBrewingRecipes(Map<ResourceLocation, Recipe<?>> recipes, ItemStack input, ItemStack output, ItemStack ingredient, int tier) {
        Ingredient[] ingredients = new Ingredient[]{Ingredient.of(input), Ingredient.of(ingredient)};
        String resourceName = PUtil.getNameOrVerbosePotionName(input);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(ingredient);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(output);

        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(new ResourceLocation(ModInfo.MOD_ID, resourceName), "", tier, ingredients, output, 0.1F, PUtil.getProcessingTime(100, input, output, 1));

        recipes.put(recipe.getId(), recipe);
    }

    private static void addRecipes(Map<ResourceLocation, Recipe<?>> generatedRecipes, List<? extends Recipe<?>> newRecipes) {
        for (Recipe<?> recipe : newRecipes) {
            generatedRecipes.put(recipe.getId(), recipe);
        }
    }

    public static int injectRuntimeRecipes(MinecraftServer server) {
        registerRecipeInjectionFunctions();

        // Generated seeded potion recipes
        seededPotionRecipes = new SeededPotionRecipes(server);
        int numInjected = 0;
        for (Pair<RecipeType<?>, Function<MinecraftServer, Map<ResourceLocation, Recipe<?>>>> pair : RECIPE_INJECTION_FUNCTIONS) {
            numInjected += injectRuntimeRecipes(server, pair.getFirst(), pair.getSecond().apply(server));
        }
        return numInjected;
    }

    private static int injectRuntimeRecipes(MinecraftServer server, RecipeType<?> recipeType, Map<ResourceLocation, Recipe<?>> additionalRecipes) {
        RecipeManager recipeManager = server.getRecipeManager();
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allMutableRecipes = new HashMap<>(recipeManager.recipes);

        Map<ResourceLocation, Recipe<?>> mutableRecipes = new HashMap<>(recipeManager.recipes.getOrDefault(recipeType, Collections.emptyMap()));
        mutableRecipes.putAll(additionalRecipes);

        allMutableRecipes.put(recipeType, mutableRecipes);
        recipeManager.recipes = ImmutableMap.copyOf(allMutableRecipes);

        // TODO: Remove when removing recipe tree alg
        if (recipeType == BREWING_CAULDRON_RECIPE.get()) {
            List<BrewingCauldronRecipe> allRecipes = recipeManager.getAllRecipesFor(BREWING_CAULDRON_RECIPE.get());
            seededPotionRecipes.createRecipeTree(allRecipes);
        }

        return additionalRecipes.size();
    }
}
