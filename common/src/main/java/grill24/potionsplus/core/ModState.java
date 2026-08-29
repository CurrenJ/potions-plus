package grill24.potionsplus.core;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModState {
    public static final Logger LOGGER = LoggerFactory.getLogger("potionsplus");

    public static @Nullable MinecraftServer SERVER;

    public static long worldSeed = -1;

    public static int expiryTime = 6000;

    public static Runnable jeiRefreshBrewingRecipesCallback = null;

    public static void refreshJeiBrewingRecipes() {
        if (jeiRefreshBrewingRecipesCallback != null) {
            try {
                jeiRefreshBrewingRecipesCallback.run();
            } catch (NoClassDefFoundError error) {
                LOGGER.warn("JEI is not loaded, cannot update brewing cauldron recipes", error);
            }
        }
    }
}
