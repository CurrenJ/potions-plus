package grill24.potionsplus.recipe;

import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ShapelessProcessingRecipeBuilder<R extends Recipe<?>, T extends ShapelessProcessingRecipeBuilder<R, T>> implements RecipeBuilder {
    protected ItemStack result;
    protected Ingredient[] ingredients;
    protected int processingTime;
    protected String group;
    protected RecipeCategory category;

    protected final Advancement.Builder advancement = Advancement.Builder.advancement();

    public ShapelessProcessingRecipeBuilder() {
        this.result = null;
        this.ingredients = null;
        this.processingTime = 0;
        this.group = "";
        this.category = RecipeCategory.MISC;
    }

    public ShapelessProcessingRecipeBuilder(ShapelessProcessingRecipe recipe) {
        this.result = recipe.result;
        this.ingredients = recipe.ingredients;
        this.processingTime = recipe.processingTime;
        this.group = recipe.group;
        this.category = recipe.category;
    }

    public T result(ItemStack result) {
        this.result = result;
        return self();
    }

    public T result(Item item) {
        this.result = new ItemStack(item);
        return self();
    }

    public T ingredients(Ingredient... ingredients) {
        this.ingredients = ingredients;
        return self();
    }

    public T ingredients(ItemStack... ingredients) {
        Ingredient[] ingredientArray = new Ingredient[ingredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            ingredientArray[i] = Ingredient.of(ingredients[i]);
        }
        return ingredients(ingredientArray);
    }

    public T ingredients(PpIngredient... ingredients) {
        Ingredient[] ingredientArray = new Ingredient[ingredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            ingredientArray[i] = Ingredient.of(ingredients[i].getItemStack());
        }
        return ingredients(ingredientArray);
    }

    public T processingTime(int processingTime) {
        this.processingTime = processingTime;
        return self();
    }

    @Override
    public @NotNull T group(@Nullable String group) {
        this.group = group;
        return self();
    }

    public T category(RecipeCategory category) {
        this.category = category;
        return self();
    }

    protected void ensureValid() {
        if (this.result == null) {
            throw new IllegalStateException("No result specified");
        } else if (this.ingredients == null) {
            throw new IllegalStateException("No ingredients specified");
        } else if (this.processingTime <= 0) {
            throw new IllegalStateException("Invalid processing time: " + this.processingTime);
        }
    }

    protected abstract RecipeHolder<R> build();

    protected abstract T self();

    // ----- RecipeBuilder -----

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        this.advancement.addCriterion(s, criterion);
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        this.ensureValid();

        RecipeHolder<R> recipeHolder = build();
        this.advancement.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(AdvancementRequirements.Strategy.OR);
        recipeOutput.accept(resourceLocation, recipeHolder.value(), advancement.build(resourceLocation));
    }
}
