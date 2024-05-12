package grill24.potionsplus.data;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BrewingCauldronRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final Ingredient[] ingredients;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;

    private BrewingCauldronRecipeBuilder(ItemStack itemLike, Ingredient[] ingredients, float experience, int processingTime) {
        this.result = itemLike;
        this.ingredients = ingredients;
        this.experience = experience;
        this.cookingTime = processingTime;
    }

    public static BrewingCauldronRecipeBuilder brewing(Ingredient[] ingredients, ItemStack itemLike, float experience, int processingTime) {
        return new BrewingCauldronRecipeBuilder(itemLike, ingredients, experience, processingTime);
    }

    public @NotNull BrewingCauldronRecipeBuilder unlockedBy(@NotNull String s, @NotNull CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(s, criterionTriggerInstance);
        return this;
    }

    public @NotNull BrewingCauldronRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public @NotNull Item getResult() {
        return this.result.getItem();
    }

    public void save(Consumer<FinishedRecipe> recipeConsumer, @NotNull ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
        recipeConsumer.accept(new BrewingCauldronRecipeBuilder.Result(resourceLocation, this.group == null ? "" : this.group, this.ingredients, this.result, this.experience, this.cookingTime, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath()), Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get()));
    }

    private void ensureValid(ResourceLocation p_126266_) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_126266_);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient[] ingredients;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<BrewingCauldronRecipe> serializer;

        public Result(ResourceLocation resourceLocation, String group, Ingredient[] ingredients, ItemStack result, float experience, int cookingTime, Advancement.Builder builder, ResourceLocation advancementId, RecipeSerializer<BrewingCauldronRecipe> serializer) {
            this.id = resourceLocation;
            this.group = group;
            this.ingredients = ingredients;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.advancement = builder;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        public void serializeRecipeData(@NotNull JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }


            JsonArray jsonArray = new JsonArray();
            ItemStack[] stacks = new ItemStack[this.ingredients.length];
            for (int i = 0; i < this.ingredients.length; i++) {
                ItemStack stack = this.ingredients[i].getItems()[0];
                CompoundTag compoundTag = stack.save(new CompoundTag());
                String itemStackJson = compoundTag.getAsString();
                jsonArray.add(itemStackJson);
            }
            jsonObject.add("ingredient", jsonArray);

            CompoundTag resultTag = this.result.save(new CompoundTag());
            String resultJson = resultTag.getAsString();
            jsonObject.addProperty("result", resultJson);
            jsonObject.addProperty("experience", this.experience);
            jsonObject.addProperty("cookingtime", this.cookingTime);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
