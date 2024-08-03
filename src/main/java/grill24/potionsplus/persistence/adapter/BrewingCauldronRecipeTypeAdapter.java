package grill24.potionsplus.persistence.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class BrewingCauldronRecipeTypeAdapter extends TypeAdapter<BrewingCauldronRecipe> {
    @Override
    public void write(JsonWriter out, BrewingCauldronRecipe brewingCauldronRecipe) throws IOException {
        JsonObject jsonObject = new JsonObject();
        Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get().toJson(brewingCauldronRecipe, jsonObject);

        out.beginObject();
        out.name("recipe").value(jsonObject.toString());
        out.name("id").value(brewingCauldronRecipe.getId().toString());
        out.endObject();
    }

    @Override
    public BrewingCauldronRecipe read(JsonReader in) throws IOException {
        JsonElement recipe = null;
        String id = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "recipe":
                    recipe = JsonParser.parseString(in.nextString());
                    break;
                case "id":
                    id = in.nextString();
                    break;
            }
        }
        in.endObject();

        if (recipe == null || id == null)
            throw new IOException("Invalid BrewingCauldronRecipe JSON");

        return Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.get().fromJson(new ResourceLocation(id), recipe.getAsJsonObject());
    }
}
