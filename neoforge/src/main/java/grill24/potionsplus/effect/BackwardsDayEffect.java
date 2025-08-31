package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class BackwardsDayEffect extends MobEffect implements IEffectTooltipDetails {
    public BackwardsDayEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    // This effect's functionality will be handled through mixins or events
    // that intercept movement input and reverse it
    
    public static float getReversalIntensity(int amplifier) {
        switch (amplifier) {
            case 0: return 0.5f;  // 50% chance to reverse
            case 1: return 0.75f; // 75% chance to reverse
            case 2: return 1.0f;  // Always reverse
            default: return 1.0f;
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        float intensity = getReversalIntensity(effectInstance.getAmplifier());
        String percentage = String.format("%.0f%%", intensity * 100);
        
        Component intensityComponent = Component.literal(percentage).withStyle(ChatFormatting.RED);
        
        List<Component> text = List.of(
                Component.translatable("effect.potionsplus.backwards_day.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                intensityComponent,
                Component.translatable("effect.potionsplus.backwards_day.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        return createTooltipLine(text);
    }
}