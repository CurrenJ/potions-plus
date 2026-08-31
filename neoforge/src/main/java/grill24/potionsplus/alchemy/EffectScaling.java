package grill24.potionsplus.alchemy;

/**
 * Shared, agreed-semantics scaling math for mob-effect amplifiers.
 * <p>
 * Replaces the four independent {@code base >> amplifier} copies (see the
 * tick-interval methods on {@code BotanicalBoostEffect}, {@code CropCollectorEffect},
 * {@code MagneticEffect} and {@code MetalDetectingEffect}) and
 * {@code PUtil.diminishingReturnsLn}.
 */
public final class EffectScaling {
    private EffectScaling() {
    }

    /**
     * Scales an effect property (e.g. tick interval) by amplifier level, halving
     * per level. Clamped to a minimum of 1 so the value never collapses to 0,
     * which the four call sites previously interpreted inconsistently as either
     * "apply every tick" or "never apply".
     */
    public static int tickInterval(int base, int amplifier) {
        return Math.max(1, base >> amplifier);
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
