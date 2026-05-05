package grill24.potionsplus.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import grill24.potionsplus.effect.LastPotionUsePlayerData;
import grill24.potionsplus.effect.ShouldBouncePlayerData;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataAttachments {
    // Injectable implementations populated by platform at startup via initPlatform()
    public static Function<Player, SkillsData> SKILL_PLAYER_DATA_GET;
    public static BiConsumer<Player, SkillsData> SKILL_PLAYER_DATA_SET;

    public static Predicate<LivingEntity> SHOULD_BOUNCE_PLAYER_DATA_HAS;
    public static BiConsumer<Player, ShouldBouncePlayerData> SHOULD_BOUNCE_PLAYER_DATA_SET;
    public static java.util.function.Consumer<LivingEntity> SHOULD_BOUNCE_PLAYER_DATA_REMOVE;

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

    public static SkillsData getSkillsData(Player player) {
        ensureInit();
        return SKILL_PLAYER_DATA_GET.apply(player);
    }

    public static void setSkillsData(Player player, SkillsData data) {
        ensureInit();
        SKILL_PLAYER_DATA_SET.accept(player, data);
    }

    public static boolean hasShouldBounceData(LivingEntity entity) {
        ensureInit();
        return SHOULD_BOUNCE_PLAYER_DATA_HAS.test(entity);
    }

    public static void setShouldBounceData(Player player, ShouldBouncePlayerData data) {
        ensureInit();
        SHOULD_BOUNCE_PLAYER_DATA_SET.accept(player, data);
    }

    public static void removeShouldBounceData(LivingEntity entity) {
        ensureInit();
        SHOULD_BOUNCE_PLAYER_DATA_REMOVE.accept(entity);
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
