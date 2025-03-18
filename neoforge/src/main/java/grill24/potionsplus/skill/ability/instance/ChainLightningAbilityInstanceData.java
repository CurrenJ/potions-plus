package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ChainLightningAbilityInstanceData extends AdjustableStrengthAbilityInstanceData {
    public static final Codec<ChainLightningAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled),
            Codec.FLOAT.optionalFieldOf("abilityStrength", 1F).forGetter(instance -> instance.abilityStrength),
            Codec.FLOAT.optionalFieldOf("maxAbilityStrength", 1F).forGetter(instance -> instance.maxAbilityStrength)
    ).apply(codecBuilder, ChainLightningAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChainLightningAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.abilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.maxAbilityStrength,
            ChainLightningAbilityInstanceData::new);


    public ChainLightningAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled, float abilityStrength, float maxAbilityStrength) {
        super(ability, isEnabled, abilityStrength, maxAbilityStrength);
    }

    public ChainLightningAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        super(ability, isEnabled);
    }
}
