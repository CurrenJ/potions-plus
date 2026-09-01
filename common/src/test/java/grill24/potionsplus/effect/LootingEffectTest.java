package grill24.potionsplus.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * No registry, no bootstrap - {@link LootingEffect#getEnchantmentBonus} is a pure function of an
 * amplifier, exactly the kind of behaviour {@code EffectScalingTest} argues does not need a game test.
 */
class LootingEffectTest {

    private static final LootingEffect EFFECT = new LootingEffect(MobEffectCategory.BENEFICIAL, 0x12A0A0);
    private static final Holder<net.minecraft.world.effect.MobEffect> HOLDER = Holder.direct(EFFECT);

    @Test
    void bonusIsAmplifierPlusOne() {
        assertEquals(1, EFFECT.getEnchantmentBonus(instance(0)));
        assertEquals(2, EFFECT.getEnchantmentBonus(instance(1)));
        assertEquals(4, EFFECT.getEnchantmentBonus(instance(3)));
    }

    private static MobEffectInstance instance(int amplifier) {
        return new MobEffectInstance(HOLDER, 200, amplifier);
    }
}
