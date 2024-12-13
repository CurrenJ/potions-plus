package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
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
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        float defaultFriction = getFriction(-1);
        float friction = getFriction(effectInstance.getAmplifier());

        Component frictionComponent = Component.literal("-" + String.format("%.0f", (friction - defaultFriction) * 100f) + "%").withStyle(ChatFormatting.GREEN);

        return List.of(frictionComponent, Component.translatable("effect.potionsplus.slip_n_slide.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
