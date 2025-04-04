package grill24.potionsplus.effect;

import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.item.tooltip.TooltipPriorities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collections;
import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public interface IEffectTooltipDetails {
    AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance);
    default AnimatedItemTooltipEvent.TooltipLines createTooltipLine(List<Component> text) {
        return AnimatedItemTooltipEvent.TooltipLines.of(ppId("effect_description"), TooltipPriorities.POTION_EFFECTS, text);
    }

    default AnimatedItemTooltipEvent.TooltipLines createTooltipLine(Component text) {
        return AnimatedItemTooltipEvent.TooltipLines.of(ppId("effect_description"), TooltipPriorities.POTION_EFFECTS, Collections.singletonList(text));
    }
}
