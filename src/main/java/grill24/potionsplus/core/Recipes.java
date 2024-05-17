package grill24.potionsplus.core;

import com.google.common.collect.ImmutableMap;
import grill24.potionsplus.core.seededrecipe.PpIngredients;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.BrewingCauldronRecipeSerializer;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Recipes {
    // Recipe Serializers
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, ModInfo.MOD_ID);
    public static final RegistryObject<BrewingCauldronRecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", () -> new BrewingCauldronRecipeSerializer<>(BrewingCauldronRecipe::new, 200));


    // Recipe Types
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ModInfo.MOD_ID);
    public static final RegistryObject<RecipeType<grill24.potionsplus.recipe.BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = RECIPE_TYPES.register("brewing_cauldron_recipe", () -> new RecipeType<>() {
    });

    public static SeededPotionRecipes seededPotionRecipes;
    public static Set<PpIngredients> ALL_UNIQUE_INGREDIENTS;

    public static Map<ResourceLocation, Recipe<?>> getAdditionalRuntimeRecipes(RecipeType<?> recipeType) {
        if (recipeType == BREWING_CAULDRON_RECIPE.get()) {
            return getRuntimeBrewingRecipes();
        }
        return new HashMap<>();
    }

    private static Map<ResourceLocation, Recipe<?>> getRuntimeBrewingRecipes() {
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        // Add all possible vanilla brewing recipes
        addVanillaBrewingRecipes(recipes);

        // Generate seed-instanced recipes
        seededPotionRecipes = new SeededPotionRecipes(PotionsPlus.worldSeed);
        addBrewingCauldronRecipes(recipes, seededPotionRecipes.getRecipes());

        return recipes;
    }

    private static void addBrewingCauldronRecipes(Map<ResourceLocation, Recipe<?>> generatedRecipes, List<BrewingCauldronRecipe> newRecipes) {
        for (BrewingCauldronRecipe recipe : newRecipes) {
            generatedRecipes.put(recipe.getId(), recipe);
        }
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

                    if (!output.isEmpty()) {
                        addRuntimeBrewingRecipes(recipes, input, output, ingredient);
                    }
                });
            });
        }
    }

    private static void addRuntimeBrewingRecipes(Map<ResourceLocation, Recipe<?>> recipes, ItemStack input, ItemStack output, ItemStack ingredient) {
        Ingredient[] ingredients = new Ingredient[]{Ingredient.of(input), Ingredient.of(ingredient)};
        String resourceName = PUtil.getNameOrVerbosePotionName(input);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(ingredient);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(output);

        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(new ResourceLocation(ModInfo.MOD_ID, resourceName), "", ingredients, output, 0.1F, PUtil.getProcessingTime(100, input, output, 1));

        recipes.put(recipe.getId(), recipe);
    }

    public static int injectRuntimeRecipes(RecipeType<?> recipeType, RecipeManager recipeManager) {
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allMutableRecipes = new HashMap<>(recipeManager.recipes);

        Map<ResourceLocation, Recipe<?>> additionalRecipes = Recipes.getAdditionalRuntimeRecipes(recipeType);
        Map<ResourceLocation, Recipe<?>> mutableRecipes = new HashMap<>(recipeManager.recipes.get(recipeType));
        mutableRecipes.putAll(additionalRecipes);

        allMutableRecipes.put(recipeType, mutableRecipes);

        recipeManager.recipes = ImmutableMap.copyOf(allMutableRecipes);
        return additionalRecipes.size();
    }

    public static void computeUniqueIngredientsList() {
        Set<PpIngredients> unique = new HashSet<>();
        if(Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()).forEach(recipe -> {
                for (ItemStack itemStack : recipe.getIngredientsAsItemStacks()) {
                    unique.add(new PpIngredients(itemStack));
                }
            });
            Recipes.ALL_UNIQUE_INGREDIENTS = unique;
        }
    }
}
