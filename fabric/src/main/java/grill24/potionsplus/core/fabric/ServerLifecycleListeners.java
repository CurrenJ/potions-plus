package grill24.potionsplus.core.fabric;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.RecipesRegistrar;
import grill24.potionsplus.persistence.SavedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * Runtime recipe injection (seeded brewing cauldron / sanguine altar recipes) on SERVER_STARTED.
 * {@link RecipesRegistrar} (common/ as of Phase 9/11a) now covers the loader-agnostic half of what
 * was NeoForge-only until this session - see docs/multi-loader-expansion.md Phase 9/11a progress
 * log. {@link SanguineAltarBlockEntity}/{@link AbyssalTroveBlockEntity} moved to common/ and are
 * now registered on Fabric too (Phase 11, 7th/8th sessions), so - unlike the stale comment this
 * replaced said - the same block-entity-facing follow-up NeoForge's equivalent runs after
 * {@link RecipesRegistrar#postProcessRecipes} applies here too; mirrored in {@link #postProcessRecipes}
 * to match {@code core.neoforge.ServerLifecycleListeners} exactly.
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

        postProcessRecipes(server.getRecipeManager());
    }

    public static void postProcessRecipes(RecipeManager recipeManager) {
        RecipesRegistrar.postProcessRecipes(recipeManager);

        SanguineAltarBlockEntity.computeRecipeMap(RecipesRegistrar.SANGUINE_ALTAR_ANALYSIS.getRecipes());
        AbyssalTroveBlockEntity.computeAbyssalTroveIngredients();
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        DimensionDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData.factory(level), SavedData.FILE_NAME);
    }
}
