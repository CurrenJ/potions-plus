package grill24.potionsplus.recipe;

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

        //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
        if (!jsonObject.has("result"))
            throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack itemstack;
        if (jsonObject.get("result").isJsonObject())
            itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
        else {
            itemstack = Utility.itemStackFromTagString(jsonObject.get("result").getAsString());
        }
        float f = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
        int i = GsonHelper.getAsInt(jsonObject, "cookingtime", this.defaultProcessingTime);
        return this.factory.create(resourceLocation, s, ingredients, itemstack, f, i);
    }

    public T fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf buf) {
        String s = buf.readUtf();
        int ingredientCount = buf.readVarInt();
        Ingredient[] ingredients = new Ingredient[ingredientCount];
        for (int i = 0; i < ingredientCount; i++) {
            ingredients[i] = Ingredient.fromNetwork(buf);
        }
        ItemStack itemstack = buf.readItem();
        float f = buf.readFloat();
        int i = buf.readVarInt();
        return this.factory.create(resourceLocation, s, ingredients, itemstack, f, i);
    }

    public void toNetwork(FriendlyByteBuf buf, T obj) {
        buf.writeUtf(obj.group);
        buf.writeVarInt(obj.ingredients.length);
        for (Ingredient ingredient : obj.ingredients) {
            ingredient.toNetwork(buf);
        }
        buf.writeItem(obj.result);
        buf.writeFloat(obj.experience);
        buf.writeVarInt(obj.processingTime);
    }

    public interface Factory<T extends BrewingCauldronRecipe> {
        T create(ResourceLocation resourceLocation, String group, Ingredient[] ingredients, ItemStack itemStack, float v, int i);
    }
}

