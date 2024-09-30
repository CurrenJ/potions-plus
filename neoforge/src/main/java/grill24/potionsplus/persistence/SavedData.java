package grill24.potionsplus.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.persistence.adapter.*;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class SavedData extends net.minecraft.world.level.saveddata.SavedData {
    public static SavedData instance = new SavedData();
    private static final String LOGGER_HEADER = "[Saved Data]";

    public static final String FILE_NAME = "potionsplus";
    public static final String PLAYER_DATA_MAP_KEY = "player_data_map";
    public static final String SEEDED_POTION_RECIPES_KEY = "seeded_potion_recipes";

    public Map<UUID, PlayerBrewingKnowledge> playerDataMap;

    public List<RecipeHolder<BrewingCauldronRecipe>> seededPotionRecipes;
    public Set<String> itemsWithRecipesInSavedData;

    public SavedData() {
        playerDataMap = new java.util.HashMap<>();
        seededPotionRecipes = new java.util.ArrayList<>();
        itemsWithRecipesInSavedData = new java.util.HashSet<>();
    }

    public static net.minecraft.world.level.saveddata.SavedData.Factory<SavedData> factory(ServerLevel level) {
        return new net.minecraft.world.level.saveddata.SavedData.Factory<>(SavedData::new, SavedData::load, null);
    }


    public static SavedData create() {
        return new SavedData();
    }

    public static SavedData load(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        Gson gson = createGson(lookupProvider);
        SavedData data = create();

        String json = compoundTag.getString(PLAYER_DATA_MAP_KEY);
        Type playerDataMapType = new TypeToken<HashMap<UUID, PlayerBrewingKnowledge>>() {
        }.getType();
        data.playerDataMap = gson.fromJson(json, playerDataMapType);
        PotionsPlus.LOGGER.info("{} Loaded {} player data entries from saved data.", LOGGER_HEADER, data.playerDataMap.size());

        json = getJoinedString(compoundTag, SEEDED_POTION_RECIPES_KEY);
        Type seededPotionRecipesType = new TypeToken<List<BrewingCauldronRecipe>>() {
        }.getType();
        data.setSeededPotionRecipes(gson.fromJson(json, seededPotionRecipesType));
        PotionsPlus.LOGGER.info("{} Loaded {} seeded potion recipes from saved data.", LOGGER_HEADER, data.seededPotionRecipes.size());

        return data;
    }

    public void setSeededPotionRecipes(List<RecipeHolder<BrewingCauldronRecipe>> recipes) {
        this.seededPotionRecipes = new ArrayList<>(recipes);
        this.itemsWithRecipesInSavedData = new HashSet<>();
        this.seededPotionRecipes.stream().map(RecipeHolder::value).map(BrewingCauldronRecipe::getResultItem)
                .forEach(itemStack -> this.itemsWithRecipesInSavedData.add(PUtil.getNameOrVerbosePotionName(itemStack)));
        setDirty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.Provider registries) {
        Gson gson = createGson(registries);

        String json = gson.toJson(playerDataMap);
        compoundTag.putString(PLAYER_DATA_MAP_KEY, json);

        json = gson.toJson(this.seededPotionRecipes);
        putSplitString(json, compoundTag, SEEDED_POTION_RECIPES_KEY);

        return compoundTag;
    }

    public void clear() {
        playerDataMap.clear();
        seededPotionRecipes.clear();
        itemsWithRecipesInSavedData.clear();
        setDirty();
    }

    private static Gson createGson(HolderLookup.Provider registries) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter(registries));
        gsonBuilder.registerTypeAdapter(BlockPos.class, new BlockPosTypeAdapter());
        gsonBuilder.registerTypeAdapter(BlockPos.MutableBlockPos.class, new MutableBlockPosTypeAdapter());
        gsonBuilder.registerTypeAdapter(BrewingCauldronRecipe.class, new BrewingCauldronRecipeTypeAdapter(registries));
        gsonBuilder.registerTypeAdapter(RecipeHolder.class, new BrewingCauldronRecipeHolderTypeAdapter(registries));
        gsonBuilder.registerTypeAdapter(String.class, new LargeStringTypeAdapter());
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

    private static void putSplitString(String string, CompoundTag tag, String key) {
        String[] chunks = splitString(string, 65535);
        for (int i = 0; i < chunks.length; i++) {
            tag.putString(key + "_" + i, chunks[i]);
        }
    }

    private static String getJoinedString(CompoundTag tag, String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; tag.contains(key + "_" + i); i++) {
            sb.append(tag.getString(key + "_" + i));
        }
        return sb.toString();
    }

    private static String[] splitString(String string, int chunkSize) {
        int len = string.length();
        int chunkCount = (len + chunkSize - 1) / chunkSize;
        String[] chunks = new String[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int start = i * chunkSize;
            int end = Math.min(len, start + chunkSize);
            chunks[i] = string.substring(start, end);
        }
        return chunks;
    }

    private static String joinString(String[] chunks) {
        StringBuilder sb = new StringBuilder();
        for (String chunk : chunks) {
            sb.append(chunk);
        }
        return sb.toString();
    }
}
