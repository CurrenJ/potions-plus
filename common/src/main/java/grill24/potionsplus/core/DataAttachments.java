package grill24.potionsplus.core;

import grill24.potionsplus.effect.LastPotionUsePlayerData;
import grill24.potionsplus.effect.ShouldBouncePlayerData;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataAttachments {
    // Injectable implementations populated by neoforge/ at startup
    public static Function<Player, SkillsData> SKILL_PLAYER_DATA_GET;
    public static BiConsumer<Player, SkillsData> SKILL_PLAYER_DATA_SET;

    public static Predicate<LivingEntity> SHOULD_BOUNCE_PLAYER_DATA_HAS;
    public static BiConsumer<Player, ShouldBouncePlayerData> SHOULD_BOUNCE_PLAYER_DATA_SET;
    public static java.util.function.Consumer<LivingEntity> SHOULD_BOUNCE_PLAYER_DATA_REMOVE;

    public static Function<Player, LastPotionUsePlayerData> LAST_POTION_USE_PLAYER_DATA_GET;
    public static BiConsumer<Player, LastPotionUsePlayerData> LAST_POTION_USE_PLAYER_DATA_SET;

    // Service methods
    public static SkillsData getSkillsData(Player player) {
        return SKILL_PLAYER_DATA_GET.apply(player);
    }

    public static void setSkillsData(Player player, SkillsData data) {
        SKILL_PLAYER_DATA_SET.accept(player, data);
    }

    public static boolean hasShouldBounceData(LivingEntity entity) {
        return SHOULD_BOUNCE_PLAYER_DATA_HAS.test(entity);
    }

    public static void setShouldBounceData(Player player, ShouldBouncePlayerData data) {
        SHOULD_BOUNCE_PLAYER_DATA_SET.accept(player, data);
    }

    public static void removeShouldBounceData(LivingEntity entity) {
        SHOULD_BOUNCE_PLAYER_DATA_REMOVE.accept(entity);
    }

    public static LastPotionUsePlayerData getLastPotionUseData(Player player) {
        return LAST_POTION_USE_PLAYER_DATA_GET.apply(player);
    }

    public static void setLastPotionUseData(Player player, LastPotionUsePlayerData data) {
        LAST_POTION_USE_PLAYER_DATA_SET.accept(player, data);
    }
}
