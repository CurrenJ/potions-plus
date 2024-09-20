package grill24.potionsplus.recipe;

import com.google.gson.JsonObject;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

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

    public T group(String group) {
        this.group = group;
        return self();
    }

    public T category(RecipeCategory category) {
        this.category = category;
        return self();
    }

    protected void ensureValid(ResourceLocation resourceLocation) {
        if (this.result == null) {
            throw new IllegalStateException("No result specified");
        } else if (this.ingredients == null) {
            throw new IllegalStateException("No ingredients specified");
        } else if (this.processingTime <= 0) {
            throw new IllegalStateException("Invalid processing time: " + this.processingTime);
        }
    }

    protected abstract R build(ResourceLocation resourceLocation);

    protected abstract T self();

    // ----- RecipeBuilder -----

    @Override
    public RecipeBuilder unlockedBy(String s, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(s, criterionTriggerInstance);
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeConsumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
        recipeConsumer.accept(getFinishedRecipe(resourceLocation));
    }

    protected abstract FinishedRecipe getFinishedRecipe(ResourceLocation resourceLocation);

    public static class Result<R extends ShapelessProcessingRecipe, T extends ShapelessProcessingRecipeBuilder<R, T>> implements FinishedRecipe {
        private final ResourceLocation id;
        private final ShapelessProcessingRecipeBuilder<R, T> recipeBuilder;
        private final ResourceLocation advancementId;
        private final ShapelessProcessingRecipeSerializer<R, T> serializer;

        public Result(ResourceLocation id, ResourceLocation advancementId, ShapelessProcessingRecipeBuilder<R, T> recipeBuilder, ShapelessProcessingRecipeSerializer<R, T> serializer) {
            this.id = id;
            this.advancementId = advancementId;
            this.recipeBuilder = recipeBuilder;
            this.serializer = serializer;
        }

        public void serializeRecipeData(@NotNull JsonObject jsonObject) {
            this.serializer.toJson(this.recipeBuilder.build(new ResourceLocation("")), jsonObject);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        @javax.annotation.Nullable
        public JsonObject serializeAdvancement() {
            return this.recipeBuilder.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
