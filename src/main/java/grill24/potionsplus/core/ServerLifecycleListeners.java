package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerLifecycleListeners {
    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event) {
        final MinecraftServer server = event.getServer();

        // Setup saved data
        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any recipes are generated or loot tables are initialized.
        PotionsPlus.worldSeed = server.getWorldData().worldGenSettings().seed();

        // Inject runtime recipes
        injectRuntimeRecipes(server);
    }

    @SubscribeEvent
    public static void onRecipesSynced(final RecipesUpdatedEvent event) {
        Recipes.computeUniqueIngredientsList();
        SanguineAltarBlockEntity.computeChainedIngredients();
    }

    private static void injectRuntimeRecipes(MinecraftServer server) {
        int numInjected = Recipes.injectRuntimeRecipes(server, Recipes.BREWING_CAULDRON_RECIPE.get());
        PotionsPlus.LOGGER.info("Injected {} runtime recipes", numInjected);
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        DimensionDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData::load, SavedData::create, SavedData.FILE_NAME);
    }
}
