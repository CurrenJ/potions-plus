package grill24.potionsplus.recipe.clotheslinerecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ClotheslineRecipe extends ShapelessProcessingRecipe {

    public ClotheslineRecipe(ClotheslineRecipe recipe) {
        super(recipe.id, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime);
    }

    public ClotheslineRecipe(ResourceLocation resourceLocation, String group, Ingredient[] ingredients, ItemStack itemStack, int processingTime) {
        super(resourceLocation, group, ingredients, itemStack, processingTime);
    }

    @Override
    public @NotNull ShapelessProcessingRecipeSerializer<ClotheslineRecipe, ClotheslineRecipeBuilder> getSerializer() {
        return Recipes.CLOTHESLINE_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.CLOTHESLINE_RECIPE.get() ;
    }

    public boolean matches(ItemStack itemStack) {
        return Arrays.stream(this.ingredients).anyMatch(ingredient -> ingredient.test(itemStack));
    }
}
