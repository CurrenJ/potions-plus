package grill24.potionsplus.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * No registry, no bootstrap - {@link NauticalNitroEffect#getSpeedMultiplier} is a pure function of an
 * amplifier, exactly the kind of behaviour {@code EffectScalingTest} argues does not need a game test.
 * {@link Holder#direct} stands in for the mod's registered holder so this needs no mod registry.
 */
class NauticalNitroEffectTest {

    private static final NauticalNitroEffect EFFECT = new NauticalNitroEffect(MobEffectCategory.BENEFICIAL, 0x0077b6);
    private static final Holder<net.minecraft.world.effect.MobEffect> HOLDER = Holder.direct(EFFECT);

    @Test
    void baseAmplifierGivesA20PercentBoost() {
        assertEquals(1.2F, EFFECT.getSpeedMultiplier(instance(0)), 1e-6);
    }

    @Test
    void eachAmplifierLevelAddsAnother20Percent() {
        assertEquals(1.4F, EFFECT.getSpeedMultiplier(instance(1)), 1e-6);
        assertEquals(1.6F, EFFECT.getSpeedMultiplier(instance(2)), 1e-6);
    }

    private static MobEffectInstance instance(int amplifier) {
        return new MobEffectInstance(HOLDER, 200, amplifier);
    }
}
