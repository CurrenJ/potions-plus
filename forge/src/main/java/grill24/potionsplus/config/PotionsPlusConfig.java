package grill24.potionsplus.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class PotionsPlusConfig {
    public static final PotionsPlusConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        Pair<PotionsPlusConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(PotionsPlusConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.IntValue potionDrinkTimeTicks;
    public final ForgeConfigSpec.IntValue potionDrinkCooldownTimeTicks;

    public PotionsPlusConfig(ForgeConfigSpec.Builder configBuilder) {
        potionDrinkTimeTicks = configBuilder
                .translation("configuration.potionsplus.potion_drink_time_ticks")
                .defineInRange("potionDrinkTimeTicks", 16, 0, Integer.MAX_VALUE);

        potionDrinkCooldownTimeTicks = configBuilder
                .translation("configuration.potionsplus.potion_use_cooldown_time_ticks")
                .defineInRange("potionUseCooldownTimeTicks", 0, 0, Integer.MAX_VALUE);
    }
}
