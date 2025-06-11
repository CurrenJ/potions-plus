package grill24.potionsplus.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.core.seededrecipe.PpMultiIngredient;
import grill24.potionsplus.event.PpFishCaughtEvent;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.*;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SavedData extends net.minecraft.world.level.saveddata.SavedData {
    public static SavedData instance = new SavedData();

    public static final String FILE_NAME = "potionsplus";

    private final ServerLevel level;
    public Map<UUID, PlayerBrewingKnowledge> playerDataMap;
    public List<RecipeHolder<BrewingCauldronRecipe>> seededPotionRecipes;
    public Map<String, List<BrewingCauldronRecipe>> recipeResultsInSavedData;
    public FishingLeaderboards fishingLeaderboards;


    public static final SavedDataType<SavedData> TYPE = new SavedDataType<>(FILE_NAME, SavedData::new, ctx ->
            RecordCodecBuilder.create(builder -> builder.group(
                    RecordCodecBuilder.point(ctx.levelOrThrow()),
                    Codec.unboundedMap(PotionsPlusExtraCodecs.UUID_CODEC, PlayerBrewingKnowledge.CODEC).fieldOf("playerDataMap").forGetter(data -> data.playerDataMap),
                    RecipeCodecs.BREWING_CAULDRON_RECIPE_HOLDER_CODEC.listOf().fieldOf("seededPotionRecipes").forGetter(data -> data.seededPotionRecipes),
                    Codec.unboundedMap(Codec.STRING, RecipeCodecs.BREWING_CAULDRON_RECIPE_CODEC.listOf())
                            .fieldOf("recipeResultsInSavedData").forGetter(data -> data.recipeResultsInSavedData),
                    FishingLeaderboards.CODEC.fieldOf("fishingLeaderboards").forGetter(data -> data.fishingLeaderboards)
            ).apply(builder, SavedData::new)));

    public SavedData(net.minecraft.world.level.saveddata.SavedData.Context ctx) {
        this(ctx.levelOrThrow(), new HashMap<>(), new ArrayList<>(), new HashMap<>(), new FishingLeaderboards());
    }

    /**
     * Default constructor for creating an empty SavedData instance.
     * This is used when the data is first created or when it needs to be reset.
     * Also used when a client has no server data loaded yet, so it can still safely query the data.
     */
    private SavedData() {
        this(null, new HashMap<>(), new ArrayList<>(), new HashMap<>(), new FishingLeaderboards());
    }

    public SavedData(ServerLevel level, Map<UUID, PlayerBrewingKnowledge> playerDataMap,
                     List<RecipeHolder<BrewingCauldronRecipe>> seededPotionRecipes,
                     Map<String, List<BrewingCauldronRecipe>> recipeResultsInSavedData,
                     FishingLeaderboards fishingLeaderboards) {
        this.level = level;
        this.playerDataMap = new HashMap<>(playerDataMap);
        this.seededPotionRecipes = new ArrayList<>(seededPotionRecipes);
        this.recipeResultsInSavedData = new HashMap<>(recipeResultsInSavedData);
        this.fishingLeaderboards = fishingLeaderboards;

        // Ensure mutability by creating new collections. Map and list codecs load immutable collections by default.
    }

    public void setBrewingCauldronRecipes(List<RecipeHolder<BrewingCauldronRecipe>> recipes) {
        this.seededPotionRecipes = new ArrayList<>(recipes);
        this.recipeResultsInSavedData = new HashMap<>();
        this.seededPotionRecipes.stream().map(RecipeHolder::value)
                .forEach(recipe -> {
                    PpIngredient result = PpIngredient.of(recipe.getResultItemWithTransformations(recipe.getIngredientsAsItemStacks()));

                    List<BrewingCauldronRecipe> recipesForResult = this.recipeResultsInSavedData.computeIfAbsent(result.toString(), key -> new ArrayList<>());
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
            List<BrewingCauldronRecipe> savedDataRecipes = recipeResultsInSavedData.getOrDefault(result.toString(), new ArrayList<>());

            // Check if their are any recipes that match the ingredients (and the result) in saved data.
            return savedDataRecipes.stream().anyMatch(savedDataRecipe -> {
                PpMultiIngredient savedDataIngredients = PpMultiIngredient.of(savedDataRecipe.getIngredientsAsItemStacks());
                PpMultiIngredient newRecipeIngredients = PpMultiIngredient.of(recipe.getIngredientsAsItemStacks());
                return savedDataIngredients.equals(newRecipeIngredients);
            });
        }
        // For the regular seeded potion recipes, we only care that the result matches. The input ingredients may be different, but we don't want more than one recipe for the same result potion.
        else {
            return recipeResultsInSavedData.containsKey(PpIngredient.of(recipe.getResultItemWithTransformations(recipe.getIngredientsAsItemStacks())).toString());
        }
    }

    public boolean isResultInRecipeSavedData(ItemStack result) {
        return recipeResultsInSavedData.containsKey(PpIngredient.of(result).toString());
    }

    @SubscribeEvent
    public static void onSizedFishCaught(final PpFishCaughtEvent event) {
        Player player = event.getPlayer();
        ItemStack fish = event.getFish();

        if (player.level().isClientSide()) {
            return;
        }

        SavedData.instance.fishingLeaderboards.onFishCaught(player, fish);
    }

    public void clear() {
        playerDataMap.clear();
        seededPotionRecipes.clear();
        recipeResultsInSavedData.clear();
        setDirty();
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
