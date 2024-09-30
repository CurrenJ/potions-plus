package grill24.potionsplus.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class ShapelessProcessingRecipeSerializerHelper {
    public static final Codec<RecipeCategory> RECIPE_CATEGORY_CODEC = Codec.STRING.comapFlatMap(
            (p_340780_) -> {
                Optional<RecipeCategory> optional;
                try {
                    optional = Optional.of(RecipeCategory.valueOf(p_340780_));
                } catch (IllegalArgumentException illegalargumentexception) {
                    optional = Optional.empty();
                }
                return optional.map(DataResult::success).orElseGet(() -> {
                    return DataResult.error(() -> "Unknown recipe category: " + p_340780_);
                });
            },
            RecipeCategory::toString
    );

//    protected Supplier<B> builderSupplier;
//    protected final int defaultProcessingTime;
//
//    public ShapelessProcessingRecipeSerializer(Supplier<B> builderSupplier, int defaultProcessingTime) {
//        this.builderSupplier = builderSupplier;
//        this.defaultProcessingTime = defaultProcessingTime;
//    }
//
//    public @NotNull R fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
//        B builder = builderSupplier.get();
//        return fromJson(builder, jsonObject).build().value();
//    }
//
//    public R fromNetwork(@NotNull ResourceLocation resourceLocation, RegistryFriendlyByteBuf buf) {
//        B builder = builderSupplier.get();
//        return fromNetwork(builder, buf).build().value();
//    }
//
//    public @NotNull B fromJson(B builder, @NotNull JsonObject jsonObject) {
//        RecipeCategory category = RecipeCategory.valueOf(GsonHelper.getAsString(jsonObject, "category", RecipeCategory.MISC.name()));
//
//        String s = GsonHelper.getAsString(jsonObject, "group", "");
//        JsonElement jsonelement = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");
//
//        // Read ingredients
//        Ingredient[] ingredients;
//        if (jsonelement.isJsonArray()) {
//            JsonArray jsonArray = jsonelement.getAsJsonArray();
//            ingredients = new Ingredient[jsonArray.size()];
//            for (int i = 0; i < jsonArray.size(); i++) {
//                ingredients[i] = Utility.ingredientFromTagString(jsonArray.get(i).getAsString());
//            }
//        } else {
//            ingredients = new Ingredient[]{Ingredient.fromJson(jsonelement)};
//        }
//
//        // Read result
//        if (!jsonObject.has("result"))
//            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
//        ItemStack result;
//        if (jsonObject.get("result").isJsonObject())
//            result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
//        else {
//            result = Utility.itemStackFromTagString(jsonObject.get("result").getAsString());
//        }
//
//        int processingTime = GsonHelper.getAsInt(jsonObject, "processingTime", this.defaultProcessingTime);
//
//        return builder.result(result).ingredients(ingredients).processingTime(processingTime).group(s).category(category);
//    }
//
//    public void toJson(R recipe, JsonObject jsonObject) {
//        jsonObject.addProperty("category", recipe.category == null ? RecipeCategory.MISC.name(): recipe.category.name());
//
//        if (!recipe.group.isEmpty()) {
//            jsonObject.addProperty("group", recipe.group);
//        }
//
//        JsonArray jsonArray = new JsonArray();
//        ItemStack[] stacks = new ItemStack[recipe.ingredients.length];
//        for (int i = 0; i < recipe.ingredients.length; i++) {
//            ItemStack stack = recipe.ingredients[i].getItems()[0];
//            CompoundTag compoundTag = stack.save(new CompoundTag());
//            String itemStackJson = compoundTag.getAsString();
//            jsonArray.add(itemStackJson);
//        }
//        jsonObject.add("ingredient", jsonArray);
//
//        CompoundTag resultTag = recipe.result.save(new CompoundTag());
//        String resultJson = resultTag.getAsString();
//        jsonObject.addProperty("result", resultJson);
//        jsonObject.addProperty("processingTime", recipe.processingTime);
//    }
//
//    public B fromNetwork(B builder, RegistryFriendlyByteBuf buf) {
//        // Read category
//        RecipeCategory category = RecipeCategory.valueOf(buf.readUtf());
//
//        // Read group
//        String group = buf.readUtf();
//
//        // Read ingredients
//        int ingredientCount = buf.readVarInt();
//        Ingredient[] ingredients = new Ingredient[ingredientCount];
//        for (int i = 0; i < ingredientCount; i++) {
//            ingredients[i] = Ingredient.fromNetwork(buf);
//        }
//
//        // Read result, experience, and processing time
//        ItemStack result = buf.readItem();
//        int processingTime = buf.readVarInt();
//
//        return builder.result(result).ingredients(ingredients).processingTime(processingTime).group(group).category(category);
//    }
//
//    public void toNetwork(FriendlyByteBuf buf, R obj) {
//        // Read category
//        buf.writeUtf(obj.category == null ? RecipeCategory.MISC.name() : obj.category.name());
//
//        // Write group
//        buf.writeUtf(obj.group);
//
//        // Write ingredients
//        buf.writeVarInt(obj.ingredients.length);
//        for (Ingredient ingredient : obj.ingredients) {
//            ingredient.toNetwork(buf);
//        }
//
//        // Write result, experience, and processing time
//        buf.writeItem(obj.result);
//        buf.writeVarInt(obj.processingTime);
//    }




    // ----- 1.21 -----

    public static void toNetwork(RegistryFriendlyByteBuf buffer, ShapelessProcessingRecipe recipe) {
        buffer.writeUtf(recipe.category.name());
        buffer.writeUtf(recipe.group);
        buffer.writeVarInt(recipe.ingredients.length);
        for (Ingredient ingredient : recipe.ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        buffer.writeVarInt(recipe.processingTime);
    }

    public static <T extends ShapelessProcessingRecipe> Products.P5
            <com.mojang.serialization.codecs.RecordCodecBuilder.Mu<T>,
            net.minecraft.data.recipes.RecipeCategory,
            java.lang.String,
            java.util.List<net.minecraft.world.item.crafting.Ingredient>,
            net.minecraft.world.item.ItemStack,
            java.lang.Integer>
    getDefaultCodecBuilder(RecordCodecBuilder.Instance<T> codecBuilder) {
        return codecBuilder.group(
                ShapelessProcessingRecipeSerializerHelper.RECIPE_CATEGORY_CODEC.fieldOf("category").forGetter(ShapelessProcessingRecipe::getCategory),
                Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::getGroup),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(ShapelessProcessingRecipe::getIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ShapelessProcessingRecipe::getResultItem),
                Codec.INT.fieldOf("processingTime").forGetter(ShapelessProcessingRecipe::getProcessingTime)
        );
    }
}
