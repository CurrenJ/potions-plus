package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
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
        return (effectInstance.getAmplifier() + 1) * 0.2F + 1F;
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        Component percentage = Utility.formatEffectNumber((getSpeedMultiplier(effectInstance)-1F)* 100, 0, "%");
        List<Component> text = List.of(percentage, Component.translatable(Translations.EFFECT_POTIONSPLUS_NAUTICAL_NITRO_TOOLTIP).withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(text);
    }
}
