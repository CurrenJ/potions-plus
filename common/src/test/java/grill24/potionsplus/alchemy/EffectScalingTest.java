package grill24.potionsplus.alchemy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * No registry, no bootstrap - these are pure functions of an int. See {@code AlchemyTestBase} for why
 * that matters: this is exactly the kind of behaviour a game test should not be needed for.
 */
class EffectScalingTest {

    // ----- the bug this class exists to fix -----

    /**
     * The raw {@code base >> amplifier} idiom hits zero at amplifier 5 for a base-20 interval. Every
     * effect that guarded {@code j > 0} disagreed on what zero meant; flooring at 1 removes the case
     * entirely, so there is exactly one behaviour left.
     */
    @Test
    void neverReturnsZeroOrLess() {
        for (int amplifier = 0; amplifier <= 40; amplifier++) {
            assertTrue(EffectScaling.tickInterval(20, amplifier) >= 1,
                    "tickInterval(20, " + amplifier + ") was not at least 1");
        }
    }

    /**
     * Java masks a shift count to five bits, so an unclamped {@code base >> amplifier} wraps back to the
     * un-shifted base at amplifier 32 - as if amplifier were 0. Amplifier 40 must not trigger that.
     */
    @Test
    void doesNotWrapAroundAtHighAmplifiers() {
        int atCeiling = EffectScaling.tickInterval(20, EffectScaling.MAX_AMPLIFIER);
        assertEquals(atCeiling, EffectScaling.tickInterval(20, 32));
        assertEquals(atCeiling, EffectScaling.tickInterval(20, 40));
    }

    @Test
    void halvesOncePerAmplifierLevelUpToTheCeiling() {
        assertEquals(20, EffectScaling.tickInterval(20, 0));
        assertEquals(10, EffectScaling.tickInterval(20, 1));
        assertEquals(5, EffectScaling.tickInterval(20, 2));
        assertEquals(2, EffectScaling.tickInterval(20, 3));
        assertEquals(1, EffectScaling.tickInterval(20, 4));
    }

    // ----- clamps -----

    @Test
    void clampAmplifierFloorsAtZeroAndCapsAtMax() {
        assertEquals(0, EffectScaling.clampAmplifier(-5));
        assertEquals(0, EffectScaling.clampAmplifier(0));
        assertEquals(EffectScaling.MAX_AMPLIFIER, EffectScaling.clampAmplifier(EffectScaling.MAX_AMPLIFIER));
        assertEquals(EffectScaling.MAX_AMPLIFIER, EffectScaling.clampAmplifier(255));
    }

    @Test
    void clampDurationFloorsAtZeroAndCapsAtMax() {
        assertEquals(0, EffectScaling.clampDuration(-1));
        assertEquals(1000, EffectScaling.clampDuration(1000));
        assertEquals(EffectScaling.MAX_DURATION_TICKS, EffectScaling.clampDuration(Integer.MAX_VALUE));
    }
}
