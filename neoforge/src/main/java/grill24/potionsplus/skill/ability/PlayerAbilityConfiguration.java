package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerAbilityConfiguration {
    public static final Codec<PlayerAbilityConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            PlayerAbilityConfigurationData.CODEC.optionalFieldOf("baseConfig", new PlayerAbilityConfigurationData()).forGetter(PlayerAbilityConfiguration::getData)
    ).apply(codecBuilder, PlayerAbilityConfiguration::new));

    private final PlayerAbilityConfigurationData data;

    public PlayerAbilityConfiguration(PlayerAbilityConfigurationData data) {
        this.data = data;
    }

    public PlayerAbilityConfigurationData getData() {
        return this.data;
    }

    public record PlayerAbilityConfigurationData(String translationKey, boolean enabledByDefault) {
        public static final Codec<PlayerAbilityConfigurationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translationKey").forGetter(PlayerAbilityConfigurationData::translationKey),
            Codec.BOOL.fieldOf("enabledByDefault").forGetter(PlayerAbilityConfigurationData::enabledByDefault)
        ).apply(instance, PlayerAbilityConfigurationData::new));

        public PlayerAbilityConfigurationData() {
            this("", true);
        }
    }
}
