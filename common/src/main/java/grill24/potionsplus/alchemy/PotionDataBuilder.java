package grill24.potionsplus.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * The single write surface for potion data, and the piece {@code PUtil} never had - it exposed
 * {@code setCustomEffects} and nothing else, so every site that needed to build or edit a potion
 * reconstructed {@code PotionContents} by hand.
 *
 * <p>Nothing here mutates its input. {@link #applyTo(ItemStack)} copies the stack it is given before
 * writing, which makes the class of bug where evaluating a potential recipe result permanently altered
 * the ingredients sitting in the block impossible to reintroduce.
 *
 * <p>Amplifier and duration are not clamped yet - that lands with {@code EffectScaling} and the global
 * amplifier ceiling in phase 4. Every write funnels through {@link #build()}, so the clamp is a
 * single-site addition when it comes.
 */
public final class PotionDataBuilder {

    private Optional<Holder<Potion>> basePotion;
    private Optional<Integer> customColor;
    private final List<MobEffectInstance> customEffects;
    private Optional<String> customName;

    private PotionDataBuilder(PotionData source) {
        this.basePotion = source.basePotion();
        this.customColor = source.customColor();
        this.customEffects = new ArrayList<>(source.customEffects());
        this.customName = source.customName();
    }

    /** A builder seeded from the stack's current potion data. The stack is not modified. */
    public static PotionDataBuilder from(ItemStack stack) {
        return new PotionDataBuilder(PotionData.read(stack));
    }

    public static PotionDataBuilder from(PotionData data) {
        return new PotionDataBuilder(data);
    }

    public static PotionDataBuilder fromEmpty() {
        return new PotionDataBuilder(PotionData.EMPTY);
    }

    // ----- base potion -----

    public PotionDataBuilder withBasePotion(Holder<Potion> potion) {
        this.basePotion = Optional.of(potion);
        return this;
    }

    /**
     * Drops the link to a registered potion, keeping the custom effects. This is how a potion gets
     * durations and amplifiers that are not pinned by a registered {@link Potion}.
     */
    public PotionDataBuilder withoutBasePotion() {
        this.basePotion = Optional.empty();
        return this;
    }

    // ----- colour and name -----

    public PotionDataBuilder withCustomColor(int argb) {
        this.customColor = Optional.of(argb);
        return this;
    }

    public PotionDataBuilder withoutCustomColor() {
        this.customColor = Optional.empty();
        return this;
    }

    public PotionDataBuilder withCustomName(String translationKeySuffix) {
        this.customName = Optional.of(translationKeySuffix);
        return this;
    }

    public PotionDataBuilder withoutCustomName() {
        this.customName = Optional.empty();
        return this;
    }

    // ----- effects -----

    /** Replaces the custom effects outright. */
    public PotionDataBuilder withEffects(List<MobEffectInstance> effects) {
        this.customEffects.clear();
        for (MobEffectInstance effect : effects) {
            this.customEffects.add(new MobEffectInstance(effect));
        }
        return this;
    }

    public PotionDataBuilder withoutEffects() {
        this.customEffects.clear();
        return this;
    }

    /** Appends an effect without merging, even if one of the same type is already present. */
    public PotionDataBuilder addEffect(MobEffectInstance effect) {
        this.customEffects.add(new MobEffectInstance(effect));
        return this;
    }

    /**
     * Adds an effect, combining it with an existing instance of the same type by taking the greater
     * duration and the greater amplifier. This is the combining rule the brewing cauldron applies when
     * merging potions; it lived inline in the recipe class before.
     */
    public PotionDataBuilder mergeEffect(MobEffectInstance effect) {
        for (int i = 0; i < this.customEffects.size(); i++) {
            MobEffectInstance existing = this.customEffects.get(i);
            if (existing.getEffect().equals(effect.getEffect())) {
                this.customEffects.set(i, new MobEffectInstance(
                        existing.getEffect(),
                        Math.max(existing.getDuration(), effect.getDuration()),
                        Math.max(existing.getAmplifier(), effect.getAmplifier()),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()));
                return this;
            }
        }
        return addEffect(effect);
    }

    public PotionDataBuilder mergeEffects(Iterable<MobEffectInstance> effects) {
        for (MobEffectInstance effect : effects) {
            mergeEffect(effect);
        }
        return this;
    }

    /**
     * Folds the base potion's effects into the custom effect list and drops the link, so that every
     * effect becomes independently editable. A potion linked to a registered {@link Potion} cannot have
     * its durations changed; this is the step that makes duration and amplifier upgrades possible.
     */
    public PotionDataBuilder detachBasePotionEffects() {
        if (this.basePotion.isPresent()) {
            List<MobEffectInstance> merged = new ArrayList<>();
            for (MobEffectInstance effect : this.basePotion.get().value().getEffects()) {
                merged.add(new MobEffectInstance(effect));
            }
            List<MobEffectInstance> existing = new ArrayList<>(this.customEffects);
            this.basePotion = Optional.empty();
            this.customEffects.clear();
            this.customEffects.addAll(merged);
            mergeEffects(existing);
        }
        return this;
    }

    /** Removes every instance of the given effect from the custom effects. */
    public PotionDataBuilder removeEffect(Holder<MobEffect> effect) {
        this.customEffects.removeIf(instance -> instance.getEffect().equals(effect));
        return this;
    }

    /**
     * Rewrites every custom effect. Operates on the custom effects only - call
     * {@link #detachBasePotionEffects()} first if the base potion's effects should be included.
     */
    public PotionDataBuilder mapEffects(UnaryOperator<MobEffectInstance> mapper) {
        for (int i = 0; i < this.customEffects.size(); i++) {
            this.customEffects.set(i, mapper.apply(this.customEffects.get(i)));
        }
        return this;
    }

    public PotionDataBuilder addDuration(int ticks) {
        return mapEffects(effect -> withDurationAndAmplifier(
                effect, effect.getDuration() + ticks, effect.getAmplifier()));
    }

    public PotionDataBuilder addAmplifier(int levels) {
        return mapEffects(effect -> withDurationAndAmplifier(
                effect, effect.getDuration(), effect.getAmplifier() + levels));
    }

    /**
     * Collapses duplicate effect types down to one instance each, keeping the greatest duration and
     * amplifier seen for that type.
     */
    public PotionDataBuilder collapseDuplicateEffects() {
        Map<Holder<MobEffect>, MobEffectInstance> byType = new LinkedHashMap<>();
        for (MobEffectInstance effect : this.customEffects) {
            byType.merge(effect.getEffect(), effect, (existing, incoming) -> new MobEffectInstance(
                    existing.getEffect(),
                    Math.max(existing.getDuration(), incoming.getDuration()),
                    Math.max(existing.getAmplifier(), incoming.getAmplifier()),
                    incoming.isAmbient(),
                    incoming.isVisible(),
                    incoming.showIcon()));
        }
        this.customEffects.clear();
        this.customEffects.addAll(byType.values());
        return this;
    }

    // ----- terminals -----

    public PotionData build() {
        return new PotionData(
                this.basePotion, this.customColor, List.copyOf(this.customEffects), this.customName);
    }

    /**
     * Returns a copy of the stack carrying this builder's potion data. The argument is never modified.
     */
    public ItemStack applyTo(ItemStack stack) {
        ItemStack result = stack.copy();
        result.set(DataComponents.POTION_CONTENTS, build().toContents());
        return result;
    }

    private static MobEffectInstance withDurationAndAmplifier(
            MobEffectInstance effect, int duration, int amplifier) {
        return new MobEffectInstance(
                effect.getEffect(),
                duration,
                amplifier,
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon());
    }
}
