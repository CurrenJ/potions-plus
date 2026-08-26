package grill24.potionsplus.debug;

import grill24.potionsplus.platform.Platform;

public class Debug {
    public static final boolean DEBUG = Platform.isDevelopmentEnvironment();

    public static final boolean DEBUG_POTION_INGREDIENTS_GENERATION = DEBUG;
    public static final boolean DEBUG_POTION_RECIPE_GENERATION = DEBUG;

    public static boolean shouldRevealAllRecipes = false;
}
