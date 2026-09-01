package grill24.potionsplus.alchemy;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * Potion equality, in one place.
 *
 * <p>This is the comparison every brewing-cauldron recipe match, recipe-viewer lookup and advancement
 * trigger funnels through. Two things it fixes relative to the {@code PUtil} implementation it replaces:
 *
 * <ul>
 *     <li><b>The ignore flags were inverted.</b> {@code IGNORE_POTION_EFFECT_AMPLIFIER} and
 *     {@code IGNORE_POTION_EFFECT_DURATION} were used as "require a match on", so asking to ignore the
 *     amplifier was the only way to make it matter, and the default path compared neither amplifier nor
 *     duration. Here, an ignore flag means ignore, and the default compares both.</li>
 *     <li><b>Comparison was order-dependent.</b> Effects were compared by list index, so two potions
 *     carrying the same effects in a different order read as unequal - and effect order is not something
 *     anyone sets deliberately, it falls out of map iteration and insertion order. Here, comparison runs
 *     over {@link #canonical} order.</li>
 * </ul>
 *
 * <p>Both are behaviour changes, not refactors. They are why the call-site migration is staged separately.
 */
public final class EffectComparison {

    private EffectComparison() {}

    /**
     * How strictly two potions must agree to count as the same.
     *
     * <p>Serialized names and ids are identical to the {@code PotionMatchingCriteria} enum this
     * replaces, so recipes already persisted in saved data continue to deserialize unchanged when the
     * call sites move over.
     */
    public enum MatchCriteria implements StringRepresentable {
        /** Item and every data component must be identical. */
        EXACT_MATCH("exact_match", 0),
        /** Effects must match in type and amplifier, but durations may differ. */
        IGNORE_POTION_EFFECT_DURATION("ignore_effect_duration", 1),
        /** Effects must match in type and duration, but amplifiers may differ. */
        IGNORE_POTION_EFFECT_AMPLIFIER("ignore_effect_amplifier", 2),
        /** Only the container matters; contents are not compared at all. */
        IGNORE_POTION_EFFECTS("ignore_potion_effects", 3),
        /** Contents are not compared, but both stacks must carry at least one effect. */
        IGNORE_POTION_EFFECTS_MIN_1_EFFECT("ignore_potion_effects_min_1_effect", 4),
        /** A splash potion may match a drinkable one, and so on. */
        IGNORE_POTION_CONTAINER("ignore_potion_container", 5),
        /** Never matches. For recipes that exist only to be displayed in a recipe viewer. */
        NEVER_MATCH("never_match", 6);

        public static final Codec<MatchCriteria> CODEC = StringRepresentable.fromEnum(MatchCriteria::values);
        public static final IntFunction<MatchCriteria> BY_ID =
                ByIdMap.continuous(MatchCriteria::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, MatchCriteria> STREAM_CODEC =
                ByteBufCodecs.idMapper(BY_ID, MatchCriteria::id);

        private final String serializedName;
        private final int id;

        MatchCriteria(String serializedName, int id) {
            this.serializedName = serializedName;
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }

        public int id() {
            return this.id;
        }
    }

    /**
     * Canonical effect order: by effect registry id, then amplifier, then duration.
     *
     * <p>Registry id is the primary key because it is the only part that is stable across how a potion
     * was assembled. Amplifier and duration only break ties between repeated instances of the same
     * effect type - a shape {@link PotionDataBuilder#collapseDuplicateEffects()} exists to remove.
     */
    private static final Comparator<MobEffectInstance> CANONICAL_ORDER =
            Comparator.comparing((MobEffectInstance effect) -> effectId(effect.getEffect()))
                    .thenComparingInt(MobEffectInstance::getAmplifier)
                    .thenComparingInt(MobEffectInstance::getDuration);

    /** The given effects in {@linkplain #CANONICAL_ORDER canonical order}. The input is not modified. */
    public static List<MobEffectInstance> canonical(Iterable<MobEffectInstance> effects) {
        List<MobEffectInstance> sorted = new ArrayList<>();
        effects.forEach(sorted::add);
        sorted.sort(CANONICAL_ORDER);
        return List.copyOf(sorted);
    }

    /**
     * A stable string identity for a potion stack: container, base potion and canonical effects.
     *
     * <p>Replaces {@code PUtil.getNameOrVerbosePotionName}, which concatenated effects in natural order
     * and so gave two identical potions different identities depending on assembly order.
     */
    public static String identityString(ItemStack stack) {
        StringBuilder identity = new StringBuilder();
        identity.append(BuiltInRegistries.ITEM.getKey(stack.getItem()));

        PotionData data = PotionData.read(stack);
        data.basePotion().ifPresent(potion -> identity.append('|').append(potion.getRegisteredName()));

        for (MobEffectInstance effect : canonical(data.effects())) {
            identity.append('|')
                    .append(effectId(effect.getEffect()))
                    .append('@').append(effect.getAmplifier())
                    .append('x').append(effect.getDuration());
        }
        return identity.toString();
    }

    public static int identityHash(ItemStack stack) {
        return identityString(stack).hashCode();
    }

    /**
     * {@link #identityString} sanitized down to the character set a {@link net.minecraft.resources.ResourceLocation}
     * path accepts ({@code [a-z0-9/._-]}). Recipe ids are built from this, not {@link #identityString}
     * directly - the raw identity string carries {@code |}, {@code @} and registry-key colons, none of
     * which survive {@code ResourceLocation.fromNamespaceAndPath}.
     */
    public static String identitySlug(ItemStack stack) {
        return identityString(stack)
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9/._-]", "_");
    }

    /** Whether two stacks count as the same under the given criteria. */
    public static boolean matches(ItemStack stack, ItemStack other, Collection<MatchCriteria> criteria) {
        Set<MatchCriteria> flags = criteria.isEmpty()
                ? EnumSet.noneOf(MatchCriteria.class)
                : EnumSet.copyOf(criteria);

        if (flags.contains(MatchCriteria.NEVER_MATCH)) {
            return false;
        }

        boolean bothArePotions =
                PotionContainer.isPotionStack(stack) && PotionContainer.isPotionStack(other);
        boolean sameContainer = flags.contains(MatchCriteria.IGNORE_POTION_CONTAINER)
                || ItemStack.isSameItem(stack, other);

        // Anything that is not a pair of potions in compatible containers falls back to plain
        // component equality - there is no potion-specific question to ask.
        if (!bothArePotions || !sameContainer) {
            return ItemStack.isSameItemSameComponents(stack, other);
        }

        if (flags.contains(MatchCriteria.EXACT_MATCH)) {
            return ItemStack.isSameItemSameComponents(stack, other);
        }

        PotionData data = PotionData.read(stack);
        PotionData otherData = PotionData.read(other);

        if (flags.contains(MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT)) {
            return data.hasEffects() && otherData.hasEffects();
        }

        if (flags.contains(MatchCriteria.IGNORE_POTION_EFFECTS)) {
            return true;
        }

        if (!data.basePotion().equals(otherData.basePotion())) {
            return false;
        }

        return effectsEqual(
                data.canonicalEffects(),
                otherData.canonicalEffects(),
                !flags.contains(MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER),
                !flags.contains(MatchCriteria.IGNORE_POTION_EFFECT_DURATION));
    }

    public static boolean matches(ItemStack stack, ItemStack other, MatchCriteria... criteria) {
        return matches(stack, other, List.of(criteria));
    }

    /**
     * Compares two already-{@linkplain #canonical canonical} effect lists. Effect type is always
     * compared; amplifier and duration only when asked for.
     */
    public static boolean effectsEqual(
            List<MobEffectInstance> effects,
            List<MobEffectInstance> otherEffects,
            boolean compareAmplifier,
            boolean compareDuration) {

        if (effects.size() != otherEffects.size()) {
            return false;
        }
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance effect = effects.get(i);
            MobEffectInstance otherEffect = otherEffects.get(i);
            if (!effect.getEffect().equals(otherEffect.getEffect())) {
                return false;
            }
            if (compareAmplifier && effect.getAmplifier() != otherEffect.getAmplifier()) {
                return false;
            }
            if (compareDuration && effect.getDuration() != otherEffect.getDuration()) {
                return false;
            }
        }
        return true;
    }

    /**
     * The registry id of an effect as a stable string. Falls back to
     * {@link Holder#getRegisteredName()} for holders that carry no key, which returns a stable
     * placeholder rather than something derived from object identity.
     */
    private static String effectId(Holder<MobEffect> effect) {
        return effect.unwrapKey()
                .map(key -> key.location().toString())
                .orElseGet(effect::getRegisteredName);
    }
}
