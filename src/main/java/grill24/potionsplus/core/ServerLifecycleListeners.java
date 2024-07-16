package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerLifecycleListeners {
    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event) {
        // Store world seed - used for generating runtime recipes. Important to do this before any recipes are generated or loot tables are initialized.
        PotionsPlus.worldSeed = event.getServer().getWorldData().worldGenSettings().seed();

        // Inject runtime recipes
        int numInjected = Recipes.injectRuntimeRecipes(event.getServer(), Recipes.BREWING_CAULDRON_RECIPE.get(), event.getServer().getRecipeManager());
        PotionsPlus.LOGGER.info("Injected " + numInjected + " runtime recipes");

        // Setup saved data
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        if (level != null) {
            DimensionDataStorage dataStorage = level.getDataStorage();
            SavedData.instance = dataStorage.computeIfAbsent(SavedData::load, SavedData::create, SavedData.SAVED_DATA_ID);
        }
    }

    @SubscribeEvent
    public static void onRecipesSynced(final RecipesUpdatedEvent event) {
        Recipes.computeUniqueIngredientsList();
        SanguineAltarBlockEntity.computeChainedIngredients();
    }
}
