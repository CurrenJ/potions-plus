package grill24.potionsplus.alchemy;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Potion/effect comparison and identity logic. Owns the match criteria used by
 * recipes, and the identity functions (structural hash / human string /
 * identifier slug) used to build recipe and advancement ids.
 */
public final class EffectComparison {
    private EffectComparison() {
    }

    /**
     * Matching criteria for potion recipes. The string ids (and their order, hence
     * the int ids) must remain identical to the retired
     * {@code BrewingCauldronRecipe.PotionMatchingCriteria} so that existing recipe
     * JSON and the network stream codec deserialize unchanged.
     */
    public enum MatchCriteria implements StringRepresentable {
        EXACT_MATCH("exact_match", 0),
        IGNORE_POTION_EFFECT_DURATION("ignore_effect_duration", 1),
        IGNORE_POTION_EFFECT_AMPLIFIER("ignore_effect_amplifier", 2),
        IGNORE_POTION_EFFECTS("ignore_potion_effects", 3),
        IGNORE_POTION_EFFECTS_MIN_1_EFFECT("ignore_potion_effects_min_1_effect", 4),
        IGNORE_POTION_CONTAINER("ignore_potion_container", 5),
        NEVER_MATCH("never_match", 6); // Used for recipes that we only want to display in JEI, but not actually match in the brewing cauldron

        public static final Codec<MatchCriteria> CODEC = StringRepresentable.fromEnum(MatchCriteria::values);
        public static final IntFunction<MatchCriteria> BY_ID = ByIdMap.continuous(MatchCriteria::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, MatchCriteria> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MatchCriteria::id);

        private final String name;
        private final int id;

        MatchCriteria(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private int id() {
            return this.id;
        }
    }

    // ----- Matching -----

    /**
     * Port of {@code PUtil.isSameItemOrPotion}. The effect amplifier/duration
     * comparison below is preserved verbatim (including its inverted {@code &&}
     * conditions) per the Phase 2 plan — it must not be silently "fixed" during
     * this migration.
     */
    public static boolean matches(ItemStack itemStack, ItemStack other, Collection<MatchCriteria> matchingCriteria) {
        boolean shouldNeverMatch = matchingCriteria.contains(MatchCriteria.NEVER_MATCH);
        if (shouldNeverMatch) {
            return false;
        }
        boolean shouldIgnorePotionContainer = matchingCriteria.contains(MatchCriteria.IGNORE_POTION_CONTAINER);
        boolean requiresExactMatch = matchingCriteria.contains(MatchCriteria.EXACT_MATCH);
        boolean shouldIgnorePotionEffects = matchingCriteria.contains(MatchCriteria.IGNORE_POTION_EFFECTS);
        boolean requiresMinimumOneEffect = matchingCriteria.contains(MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT);
        boolean shouldIgnorePotionEffectAmplifier = matchingCriteria.contains(MatchCriteria.IGNORE_POTION_EFFECT_AMPLIFIER);
        boolean shouldIgnorePotionEffectDuration = matchingCriteria.contains(MatchCriteria.IGNORE_POTION_EFFECT_DURATION);

        boolean isSameContainer = shouldIgnorePotionContainer || ItemStack.isSameItem(itemStack, other);
        if (PotionContainer.isPotion(itemStack) && PotionContainer.isPotion(other) && isSameContainer) {
            PotionContents potionContents = PotionData.getPotionContents(itemStack);
            PotionContents otherPotionContents = PotionData.getPotionContents(other);

            // Exact match ez
            if (requiresExactMatch) {
                return ItemStack.isSameItemSameComponents(itemStack, other);
            }

            // Ignore potion effects but require at least one effect of any type
            if (requiresMinimumOneEffect) {
                return potionContents.hasEffects() && otherPotionContents.hasEffects();
            }

            // Ignore potion effects - only check the potion container
            if (shouldIgnorePotionEffects) {
                return true;
            }

            // Check linked Potion in PotionContents
            if (!potionContents.potion().equals(otherPotionContents.potion())) {
                return false;
            }

            // Check potion effects for matching duration and/or amplifier depending on the matching criteria
            List<MobEffectInstance> effects = PotionData.getAllEffects(potionContents);
            List<MobEffectInstance> otherEffects = PotionData.getAllEffects(otherPotionContents);
            if (effects.size() != otherEffects.size()) {
                return false;
            }
            for (int i = 0; i < effects.size(); i++) {
                MobEffectInstance effect = effects.get(i);
                MobEffectInstance otherEffect = otherEffects.get(i);
                if (!effect.getEffect().equals(otherEffect.getEffect())
                        || (effect.getAmplifier() != otherEffect.getAmplifier() && shouldIgnorePotionEffectAmplifier)
                        || (effect.getDuration() != otherEffect.getDuration() && shouldIgnorePotionEffectDuration)) {
                    return false;
                }
            }
            // If we reach here, the potion effects are the same
            return true;
        } else {
            return ItemStack.isSameItemSameComponents(itemStack, other);
        }
    }

    // ----- Identity -----

    /**
     * Human-readable identity for logs/debug only. Do NOT use for recipe or
     * advancement ids — see {@link #identitySlug(ItemStack)}.
     */
    public static String identityString(ItemStack stack) {
        if (PotionContainer.isPotion(stack)) {
            StringBuilder name = new StringBuilder();
            if (PotionData.hasPotion(stack)) {
                name.append(PotionData.getPotionHolder(stack).getKey().location().getPath()).append("_");
            }
            for (MobEffectInstance effect : PotionData.getAllEffects(stack)) {
                name.append(effect.getEffect().getKey().location().getPath()).append("_")
                        .append("a").append(effect.getAmplifier()).append("_")
                        .append("d").append(effect.getDuration()).append("_");
            }
            name.append(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
            return name.toString();
        }
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    /**
     * Identifier-path-safe identity ({@code [a-z0-9_./-]}). Use anywhere a
     * {@code ResourceLocation} path is built from a recipe's ingredients.
     */
    public static String identitySlug(ItemStack stack) {
        return identityString(stack).toLowerCase().replaceAll("[^a-z0-9_./-]", "_");
    }

    /**
     * Order-independent structural hash over a potion's identity, replacing
     * {@code PpIngredient}'s string-concatenation-of-verbose-names approach. Not
     * required to be stable across JVM runs (see the 2.5 re-roll decision).
     */
    public static int identityHash(ItemStack stack) {
        if (!PotionContainer.isPotion(stack)) {
            return BuiltInRegistries.ITEM.getKey(stack.getItem()).hashCode();
        }

        int hash = BuiltInRegistries.ITEM.getKey(stack.getItem()).hashCode();
        hash = 31 * hash + (PotionData.hasPotion(stack)
                ? PotionData.getPotionHolder(stack).getKey().location().hashCode()
                : 0);
        return 31 * hash + orderIndependentEffectHash(PotionData.getAllEffects(stack));
    }

    private static int orderIndependentEffectHash(List<MobEffectInstance> effects) {
        int[] hashes = effects.stream()
                .mapToInt(effect -> Objects.hash(effect.getEffect().getKey().location(), effect.getAmplifier(), effect.getDuration()))
                .sorted()
                .toArray();
        return Arrays.hashCode(hashes);
    }
}
