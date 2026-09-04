package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.RecipesRegistrar;
import grill24.potionsplus.persistence.SavedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * Runtime recipe injection (seeded brewing cauldron / sanguine altar recipes) on SERVER_STARTED.
 * {@link RecipesRegistrar} (common/ as of Phase 9/11a) now covers the loader-agnostic half of what
 * was NeoForge-only until this session - see docs/multi-loader-expansion.md Phase 9/11a progress
 * log. Fabric has no sanguine-altar or abyssal-trove block entity yet, so unlike NeoForge's
 * equivalent, there is no block-entity-facing follow-up to run after
 * {@link RecipesRegistrar#postProcessRecipes}.
 */
public final class ServerLifecycleListeners {
    private ServerLifecycleListeners() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleListeners::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        PotionsPlus.SERVER = server;

        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any
        // recipes are generated or loot tables are initialized.
        PotionsPlus.worldSeed = server.getWorldData().worldGenOptions().seed();

        int numInjected = RecipesRegistrar.injectRuntimeRecipes(server);
        PotionsPlus.LOGGER.info("Injected {} runtime recipes", numInjected);

        RecipesRegistrar.postProcessRecipes(server.getRecipeManager());
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        DimensionDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData.factory(level), SavedData.FILE_NAME);
    }
}
