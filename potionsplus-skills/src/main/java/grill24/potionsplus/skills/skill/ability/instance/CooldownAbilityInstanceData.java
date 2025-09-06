package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class CooldownAbilityInstanceData extends AdjustableStrengthAbilityInstanceData {
    private long lastTriggeredTick;

    public static final Codec<CooldownAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled),
            Codec.FLOAT.optionalFieldOf("abilityStrength", 1F).forGetter(instance -> instance.abilityStrength),
            Codec.FLOAT.optionalFieldOf("minAbilityStrength", 0F).forGetter(instance -> instance.minAbilityStrength),
            Codec.FLOAT.optionalFieldOf("maxAbilityStrength", 1F).forGetter(instance -> instance.maxAbilityStrength),
            Codec.LONG.optionalFieldOf("lastTriggeredTick", 0L).forGetter(instance -> instance.lastTriggeredTick)
    ).apply(codecBuilder, CooldownAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CooldownAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.abilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.minAbilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.maxAbilityStrength,
            ByteBufCodecs.VAR_LONG,
            (instance) -> instance.lastTriggeredTick,
            CooldownAbilityInstanceData::new);

    public CooldownAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled, float abilityStrength, float minAbilityStrength, float maxAbilityStrength, long lastTriggeredTick) {
        super(ability, isEnabled);

        this.abilityStrength = abilityStrength;
        this.minAbilityStrength = minAbilityStrength;
        this.maxAbilityStrength = maxAbilityStrength;
        this.lastTriggeredTick = lastTriggeredTick;
    }

    public void onTrigger(long timestamp) {
        this.lastTriggeredTick = timestamp;
    }

    public long getLastTriggeredTick() {
        return this.lastTriggeredTick;
    }

    public void setLastTriggeredTick(long lastTriggeredTick) {
        this.lastTriggeredTick = lastTriggeredTick;
    }
}
