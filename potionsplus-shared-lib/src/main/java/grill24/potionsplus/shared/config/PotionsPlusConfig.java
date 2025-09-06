package grill24.potionsplus.shared.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class PotionsPlusConfig {
    public static final PotionsPlusConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<PotionsPlusConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(PotionsPlusConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<Boolean> enableSkills;

    public final ModConfigSpec.IntValue potionDrinkTimeTicks;
    public final ModConfigSpec.IntValue potionDrinkCooldownTimeTicks;

    public PotionsPlusConfig(ModConfigSpec.Builder configBuilder) {
        enableSkills = configBuilder
                .translation("configuration.potionsplus.enable_skills")
                .define("enableSkills", true);

        potionDrinkTimeTicks = configBuilder
                .translation("configuration.potionsplus.potion_drink_time_ticks")
                .defineInRange("potionDrinkTimeTicks", 16, 0, Integer.MAX_VALUE);

        potionDrinkCooldownTimeTicks = configBuilder
                .translation("configuration.potionsplus.potion_use_cooldown_time_ticks")
                .defineInRange("potionUseCooldownTimeTicks", 0, 0, Integer.MAX_VALUE);
    }
}
