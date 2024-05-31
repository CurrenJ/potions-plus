package grill24.potionsplus.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import grill24.potionsplus.persistence.adapter.BlockPosTypeAdapter;
import grill24.potionsplus.persistence.adapter.ItemStackTypeAdapter;
import grill24.potionsplus.persistence.adapter.MutableBlockPosTypeAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        gsonBuilder.registerTypeAdapter(BlockPos.class, new BlockPosTypeAdapter());
        gsonBuilder.registerTypeAdapter(BlockPos.MutableBlockPos.class, new MutableBlockPosTypeAdapter());
        return gsonBuilder.create();
    }

    public void updateDataForPlayer(Player player, Consumer<PlayerBrewingKnowledge> consumer) {
        PlayerBrewingKnowledge playerBrewingKnowledgeData = playerDataMap.computeIfAbsent(player.getUUID(), uuid -> new PlayerBrewingKnowledge());
        consumer.accept(playerBrewingKnowledgeData);
        setDirty();
    }

    public PlayerBrewingKnowledge getData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUUID(), uuid -> new PlayerBrewingKnowledge());
    }

    public PlayerBrewingKnowledge getData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, (key) -> new PlayerBrewingKnowledge());
    }
}
