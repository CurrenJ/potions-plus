package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

/**
 * NeoForge mod-event-bus wrapper around the loader-agnostic {@link AnimatedItemTooltipEvent}, so
 * other mods can listen to/modify tooltip contribution the same way the old single-loader event did.
 */
public abstract class AnimatedItemTooltipBusEvent extends Event {
    private final AnimatedItemTooltipEvent delegate;

    protected AnimatedItemTooltipBusEvent(Player player, ItemStack stack, List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages) {
        this.delegate = new AnimatedItemTooltipEvent(player, stack, tooltipMessages);
    }

    public List<AnimatedItemTooltipEvent.TooltipLines> getTooltipMessages() {
        return delegate.getTooltipMessages();
    }

    public void addTooltipMessage(AnimatedItemTooltipEvent.TooltipLines tooltipLines) {
        delegate.addTooltipMessage(tooltipLines);
    }

    public void removeTooltipMessage(ResourceLocation id) {
        delegate.removeTooltipMessage(id);
    }

    public void setTooltipMessage(ResourceLocation id, AnimatedItemTooltipEvent.TooltipLines tooltipLines) {
        delegate.setTooltipMessage(id, tooltipLines);
    }

    public Player getPlayer() {
        return delegate.getPlayer();
    }

    public ItemStack getItemStack() {
        return delegate.getItemStack();
    }

    public static class Add extends AnimatedItemTooltipBusEvent {
        public Add(Player player, ItemStack stack, List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages) {
            super(player, stack, tooltipMessages);
        }
    }

    public static class Modify extends AnimatedItemTooltipBusEvent {
        public Modify(Player player, ItemStack stack, List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages) {
            super(player, stack, tooltipMessages);
        }
    }
}
