package grill24.potionsplus.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AnimatedItemTooltipEvent extends Event {
    /**
     * Represents tooltip lines to display on an item.
     *
     * @param id       Unique identifier for the tooltip - not used for any display logic.
     *                 Allows for easy identification of specific tooltips, for example if someone wants to modify a specific line in a tooltip.
     * @param priority Priority of the tooltip - lower numbers are displayed first.
     * @param text     The text to be displayed in the tooltip.
     */
    public record TooltipLines(ResourceLocation id, int priority, List<List<Component>> text) {
        public static TooltipLines of(ResourceLocation id, int priority, List<Component> text) {
            return new TooltipLines(id, priority, Collections.singletonList(text));
        }

        public static TooltipLines of(ResourceLocation id, int priority, Component text) {
            return TooltipLines.of(id, priority, Collections.singletonList(text));
        }
    }

    private List<TooltipLines> tooltipMessages;
    private ItemStack stack;
    private final Player player;

    public AnimatedItemTooltipEvent(Player player, ItemStack stack, List<TooltipLines> tooltipMessages) {
        this.player = player;
        this.stack = stack;
        this.tooltipMessages = tooltipMessages;
    }

    public List<TooltipLines> getTooltipMessages() {
        return tooltipMessages;
    }

    public void addTooltipMessage(TooltipLines tooltipLines) {
        this.tooltipMessages.add(tooltipLines);
    }

    public void removeTooltipMessage(ResourceLocation id) {
        this.tooltipMessages.removeIf(tooltipLine -> tooltipLine.id().equals(id));
    }

    public void setTooltipMessage(ResourceLocation id, TooltipLines tooltipLines) {
        this.tooltipMessages.removeIf(line -> line.id().equals(id));
        this.tooltipMessages.add(tooltipLines);
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public static List<List<Component>> getPriorityOrderTooltipLines(List<TooltipLines> tooltipMessages) {
        return tooltipMessages.stream()
                .sorted(Comparator.comparingInt(TooltipLines::priority)
                        .thenComparing(tooltipLine -> tooltipLine.id().toString()))
                .map(TooltipLines::text)
                .flatMap(List::stream)
                .toList();
    }

    public static class Add extends AnimatedItemTooltipEvent {
        public Add(Player player, ItemStack stack, List<TooltipLines> tooltipMessages) {
            super(player, stack, tooltipMessages);
        }
    }

    public static class Modify extends AnimatedItemTooltipEvent {
        public Modify(Player player, ItemStack stack, List<TooltipLines> tooltipMessages) {
            super(player, stack, tooltipMessages);
        }
    }
}
