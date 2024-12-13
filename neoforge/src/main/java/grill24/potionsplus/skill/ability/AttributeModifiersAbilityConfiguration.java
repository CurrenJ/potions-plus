package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

public class AttributeModifiersAbilityConfiguration extends PlayerAbilityConfiguration {
    public static final Codec<AttributeModifiersAbilityConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
        PlayerAbilityConfigurationData.CODEC.optionalFieldOf("baseConfig", new PlayerAbilityConfigurationData()).forGetter(AttributeModifiersAbilityConfiguration::getData),
        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(instance -> instance.attributeHolder),
        AttributeModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(instance -> instance.modifiers)
    ).apply(codecBuilder, AttributeModifiersAbilityConfiguration::new));

    private final Holder<Attribute> attributeHolder;
    private final List<AttributeModifier> modifiers;

    public AttributeModifiersAbilityConfiguration(PlayerAbilityConfigurationData baseConfig, Holder<Attribute> attributeHolder, List<AttributeModifier> modifiers) {
        super(baseConfig);
        this.attributeHolder = attributeHolder;
        this.modifiers = modifiers;
    }

    public Holder<Attribute> getAttributeHolder() {
        return this.attributeHolder;
    }

    public List<AttributeModifier> getModifiers() {
        return this.modifiers;
    }
}
