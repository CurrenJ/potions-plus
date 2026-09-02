package grill24.potionsplus.core.forge;

/**
 * PHASE 5/7: on NeoForge this captures the world seed and injects runtime recipes (seeded brewing
 * cauldron recipes, sanguine altar recipes) on SERVER_STARTED. Both depend on the recipe-injection
 * / SavedData machinery that is still NeoForge-only; until then there is nothing to register.
 */
public final class ServerLifecycleListeners {
    private ServerLifecycleListeners() {
    }

    public static void register() {
    }
}
