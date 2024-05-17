package grill24.potionsplus.core;

import grill24.potionsplus.persistence.SavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerLifecycleListeners {
    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event) {
        PotionsPlus.worldSeed = event.getServer().getWorldData().worldGenSettings().seed();
        int numInjected = Recipes.injectRuntimeRecipes(Recipes.BREWING_CAULDRON_RECIPE.get(), event.getServer().getRecipeManager());
        PotionsPlus.LOGGER.info("[PotionsPlus] Injected " + numInjected + " runtime recipes");

        // Setup saved data
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        if(level != null) {
            DimensionDataStorage dataStorage = level.getDataStorage();
            SavedData.instance = dataStorage.computeIfAbsent(SavedData::load, SavedData::create, SavedData.SAVED_DATA_ID);
        }
    }

    @SubscribeEvent
    public static void onRecipesSynced(final RecipesUpdatedEvent event) {
        Recipes.computeUniqueIngredientsList();
    }
}
