package grill24.potionsplus.alchemy;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PotionDataTest extends AlchemyTestBase {

    // ----- reading is total -----

    /**
     * The central contract this type exists for. Its predecessor threw IllegalArgumentException for a
     * missing component from three separate accessors, which is why call sites pre-checked the raw
     * component themselves and the bypass kept leaking back in.
     */
    @Test
    void readNeverThrows() {
        assertDoesNotThrow(() -> PotionData.read(ItemStack.EMPTY));
        assertDoesNotThrow(() -> PotionData.read(new ItemStack(Items.DIAMOND_SWORD)));
        assertDoesNotThrow(() -> PotionData.read(new ItemStack(Items.POTION)));
        assertDoesNotThrow(() -> PotionData.read(new ItemStack(Items.TIPPED_ARROW)));
        assertDoesNotThrow(() -> PotionData.read(PotionContainer.SPLASH_POTION.create(Potions.HEALING)));
    }

    @Test
    void nonPotionStackReadsAsEmpty() {
        assertSame(PotionData.EMPTY, PotionData.read(new ItemStack(Items.DIAMOND_SWORD)));
        assertSame(PotionData.EMPTY, PotionData.read(ItemStack.EMPTY));
    }

    /**
     * A bare potion item carries POTION_CONTENTS.EMPTY as a default component, so it reads as data that
     * is empty rather than as no data. Both answer true to isEmpty().
     */
    @Test
    void bareContainerReadsAsEmptyData() {
        PotionData data = PotionData.read(new ItemStack(Items.POTION));

        assertTrue(data.isEmpty());
        assertFalse(data.hasBasePotion());
        assertFalse(data.hasEffects());
    }

    // ----- base potion vs custom effects -----

    @Test
    void basePotionEffectsAreVisibleThroughEffects() {
        ItemStack stack = PotionContainer.POTION.create(Potions.REGENERATION);
        PotionData data = PotionData.read(stack);

        assertTrue(data.hasBasePotion());
        assertTrue(data.hasEffects());
        assertTrue(data.customEffects().isEmpty(), "regeneration's effects belong to the potion, not the stack");
        assertTrue(data.has(MobEffects.REGENERATION));
    }

    @Test
    void customEffectsAreVisibleWithoutABasePotion() {
        ItemStack stack = customPotion(PotionContainer.POTION,
                effect(MobEffects.MOVEMENT_SPEED, 600, 1));
        PotionData data = PotionData.read(stack);

        assertFalse(data.hasBasePotion());
        assertEquals(1, data.effects().size());
        assertEquals(1, data.effect(MobEffects.MOVEMENT_SPEED).orElseThrow().getAmplifier());
        assertEquals(600, data.effect(MobEffects.MOVEMENT_SPEED).orElseThrow().getDuration());
    }

    /** Base potion effects come first, then custom effects - vanilla's natural order. */
    @Test
    void effectsCombinesBasePotionAndCustomEffectsInNaturalOrder() {
        ItemStack stack = PotionDataBuilder.fromEmpty()
                .withBasePotion(Potions.REGENERATION)
                .addEffect(effect(MobEffects.MOVEMENT_SPEED, 600, 0))
                .applyTo(new ItemStack(Items.POTION));

        List<MobEffectInstance> effects = PotionData.read(stack).effects();

        assertEquals(2, effects.size());
        assertEquals(MobEffects.REGENERATION, effects.get(0).getEffect());
        assertEquals(MobEffects.MOVEMENT_SPEED, effects.get(1).getEffect());
    }

    @Test
    void missingEffectLookupReturnsEmptyRatherThanNull() {
        PotionData data = PotionData.read(customPotion(PotionContainer.POTION,
                effect(MobEffects.MOVEMENT_SPEED, 600, 0)));

        assertTrue(data.effect(MobEffects.WITHER).isEmpty());
        assertFalse(data.has(MobEffects.WITHER));
    }

    // ----- canonical order -----

    /**
     * Canonical order is independent of assembly order. This is the property that makes potion identity
     * survive map iteration and insertion order - the two things the old index-wise comparison could
     * not.
     */
    @Test
    void canonicalEffectsIgnoreAssemblyOrder() {
        ItemStack forwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.MOVEMENT_SPEED, 600, 0),
                effect(MobEffects.REGENERATION, 300, 1),
                effect(MobEffects.WITHER, 100, 2));
        ItemStack backwards = customPotion(PotionContainer.POTION,
                effect(MobEffects.WITHER, 100, 2),
                effect(MobEffects.REGENERATION, 300, 1),
                effect(MobEffects.MOVEMENT_SPEED, 600, 0));

        List<MobEffectInstance> a = PotionData.read(forwards).canonicalEffects();
        List<MobEffectInstance> b = PotionData.read(backwards).canonicalEffects();

        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            assertEquals(a.get(i).getEffect(), b.get(i).getEffect());
            assertEquals(a.get(i).getAmplifier(), b.get(i).getAmplifier());
            assertEquals(a.get(i).getDuration(), b.get(i).getDuration());
        }
    }

    @Test
    void naturalOrderStillReflectsAssemblyOrder() {
        ItemStack stack = customPotion(PotionContainer.POTION,
                effect(MobEffects.WITHER, 100, 0),
                effect(MobEffects.MOVEMENT_SPEED, 600, 0));

        assertEquals(MobEffects.WITHER, PotionData.read(stack).effects().get(0).getEffect());
    }

    // ----- conversion -----

    @Test
    void toContentsRoundTrips() {
        PotionContents original = new PotionContents(
                java.util.Optional.of(Potions.REGENERATION),
                java.util.Optional.of(0xFF00FF),
                List.of(effect(MobEffects.MOVEMENT_SPEED, 600, 1)));

        assertEquals(original, PotionData.of(original).toContents());
    }

    @Test
    void customEffectListIsImmutable() {
        PotionData data = PotionData.read(customPotion(PotionContainer.POTION,
                effect(MobEffects.MOVEMENT_SPEED, 600, 0)));

        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> data.customEffects().add(effect(MobEffects.WITHER, 100, 0)));
    }

    @Test
    void colorIsDerivedFromEffects() {
        ItemStack plain = new ItemStack(Items.POTION);
        ItemStack coloured = PotionDataBuilder.fromEmpty()
                .withCustomColor(0x00FF00)
                .applyTo(plain);

        assertEquals(0x00FF00, PotionData.read(coloured).color());
        // -13083194 is PotionContents.BASE_POTION_COLOR, private in 1.21.1 - the fallback colour
        // getColor() uses when there is no custom colour and no effects to blend.
        assertEquals(-13083194, PotionData.read(plain).color());
    }
}
