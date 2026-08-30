package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.ModState;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.persistence.SavedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.Collection;

/**
 * Fabric counterpart of {@code neoforge/.../core/neoforge/ServerLifecycleListeners}' {@code
 * onServerStarted} handler - missed during Phase 5 ("NeoForge-only systems"), and only surfaced by
 * Phase 9's game tests: without it, {@code Recipes.RECIPE_INJECTION_FUNCTIONS} (seeded brewing
 * cauldron recipes, sanguine altar recipes - see common's {@code RecipeManagerMixin}) never ran on
 * Fabric, so every recipe-dependent game test failed with "no recipe was generated" even though the
 * mixin, the generation logic, and the recipe types themselves are all shared common/ code.
 *
 * <p>Does not port {@code onDataPackSync}/{@code onRecipesSynced} - those exist only to push
 * NeoForge's custom-`RecipeType`-aware datapack-sync payload to real clients; vanilla already
 * network-syncs the recipe manager to every joining player without an explicit hook, and Fabric's own
 * game-test server never has a real client to sync to.
 */
public final class ServerLifecycleListeners {

    private ServerLifecycleListeners() {}

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleListeners::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        ModState.SERVER = server;

        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any
        // recipes are generated or loot tables are initialized.
        long seed = server.getWorldGenSettings().options().seed();
        PotionsPlus.worldSeed = seed;
        ModState.worldSeed = seed;

        injectRuntimeRecipes(server);
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        SavedDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData.TYPE);
    }

    private static void injectRuntimeRecipes(MinecraftServer server) {
        // RecipeManagerMixin (common/) reads Recipes.RECIPE_INJECTION_FUNCTIONS during a resource
        // reload and already calls Recipes.postProcessRecipes(...) itself after each injection
        // (needed because sanguine altar recipes depend on completed brewing cauldron recipe
        // analysis) - no separate post-process call is needed here. Unlike NeoForge, vanilla's
        // RecipeManager has no public recipeMap() accessor to call it again with afterward.
        Recipes.registerRecipeInjectionFunctions();

        PackRepository packRepository = server.getPackRepository();
        Collection<String> selectedIds = packRepository.getSelectedIds();
        server.reloadResources(selectedIds);
    }
}
