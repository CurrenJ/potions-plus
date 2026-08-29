package grill24.potionsplus.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An immutable, always-constructible view of a stack's potion contents.
 *
 * <p>This is the single read surface for potion data. Unlike its predecessor in {@code PUtil} - where
 * {@code getPotion} threw for a missing component but silently returned Water for a present-but-unlinked
 * one, and {@code getPotionHolder} threw or returned null - reading is total: a stack with no potion
 * contents reads as {@link #EMPTY}. Callers that need to distinguish ask {@link #isEmpty()} or
 * {@link #hasBasePotion()}.
 *
 * <p>Mirrors the shape of {@link PotionContents} so the two convert without loss.
 *
 * @param basePotion    the linked registered potion, if any. Absent for everything the brewing cauldron
 *                      brews - those carry custom effects only, so durations are not pinned by a
 *                      registered {@link Potion}.
 * @param customColor   an explicit colour override, if any
 * @param customEffects effects carried by the stack itself rather than by {@link #basePotion}
 * @param customName    the translation-key suffix vanilla names the stack with, taking precedence over
 *                      {@code Potion.name()}
 */
public record PotionData(
        Optional<Holder<Potion>> basePotion,
        Optional<Integer> customColor,
        List<MobEffectInstance> customEffects,
        Optional<String> customName
) {
    public static final PotionData EMPTY =
            new PotionData(Optional.empty(), Optional.empty(), List.of(), Optional.empty());

    public PotionData {
        customEffects = List.copyOf(customEffects);
    }

    /**
     * Reads the potion contents of a stack. Never throws: a stack with no potion contents - or an
     * empty stack - reads as {@link #EMPTY}.
     */
    public static PotionData read(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents == null ? EMPTY : of(contents);
    }

    public static PotionData of(PotionContents contents) {
        return new PotionData(
                contents.potion(), contents.customColor(), contents.customEffects(), contents.customName());
    }

    public PotionContents toContents() {
        return new PotionContents(basePotion, customColor, customEffects, customName);
    }

    /**
     * Every effect this potion applies, in vanilla's natural order: the base potion's effects first,
     * then the custom effects. This is display order - the order tooltips and colour blending want.
     *
     * <p>For comparison and identity, use {@link #canonicalEffects()} instead. Comparing in natural
     * order is what made two potions carrying the same effects in a different order read as unequal.
     */
    public List<MobEffectInstance> effects() {
        List<MobEffectInstance> all = new ArrayList<>();
        toContents().getAllEffects().forEach(all::add);
        return List.copyOf(all);
    }

    /**
     * Every effect this potion applies, in a stable order that does not depend on how the potion was
     * assembled. See {@link EffectComparison#canonical}.
     */
    public List<MobEffectInstance> canonicalEffects() {
        return EffectComparison.canonical(effects());
    }

    public boolean hasEffects() {
        return !effects().isEmpty();
    }

    public boolean hasBasePotion() {
        return basePotion.isPresent();
    }

    /** Whether this stack carries no potion data at all. */
    public boolean isEmpty() {
        return basePotion.isEmpty()
                && customColor.isEmpty()
                && customEffects.isEmpty()
                && customName.isEmpty();
    }

    /** The first instance of the given effect, from {@link #effects()}. */
    public Optional<MobEffectInstance> effect(Holder<MobEffect> effect) {
        for (MobEffectInstance instance : effects()) {
            if (instance.getEffect().equals(effect)) {
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }

    public boolean has(Holder<MobEffect> effect) {
        return effect(effect).isPresent();
    }

    /** The blended display colour, matching {@link PotionContents#getColor()}. */
    public int color() {
        return toContents().getColor();
    }
}
