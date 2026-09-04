package grill24.potionsplus.event.forge;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.item.tooltip.BrewingTooltips;
import grill24.potionsplus.item.tooltip.PotionEffectTooltips;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Forge equivalent of NeoForge's {@code event/neoforge/NeoItemListeners} (Phase 7 "Client tooltips"
 * bucket). Registered explicitly from {@code PotionsPlusForge}'s constructor, matching this
 * module's established style (see {@code EffectListeners}/{@code TickListeners}). {@code
 * BrewingTooltips} is now called here too (Phase 11a): it moved to {@code common/} once its former
 * blockers, {@code RecipesRegistrar} and {@code AbyssalTroveBlockEntity}, both became common.
 */
public final class TooltipListeners {
    private static float animationStartTimestamp = 0;
    private static float lastTooltipTimestamp = 0;
    private static ItemStack lastItemStack = ItemStack.EMPTY;

    private TooltipListeners() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(TooltipListeners::onToolTipEvent);
    }

    private static void onToolTipEvent(final ItemTooltipEvent event) {
        List<List<Component>> tooltipMessages = getTooltipMessages(event);
        animateTooltipMessages(tooltipMessages, event);
    }

    private static List<List<Component>> getTooltipMessages(final ItemTooltipEvent event) {
        List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages = new ArrayList<>();
        Player player = event.getEntity();
        if (player != null) {
            ItemStack itemStack = event.getItemStack();
            AnimatedItemTooltipEvent.Add addEvent = new AnimatedItemTooltipEvent.Add(player, itemStack, tooltipMessages);
            BrewingTooltips.onBrewingTooltip(addEvent);
            PotionEffectTooltips.onPotionEffectTooltip(addEvent);
        }

        return AnimatedItemTooltipEvent.getPriorityOrderTooltipLines(tooltipMessages);
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, ItemTooltipEvent event) {
        if (!EffectComparison.matches(lastItemStack, event.getItemStack(), List.of(EffectComparison.MatchCriteria.EXACT_MATCH)) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
            animationStartTimestamp = ClientTickHandler.total();
        }

        for (int i = 0; i < tooltipMessages.size(); i++) {
            List<Component> tooltipMessage = tooltipMessages.get(i);

            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = ItemListenersGame.animateComponentText(tooltipMessage, ItemListenersGame.durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            if (animatedComponent.getSecond() > 0 || i < 2) { // First two components are vanilla tooltip components - don't remove
                event.getToolTip().add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = event.getItemStack();
    }
}
