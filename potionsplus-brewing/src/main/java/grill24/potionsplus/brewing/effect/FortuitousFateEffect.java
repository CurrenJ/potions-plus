package grill24.potionsplus.effect;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class FortuitousFateEffect extends MobEffect implements IEnchantmentBonusTooltipDetails {
    public FortuitousFateEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public int getEnchantmentBonus(MobEffectInstance effectInstance) {
        return effectInstance.getAmplifier() + 1;
    }

    @Override
    public ResourceKey<Enchantment> getEffect() {
        return Enchantments.FORTUNE;
    }
}
