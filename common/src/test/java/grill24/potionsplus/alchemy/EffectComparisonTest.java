package grill24.potionsplus.alchemy;

import grill24.potionsplus.alchemy.EffectComparison.MatchCriteria;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The comparison matrix. Every criteria value against same and different effect type, amplifier,
 * duration, container and effect order.
 *
 * <p>This is the phase 1 gate: the semantics asserted here are the intended ones, and they differed from
 * the retired {@code PUtil} implementation in two documented ways (inverted ignore flags, order
 * dependence) - see {@code d63a69a} and the phase 3 migration for the call sites that used to depend on
 * the old behaviour.
 */
class EffectComparisonTest extends AlchemyTestBase {

    private static ItemStack speed(int duration, int amplifier) {
        return customPotion(PotionContainer.POTION, effect(MobEffects.SPEED, duration, amplifier));
    }

    private static ItemStack speedSplash(int duration, int amplifier) {
        return customPotion(PotionContainer.SPLASH_POTION, effect(MobEffects.SPEED, duration, amplifier));
    }

    // ----- default: everything must agree -----

    @Nested
    class Default {

        @Test
        void identicalPotionsMatch() {
            assertTrue(EffectComparison.matches(speed(600, 1), speed(600, 1)));
        }

        @Test
        void differentEffectTypeDoesNotMatch() {
            ItemStack wither = customPotion(PotionContainer.POTION, effect(MobEffects.WITHER, 600, 1));
            assertFalse(EffectComparison.matches(speed(600, 1), wither));
        }

        /**
         * The corrected behaviour. The implementation this replaces compared neither amplifier nor
         * duration unless explicitly told to ignore them.
         */
        @Test
        void differentAmplifierDoesNotMatch() {
            assertFalse(EffectComparison.matches(speed(600, 1), speed(600, 2)));
        }

        @Test
        void differentDurationDoesNotMatch() {
            assertFalse(EffectComparison.matches(speed(600, 1), speed(300, 1)));
        }

        @Test
        void differentContainerDoesNotMatch() {
            assertFalse(EffectComparison.matches(speed(600, 1), speedSplash(600, 1)));
        }

        @Test
        void differentEffectCountDoesNotMatch() {
            ItemStack two = customPotion(PotionContainer.POTION,
                    effect(MobEffects.SPEED, 600, 1),
                    effect(MobEffects.WITHER, 100, 0));
            assertFalse(EffectComparison.matches(speed(600, 1), two));
        }

        @Test
        void differentBasePotionDoesNotMatch() {
            assertFalse(EffectComparison.matches(
                    PotionContainer.POTION.create(Potions.HEALING),
                    PotionContainer.POTION.create(Potions.REGENERATION)));
        }

        @Test
        void sameBasePotionMatches() {
            assertTrue(EffectComparison.matches(
                    PotionContainer.POTION.create(Potions.HEALING),
                    PotionContainer.POTION.create(Potions.HEALING)));
        }

        /**
         * A linked potion and a loose potion carrying the same effects are not the same thing - one
         * has durations pinned by the registry, the other does not.
         */
        @Test
        void basePotionDoesNotMatchEquivalentCustomEffects() {
            ItemStack linked = PotionContainer.POTION.create(Potions.REGENERATION);
            ItemStack loose = PotionDataBuilder.from(linked)
                    .detachBasePotionEffects()
                    .applyTo(new ItemStack(Items.POTION));

            assertFalse(EffectComparison.matches(linked, loose));
        }
    }

    // ----- order independence -----

