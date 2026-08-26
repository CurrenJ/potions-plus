package grill24.potionsplus.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import grill24.potionsplus.effect.LastPotionUsePlayerData;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataAttachments {
    // Injectable implementations populated by platform at startup via initPlatform()
    public static Function<Player, LastPotionUsePlayerData> LAST_POTION_USE_PLAYER_DATA_GET;
    public static BiConsumer<Player, LastPotionUsePlayerData> LAST_POTION_USE_PLAYER_DATA_SET;

    private static volatile boolean initialized;

    @ExpectPlatform
    public static void initPlatform() {
        throw new AssertionError();
    }

    private static void ensureInit() {
        if (!initialized) {
            initPlatform();
            initialized = true;
        }
    }

    public static LastPotionUsePlayerData getLastPotionUseData(Player player) {
        ensureInit();
        return LAST_POTION_USE_PLAYER_DATA_GET.apply(player);
    }

    public static void setLastPotionUseData(Player player, LastPotionUsePlayerData data) {
        ensureInit();
        LAST_POTION_USE_PLAYER_DATA_SET.accept(player, data);
    }
}
