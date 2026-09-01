package grill24.potionsplus.alchemy;

/**
 * Shared, agreed-semantics scaling math for mob-effect amplifiers.
 * <p>
 * Replaces the four independent {@code base >> amplifier} copies (see the
 * tick-interval methods on {@code BotanicalBoostEffect}, {@code CropCollectorEffect},
 * {@code MagneticEffect} and {@code MetalDetectingEffect}) and
 * {@code PUtil.diminishingReturnsLn}.
 * <p>
 * The raw {@code base >> amplifier} idiom hits zero at amplifier 5 for a base-20 interval, and Java
 * masks a shift count to five bits, so an unclamped shift wraps back to the un-shifted base at
 * amplifier 32 - as if amplifier were 0. {@link #clampAmplifier(int)} keeps the amplifier below that
 * wraparound, and {@link #tickInterval(int, int)} additionally floors the result at 1, so the zero case
 * every call site used to interpret differently (fire every tick vs. never fire again) is unreachable.
 */
public final class EffectScaling {

    /**
     * The amplifier ceiling. Chosen because the {@code >>}-based tick intervals stop being meaningful
     * past this point - {@link #tickInterval(int, int)} would otherwise plateau at its floor for most of
     * the remaining range anyway.
     */
    public static final int MAX_AMPLIFIER = 4;

    /** A generous safety ceiling on effect duration, well above anything a real recipe chain produces. */
    public static final int MAX_DURATION_TICKS = 20 * 60 * 60 * 24;

    private EffectScaling() {
    }

    public static int clampAmplifier(int amplifier) {
        return Math.clamp(amplifier, 0, MAX_AMPLIFIER);
    }

    public static int clampDuration(int duration) {
        return Math.clamp(duration, 0, MAX_DURATION_TICKS);
    }

    /**
     * Scales an effect property (e.g. tick interval) by amplifier level, halving
     * per level. Clamped to a minimum of 1 so the value never collapses to 0,
     * which the four call sites previously interpreted inconsistently as either
     * "apply every tick" or "never apply".
     */
    public static int tickInterval(int base, int amplifier) {
        return Math.max(1, base >> clampAmplifier(amplifier));
    }

    /**
     * Logarithmic diminishing returns, ported verbatim from
     * {@code PUtil.diminishingReturnsLn}. Preserved because
     * {@code FlyingTimeEffect} depends on its exact output.
     */
    public static float diminishingReturnsLn(float amplifier) {
        return (float) Math.log(amplifier + 1) + 1;
    }
}
