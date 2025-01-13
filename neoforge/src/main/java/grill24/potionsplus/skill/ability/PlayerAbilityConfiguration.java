package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.ConfiguredSkill;
import net.minecraft.core.Holder;

public class PlayerAbilityConfiguration {
    public static final Codec<PlayerAbilityConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            PlayerAbilityConfigurationData.CODEC.fieldOf("baseConfig").forGetter(PlayerAbilityConfiguration::getData)
    ).apply(codecBuilder, PlayerAbilityConfiguration::new));

    private final PlayerAbilityConfigurationData data;

    public PlayerAbilityConfiguration(PlayerAbilityConfigurationData data) {
        this.data = data;
    }

    public PlayerAbilityConfigurationData getData() {
        return this.data;
    }

    public record PlayerAbilityConfigurationData(String translationKey, boolean enabledByDefault, Holder<ConfiguredSkill<?, ?>> parentSkill) {
        public static final Codec<PlayerAbilityConfigurationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translationKey").forGetter(PlayerAbilityConfigurationData::translationKey),
            Codec.BOOL.fieldOf("enabledByDefault").forGetter(PlayerAbilityConfigurationData::enabledByDefault),
            ConfiguredSkill.CODEC.fieldOf("parentSkill").forGetter(PlayerAbilityConfigurationData::parentSkill)
        ).apply(instance, PlayerAbilityConfigurationData::new));

    }
}
