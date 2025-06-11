package grill24.potionsplus.core;

import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.Collection;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerLifecycleListeners {

    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event) {
        final MinecraftServer server = event.getServer();
        PotionsPlus.SERVER = server;

        // Setup saved data
        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any recipes are generated or loot tables are initialized.
        PotionsPlus.worldSeed = server.getWorldData().worldGenOptions().seed();

        // Inject runtime recipes (seeded brewing cauldron recipes, sanguine alter recipes, etc.)
        injectRuntimeRecipes(server);
    }

    /**
     * Tell the server to sync our custom recipe types to the client.
     * Otherwise, they won't be sent to the client and client-side recipe functions will not work.
     * @param event The event that is fired when the server syncs data packs to the client.
     */
    @SubscribeEvent
    public static void onDataPackSync(final OnDatapackSyncEvent event) {
        event.sendRecipes(Recipes.BREWING_CAULDRON_RECIPE.value(), Recipes.CLOTHESLINE_RECIPE.value(), Recipes.SANGUINE_ALTAR_RECIPE.value());
    }

    /**
     * Post-process recipes after they have been received by the client.
     * This is where we can perform any additional processing on the recipes.
     * @param event The event that is fired when recipes are received by the client.
     */
    @SubscribeEvent
    public static void onRecipesSynced(final RecipesReceivedEvent event) {
        Recipes.postProcessRecipes(event.getRecipeMap());
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        DimensionDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData.TYPE);
    }

    private static void injectRuntimeRecipes(MinecraftServer server) {
        // Init static recipe injection functions before injecting recipes (recipes injected after server start)
        Recipes.registerRecipeInjectionFunctions();

        // Reload server resources
        PackRepository packrepository = server.getPackRepository();
        Collection<String> selectedIds = packrepository.getSelectedIds();
        server.reloadResources(selectedIds);

        Recipes.postProcessRecipes(server.getRecipeManager().recipeMap());
    }
}
