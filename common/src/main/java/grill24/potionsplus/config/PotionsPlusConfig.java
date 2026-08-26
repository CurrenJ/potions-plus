package grill24.potionsplus.config;

public class PotionsPlusConfig {
    public static final PotionsPlusConfig CONFIG = new PotionsPlusConfig();

    public final IntValue potionDrinkTimeTicks = new IntValue(16);
    public final IntValue potionDrinkCooldownTimeTicks = new IntValue(0);

    public static class BoolValue {
        private final boolean defaultValue;
        public BoolValue(boolean defaultValue) { this.defaultValue = defaultValue; }
        public boolean get() { return defaultValue; }
    }

    public static class IntValue {
        private final int defaultValue;
        public IntValue(int defaultValue) { this.defaultValue = defaultValue; }
        public int get() { return defaultValue; }
    }
}
