package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.ModState;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.network.ClientboundSyncRuntimeRecipesPacket;
import grill24.potionsplus.persistence.SavedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
 * <p>{@code onDataPackSync}/{@code onRecipesSynced} have no Fabric counterpart either - vanilla
 * network-syncs only recipe <em>displays</em> since 1.21.2, never whole custom-type recipes - so
 * remote clients get them through {@link ClientboundSyncRuntimeRecipesPacket}, sent from the two
 * events registered above.
 */
public final class ServerLifecycleListeners {

    private ServerLifecycleListeners() {}

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleListeners::onServerStarted);

        // Stand-ins for NeoForge's OnDatapackSyncEvent, which fires on both join and /reload:
        // Fabric splits those into a connection event and a datapack-reload event.
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> ClientboundSyncRuntimeRecipesPacket.sendTo(handler.getPlayer()));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                server.getPlayerList().getPlayers().forEach(ClientboundSyncRuntimeRecipesPacket::sendTo);
            }
        });
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
