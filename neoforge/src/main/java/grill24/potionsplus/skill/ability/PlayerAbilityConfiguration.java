package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.ConfiguredSkill;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    public record PlayerAbilityConfigurationData(@NonNull String translationKey, String longTranslationKey, boolean enabledByDefault, Holder<ConfiguredSkill<?, ?>> parentSkill, ItemPredicate itemPredicate) {
        public static final Codec<PlayerAbilityConfigurationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("translationKey", "").forGetter(PlayerAbilityConfigurationData::translationKey),
            Codec.STRING.optionalFieldOf("longTranslationKey", "").forGetter(PlayerAbilityConfigurationData::longTranslationKey),
            Codec.BOOL.fieldOf("enabledByDefault").forGetter(PlayerAbilityConfigurationData::enabledByDefault),
            ConfiguredSkill.CODEC.fieldOf("parentSkill").forGetter(PlayerAbilityConfigurationData::parentSkill),
            ItemPredicate.CODEC.optionalFieldOf("itemPredicate", ItemPredicate.Builder.item().build()).forGetter(PlayerAbilityConfigurationData::itemPredicate)
        ).apply(instance, PlayerAbilityConfigurationData::new));
    }
}
