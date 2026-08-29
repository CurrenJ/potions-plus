package grill24.potionsplus.alchemy;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.AnyOtherPotionEffect;
import grill24.potionsplus.effect.AnyPotionEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Owns effect enumeration: the icon index {@code ItemOverrideUtility} bakes into models at datagen time
 * and {@code HerbalistsLecternBlockEntity} reads at runtime, and the marker-effect predicate the passive
 * effect roll needs to exclude {@code ANY_POTION}/{@code ANY_OTHER_POTION} structurally instead of by a
 * datagen list remembering both.
 *
 * <p>Replaces {@code Utility.getAllMobEffects()}/{@code getAllMobEffectsIconStackSizeMap()}, which
 * indexed effects by their position in a name-sorted list spanning both the {@code minecraft} and
 * {@code potionsplus} namespaces - inserting or removing a single effect shifted every index after it,
 * silently, because the datagen run and the runtime read of that ordering could disagree on registry
 * contents. {@link #iconOrder()} instead orders vanilla effects by their (Mojang-controlled, effectively
 * fixed) registry iteration order, followed by {@link #POTIONSPLUS_ICON_ORDER} - a declared list that
 * only grows at the end, so appending a new effect there is the only way to add one, and doing so cannot
 * shift any existing effect's index.
 */
public final class EffectRegistry {

    /** The icon scheme's cap - {@link #iconIndex(Holder)} never returns more than this. */
    public static final int ICON_STACK_CAP = 64;

    /**
     * Every Potions Plus effect, in the order it was given a stable icon index. Append new effects to the
     * end; never reorder or remove an entry, or every effect after it silently gets a new index.
     */
    private static final List<Supplier<Holder<MobEffect>>> POTIONSPLUS_ICON_ORDER = List.of(
            () -> MobEffects.ANY_POTION,
            () -> MobEffects.ANY_OTHER_POTION,
            () -> MobEffects.GEODE_GRACE,
            () -> MobEffects.FALL_OF_THE_VOID,
            () -> MobEffects.EXPLODING,
            () -> MobEffects.MAGNETIC,
            () -> MobEffects.TELEPORTATION,
            () -> MobEffects.LOOTING,
            () -> MobEffects.FORTUITOUS_FATE,
            () -> MobEffects.METAL_DETECTING,
            () -> MobEffects.GIANT_STEPS,
            () -> MobEffects.REACH_FOR_THE_STARS,
            () -> MobEffects.NAUTICAL_NITRO,
            () -> MobEffects.CROP_COLLECTOR,
            () -> MobEffects.BOTANICAL_BOOST,
            () -> MobEffects.SLIP_N_SLIDE,
            () -> MobEffects.HARROWING_HANDS,
            () -> MobEffects.BONE_BUDDY,
            () -> MobEffects.SHEPHERDS_SERENADE,
            () -> MobEffects.SOUL_MATE,
            () -> MobEffects.FLYING_TIME,
            () -> MobEffects.BOUNCING
    );

    private static List<Holder<MobEffect>> iconOrderCache;

    private EffectRegistry() {
    }

    /**
     * Every vanilla and Potions Plus effect, in stable icon-index order. Must not be called before
     * {@link MobEffects#init} has run.
     */
    public static List<Holder<MobEffect>> iconOrder() {
        if (iconOrderCache == null) {
            List<Holder<MobEffect>> order = new ArrayList<>();
            BuiltInRegistries.MOB_EFFECT.listElements().forEach(reference -> {
                if (reference.key().identifier().getNamespace().equals("minecraft")) {
                    order.add(reference);
                }
            });
            for (Supplier<Holder<MobEffect>> supplier : POTIONSPLUS_ICON_ORDER) {
                order.add(supplier.get());
            }
            iconOrderCache = List.copyOf(order);
        }
        return iconOrderCache;
    }

    /** The stable, 1-indexed icon index for an effect. Never depends on registry iteration order changing. */
    public static int iconIndex(Holder<MobEffect> effect) {
        int index = iconOrder().indexOf(effect);
        if (index < 0) {
            throw new IllegalArgumentException("No icon index declared for " + effect);
        }
        return index + 1;
    }

    /**
     * A marker effect with no gameplay implementation of its own - {@code ANY_POTION}/
     * {@code ANY_OTHER_POTION}, used only to express "any potion effect" in recipe matching. Structurally
     * ineligible for the passive-effect roll, regardless of what any datagen blacklist says.
     */
    public static boolean isMarker(Holder<MobEffect> effect) {
        MobEffect value = effect.value();
        return value instanceof AnyPotionEffect || value instanceof AnyOtherPotionEffect;
    }

    /**
     * Every effect eligible to be rolled as a passive effect: the full registry, minus marker effects,
     * minus {@code excludedEffects}. Built once and sampled directly, instead of rejection-sampling the
     * whole registry and giving up after a few misses - which silently drops the excluded effects from the
     * roll rather than excluding them.
     */
    public static List<Holder<MobEffect>> passiveEligible(Set<ResourceKey<MobEffect>> excludedEffects) {
        List<Holder<MobEffect>> pool = new ArrayList<>();
        BuiltInRegistries.MOB_EFFECT.listElements().forEach(reference -> {
            if (!isMarker(reference) && !excludedEffects.contains(reference.key())) {
                pool.add(reference);
            }
        });
        return List.copyOf(pool);
    }
}
