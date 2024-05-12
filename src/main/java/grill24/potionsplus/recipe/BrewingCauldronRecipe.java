package grill24.potionsplus.recipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BrewingCauldronRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient[] ingredients;
    protected final ItemStack result;
    protected final float experience;
    protected final int processingTime;

    public BrewingCauldronRecipe(BrewingCauldronRecipe recipe) {
        this.id = recipe.id;
        this.group = recipe.group;
        this.ingredients = recipe.ingredients;
        this.result = recipe.result;
        this.experience = recipe.experience;
        this.processingTime = recipe.processingTime;
    }

    public BrewingCauldronRecipe(ResourceLocation resourceLocation, String group, Ingredient[] ingredients, ItemStack itemStack, float experience, int processingTime) {
        this.id = resourceLocation;
        this.group = group;
        this.ingredients = ingredients;
        this.result = itemStack;
        this.experience = experience;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Container container, @NotNull Level level) {
        boolean hasAllIngredients = true;
        for (Ingredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack itemStack = container.getItem(i);
                if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItems()[0])) {
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                hasAllIngredients = false;
                break;
            }
        }
        return hasAllIngredients;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43743_, int p_43744_) {
        return true;
    }

    @Override

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(List.of(this.ingredients));
        return nonnulllist;
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get();
    }


    @Override
    public @NotNull RecipeType<?> getType() {
        return Recipes.BREWING_CAULDRON_RECIPE.get();
    }
}
