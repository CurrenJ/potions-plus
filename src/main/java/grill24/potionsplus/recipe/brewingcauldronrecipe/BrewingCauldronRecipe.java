package grill24.potionsplus.recipe.brewingcauldronrecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.Arrays;
import java.util.function.Function;


public class BrewingCauldronRecipe extends ShapelessProcessingRecipe {
    protected final int tier;
    protected final float experience;

    public BrewingCauldronRecipe(BrewingCauldronRecipe recipe) {
        super(recipe.id, recipe.category, recipe.group, recipe.ingredients, recipe.result, recipe.processingTime);
        this.tier = recipe.tier;
        this.experience = recipe.experience;
    }

    public BrewingCauldronRecipe(ResourceLocation resourceLocation, RecipeCategory category, String group, int tier, Ingredient[] ingredients, ItemStack itemStack, float experience, int processingTime) {
        super(resourceLocation, category, group, ingredients, itemStack, processingTime);
        this.tier = tier;
        this.experience = experience;
    }

    public boolean isIngredient(ItemStack itemStack) {
        for (Ingredient ingredient : this.ingredients) {
            if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItems()[0])) {
                return true;
            }
        }
        return false;
    }

    public boolean isAmpUpgrade() {
        return this.isTrueInIngredients((pair) -> pair.getA().getAmplifier() < pair.getB().getAmplifier());
    }

    public boolean isDurationUpgrade() {
        return this.isTrueInIngredients((pair) -> pair.getA().getDuration() < pair.getB().getDuration());
    }

    public int getOutputTier() {
        return tier;
    }

    public boolean isTrueInIngredients(Function<Pair<MobEffectInstance, MobEffectInstance>, Boolean> function) {
        for (Ingredient ingredient : this.ingredients) {
            ItemStack itemStack = ingredient.getItems()[0];
            if (PUtil.isPotion(itemStack)) {
                Potion inputPotion = PotionUtils.getPotion(itemStack);
                Potion outputPotion = PotionUtils.getPotion(this.result);
                if (!inputPotion.getEffects().isEmpty() && !outputPotion.getEffects().isEmpty() &&
                        function.apply(new Pair<>(inputPotion.getEffects().get(0), outputPotion.getEffects().get(0)))) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack[] getIngredientsAsItemStacks() {
        return Arrays.stream(this.ingredients).map((ingredient) -> ingredient.getItems()[0]).toArray(ItemStack[]::new);
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get();
    }


    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.BREWING_CAULDRON_RECIPE.get();
    }

    @Override
    public String toString() {
        StringBuilder recipeString = new StringBuilder("[BCR] ");
        for (int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            recipeString.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0]));
            if (i < ingredients.length - 1) {
                recipeString.append(" + ");
            }
        }
        recipeString.append(" => ").append(PUtil.getNameOrVerbosePotionName(result));

        return recipeString.toString();
    }
}
