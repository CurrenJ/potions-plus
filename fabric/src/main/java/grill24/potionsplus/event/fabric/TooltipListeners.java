package grill24.potionsplus.event.fabric;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.item.tooltip.BrewingTooltips;
import grill24.potionsplus.item.tooltip.PotionEffectTooltips;
import grill24.potionsplus.utility.ClientTickHandler;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric equivalent of NeoForge's {@code event/neoforge/NeoItemListeners} (Phase 7 "Client
 * tooltips" bucket). {@code fabric-item-api-v1} 11.2.0's {@link ItemTooltipCallback} has no player
 * parameter (unlike NeoForge's/Forge's {@code ItemTooltipEvent}), so this uses
 * {@code Minecraft.getInstance().player} - always non-null while a tooltip is being built, since
 * tooltips only render in-world/in-inventory. {@code BrewingTooltips} is now called here too (Phase
 * 11a): it moved to {@code common/} once its former blockers, {@code RecipesRegistrar} and
 * {@code AbyssalTroveBlockEntity}, both became common.
 */
public final class TooltipListeners {
    private static float animationStartTimestamp = 0;
    private static float lastTooltipTimestamp = 0;
    private static ItemStack lastItemStack = ItemStack.EMPTY;

    private TooltipListeners() {
    }

    public static void registerClient() {
        ItemTooltipCallback.EVENT.register(TooltipListeners::onToolTipEvent);
    }

    private static void onToolTipEvent(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.TooltipFlag flag, List<Component> tooltipLines) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages = new ArrayList<>();
        AnimatedItemTooltipEvent.Add addEvent = new AnimatedItemTooltipEvent.Add(player, stack, tooltipMessages);
        BrewingTooltips.onBrewingTooltip(addEvent);
        PotionEffectTooltips.onPotionEffectTooltip(addEvent);

        List<List<Component>> orderedTooltipMessages = AnimatedItemTooltipEvent.getPriorityOrderTooltipLines(tooltipMessages);
        animateTooltipMessages(orderedTooltipMessages, stack, tooltipLines);
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, ItemStack stack, List<Component> tooltip) {
        if (!EffectComparison.matches(lastItemStack, stack, List.of(EffectComparison.MatchCriteria.EXACT_MATCH)) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
            animationStartTimestamp = ClientTickHandler.total();
        }

        for (int i = 0; i < tooltipMessages.size(); i++) {
            List<Component> tooltipMessage = tooltipMessages.get(i);

            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = ItemListenersGame.animateComponentText(tooltipMessage, ItemListenersGame.durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            if (animatedComponent.getSecond() > 0 || i < 2) { // First two components are vanilla tooltip components - don't remove
                tooltip.add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = stack;
    }
}
