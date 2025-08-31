package grill24.potionsplus.core;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.seededrecipe.IRuntimeRecipeProvider;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.core.seededrecipe.SanguineAltarRecipes;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipeAnalysis;
import grill24.potionsplus.recipe.RecipeAnalysis;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeDisplay;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS = DeferredRegister.create(Registries.RECIPE_DISPLAY, ModInfo.MOD_ID);

    // Brewing Cauldron Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = registerRecipeType("brewing_cauldron_recipe");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", BrewingCauldronRecipe.Serializer::new);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<BrewingCauldronRecipeDisplay>> BREWING_CAULDRON_RECIPE_DISPLAY = RECIPE_DISPLAYS.register("brewing_cauldron_recipe_display", () -> BrewingCauldronRecipeDisplay.TYPE);

    // Clothesline Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<ClotheslineRecipe>> CLOTHESLINE_RECIPE = registerRecipeType("clothesline_recipe");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ClotheslineRecipe>> CLOTHESLINE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("clothesline_recipe", ClotheslineRecipe.Serializer::new);

    // Sanguine Altar Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE = registerRecipeType("sanguine_altar_recipe");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("sanguine_altar_recipe", SanguineAltarRecipe.Serializer::new);

    public static final List<Pair<RecipeType<?>, IRuntimeRecipeProvider>> RECIPE_INJECTION_FUNCTIONS = new ArrayList<>();

    public static SeededPotionRecipes seededPotionRecipes = new SeededPotionRecipes();

    static void registerRecipeInjectionFunctions() {
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(BREWING_CAULDRON_RECIPE.get(), Recipes::generateRuntimeBrewingCauldronRecipes));
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(SANGUINE_ALTAR_RECIPE.get(), (server) -> SanguineAltarRecipes.generateAllSanguineAltarRecipes(PotionsPlus.worldSeed)));
        RECIPE_INJECTION_FUNCTIONS.add(Pair.of(CLOTHESLINE_RECIPE.get(), Recipes::generateRuntimeClotheslineFishRecipes));
    }

    public static RecipeMap recipes;

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
        for (PUtil.PotionType inputPotionContainer : PUtil.PotionType.values()) {
            List<ItemStack> POTIONS = BuiltInRegistries.POTION.registryKeySet().stream().map(BuiltInRegistries.POTION::getOrThrow).map((potionHolder) -> PUtil.createPotionItemStack(potionHolder, inputPotionContainer)).filter((item) -> server.potionBrewing().isInput(item)).toList();
            POTIONS.forEach(potion -> {
                INGREDIENTS.forEach(ingredient -> {
                    ItemStack output = server.potionBrewing().mix(ingredient, potion);
                    if (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, potion)) {
                        RecipeHolder<BrewingCauldronRecipe> recipe = new BrewingCauldronRecipeBuilder()
                                .result(output)
                                .ingredients(potion, ingredient)
                                .processingTime(100)
                                .potionMatchingCriteria(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)
                                .canShowInJei(false)
                                .build("minecraft");
                        vanillaBrewingRecipes.add(recipe);
                    }
                });
            });
        }

        return vanillaBrewingRecipes;
    }

    // ----- Fish Clothesline Recipes -----

    private static List<RecipeHolder<?>> generateRuntimeClotheslineFishRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> fishRecipes = new ArrayList<>();
        
        // Get all items tagged as PP_FISH
        HolderGetter<Item> itemLookup = server.registryAccess().lookupOrThrow(Registries.ITEM);
        Optional<HolderSet.Named<Item>> fishTagOptional = itemLookup.get(Tags.Items.PP_FISH);
        
        if (fishTagOptional.isPresent()) {
            HolderSet.Named<Item> fishItems = fishTagOptional.get();
            
            // Create a seeded random based on world seed for consistent fish -> resource mapping
            Random seededRandom = new Random(PotionsPlus.worldSeed);
            
            // List of possible resources that fish can yield (small amounts)
            ItemStack[] possibleResources = {
                new ItemStack(net.minecraft.world.item.Items.BONE_MEAL, 1),
                new ItemStack(net.minecraft.world.item.Items.KELP, 1),
                new ItemStack(net.minecraft.world.item.Items.STRING, 1),
                new ItemStack(net.minecraft.world.item.Items.PRISMARINE_SHARD, 1),
                new ItemStack(net.minecraft.world.item.Items.INK_SAC, 1),
                new ItemStack(net.minecraft.world.item.Items.SEA_PICKLE, 1),
                new ItemStack(net.minecraft.world.item.Items.NAUTILUS_SHELL, 1),
                new ItemStack(net.minecraft.world.item.Items.TURTLE_SCUTE, 1)
            };
            
            // Generate recipe for each fish
            for (Holder<Item> fishHolder : fishItems) {
                Item fishItem = fishHolder.value();
                
                // Use the fish item's resource location hash with world seed for consistent mapping
                Random fishRandom = new Random(PotionsPlus.worldSeed + fishItem.toString().hashCode());
                ItemStack resource = possibleResources[fishRandom.nextInt(possibleResources.length)].copy();
                
                // Generate random success chance between 0.1 (10%) and 0.5 (50%)
                float successChance = 0.1f + fishRandom.nextFloat() * 0.4f; // 0.1 to 0.5
                
                // Create clothesline recipe: fish -> resource
                ClotheslineRecipe recipe = new ClotheslineRecipe(
                    RecipeCategory.MISC,
                    List.of(PpIngredient.of(new ItemStack(fishItem))),
                    resource,
                    2400, // 2 minutes processing time (120 seconds * 20 ticks)
                    true, // Show in JEI
                    successChance
                );
                
                // Create recipe holder with a unique ID
                ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(
                    Registries.RECIPE,
                    ResourceLocation.fromNamespaceAndPath(
                        "potionsplus", 
                        "clothesline_fish_" + BuiltInRegistries.ITEM.getKey(fishItem).getPath()
                    )
                );
                fishRecipes.add(new RecipeHolder<>(recipeKey, recipe));
            }
        }
        
        return fishRecipes;
    }

    public static void postProcessRecipes(RecipeMap recipeMap) {
        recipes = recipeMap;

        List<RecipeHolder<SanguineAltarRecipe>> sanguineAltarRecipes = recipeMap.byType(SANGUINE_ALTAR_RECIPE.get()).stream().toList();
        SANGUINE_ALTAR_ANALYSIS.compute(sanguineAltarRecipes);
        SanguineAltarBlockEntity.computeRecipeMap(SANGUINE_ALTAR_ANALYSIS.getRecipes());

        List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronRecipes = recipeMap.byType(BREWING_CAULDRON_RECIPE.get()).stream().toList();
        DURATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isDurationUpgrade()).toList());
        AMPLIFICATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isAmplifierUpgrade()).toList());
        ALL_SEEDED_POTION_RECIPES_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isSeededRuntimeRecipe()).toList());
        ALL_BCR_RECIPES_ANALYSIS.compute(brewingCauldronRecipes);

        AbyssalTroveBlockEntity.computeAbyssalTroveIngredients();
    }

    public static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerRecipeType(String name) {
        return RECIPE_TYPES.register(name, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }
}
