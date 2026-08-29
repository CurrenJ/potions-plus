package grill24.potionsplus.alchemy;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PotionDataBuilderTest extends AlchemyTestBase {

    // ----- the no-mutation invariant -----

    /**
     * The invariant the whole package is built around. Its predecessor wrote the component onto the
     * stack it was handed and returned that same stack, so merely evaluating what a block could brew
     * permanently altered the ingredients sitting in it.
     */
    @Test
    void applyToDoesNotMutateItsArgument() {
        ItemStack source = new ItemStack(Items.DIAMOND_SWORD);

        ItemStack result = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 1))
                .applyTo(source);

        assertNotSame(source, result);
        assertTrue(PotionData.read(source).isEmpty(), "the source stack must be untouched");
        assertTrue(PotionData.read(result).has(MobEffects.SPEED));
    }

    @Test
    void applyToPreservesUnrelatedComponentsAndCount() {
        ItemStack source = new ItemStack(Items.DIAMOND_SWORD, 1);
        source.setDamageValue(42);

        ItemStack result = PotionDataBuilder.from(source)
                .addEffect(effect(MobEffects.REGENERATION, 200, 0))
                .applyTo(source);

        assertEquals(42, result.getDamageValue());
        assertEquals(source.getCount(), result.getCount());
    }

    /** Seeding a builder from a stack must not let later edits reach back into it. */
    @Test
    void fromDoesNotAliasTheSourcesEffects() {
        ItemStack source = customPotion(PotionContainer.POTION, effect(MobEffects.SPEED, 600, 0));

        PotionDataBuilder.from(source)
                .addEffect(effect(MobEffects.WITHER, 100, 0))
                .addAmplifier(5)
                .build();

        List<MobEffectInstance> sourceEffects = PotionData.read(source).effects();
        assertEquals(1, sourceEffects.size());
        assertEquals(0, sourceEffects.get(0).getAmplifier());
    }

    @Test
    void withEffectsCopiesTheInstancesItIsGiven() {
        MobEffectInstance shared = effect(MobEffects.SPEED, 600, 0);

        PotionData first = PotionDataBuilder.fromEmpty().withEffects(List.of(shared)).build();
        PotionData second = PotionDataBuilder.fromEmpty().withEffects(List.of(shared)).addAmplifier(3).build();

        assertEquals(0, first.effects().get(0).getAmplifier());
        assertEquals(3, second.effects().get(0).getAmplifier());
    }

    // ----- effect composition -----

    @Test
    void addEffectAppendsWithoutMerging() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .addEffect(effect(MobEffects.SPEED, 300, 2))
                .build();

        assertEquals(2, data.effects().size());
    }

    @Test
    void mergeEffectKeepsTheGreaterDurationAndAmplifier() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .mergeEffect(effect(MobEffects.SPEED, 300, 2))
                .build();

        assertEquals(1, data.effects().size());
        assertEquals(600, data.effect(MobEffects.SPEED).orElseThrow().getDuration());
        assertEquals(2, data.effect(MobEffects.SPEED).orElseThrow().getAmplifier());
    }

    @Test
    void mergeEffectAddsUnrelatedEffects() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .mergeEffect(effect(MobEffects.WITHER, 100, 1))
                .build();

        assertEquals(2, data.effects().size());
    }

    @Test
    void collapseDuplicateEffectsKeepsOneOfEachType() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .addEffect(effect(MobEffects.SPEED, 300, 2))
                .addEffect(effect(MobEffects.WITHER, 100, 0))
                .collapseDuplicateEffects()
                .build();

        assertEquals(2, data.effects().size());
        assertEquals(600, data.effect(MobEffects.SPEED).orElseThrow().getDuration());
        assertEquals(2, data.effect(MobEffects.SPEED).orElseThrow().getAmplifier());
    }

    @Test
    void removeEffectDropsEveryInstanceOfThatType() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .addEffect(effect(MobEffects.SPEED, 300, 2))
                .addEffect(effect(MobEffects.WITHER, 100, 0))
                .removeEffect(MobEffects.SPEED)
                .build();

        assertEquals(1, data.effects().size());
        assertFalse(data.has(MobEffects.SPEED));
    }

    // ----- upgrades -----

    @Test
    void addDurationAndAmplifierApplyToEveryCustomEffect() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .addEffect(effect(MobEffects.WITHER, 100, 1))
                .addDuration(200)
                .addAmplifier(1)
                .build();

        assertEquals(800, data.effect(MobEffects.SPEED).orElseThrow().getDuration());
        assertEquals(1, data.effect(MobEffects.SPEED).orElseThrow().getAmplifier());
        assertEquals(300, data.effect(MobEffects.WITHER).orElseThrow().getDuration());
        assertEquals(2, data.effect(MobEffects.WITHER).orElseThrow().getAmplifier());
    }

    /**
     * A potion linked to a registered Potion cannot have its durations edited - the effects belong to
     * the potion, not the stack. Upgrades therefore have to detach first.
     */
    @Test
    void addDurationDoesNotReachBasePotionEffects() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .withBasePotion(Potions.REGENERATION)
                .addDuration(1000)
                .build();

        int vanillaDuration = Potions.REGENERATION.value().getEffects().get(0).getDuration();
        assertEquals(vanillaDuration, data.effect(MobEffects.REGENERATION).orElseThrow().getDuration());
    }

    @Test
    void detachBasePotionEffectsMakesThemEditable() {
        int vanillaDuration = Potions.REGENERATION.value().getEffects().get(0).getDuration();

        PotionData data = PotionDataBuilder.fromEmpty()
                .withBasePotion(Potions.REGENERATION)
                .detachBasePotionEffects()
                .addDuration(1000)
                .build();

        assertFalse(data.hasBasePotion());
        assertEquals(vanillaDuration + 1000,
                data.effect(MobEffects.REGENERATION).orElseThrow().getDuration());
    }

    @Test
    void detachMergesBasePotionEffectsWithExistingCustomOnes() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .withBasePotion(Potions.REGENERATION)
                .addEffect(effect(MobEffects.REGENERATION, 99999, 4))
                .detachBasePotionEffects()
                .build();

        assertEquals(1, data.effects().size(), "the duplicate regeneration instances must collapse");
        assertEquals(99999, data.effect(MobEffects.REGENERATION).orElseThrow().getDuration());
        assertEquals(4, data.effect(MobEffects.REGENERATION).orElseThrow().getAmplifier());
    }

    @Test
    void detachOnAPotionWithNoBaseIsANoOp() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .detachBasePotionEffects()
                .build();

        assertEquals(1, data.effects().size());
    }

    // ----- amplifier and duration ceilings -----

    /** Every write funnels through {@code build()}, so the clamp applies regardless of which builder method wrote the effect. */
    @Test
    void buildClampsAmplifierToTheCeiling() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, 600, EffectScaling.MAX_AMPLIFIER + 50))
                .build();

        assertEquals(EffectScaling.MAX_AMPLIFIER, data.effect(MobEffects.SPEED).orElseThrow().getAmplifier());
    }

    @Test
    void buildClampsDurationToTheCeiling() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .addEffect(effect(MobEffects.SPEED, EffectScaling.MAX_DURATION_TICKS + 5000, 0))
                .build();

        assertEquals(EffectScaling.MAX_DURATION_TICKS, data.effect(MobEffects.SPEED).orElseThrow().getDuration());
    }

    /** Repeated {@code addAmplifier} calls compose past the ceiling but the final build still clamps. */
    @Test
    void addAmplifierStopsAtTheCeilingAfterRepeatedUpgrades() {
        PotionDataBuilder builder = PotionDataBuilder.fromEmpty().addEffect(effect(MobEffects.SPEED, 600, 0));
        for (int i = 0; i < EffectScaling.MAX_AMPLIFIER + 10; i++) {
            builder.addAmplifier(1);
        }

        assertEquals(EffectScaling.MAX_AMPLIFIER, builder.build().effect(MobEffects.SPEED).orElseThrow().getAmplifier());
    }

    /** Effects still linked to a registered {@link net.minecraft.world.item.alchemy.Potion} are not the builder's to clamp. */
    @Test
    void buildDoesNotClampEffectsStillLinkedToABasePotion() {
        PotionData data = PotionDataBuilder.fromEmpty().withBasePotion(Potions.REGENERATION).build();

        int vanillaAmplifier = Potions.REGENERATION.value().getEffects().get(0).getAmplifier();
        assertEquals(vanillaAmplifier, data.effect(MobEffects.REGENERATION).orElseThrow().getAmplifier());
    }

    // ----- colour and name -----

    @Test
    void colourAndNameSurviveTheRoundTrip() {
        ItemStack stack = PotionDataBuilder.fromEmpty()
                .withCustomColor(0x123456)
                .withCustomName("merged_potions_2_effects")
                .applyTo(new ItemStack(Items.POTION));

        PotionData data = PotionData.read(stack);
        assertEquals(0x123456, data.customColor().orElseThrow());
        assertEquals("merged_potions_2_effects", data.customName().orElseThrow());
    }

    @Test
    void withoutBasePotionKeepsCustomEffects() {
        PotionData data = PotionDataBuilder.fromEmpty()
                .withBasePotion(Potions.HEALING)
                .addEffect(effect(MobEffects.SPEED, 600, 0))
                .withoutBasePotion()
                .build();

        assertFalse(data.hasBasePotion());
        assertTrue(data.has(MobEffects.SPEED));
        assertFalse(data.has(MobEffects.INSTANT_HEALTH));
    }
}
