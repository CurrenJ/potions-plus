package grill24.potionsplus.alchemy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PotionContainerTest extends AlchemyTestBase {

    @ParameterizedTest
    @EnumSource(PotionContainer.class)
    void everyContainerRoundTripsThroughItsItem(PotionContainer container) {
        assertEquals(container, PotionContainer.of(container.item()).orElseThrow());
        assertEquals(container, PotionContainer.of(new ItemStack(container.item())).orElseThrow());
    }

    @Test
    void recognisesTheFourVanillaContainers() {
        assertSame(PotionContainer.POTION, PotionContainer.of(Items.POTION).orElseThrow());
        assertSame(PotionContainer.SPLASH_POTION, PotionContainer.of(Items.SPLASH_POTION).orElseThrow());
        assertSame(PotionContainer.LINGERING_POTION, PotionContainer.of(Items.LINGERING_POTION).orElseThrow());
        assertSame(PotionContainer.TIPPED_ARROW, PotionContainer.of(Items.TIPPED_ARROW).orElseThrow());
    }

    @Test
    void rejectsNonContainers() {
        assertTrue(PotionContainer.of(Items.DIAMOND_SWORD).isEmpty());
        assertTrue(PotionContainer.of(new ItemStack(Items.GLASS_BOTTLE)).isEmpty());
        assertFalse(PotionContainer.isPotionStack(new ItemStack(Items.DIAMOND_SWORD)));
        assertTrue(PotionContainer.isPotionStack(new ItemStack(Items.SPLASH_POTION)));
    }

    /** An empty stack is not a potion, and asking must not throw. */
    @Test
    void emptyStackIsNotAContainer() {
        assertTrue(PotionContainer.of(ItemStack.EMPTY).isEmpty());
        assertFalse(PotionContainer.isPotionStack(ItemStack.EMPTY));
    }

    @ParameterizedTest
    @EnumSource(PotionContainer.class)
    void createLinksTheBasePotion(PotionContainer container) {
        ItemStack stack = container.create(Potions.HEALING, 3);

        assertSame(container.item(), stack.getItem());
        assertEquals(3, stack.getCount());
        assertEquals(Potions.HEALING, PotionData.read(stack).basePotion().orElseThrow());
    }

    /**
     * The shape every brewed potion takes: a container with no linked potion, so effect durations are
     * not pinned by a registered Potion.
     */
    @ParameterizedTest
    @EnumSource(PotionContainer.class)
    void createEmptyHasNoBasePotion(PotionContainer container) {
        ItemStack stack = container.createEmpty(1);

        assertFalse(PotionData.read(stack).hasBasePotion());
        assertTrue(PotionData.read(stack).isEmpty());
    }

    /**
     * Vanilla builds a potion's display name as this prefix plus the potion's own name string. The
     * prefix is per container, which is why "Splash Potion of X" and "Potion of X" differ.
     */
    @Test
    void nameTranslationPrefixMatchesVanillaLayout() {
        assertEquals("item.minecraft.potion.effect.", PotionContainer.POTION.nameTranslationPrefix());
        assertEquals("item.minecraft.splash_potion.effect.",
                PotionContainer.SPLASH_POTION.nameTranslationPrefix());
        assertEquals("item.minecraft.lingering_potion.effect.",
                PotionContainer.LINGERING_POTION.nameTranslationPrefix());
        assertEquals("item.minecraft.tipped_arrow.effect.",
                PotionContainer.TIPPED_ARROW.nameTranslationPrefix());
    }

    @Test
    void serializedNamesAreStable() {
        assertEquals("potion", PotionContainer.POTION.getSerializedName());
        assertEquals("splash_potion", PotionContainer.SPLASH_POTION.getSerializedName());
        assertEquals("lingering_potion", PotionContainer.LINGERING_POTION.getSerializedName());
        assertEquals("tipped_arrow", PotionContainer.TIPPED_ARROW.getSerializedName());
    }
}
