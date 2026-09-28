package grill24.potionsplus.effect;

import net.minecraft.world.effect.MobEffectCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * No registry, no bootstrap - {@link MetalDetectingEffect#getRadius}/{@code getTickInterval} are pure
 * functions of an amplifier. The effect's actual world behaviour (playing a ping sound per ore type
 * found) has no server-observable state a game test can assert on, so this covers the amplifier
 * scaling that behaviour depends on instead.
 */
class MetalDetectingEffectTest {

    private static final MetalDetectingEffect EFFECT = new MetalDetectingEffect(MobEffectCategory.BENEFICIAL, 0x7A7A7A);

    @Test
    void radiusGrowsByFourPerAmplifierLevel() {
        assertEquals(4, EFFECT.getRadius(0));
        assertEquals(8, EFFECT.getRadius(1));
        assertEquals(12, EFFECT.getRadius(2));
    }

    @Test
    void tickIntervalHalvesPerAmplifierLevel() {
        assertEquals(400, EFFECT.getTickInterval(0));
        assertEquals(200, EFFECT.getTickInterval(1));
        assertEquals(100, EFFECT.getTickInterval(2));
    }
}
