package grill24.potionsplus.alchemy;

import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe.PotionMatchingCriteria;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Records exactly how {@code PUtil} behaves today, so the behaviour change the alchemy package
 * introduces is written down rather than discovered later.
 *
 * <p><b>These tests assert current, incorrect behaviour on purpose.</b> They pass today. When the call
 * sites move onto {@link EffectComparison} in phase 3 and {@code PUtil} is retired, every test here
 * will fail - that is the signal to delete this file, not to repair it. The corrected semantics live
 * in {@link EffectComparisonTest}.
 *
 * <p>Nothing else in the codebase should depend on the behaviour asserted here.
 */
class PUtilDivergenceTest extends AlchemyTestBase {

    private static ItemStack speed(int duration, int amplifier) {
        return customPotion(PotionContainer.POTION, effect(MobEffects.SPEED, duration, amplifier));
    }

    private static boolean legacyMatches(ItemStack a, ItemStack b, PotionMatchingCriteria... criteria) {
        return PUtil.isSameItemOrPotion(a, b, List.of(criteria));
    }

    // ----- P-02: the ignore flags are inverted -----

    @Test
    @DisplayName("P-02: the default comparison ignores amplifier entirely")
    void legacyDefaultIgnoresAmplifier() {
        assertTrue(legacyMatches(speed(600, 1), speed(600, 3)),
                "PUtil only compares amplifier when told to IGNORE it");

        assertFalse(EffectComparison.matches(speed(600, 1), speed(600, 3)),
                "the replacement compares it by default");
    }

    @Test
    @DisplayName("P-02: the default comparison ignores duration entirely")
    void legacyDefaultIgnoresDuration() {
        assertTrue(legacyMatches(speed(600, 1), speed(20, 1)),
                "PUtil only compares duration when told to IGNORE it");

        assertFalse(EffectComparison.matches(speed(600, 1), speed(20, 1)),
                "the replacement compares it by default");
    }

    @Test
    @DisplayName("P-02: asking to ignore the amplifier is what makes it significant")
    void legacyIgnoreAmplifierMakesAmplifierMatter() {
        assertFalse(legacyMatches(speed(600, 1), speed(600, 3),
                PotionMatchingCriteria.IGNORE_POTION_EFFECT_AMPLIFIER));

        assertTrue(EffectComparison.matches(speed(600, 1), speed(600, 3),
                EffectComparison.MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER),
                "the replacement honours the flag as named");
    }

    @Test
    @DisplayName("P-02: asking to ignore the duration is what makes it significant")
    void legacyIgnoreDurationMakesDurationMatter() {
        assertFalse(legacyMatches(speed(600, 1), speed(20, 1),
                PotionMatchingCriteria.IGNORE_POTION_EFFECT_DURATION));

        assertTrue(EffectComparison.matches(speed(600, 1), speed(20, 1),
                EffectComparison.MatchCriteria.IGNORE_POTION_EFFECT_DURATION),
                "the replacement honours the flag as named");
    }

    // ----- P-03: comparison and identity are order-dependent -----

    @Test
    @DisplayName("P-03: the same potion assembled in a different order does not match itself")
    void legacyComparisonIsOrderDependent() {
        ItemStack forwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.SPEED, 600, 1),
                effect(MobEffects.WITHER, 100, 0));
        ItemStack backwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.WITHER, 100, 0),
                effect(MobEffects.SPEED, 600, 1));

        assertFalse(legacyMatches(forwards, backwards));

        assertTrue(EffectComparison.matches(forwards, backwards),
                "the replacement compares in canonical order");
    }

    @Test
    @DisplayName("P-03: potion identity strings differ by assembly order")
    void legacyIdentityIsOrderDependent() {
        ItemStack forwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.SPEED, 600, 1),
                effect(MobEffects.WITHER, 100, 0));
        ItemStack backwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.WITHER, 100, 0),
                effect(MobEffects.SPEED, 600, 1));

        assertNotEquals(
                PUtil.getNameOrVerbosePotionName(forwards),
                PUtil.getNameOrVerbosePotionName(backwards),
                "this is what PpIngredient hashes, so these two occupy different map keys");

        org.junit.jupiter.api.Assertions.assertEquals(
                EffectComparison.identityString(forwards),
                EffectComparison.identityString(backwards),
                "the replacement gives them one identity");
    }

    // ----- P-05: the only write helper mutates its argument -----

    @Test
    @DisplayName("P-05: setCustomEffects writes onto the stack it is handed")
    void legacySetCustomEffectsMutatesItsArgument() {
        ItemStack source = new ItemStack(Items.DIAMOND_SWORD);

        ItemStack returned = PUtil.setCustomEffects(source,
                List.of(effect(MobEffects.SPEED, 600, 1)));

        org.junit.jupiter.api.Assertions.assertSame(source, returned,
                "PUtil returns the same instance it was given");
        assertTrue(PotionData.read(source).has(MobEffects.SPEED),
                "so the caller's stack has been altered");

        ItemStack untouched = new ItemStack(Items.DIAMOND_SWORD);
        PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 1))
                .applyTo(untouched);
        assertFalse(PotionData.read(untouched).has(MobEffects.SPEED),
                "the replacement leaves it alone");
    }
}