    /**
     * The other corrected behaviour: effects were compared by list index, so the same potion assembled
     * in a different order read as unequal. Order is not something anyone sets deliberately.
     */
    @Test
    void effectOrderDoesNotAffectMatching() {
        ItemStack forwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.SPEED, 600, 1),
                effect(MobEffects.WITHER, 100, 0),
                effect(MobEffects.REGENERATION, 300, 2));
        ItemStack backwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.REGENERATION, 300, 2),
                effect(MobEffects.WITHER, 100, 0),
                effect(MobEffects.SPEED, 600, 1));

        assertTrue(EffectComparison.matches(forwards, backwards));
    }

    // ----- individual criteria -----

    @Test
    void ignoreAmplifierIgnoresTheAmplifierOnly() {
        assertTrue(EffectComparison.matches(speed(600, 1), speed(600, 3),
                MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER));
        assertFalse(EffectComparison.matches(speed(600, 1), speed(300, 1),
                MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER),
                "duration must still be compared");
    }

    @Test
    void ignoreDurationIgnoresTheDurationOnly() {
        assertTrue(EffectComparison.matches(speed(600, 1), speed(300, 1),
                MatchCriteria.IGNORE_POTION_EFFECT_DURATION));
        assertFalse(EffectComparison.matches(speed(600, 1), speed(600, 3),
                MatchCriteria.IGNORE_POTION_EFFECT_DURATION),
                "amplifier must still be compared");
    }

    @Test
    void ignoringBothLeavesOnlyTheEffectTypes() {
        assertTrue(EffectComparison.matches(speed(600, 1), speed(20, 4),
                MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER,
                MatchCriteria.IGNORE_POTION_EFFECT_DURATION));

        ItemStack wither = customPotion(PotionContainer.POTION, effect(MobEffects.WITHER, 20, 4));
        assertFalse(EffectComparison.matches(speed(600, 1), wither,
                MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER,
                MatchCriteria.IGNORE_POTION_EFFECT_DURATION));
    }

    @Test
    void ignoreContainerCrossesContainerTypes() {
        assertTrue(EffectComparison.matches(speed(600, 1), speedSplash(600, 1),
                MatchCriteria.IGNORE_POTION_CONTAINER));
        assertFalse(EffectComparison.matches(speed(600, 1), speedSplash(600, 2),
                MatchCriteria.IGNORE_POTION_CONTAINER),
                "the contents must still agree");
    }

    @Test
    void ignoreEffectsComparesTheContainerOnly() {
        ItemStack wither = customPotion(PotionContainer.POTION, effect(MobEffects.WITHER, 20, 4));
        assertTrue(EffectComparison.matches(speed(600, 1), wither,
                MatchCriteria.IGNORE_POTION_EFFECTS));
        assertTrue(EffectComparison.matches(speed(600, 1), new ItemStack(Items.POTION),
                MatchCriteria.IGNORE_POTION_EFFECTS));
        assertFalse(EffectComparison.matches(speed(600, 1), speedSplash(600, 1),
                MatchCriteria.IGNORE_POTION_EFFECTS),
                "the container must still agree unless it is also ignored");
    }

    @Test
    void minimumOneEffectRequiresBothSidesToCarrySomething() {
        ItemStack wither = customPotion(PotionContainer.POTION, effect(MobEffects.WITHER, 20, 4));
        ItemStack empty = new ItemStack(Items.POTION);

        assertTrue(EffectComparison.matches(speed(600, 1), wither,
                MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT));
        assertFalse(EffectComparison.matches(speed(600, 1), empty,
                MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT));
        assertFalse(EffectComparison.matches(empty, empty,
                MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT));
    }

    /** A base potion counts as carrying effects, even with no custom effects on the stack. */
    @Test
    void minimumOneEffectCountsBasePotionEffects() {
        assertTrue(EffectComparison.matches(
                PotionContainer.POTION.create(Potions.REGENERATION),
                speed(600, 1),
                MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT));
    }

    @Test
    void exactMatchRequiresEveryComponent() {
        assertTrue(EffectComparison.matches(speed(600, 1), speed(600, 1), MatchCriteria.EXACT_MATCH));
        assertFalse(EffectComparison.matches(speed(600, 1), speed(600, 2), MatchCriteria.EXACT_MATCH));

        ItemStack named = PotionDataBuilder.from(speed(600, 1))
                .withCustomName("something").applyTo(speed(600, 1));
        assertFalse(EffectComparison.matches(speed(600, 1), named, MatchCriteria.EXACT_MATCH));
    }

    /** Exact match is index-wise by nature - it is vanilla component equality. */
    @Test
    void exactMatchIsSensitiveToEffectOrder() {
        ItemStack forwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.SPEED, 600, 1), effect(MobEffects.WITHER, 100, 0));
        ItemStack backwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.WITHER, 100, 0), effect(MobEffects.SPEED, 600, 1));

        assertFalse(EffectComparison.matches(forwards, backwards, MatchCriteria.EXACT_MATCH));
        assertTrue(EffectComparison.matches(forwards, backwards), "but the default comparison is not");
    }

    @ParameterizedTest
    @EnumSource(MatchCriteria.class)
    void neverMatchBeatsEveryOtherCriterion(MatchCriteria other) {
        assertFalse(EffectComparison.matches(speed(600, 1), speed(600, 1),
                List.of(MatchCriteria.NEVER_MATCH, other)));
    }

    // ----- non-potions -----

    @Test
    void nonPotionStacksFallBackToComponentEquality() {
        assertTrue(EffectComparison.matches(
                new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.DIAMOND_SWORD)));
        assertFalse(EffectComparison.matches(
                new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.IRON_SWORD)));
    }

    /**
     * An imbued tool carries potion contents but is not a potion container, so it compares as an item,
     * not as a potion - the criteria do not apply to it.
     */
    @Test
    void imbuedToolIsNotComparedAsAPotion() {
        ItemStack imbued = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 1))
                .applyTo(new ItemStack(Items.DIAMOND_SWORD));
        ItemStack otherImbued = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 300, 1))
                .applyTo(new ItemStack(Items.DIAMOND_SWORD));

        assertTrue(EffectComparison.matches(imbued, imbued.copy()));
        assertFalse(EffectComparison.matches(imbued, otherImbued,
                MatchCriteria.IGNORE_POTION_EFFECT_DURATION),
                "potion criteria do not loosen a non-container comparison");
    }

    @Test
    void emptyStacksDoNotThrow() {
        assertTrue(EffectComparison.matches(ItemStack.EMPTY, ItemStack.EMPTY));
        assertFalse(EffectComparison.matches(ItemStack.EMPTY, speed(600, 1)));
    }

    // ----- identity -----

    @Test
    void identityIsIndependentOfEffectOrder() {
        ItemStack forwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.SPEED, 600, 1), effect(MobEffects.WITHER, 100, 0));
        ItemStack backwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.WITHER, 100, 0), effect(MobEffects.SPEED, 600, 1));

        assertEquals(EffectComparison.identityString(forwards), EffectComparison.identityString(backwards));
        assertEquals(EffectComparison.identityHash(forwards), EffectComparison.identityHash(backwards));
    }

    @Test
    void identityDistinguishesAmplifierDurationAndContainer() {
        assertNotEquals(EffectComparison.identityString(speed(600, 1)),
                EffectComparison.identityString(speed(600, 2)));
        assertNotEquals(EffectComparison.identityString(speed(600, 1)),
                EffectComparison.identityString(speed(300, 1)));
        assertNotEquals(EffectComparison.identityString(speed(600, 1)),
                EffectComparison.identityString(speedSplash(600, 1)));
    }

    @Test
    void identityDistinguishesLinkedFromLoosePotions() {
        ItemStack linked = PotionContainer.POTION.create(Potions.REGENERATION);
        ItemStack loose = PotionDataBuilder.from(linked)
                .detachBasePotionEffects()
                .applyTo(new ItemStack(Items.POTION));

        assertNotEquals(EffectComparison.identityString(linked), EffectComparison.identityString(loose));
    }

    @Test
    void identityIsStableAcrossCopies() {
        ItemStack stack = speed(600, 1);
        assertEquals(EffectComparison.identityString(stack), EffectComparison.identityString(stack.copy()));
    }

    // ----- serialization compatibility -----

    /**
     * Ids and names must stay identical to the enum this replaces, or recipes already persisted in
     * saved data stop deserializing when the call sites move over in phase 3.
     */
    @Test
    void criteriaIdsAndNamesAreUnchanged() {
        assertEquals(0, MatchCriteria.EXACT_MATCH.id());
        assertEquals(1, MatchCriteria.IGNORE_POTION_EFFECT_DURATION.id());
        assertEquals(2, MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER.id());
        assertEquals(3, MatchCriteria.IGNORE_POTION_EFFECTS.id());
        assertEquals(4, MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT.id());
        assertEquals(5, MatchCriteria.IGNORE_POTION_CONTAINER.id());
        assertEquals(6, MatchCriteria.NEVER_MATCH.id());

        assertEquals("exact_match", MatchCriteria.EXACT_MATCH.getSerializedName());
        assertEquals("ignore_effect_duration", MatchCriteria.IGNORE_POTION_EFFECT_DURATION.getSerializedName());
        assertEquals("ignore_effect_amplifier", MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER.getSerializedName());
        assertEquals("ignore_potion_effects", MatchCriteria.IGNORE_POTION_EFFECTS.getSerializedName());
        assertEquals("ignore_potion_effects_min_1_effect",
                MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT.getSerializedName());
        assertEquals("ignore_potion_container", MatchCriteria.IGNORE_POTION_CONTAINER.getSerializedName());
        assertEquals("never_match", MatchCriteria.NEVER_MATCH.getSerializedName());
    }
}
