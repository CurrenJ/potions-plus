package grill24.potionsplus.core;

import com.google.common.collect.ImmutableMap;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeSerializer;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.client.Minecraft;
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

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, ModInfo.MOD_ID);

    // Brewing Cauldron Recipe
    public static final RegistryObject<RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = RECIPE_TYPES.register("brewing_cauldron_recipe", () -> new RecipeType<>() {});
    public static final RegistryObject<BrewingCauldronRecipeSerializer> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", () -> new BrewingCauldronRecipeSerializer(200));

    public static SeededPotionRecipes seededPotionRecipes;

    public static Map<ResourceLocation, Recipe<?>> getAdditionalRuntimeRecipes(MinecraftServer server, RecipeType<?> recipeType) {
        if (recipeType == BREWING_CAULDRON_RECIPE.get()) {
            return getRuntimeBrewingRecipes(server);
        }
        return new HashMap<>();
    }

    private static Map<ResourceLocation, Recipe<?>> getRuntimeBrewingRecipes(MinecraftServer server) {
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        // Add all possible vanilla brewing recipes
        addVanillaBrewingRecipes(recipes);

        // Generate seed-instanced recipes
        seededPotionRecipes = new SeededPotionRecipes(server);
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

    public static int injectRuntimeRecipes(MinecraftServer server, RecipeType<?> recipeType) {
        RecipeManager recipeManager = server.getRecipeManager();
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allMutableRecipes = new HashMap<>(recipeManager.recipes);

        Map<ResourceLocation, Recipe<?>> additionalRecipes = Recipes.getAdditionalRuntimeRecipes(server, recipeType);
        Map<ResourceLocation, Recipe<?>> mutableRecipes = new HashMap<>(recipeManager.recipes.get(recipeType));
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

    /**
     * Compute the unique ingredients list for the brewing cauldron
     * Called when recipes are synced
     */
    public static void computeUniqueIngredientsList() {
        if (Minecraft.getInstance().level != null) {
            seededPotionRecipes.allPotionsBrewingIngredientsByTierNoPotions = new HashMap<>();
            seededPotionRecipes.allUniqueRecipeInputs = new HashSet<>();

            Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()).forEach(recipe -> {
                for (ItemStack itemStack : recipe.getIngredientsAsItemStacks()) {
                    seededPotionRecipes.allUniqueRecipeInputs.add(PpIngredient.of(itemStack));
                    if (!PUtil.isPotion(itemStack)) {
                        if (PUtil.isPotion(recipe.getResultItem()) && PUtil.isPotionsPlusPotion(recipe.getResultItem())) {
                            seededPotionRecipes.allPotionsBrewingIngredientsByTierNoPotions.computeIfAbsent(recipe.getOutputTier(), k -> new HashSet<>()).add(PpIngredient.of(itemStack));
                            seededPotionRecipes.allPotionBrewingIngredientsNoPotions.add(PpIngredient.of(itemStack));
                        }
                        seededPotionRecipes.allPotionsPlusIngredientsNoPotions.add(PpIngredient.of(itemStack));
                    }
                }
            });
        }
    }
}
