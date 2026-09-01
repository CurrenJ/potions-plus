package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.ModState;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.SavedDataStorage;
import grill24.potionsplus.network.ClientboundSyncRuntimeRecipesPacket;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;

import java.util.Collection;

/**
 * Forge counterpart of {@code neoforge/.../core/neoforge/ServerLifecycleListeners}' {@code
 * onServerStarted} handler (and of the Fabric class of the same name) - missed during Phase 5
 * ("NeoForge-only systems"). Without it, {@link ModState#SERVER} stayed null and
 * {@code Recipes.RECIPE_INJECTION_FUNCTIONS} stayed empty, so common's {@code RecipeManagerMixin}
 * logged "MinecraftServer not ready, can't inject seeded runtime recipes" and injected nothing:
 * no seeded brewing cauldron recipes, no sanguine altar recipes, hence no potion tooltips on
 * ingredients and an empty abyssal trove. {@link SavedData#instance} was never bound to the level's
 * storage either, so per-player brewing knowledge was neither loaded nor persisted.
 *
 * <p>NeoForge's {@code onDataPackSync}/{@code onRecipesSynced} pair is replaced here by
 * {@link ClientboundSyncRuntimeRecipesPacket}: Forge's {@code OnDatapackSyncEvent} has no
 * {@code sendRecipes}, and its client-side {@code RecipesUpdatedEvent} carries only a
 * {@code ClientRecipeBook}, so remote clients get the recipes through a packet of our own instead.
 */
public final class ServerLifecycleListeners {

    private ServerLifecycleListeners() {
    }

    public static void register() {
        ServerStartedEvent.BUS.addListener(event -> onServerStarted(event.getServer()));

        // Fires on player join and on /reload, before vanilla sends tags and recipes - the same hook
        // NeoForge's sendRecipes rides on. Forge's event has no sendRecipes, so push our own packet.
        OnDatapackSyncEvent.BUS.addListener(
                (OnDatapackSyncEvent event) -> event.getPlayers().forEach(ClientboundSyncRuntimeRecipesPacket::sendTo));
    }

    private static void onServerStarted(MinecraftServer server) {
        PotionsPlusForge.SERVER = server;
        ModState.SERVER = server;

        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any
        // recipes are generated or loot tables are initialized.
        long seed = server.getWorldGenSettings().options().seed();
        PotionsPlusForge.worldSeed = seed;
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
        // Must go through the common Recipes class - RecipeManagerMixin (common/) reads
        // grill24.potionsplus.core.Recipes.RECIPE_INJECTION_FUNCTIONS, which is a *different* list
        // from this forge module's own Recipes class. Populating the wrong one injects nothing.
        // The mixin calls Recipes.postProcessRecipes(...) itself after each injection (sanguine
        // altar recipes depend on completed brewing cauldron analysis), so the reload below is all
        // that is needed to bring the analyses up to date. (No explicit post-process call afterward:
        // unlike NeoForge, vanilla's RecipeManager exposes no public recipeMap() accessor.)
        Recipes.registerRecipeInjectionFunctions();

        PackRepository packRepository = server.getPackRepository();
        Collection<String> selectedIds = packRepository.getSelectedIds();
        server.reloadResources(selectedIds);
    }
}
