package grill24.potionsplus.persistence.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.io.IOException;

public class BrewingCauldronRecipeHolderTypeAdapter extends TypeAdapter<RecipeHolder<BrewingCauldronRecipe>> {
    private final HolderLookup.Provider registries;
    public BrewingCauldronRecipeHolderTypeAdapter(HolderLookup.Provider registries) {
        this.registries = registries;
    }


    @Override
    public void write(JsonWriter out, RecipeHolder<BrewingCauldronRecipe> brewingCauldronRecipeHolder) throws IOException {
        JsonObject jsonObject = new JsonObject();
        out.beginObject();
        BrewingCauldronRecipe.CODEC.encodeStart(JsonOps.INSTANCE, brewingCauldronRecipeHolder.value()).result().ifPresent(jsonElement -> jsonObject.add("recipe", jsonElement));
        Codec.STRING.encodeStart(JsonOps.INSTANCE, brewingCauldronRecipeHolder.id().toString()).result().ifPresent(jsonElement -> jsonObject.add("id", jsonElement));
        out.endObject();
    }

    @Override
    public RecipeHolder<BrewingCauldronRecipe> read(JsonReader in) throws IOException {
        JsonElement recipeJson = null;
        String id = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "recipe":
                    recipeJson = JsonParser.parseString(in.nextString());
                    break;
                case "id":
                    id = in.nextString();
                    break;
            }
        }
        in.endObject();

        if (recipeJson == null || id == null)
            throw new IOException("Invalid BrewingCauldronRecipe JSON");

        String finalId = id;
        return BrewingCauldronRecipe.CODEC.decode(JsonOps.INSTANCE, recipeJson).result().map(brewingCauldronRecipe ->
                new RecipeHolder(ResourceLocation.parse(finalId), brewingCauldronRecipe.getFirst()))
                .orElseThrow(() -> new IOException("Failed to decode BrewingCauldronRecipe JSON"));
    }
}
