package grill24.potionsplus.recipe;

import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class ShapelessProcessingRecipeBuilder<R extends Recipe<?>, T extends ShapelessProcessingRecipeBuilder<R, T>> implements RecipeBuilder {
    protected ItemStack result;
    protected List<PpIngredient> ingredients;
    protected int processingTime;
    // TODO: Unused - find a way to use in recipe?
    protected String group;
    protected RecipeCategory category;
    protected boolean canShowInJei;

    protected final Advancement.Builder advancement = Advancement.Builder.advancement();

    public ShapelessProcessingRecipeBuilder() {
        this.result = null;
        this.ingredients = null;
        this.processingTime = 0;
        this.category = RecipeCategory.MISC;
        this.canShowInJei = true;
    }

    public ShapelessProcessingRecipeBuilder(ShapelessProcessingRecipe recipe) {
        this.result = recipe.result;
        this.ingredients = recipe.ingredients;
        this.processingTime = recipe.processingTime;
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

    public T ingredients(ItemStack... ingredients) {
        return ingredients(Arrays.stream(ingredients).map(PpIngredient::of).toArray(PpIngredient[]::new));
    }

    public T ingredients(ItemLike... ingredients) {
        return ingredients(Arrays.stream(ingredients).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    public T ingredients(PpIngredient... ingredients) {
        this.ingredients = Arrays.asList(ingredients);
        return self();
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

    public T canShowInJei(boolean canShowInJei) {
        this.canShowInJei = canShowInJei;
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
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> recipeResourceKey) {
        this.ensureValid();

        RecipeHolder<R> recipeHolder = build();
        this.advancement.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeResourceKey)).rewards(AdvancementRewards.Builder.recipe(recipeResourceKey)).requirements(AdvancementRequirements.Strategy.OR);
        recipeOutput.accept(recipeResourceKey, recipeHolder.value(),  this.advancement.build(recipeResourceKey.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        this.save(recipeOutput, ShapelessProcessingRecipe.getUniqueRecipeName(ingredients, result));
    }
}
