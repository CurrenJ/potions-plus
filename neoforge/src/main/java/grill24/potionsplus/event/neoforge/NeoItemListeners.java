package grill24.potionsplus.event.neoforge;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.item.tooltip.neoforge.BrewingTooltips;
import grill24.potionsplus.item.tooltip.PotionEffectTooltips;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge-specific event handlers for item tooltip animation and item use speed. Renamed from
 * {@code ItemListenersGame} to match 26.1.2's naming (Decision 4 mirror discipline) - the
 * loader-agnostic animation math it used to also carry now lives in
 * {@code common/event/ItemListenersGame}. Direct-calls the tooltip contributors instead of posting
 * a NeoForge-only bus event (26.1.2's design; the old {@code AnimatedItemTooltipBusEvent} wrapper
 * had exactly one poster and no other listeners, so it added nothing).
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class NeoItemListeners {
    private static float animationStartTimestamp = 0;
    private static float lastTooltipTimestamp = 0;

    private static ItemStack lastItemStack = ItemStack.EMPTY;

    @SubscribeEvent
    public static void onToolTipEvent(final ItemTooltipEvent event) {
        List<List<Component>> tooltipMessages = getTooltipMessages(event);
        animateTooltipMessages(tooltipMessages, event);
    }

    private static List<List<Component>> getTooltipMessages(final ItemTooltipEvent event) {
        List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages = new ArrayList<>();
        if (event.getEntity() instanceof Player player) {
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

    /**
     * This event is used to shorten the duration of the item use animation.
     * @param event the event
     */
    @SubscribeEvent
    public static void onLivingUseItem(final LivingEntityUseItemEvent.Tick event) {
        ItemStack itemStack = event.getItem();
        for(ItemAttributeModifiers.Entry entry : itemStack.getAttributeModifiers().modifiers()) {
            ResourceKey<Attribute> attributeKey = entry.attribute().getKey();
            if(attributeKey != null && attributeKey.equals(Attributes.USE_SPEED_BONUS.getKey())) {
                float useSpeedBonus = (float) entry.modifier().amount(); // 0.05 = 5% faster = skip every 20th tick
                int skipTickEveryTicks = Math.round(1.0F / useSpeedBonus);
                if (event.getDuration() % skipTickEveryTicks == 0) {
                    event.setDuration(event.getDuration() - 1);
                }
            }
        }
    }
}
