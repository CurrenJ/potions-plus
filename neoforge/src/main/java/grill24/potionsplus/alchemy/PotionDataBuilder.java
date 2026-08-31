package grill24.potionsplus.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Mutable builder for constructing {@link PotionData} / {@link PotionContents}
 * from scratch (recipe outputs, loot, commands).
 */
public final class PotionDataBuilder {
    private Optional<Holder<Potion>> potion = Optional.empty();
    private Optional<Integer> customColor = Optional.empty();
    private List<MobEffectInstance> effects = new ArrayList<>();

    public PotionDataBuilder potion(Holder<Potion> potion) {
        this.potion = Optional.ofNullable(potion);
        return this;
    }

    public PotionDataBuilder customColor(int color) {
        this.customColor = Optional.of(color);
        return this;
    }

    public PotionDataBuilder effects(List<MobEffectInstance> effects) {
        this.effects = new ArrayList<>(effects);
        return this;
    }

    public PotionDataBuilder addEffect(MobEffectInstance effect) {
        this.effects.add(effect);
        return this;
    }

    public PotionData build() {
        return new PotionData(toPotionContents());
    }

    public PotionContents toPotionContents() {
        return new PotionContents(potion, customColor, effects);
    }

    /**
     * Replaces an item stack's custom effects while preserving its linked potion
     * and color. Ported verbatim from {@code PUtil.setCustomEffects}.
     */
    public static ItemStack setCustomEffects(ItemStack stack, List<MobEffectInstance> customEffects) {
        PotionContents old = stack.getOrDefault(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), Collections.emptyList()));
        PotionContents next = new PotionContents(old.potion(), old.customColor(), customEffects);
        stack.set(DataComponents.POTION_CONTENTS, next);
        return stack;
    }
}
