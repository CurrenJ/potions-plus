package grill24.potionsplus.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.Utility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ShapelessProcessingRecipeSerializer<R extends ShapelessProcessingRecipe, B extends ShapelessProcessingRecipeBuilder<R, B>> extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<R> {
    protected Supplier<B> builderSupplier;
    protected final int defaultProcessingTime;

    public ShapelessProcessingRecipeSerializer(Supplier<B> builderSupplier, int defaultProcessingTime) {
        this.builderSupplier = builderSupplier;
        this.defaultProcessingTime = defaultProcessingTime;
    }

    @Override
    public @NotNull R fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
        B builder = builderSupplier.get();
        return fromJson(builder, jsonObject).build(resourceLocation);
    }

    @Override
    public R fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf buf) {
        B builder = builderSupplier.get();
        return fromNetwork(builder, buf).build(resourceLocation);
    }

    public @NotNull B fromJson(B builder, @NotNull JsonObject jsonObject) {
        String s = GsonHelper.getAsString(jsonObject, "group", "");
        JsonElement jsonelement = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");

        // Read ingredients
        Ingredient[] ingredients;
        if (jsonelement.isJsonArray()) {
            JsonArray jsonArray = jsonelement.getAsJsonArray();
            ingredients = new Ingredient[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                ingredients[i] = Utility.ingredientFromTagString(jsonArray.get(i).getAsString());
            }
        } else {
            ingredients = new Ingredient[]{Ingredient.fromJson(jsonelement)};
        }

        // Read result
        if (!jsonObject.has("result"))
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack result;
        if (jsonObject.get("result").isJsonObject())
            result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
        else {
            result = Utility.itemStackFromTagString(jsonObject.get("result").getAsString());
        }

        int processingTime = GsonHelper.getAsInt(jsonObject, "processingTime", this.defaultProcessingTime);

        return builder.result(result).ingredients(ingredients).processingTime(processingTime).group(s);
    }

    public void toJson(R recipe, JsonObject jsonObject) {
        if (!recipe.group.isEmpty()) {
            jsonObject.addProperty("group", recipe.group);
        }

        JsonArray jsonArray = new JsonArray();
        ItemStack[] stacks = new ItemStack[recipe.ingredients.length];
        for (int i = 0; i < recipe.ingredients.length; i++) {
            ItemStack stack = recipe.ingredients[i].getItems()[0];
            CompoundTag compoundTag = stack.save(new CompoundTag());
            String itemStackJson = compoundTag.getAsString();
            jsonArray.add(itemStackJson);
        }
        jsonObject.add("ingredient", jsonArray);

        CompoundTag resultTag = recipe.result.save(new CompoundTag());
        String resultJson = resultTag.getAsString();
        jsonObject.addProperty("result", resultJson);
        jsonObject.addProperty("processingTime", recipe.processingTime);
    }

    public B fromNetwork(B builder, FriendlyByteBuf buf) {
        // Read group
        String group = buf.readUtf();

        // Read ingredients
        int ingredientCount = buf.readVarInt();
        Ingredient[] ingredients = new Ingredient[ingredientCount];
        for (int i = 0; i < ingredientCount; i++) {
            ingredients[i] = Ingredient.fromNetwork(buf);
        }

        // Read result, experience, and processing time
        ItemStack result = buf.readItem();
        int processingTime = buf.readVarInt();

        return builder.result(result).ingredients(ingredients).processingTime(processingTime).group(group);
    }

    public void toNetwork(FriendlyByteBuf buf, R obj) {
        // Write group
        buf.writeUtf(obj.group);

        // Write ingredients
        buf.writeVarInt(obj.ingredients.length);
        for (Ingredient ingredient : obj.ingredients) {
            ingredient.toNetwork(buf);
        }

        // Write result, experience, and processing time
        buf.writeItem(obj.result);
        buf.writeVarInt(obj.processingTime);
    }

}
