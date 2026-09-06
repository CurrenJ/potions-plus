package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class SlipNSlideEffect extends MobEffect implements IEffectTooltipDetails {
    public SlipNSlideEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    public static float getFriction(int amplifier) {
        if (amplifier < 0) {
            return 0.6f;
        }
        return switch (amplifier) {
            case 0 -> 0.91f;
            case 1 -> 0.94f;
            default -> 0.97f;
        };
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        float defaultFriction = getFriction(-1);
        float friction = getFriction(effectInstance.getAmplifier());

        Component frictionComponent = Component.literal("-" + String.format("%.0f", (friction - defaultFriction) * 100f) + "%").withStyle(ChatFormatting.GREEN);
        List<Component> text = List.of(frictionComponent, Component.translatable(Translations.EFFECT_POTIONSPLUS_SLIP_N_SLIDE_TOOLTIP).withStyle(ChatFormatting.LIGHT_PURPLE));
        return createTooltipLine(text);
    }
}
