package grill24.potionsplus.persistence.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.skill.SkillInstance;

import java.io.IOException;

public class SkillInstanceTypeAdapter extends TypeAdapter<SkillInstance<?, ?>> {
    @Override
    public void write(JsonWriter out, SkillInstance<?, ?> skillInstance) throws IOException {
        SkillInstance.CODEC.encodeStart(JsonOps.INSTANCE, skillInstance).result().ifPresent(jsonElement -> {
            try {
                // Don't start/end json object bc we just dump the json as a string.
                out.value(jsonElement.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public SkillInstance<?, ?> read(JsonReader in) throws IOException {
        JsonElement skillInstance = null;

        skillInstance = JsonParser.parseString(in.nextString());
        if (skillInstance == null)
            throw new IOException("Invalid SkillInstance JSON");

        return SkillInstance.CODEC.decode(JsonOps.INSTANCE, skillInstance)
                .result().map(Pair::getFirst)
                .orElseThrow(() -> new IOException("Failed to decode SkillInstance JSON"));
    }
}
