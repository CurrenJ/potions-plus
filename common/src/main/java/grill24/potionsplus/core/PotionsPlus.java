package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Loader-agnostic constants shared by every platform. The NeoForge {@code @Mod} entrypoint that
 * flushes the registries lives at {@code core.neoforge.PotionsPlus} (see
 * docs/multi-loader-expansion.md Phase 6).
 */
public class PotionsPlus {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable MinecraftServer SERVER;

    public static long worldSeed = -1;

    public static class Debug {
        public static final boolean DEBUG = true;

        public static final boolean DEBUG_POTION_INGREDIENTS_GENERATION = true;
        public static final boolean DEBUG_POTION_RECIPE_GENERATION = true;

        public static boolean shouldRevealAllRecipes = false;
    }
}
