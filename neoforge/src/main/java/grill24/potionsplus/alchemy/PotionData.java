package grill24.potionsplus.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Immutable value object for a potion's identity (linked {@link Potion}) plus its
 * explicit effect list. Replaces the ad hoc {@link PotionContents} reads scattered
 * across call sites.
 * <p>
 * The value is copied before any speculative evaluation (see the brewing-cauldron
 * passive-effect branch) rather than mutating a live {@link ItemStack}'s
 * {@link PotionContents} in place.
 */
public final class PotionData {
    private final PotionContents contents;

    PotionData(PotionContents contents) {
        this.contents = contents;
    }

    public static PotionData from(PotionContents contents) {
        return new PotionData(contents);
    }

    /** Returns a lenient read of an item stack's potion data (empty if absent). */
    public static PotionData from(ItemStack stack) {
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            return new PotionData(stack.get(DataComponents.POTION_CONTENTS));
        }
        return new PotionData(new PotionContents(Optional.empty(), Optional.empty(), Collections.emptyList()));
    }

    public Optional<Holder<Potion>> potion() {
        return contents.potion();
    }

    public Optional<Integer> customColor() {
        return contents.customColor();
    }

    public List<MobEffectInstance> customEffects() {
        return contents.customEffects();
    }

    public boolean hasPotion() {
        return contents.potion().isPresent();
    }

    public boolean hasEffects() {
        return contents.hasEffects();
    }

    public List<MobEffectInstance> getAllEffects() {
        return getAllEffects(contents);
    }

    public PotionContents toPotionContents() {
        return contents;
    }

    // ----- Static readers (ported from PUtil) -----

    public static boolean hasPotionContents(ItemStack stack) {
        return stack.has(DataComponents.POTION_CONTENTS);
    }

    public static PotionContents getPotionContents(ItemStack stack) {
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            return stack.get(DataComponents.POTION_CONTENTS);
        }
        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static List<MobEffectInstance> getAllEffects(PotionContents contents) {
        List<MobEffectInstance> allEffects = new ArrayList<>();
        contents.getAllEffects().forEach(allEffects::add);
        return allEffects;
    }

    public static List<MobEffectInstance> getAllEffects(ItemStack stack) {
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            return getAllEffects(getPotionContents(stack));
        }
        return Collections.emptyList();
    }

    public static Potion getPotion(ItemStack stack) {
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            Optional<Holder<Potion>> potion = getPotionContents(stack).potion();
            return potion.map(Holder::value).orElse(Potions.WATER.value());
        }
        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static Holder<Potion> getPotionHolder(ItemStack stack) {
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            return stack.get(DataComponents.POTION_CONTENTS).potion().orElse(null);
        }
        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static Holder<Potion> getPotionHolder(Potion potion) {
        return BuiltInRegistries.POTION.getHolder(BuiltInRegistries.POTION.getKey(potion)).orElseThrow();
    }

    public static boolean hasPotion(ItemStack stack) {
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            return stack.get(DataComponents.POTION_CONTENTS).potion().isPresent();
        }
        return false;
    }
}
