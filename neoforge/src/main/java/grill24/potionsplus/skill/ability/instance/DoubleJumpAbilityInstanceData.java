package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DoubleJumpAbilityInstanceData extends AdjustableStrengthAbilityInstanceData {
    private int jumpsLeft;
    private long lastJumpTick;

    public static final Codec<DoubleJumpAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled),
            Codec.FLOAT.optionalFieldOf("abilityStrength", 1F).forGetter(instance -> instance.abilityStrength),
            Codec.FLOAT.optionalFieldOf("maxAbilityStrength", 1F).forGetter(instance -> instance.maxAbilityStrength),
            Codec.INT.optionalFieldOf("jumpsLeft", 0).forGetter(instance -> instance.jumpsLeft),
            Codec.LONG.optionalFieldOf("lastJumpTick", 0L).forGetter(instance -> instance.lastJumpTick)
    ).apply(codecBuilder, DoubleJumpAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.abilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.maxAbilityStrength,
            ByteBufCodecs.INT,
            (instance) -> instance.jumpsLeft,
            ByteBufCodecs.VAR_LONG,
            (instance) -> instance.lastJumpTick,
            DoubleJumpAbilityInstanceData::new);

    // For deserialization
    private DoubleJumpAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled, float abilityStrength, float maxAbilityStrength, int jumpsLeft, long lastJumpTick) {
        super(ability, isEnabled);

        this.abilityStrength = abilityStrength;
        this.maxAbilityStrength = maxAbilityStrength;
    }

    public DoubleJumpAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        this(ability, isEnabled, 0F, 0F, 0, 0);
    }

    public int getJumpsLeft() {
        return this.jumpsLeft;
    }

    public void onInitialJump(long timestamp) {
        this.jumpsLeft = (int) this.abilityStrength;
        this.lastJumpTick = timestamp;
    }

    public void resetJumps() {
        this.jumpsLeft = (int) this.abilityStrength;
    }

    public void decrementJumps(long timestamp) {
        this.jumpsLeft--;
        this.lastJumpTick = timestamp;
    }

    public boolean hasFinishedCooldown(long timestamp) {
        return this.lastJumpTick < timestamp - 5;
    }
}
