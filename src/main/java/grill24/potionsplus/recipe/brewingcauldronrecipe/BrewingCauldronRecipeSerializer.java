package grill24.potionsplus.recipe.brewingcauldronrecipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import grill24.potionsplus.utility.Utility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class BrewingCauldronRecipeSerializer<T extends BrewingCauldronRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
    private final int defaultProcessingTime;
    private final BrewingCauldronRecipeSerializer.Factory<T> factory;

    public BrewingCauldronRecipeSerializer(BrewingCauldronRecipeSerializer.Factory<T> factory, int defaultProcessingTime) {
        this.defaultProcessingTime = defaultProcessingTime;
        this.factory = factory;
    }

    public @NotNull T fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
        String s = GsonHelper.getAsString(jsonObject, "group", "");
        JsonElement jsonelement = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");

        // Read tier
        int tier = GsonHelper.getAsInt(jsonObject, "tier", -1);

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

        // Read experience and processing time
        float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
        int processingTime = GsonHelper.getAsInt(jsonObject, "cookingtime", this.defaultProcessingTime);

        return this.factory.create(resourceLocation, s, tier, ingredients, result, experience, processingTime);
    }

    public T fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf buf) {
        // Read group
        String group = buf.readUtf();

        // Read tier
        int tier = buf.readVarInt();

        // Read ingredients
        int ingredientCount = buf.readVarInt();
        Ingredient[] ingredients = new Ingredient[ingredientCount];
        for (int i = 0; i < ingredientCount; i++) {
            ingredients[i] = Ingredient.fromNetwork(buf);
        }

        // Read result, experience, and processing time
        ItemStack result = buf.readItem();
        float experience = buf.readFloat();
        int processingTime = buf.readVarInt();

        // Create the recipe
        return this.factory.create(resourceLocation, group, tier, ingredients, result, experience, processingTime);
    }

    public void toNetwork(FriendlyByteBuf buf, T obj) {
        // Write group
        buf.writeUtf(obj.group);

        // Write tier
        buf.writeVarInt(obj.tier);

        // Write ingredients
        buf.writeVarInt(obj.ingredients.length);
        for (Ingredient ingredient : obj.ingredients) {
            ingredient.toNetwork(buf);
        }

        // Write result, experience, and processing time
        buf.writeItem(obj.result);
        buf.writeFloat(obj.experience);
        buf.writeVarInt(obj.processingTime);
    }

    public interface Factory<T extends BrewingCauldronRecipe> {
        T create(ResourceLocation resourceLocation, String group, int tier, Ingredient[] ingredients, ItemStack itemStack, float v, int i);
    }
}

