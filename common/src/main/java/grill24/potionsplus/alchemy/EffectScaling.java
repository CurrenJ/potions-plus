package grill24.potionsplus.alchemy;

/**
 * The named curves the effect classes in {@code grill24.potionsplus.effect} used to each reinvent as a
 * copy-pasted {@code base >> amplifier} tick interval, plus the amplifier ceiling every write in
 * {@link PotionDataBuilder} clamps to.
 *
 * <p>{@link #tickInterval(int, int)} is the fix for the bug that motivated this class: the raw idiom
 * hits zero at amplifier 5 (Java then masks the shift count to five bits, so amplifier 32 wraps back to
 * the un-shifted base), and every effect that guarded {@code j > 0} picked its own meaning for the zero
 * case - some fired every tick, some never fired again. Clamping the amplifier before shifting, and
 * flooring the result at 1, makes the zero case unreachable, so there is exactly one behaviour left to
 * agree on.
 */
public final class EffectScaling {

    /**
     * The amplifier ceiling. Chosen because the {@code >>}-based tick intervals stop being meaningful
     * past this point - {@link #tickInterval(int, int)} would otherwise plateau at its floor for most of
     * the remaining range anyway. Effects that want a wider range past amplifier 4 need a curve other
     * than repeated halving, not a higher ceiling on this one.
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
     * Ticks between applications of an effect that halves its interval once per amplifier level, e.g.
     * {@code base = 20} ticks once a second at amplifier 0, twice a second at amplifier 1, and so on.
     * Floors at 1 - never 0, never a wraparound - so {@code duration % tickInterval(base, amplifier) == 0}
     * is always well-defined and has exactly one meaning regardless of amplifier.
     */
    public static int tickInterval(int base, int amplifier) {
        return Math.max(1, base >> clampAmplifier(amplifier));
    }

    /** {@code perLevel} added once per amplifier level above 0. */
    public static float linear(int amplifier, float perLevel) {
        return clampAmplifier(amplifier) * perLevel;
    }

    /** {@code base} halved once per amplifier level. */
    public static float halving(int amplifier, float base) {
        return base / (1 << clampAmplifier(amplifier));
    }

    /** Diminishing returns: grows without bound but ever more slowly. See {@code Utility.diminishingReturnsLn}. */
    public static float logarithmic(int amplifier) {
        int clamped = clampAmplifier(amplifier);
        return (float) Math.log(clamped + 1) + 1;
    }

    /** Approaches {@code ceiling} but never reaches it. */
    public static float asymptotic(int amplifier, float ceiling) {
        int clamped = clampAmplifier(amplifier);
        return ceiling * (1 - 1f / (clamped + 1));
    }
}
