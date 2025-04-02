package grill24.potionsplus.persistence.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.skill.SkillInstance;
import grill24.potionsplus.utility.FishingLeaderboards;

import java.io.IOException;

public class FishingLeaderboardsTypeAdapter extends TypeAdapter<FishingLeaderboards> {
    @Override
    public void write(JsonWriter out, FishingLeaderboards fishingLeaderboards) throws IOException {
        FishingLeaderboards.CODEC.encodeStart(JsonOps.INSTANCE, fishingLeaderboards).result().ifPresent(jsonElement -> {
            try {
                out.value(jsonElement.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public FishingLeaderboards read(JsonReader in) throws IOException {
        JsonElement fishingLeaderboards = null;

        fishingLeaderboards = JsonParser.parseString(in.nextString());
        if (fishingLeaderboards == null)
            throw new IOException("Invalid FishingLeaderboards JSON");

        return FishingLeaderboards.CODEC.decode(JsonOps.INSTANCE, fishingLeaderboards)
                .result().map(Pair::getFirst)
                .orElseThrow(() -> new IOException("Failed to decode FishingLeaderboards JSON"));
    }
}
