package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeDisplay;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.MOD_ID);
    public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS = DeferredRegister.create(Registries.RECIPE_DISPLAY, ModInfo.MOD_ID);

    // Brewing Cauldron Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = registerRecipeType("brewing_cauldron_recipe");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", () -> BrewingCauldronRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<BrewingCauldronRecipeDisplay>> BREWING_CAULDRON_RECIPE_DISPLAY = RECIPE_DISPLAYS.register("brewing_cauldron_recipe_display", () -> BrewingCauldronRecipeDisplay.TYPE);

    // Clothesline Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<ClotheslineRecipe>> CLOTHESLINE_RECIPE = registerRecipeType("clothesline_recipe");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ClotheslineRecipe>> CLOTHESLINE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("clothesline_recipe", () -> ClotheslineRecipe.SERIALIZER);

    // Sanguine Altar Recipe
    public static final DeferredHolder<RecipeType<?>, RecipeType<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE = registerRecipeType("sanguine_altar_recipe");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("sanguine_altar_recipe", () -> SanguineAltarRecipe.SERIALIZER);

    static {
        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE = BREWING_CAULDRON_RECIPE;
        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER = BREWING_CAULDRON_RECIPE_SERIALIZER;
        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE_DISPLAY = BREWING_CAULDRON_RECIPE_DISPLAY;
        grill24.potionsplus.core.Recipes.CLOTHESLINE_RECIPE = CLOTHESLINE_RECIPE;
        grill24.potionsplus.core.Recipes.CLOTHESLINE_RECIPE_SERIALIZER = CLOTHESLINE_RECIPE_SERIALIZER;
        grill24.potionsplus.core.Recipes.SANGUINE_ALTAR_RECIPE = SANGUINE_ALTAR_RECIPE;
        grill24.potionsplus.core.Recipes.SANGUINE_ALTAR_RECIPE_SERIALIZER = SANGUINE_ALTAR_RECIPE_SERIALIZER;

        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE_KEY = BREWING_CAULDRON_RECIPE.getKey();
        grill24.potionsplus.core.Recipes.CLOTHESLINE_RECIPE_KEY = CLOTHESLINE_RECIPE.getKey();
        grill24.potionsplus.core.Recipes.SANGUINE_ALTAR_RECIPE_KEY = SANGUINE_ALTAR_RECIPE.getKey();
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
