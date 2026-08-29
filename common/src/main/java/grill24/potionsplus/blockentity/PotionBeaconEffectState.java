package grill24.potionsplus.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * The {@link PotionBeaconBlockEntity}'s own view of an effect it is broadcasting to nearby players:
 * which effect, how many ticks of it are left to hand out, and the cosmetic flags a fresh
 * {@link MobEffectInstance} needs.
 *
 * <p>Replaces storing and mutating live {@link MobEffectInstance}s in place through
 * {@code MobEffectInstanceMixin} - that mixin existed only to let the beacon decrement a stored
 * instance's duration each period, which meant an object also being serialised by
 * {@code MobEffectInstance.CODEC} was mutated out from under itself. This record is immutable; "ticking
 * it down" means replacing the list entry with {@link #withRemainingTicks(int)}.
 *
 * <p>{@link #CODEC} deliberately uses the same field names as {@code MobEffectInstance.CODEC} (via its
 * private {@code Details} record) so NBT written by the old, mixin-based beacon before this change still
 * reads back correctly - the compound shape is identical, this class just doesn't mutate what it reads.
 */
public record PotionBeaconEffectState(
        Holder<MobEffect> effect,
        int amplifier,
        int remainingTicks,
        boolean ambient,
        boolean visible,
        boolean showIcon
) {
    public static final Codec<PotionBeaconEffectState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffect.CODEC.fieldOf("id").forGetter(PotionBeaconEffectState::effect),
            ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0).forGetter(PotionBeaconEffectState::amplifier),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(PotionBeaconEffectState::remainingTicks),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(PotionBeaconEffectState::ambient),
            Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(PotionBeaconEffectState::visible),
            Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(PotionBeaconEffectState::showIcon)
    ).apply(instance, PotionBeaconEffectState::new));

    public static PotionBeaconEffectState of(MobEffectInstance instance) {
        return new PotionBeaconEffectState(
                instance.getEffect(),
                instance.getAmplifier(),
                instance.getDuration(),
                instance.isAmbient(),
                instance.isVisible(),
                instance.showIcon());
    }

    public PotionBeaconEffectState withRemainingTicks(int remainingTicks) {
        return new PotionBeaconEffectState(effect, amplifier, remainingTicks, ambient, visible, showIcon);
    }

    /** A fresh {@link MobEffectInstance} carrying this state's cosmetic flags, with the given duration. */
    public MobEffectInstance toMobEffectInstance(int duration) {
        return new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
    }
}
