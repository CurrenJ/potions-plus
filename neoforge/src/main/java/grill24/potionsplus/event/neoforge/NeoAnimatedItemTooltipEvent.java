package grill24.potionsplus.event.neoforge;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

/**
 * NeoForge event wrapper for AnimatedItemTooltipEvent.
 * The common module's AnimatedItemTooltipEvent is the data holder;
 * this class wraps it as a NeoForge bus event.
 */
public class NeoAnimatedItemTooltipEvent extends Event {
    private final grill24.potionsplus.event.AnimatedItemTooltipEvent delegate;

    public NeoAnimatedItemTooltipEvent(grill24.potionsplus.event.AnimatedItemTooltipEvent delegate) {
        this.delegate = delegate;
    }

    public grill24.potionsplus.event.AnimatedItemTooltipEvent getDelegate() {
        return delegate;
    }

    public static class Add extends NeoAnimatedItemTooltipEvent {
        public Add(Player player, ItemStack stack, List<grill24.potionsplus.event.AnimatedItemTooltipEvent.TooltipLines> tooltipMessages) {
            super(new grill24.potionsplus.event.AnimatedItemTooltipEvent.Add(player, stack, tooltipMessages));
        }
    }

    public static class Modify extends NeoAnimatedItemTooltipEvent {
        public Modify(Player player, ItemStack stack, List<grill24.potionsplus.event.AnimatedItemTooltipEvent.TooltipLines> tooltipMessages) {
            super(new grill24.potionsplus.event.AnimatedItemTooltipEvent.Modify(player, stack, tooltipMessages));
        }
    }
}
