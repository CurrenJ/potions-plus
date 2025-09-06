package grill24.potionsplus.persistence.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.HolderLookup;

import java.io.IOException;

public class BrewingCauldronRecipeTypeAdapter extends TypeAdapter<BrewingCauldronRecipe> {
    private final HolderLookup.Provider registries;

    public BrewingCauldronRecipeTypeAdapter(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void write(JsonWriter out, BrewingCauldronRecipe brewingCauldronRecipe) throws IOException {
        JsonObject jsonObject = new JsonObject();
        BrewingCauldronRecipe.CODEC.encodeStart(JsonOps.INSTANCE, brewingCauldronRecipe).result().ifPresent(jsonElement -> jsonObject.add("recipe", jsonElement));

        out.beginObject();
        out.jsonValue(jsonObject.toString());
        out.endObject();
    }

    @Override
    public BrewingCauldronRecipe read(JsonReader in) throws IOException {
        JsonElement recipe = null;

        in.beginObject();
        while (in.hasNext()) {
            if (in.nextName().equals("recipe")) {
                recipe = JsonParser.parseString(in.nextString());
            }
        }
        in.endObject();

        if (recipe == null)
            throw new IOException("Invalid BrewingCauldronRecipe JSON");

        return (BrewingCauldronRecipe) BrewingCauldronRecipe.CODEC.decode(JsonOps.INSTANCE, recipe)
                .result().map(Pair::getFirst)
                .orElseThrow(() -> new IOException("Failed to decode BrewingCauldronRecipe JSON"));
    }
}
