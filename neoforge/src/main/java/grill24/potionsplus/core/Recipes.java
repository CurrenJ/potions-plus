package grill24.potionsplus.core;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.seededrecipe.IRuntimeRecipeProvider;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.core.seededrecipe.SanguineAltarRecipes;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients.Rarity.COMMON;
import static grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients.Rarity.RARE;

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

            // Only use items that are in the PP_FISH tag
            List<Item> taggedFishItems = fishItems.stream().map(Holder::value).toList();

            // Get all possible potion ingredients, excluding potions and fish
            List<ItemStack> possibleIngredients = ALL_SEEDED_POTION_RECIPES_ANALYSIS.getAllPotionsPlusIngredientsNoPotions()
                    .stream()
                    .map(PpIngredient::getItemStack)
                    .filter(stack -> !stack.is(Tags.Items.PP_FISH))
                    .toList();

            // Define fallback junk items (ocean/fish related)
            List<ItemStack> fallbackJunk = List.of(
                    new ItemStack(Items.KELP),
                    new ItemStack(Items.BONE),
                    new ItemStack(Items.LEATHER),
                    new ItemStack(Items.ROTTEN_FLESH),
                    new ItemStack(Items.STICK),
                    new ItemStack(Items.SAND),
                    new ItemStack(Items.STRING),
                    new ItemStack(Items.GOLD_NUGGET)
            );

            if (possibleIngredients.isEmpty()) {
                return fishRecipes;
            }

            for (Item fishItem : taggedFishItems) {
                Random fishRandom = new Random(PotionsPlus.worldSeed + fishItem.toString().hashCode());
                ItemStack ingredientStack = possibleIngredients.get(fishRandom.nextInt(possibleIngredients.size())).copy();

                // Pick a random fallback junk item
                ItemStack fallbackStack = fallbackJunk.get(fishRandom.nextInt(fallbackJunk.size())).copy();

                PpIngredient ingredient = PpIngredient.of(ingredientStack);
                float successChance = Math.clamp(fishRandom.nextFloat(), 0.25f, 1.0f);
                if (SeededIngredientsLootTables.isRarity(COMMON, ingredient)) {
                    successChance *= 0.5f;
                } else if (SeededIngredientsLootTables.isRarity(RARE, ingredient)) {
                    successChance *= 0.15f;
                } else {
                    successChance *= 0.5f;
                }

                ClotheslineRecipe recipe = new ClotheslineRecipe(
                        RecipeCategory.MISC,
                        List.of(PpIngredient.of(new ItemStack(fishItem))),
                        ingredientStack,
                        2400,
                        true,
                        successChance,
                        fallbackStack
                );

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
