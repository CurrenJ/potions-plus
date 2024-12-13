package grill24.potionsplus.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.core.seededrecipe.PpMultiIngredient;
import grill24.potionsplus.persistence.adapter.*;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.skill.SkillInstance;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
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
    public Map<PpIngredient, List<BrewingCauldronRecipe>> recipeResultsInSavedData;

    public SavedData() {
        playerDataMap = new java.util.HashMap<>();
        seededPotionRecipes = new java.util.ArrayList<>();
        recipeResultsInSavedData = new java.util.HashMap<>();
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
        Type seededPotionRecipesType = new TypeToken<List<RecipeHolder>>() {
        }.getType();
        data.setBrewingCauldronRecipes(gson.fromJson(json, seededPotionRecipesType));
        PotionsPlus.LOGGER.info("{} Loaded {} seeded potion recipes from saved data.", LOGGER_HEADER, data.seededPotionRecipes.size());

        return data;
    }

    public void setBrewingCauldronRecipes(List<RecipeHolder<BrewingCauldronRecipe>> recipes) {
        this.seededPotionRecipes = new ArrayList<>(recipes);
        this.recipeResultsInSavedData = new HashMap<>();
        this.seededPotionRecipes.stream().map(RecipeHolder::value)
                .forEach(recipe -> {
                    PpIngredient result = PpIngredient.of(recipe.getResultItemWithTransformations(recipe.getIngredientsAsItemStacks()));

                    List<BrewingCauldronRecipe> recipesForResult = this.recipeResultsInSavedData.computeIfAbsent(result, key -> new ArrayList<>());
                    recipesForResult.add(recipe);
                });
        setDirty();
    }

    public boolean isRecipeResultInSavedData(RecipeHolder<BrewingCauldronRecipe> recipeHolder) {
        BrewingCauldronRecipe recipe = recipeHolder.value();
        PpIngredient result = PpIngredient.of(recipe.getResultItemWithTransformations(recipe.getIngredientsAsItemStacks()));

        // Maybe should check potionMatchingCriteria from recipe here instead. This relies on the recipe using the Any Potion, which is just a visual indicator.
        boolean isAnyPotionEffect = PUtil.getAllEffects(recipe.getResult()).stream().anyMatch(effect -> effect.getEffect().is(MobEffects.ANY_POTION));
        boolean isAmpOrDurUpgrade = recipe.isAmplifierUpgrade() || recipe.isDurationUpgrade();
        if (isAnyPotionEffect && isAmpOrDurUpgrade) {
            // If the recipe is an amplifier or duration upgrade, we care that the result AND the ingredients match. Otherwise, this is a different recipe in saved data.
            List<BrewingCauldronRecipe> savedDataRecipes = recipeResultsInSavedData.getOrDefault(result, new ArrayList<>());

            // Check if their are any recipes that match the ingredients (and the result) in saved data.
            return savedDataRecipes.stream().anyMatch(savedDataRecipe -> {
                PpMultiIngredient savedDataIngredients = PpMultiIngredient.of(savedDataRecipe.getIngredientsAsItemStacks());
                PpMultiIngredient newRecipeIngredients = PpMultiIngredient.of(recipe.getIngredientsAsItemStacks());
                return savedDataIngredients.equals(newRecipeIngredients);
            });
        }
        // For the regular seeded potion recipes, we only care that the result matches. The input ingredients may be different, but we don't want more than one recipe for the same result potion.
        else {
            return recipeResultsInSavedData.containsKey(PpIngredient.of(recipe.getResultItemWithTransformations(recipe.getIngredientsAsItemStacks())));
        }
    }

    public boolean isResultInRecipeSavedData(ItemStack result) {
        return recipeResultsInSavedData.containsKey(PpIngredient.of(result));
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
        recipeResultsInSavedData.clear();
        setDirty();
    }

    private static Gson createGson(HolderLookup.Provider registries) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT, Modifier.STATIC);
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter(registries));
        gsonBuilder.registerTypeAdapter(BlockPos.class, new BlockPosTypeAdapter());
        gsonBuilder.registerTypeAdapter(BlockPos.MutableBlockPos.class, new MutableBlockPosTypeAdapter());
        gsonBuilder.registerTypeAdapter(BrewingCauldronRecipe.class, new BrewingCauldronRecipeTypeAdapter(registries));
        gsonBuilder.registerTypeAdapter(RecipeHolder.class, new BrewingCauldronRecipeHolderTypeAdapter(registries));
        gsonBuilder.registerTypeAdapter(String.class, new LargeStringTypeAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(SkillInstance.class, new SkillInstanceTypeAdapter());
        gsonBuilder.enableComplexMapKeySerialization();
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
