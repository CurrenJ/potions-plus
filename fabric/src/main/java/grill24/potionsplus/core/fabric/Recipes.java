package grill24.potionsplus.core.fabric;

import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeDisplay;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import static grill24.potionsplus.utility.Utility.ppId;

public class Recipes {

    // Brewing Cauldron Recipe
    public static final Holder<RecipeType<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = registerRecipeType("brewing_cauldron_recipe");
    public static final Holder<RecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER =
            FabricRegistration.register(BuiltInRegistries.RECIPE_SERIALIZER, "brewing_cauldron_recipe", () -> BrewingCauldronRecipe.SERIALIZER);
    public static final Holder<RecipeDisplay.Type<BrewingCauldronRecipeDisplay>> BREWING_CAULDRON_RECIPE_DISPLAY =
            FabricRegistration.register(BuiltInRegistries.RECIPE_DISPLAY, "brewing_cauldron_recipe_display", () -> BrewingCauldronRecipeDisplay.TYPE);

    // Clothesline Recipe
    public static final Holder<RecipeType<ClotheslineRecipe>> CLOTHESLINE_RECIPE = registerRecipeType("clothesline_recipe");
    public static final Holder<RecipeSerializer<ClotheslineRecipe>> CLOTHESLINE_RECIPE_SERIALIZER =
            FabricRegistration.register(BuiltInRegistries.RECIPE_SERIALIZER, "clothesline_recipe", () -> ClotheslineRecipe.SERIALIZER);

    // Sanguine Altar Recipe
    public static final Holder<RecipeType<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE = registerRecipeType("sanguine_altar_recipe");
    public static final Holder<RecipeSerializer<SanguineAltarRecipe>> SANGUINE_ALTAR_RECIPE_SERIALIZER =
            FabricRegistration.register(BuiltInRegistries.RECIPE_SERIALIZER, "sanguine_altar_recipe", () -> SanguineAltarRecipe.SERIALIZER);

    static {
        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE = () -> BREWING_CAULDRON_RECIPE.value();
        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER = () -> BREWING_CAULDRON_RECIPE_SERIALIZER.value();
        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE_DISPLAY = () -> BREWING_CAULDRON_RECIPE_DISPLAY.value();
        grill24.potionsplus.core.Recipes.CLOTHESLINE_RECIPE = () -> CLOTHESLINE_RECIPE.value();
        grill24.potionsplus.core.Recipes.CLOTHESLINE_RECIPE_SERIALIZER = () -> CLOTHESLINE_RECIPE_SERIALIZER.value();
        grill24.potionsplus.core.Recipes.SANGUINE_ALTAR_RECIPE = () -> SANGUINE_ALTAR_RECIPE.value();
        grill24.potionsplus.core.Recipes.SANGUINE_ALTAR_RECIPE_SERIALIZER = () -> SANGUINE_ALTAR_RECIPE_SERIALIZER.value();

        grill24.potionsplus.core.Recipes.BREWING_CAULDRON_RECIPE_KEY = ResourceKey.create(Registries.RECIPE_TYPE, ppId("brewing_cauldron_recipe"));
        grill24.potionsplus.core.Recipes.CLOTHESLINE_RECIPE_KEY = ResourceKey.create(Registries.RECIPE_TYPE, ppId("clothesline_recipe"));
        grill24.potionsplus.core.Recipes.SANGUINE_ALTAR_RECIPE_KEY = ResourceKey.create(Registries.RECIPE_TYPE, ppId("sanguine_altar_recipe"));
    }

    public static void init() {
        // No-op: forces class loading so the static initializers run.
    }

    public static <T extends Recipe<?>> Holder<RecipeType<T>> registerRecipeType(String name) {
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        return FabricRegistration.register(BuiltInRegistries.RECIPE_TYPE, name, () -> type);
    }
}
