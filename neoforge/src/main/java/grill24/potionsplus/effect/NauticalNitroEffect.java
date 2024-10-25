package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class NauticalNitroEffect extends MobEffect implements IEffectTooltipDetails {
    public NauticalNitroEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    public float getSpeedMultiplier(MobEffectInstance effectInstance) {
        return (effectInstance.getAmplifier() + 1) * 1.2f;
    }

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        Component percentage = Utility.formatEffectNumber((getSpeedMultiplier(effectInstance)-1F)* 100, 0, "%");
        return List.of(percentage, Component.translatable("effect.potionsplus.nautical_nitro.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
