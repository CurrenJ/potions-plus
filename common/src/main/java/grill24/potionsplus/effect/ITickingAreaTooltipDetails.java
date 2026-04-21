package grill24.potionsplus.effect;

import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public interface ITickingAreaTooltipDetails extends ITickingTooltipDetails {
    int getRadius(int amplifier);

    Component getVerb();

    @Override
    default AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        String tickInterval = String.valueOf(getTickInterval(effectInstance.getAmplifier()));
        MutableComponent ticks = Component.translatable("tooltip.potionsplus.ticks", tickInterval).withStyle(ChatFormatting.GREEN);

        String rangeString = String.valueOf(1 + 2 * getRadius(effectInstance.getAmplifier()));
        MutableComponent range = Component.literal(rangeString + "x" + rangeString).withStyle(ChatFormatting.GREEN);
        List<Component> components = List.of(
                Component.translatable("effect.potionsplus.ticking_area.tooltip_1", getVerb()).withStyle(ChatFormatting.LIGHT_PURPLE),
                ticks,
                Component.translatable("effect.potionsplus.ticking_area.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE),
                range,
                Component.translatable("effect.potionsplus.ticking_area.tooltip_3").withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(components);
    }
}
