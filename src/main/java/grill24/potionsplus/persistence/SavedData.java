package grill24.potionsplus.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SavedData extends net.minecraft.world.level.saveddata.SavedData {
    public static SavedData instance;
    public static final String SAVED_DATA_ID = "potionsplus_saved_data";

    public Map<UUID, PlayerBrewingKnowledge> playerDataMap;

    public SavedData() {
        playerDataMap = new java.util.HashMap<>();
    }

    public static SavedData create() {
        return new SavedData();
    }

    public static SavedData load(CompoundTag compoundTag) {
        Gson gson = createGson();
        SavedData data = create();

        byte[] testMapBytes = compoundTag.getByteArray(SAVED_DATA_ID);
        String json = new String(testMapBytes, StandardCharsets.UTF_8);
        Type type = new TypeToken<HashMap<UUID, PlayerBrewingKnowledge>>() {
        }.getType();
        data.playerDataMap = gson.fromJson(json, type);

        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        Gson gson = createGson();
        String json = gson.toJson(playerDataMap);
        byte[] dataBytes = json.getBytes(StandardCharsets.UTF_8);
        compoundTag.putByteArray(SAVED_DATA_ID, dataBytes);

        return compoundTag;
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
        gsonBuilder.registerTypeAdapter(net.minecraft.world.item.ItemStack.class, new grill24.potionsplus.persistence.adapter.ItemStackTypeAdapter());
        return gsonBuilder.create();
    }
}
