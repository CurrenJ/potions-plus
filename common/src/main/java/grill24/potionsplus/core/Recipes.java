package grill24.potionsplus.core;

import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * The loader-agnostic half of the recipe-type/serializer registrations. Each loader flushes these
 * into its own registry mechanism ({@code core.neoforge.Recipes}, {@code core.forge.Recipes},
 * {@code core.fabric.Recipes}). Runtime recipe injection and analysis now live at
 * {@link RecipesRegistrar} (common/ as of Phase 9/11a) - see docs/multi-loader-expansion.md Phase 4
 * and the Phase 9/11a progress log.
 */
public class Recipes {
    public static Holder<RecipeType<?>> BREWING_CAULDRON_RECIPE;
    public static Holder<RecipeSerializer<?>> BREWING_CAULDRON_RECIPE_SERIALIZER;

    public static Holder<RecipeType<?>> CLOTHESLINE_RECIPE;
    public static Holder<RecipeSerializer<?>> CLOTHESLINE_RECIPE_SERIALIZER;

    public static Holder<RecipeType<?>> SANGUINE_ALTAR_RECIPE;
    public static Holder<RecipeSerializer<?>> SANGUINE_ALTAR_RECIPE_SERIALIZER;

    public static void initTypes(BiFunction<String, Supplier<RecipeType<?>>, Holder<RecipeType<?>>> registerType) {
        BREWING_CAULDRON_RECIPE = registerType.apply("brewing_cauldron_recipe", () -> new RecipeType<>() {
        });
        CLOTHESLINE_RECIPE = registerType.apply("clothesline_recipe", () -> new RecipeType<>() {
        });
        SANGUINE_ALTAR_RECIPE = registerType.apply("sanguine_altar_recipe", () -> new RecipeType<>() {
        });
    }

    public static void initSerializers(BiFunction<String, Supplier<RecipeSerializer<?>>, Holder<RecipeSerializer<?>>> registerSerializer) {
        BREWING_CAULDRON_RECIPE_SERIALIZER = registerSerializer.apply("brewing_cauldron_recipe", BrewingCauldronRecipe.Serializer::new);
        CLOTHESLINE_RECIPE_SERIALIZER = registerSerializer.apply("clothesline_recipe", ClotheslineRecipe.Serializer::new);
        SANGUINE_ALTAR_RECIPE_SERIALIZER = registerSerializer.apply("sanguine_altar_recipe", SanguineAltarRecipe.Serializer::new);
    }
}
