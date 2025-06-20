package grill24.potionsplus.debug;

import net.neoforged.fml.loading.FMLEnvironment;

public class Debug {
    public static final boolean DEBUG = !FMLEnvironment.production;

    public static final boolean DEBUG_POTION_INGREDIENTS_GENERATION = DEBUG;
    public static final boolean DEBUG_POTION_RECIPE_GENERATION = DEBUG;
    public static final boolean DEBUG_RUNTIME_RESOURCE_INJECTION = false;
    public static final boolean DEBUG_RUNTIME_RESOURCE_TIME = DEBUG;

    public static boolean shouldRevealAllRecipes = false;
}
