package grill24.potionsplus.event.forge;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.item.tooltip.BrewingTooltips;
import grill24.potionsplus.item.tooltip.PotionEffectTooltips;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.TickHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Forge equivalents of the NeoForge client-side event listeners, registered against each event
 * class's static {@code BUS}.
 */
public final class ForgeClientEventListeners {

    private ForgeClientEventListeners() {
    }

    public static void register() {
        registerTicks();
        registerTooltip();
        registerUseItem();
    }

    // ----- ClientGameListeners + NeoDelayedEvents.onClientTickEnd -----

    private static void registerTicks() {
        TickEvent.ClientTickEvent.Post.BUS.addListener((TickEvent.ClientTickEvent.Post event) -> {
            DelayedEvents.tick(TickHandler.ticks());
            ClientTickHandler.clientTickEnd();
        });

        // RenderFrameEvent.Post equivalent (Forge has no RenderFrameEvent; use RenderTickEvent.Post).
        TickEvent.RenderTickEvent.Post.BUS.addListener((TickEvent.RenderTickEvent.Post event) ->
                ClientTickHandler.renderTick(event.timer().getGameTimeDeltaPartialTick(true)));
    }

    // ----- NeoItemListeners.onToolTipEvent -----

    private static float animationStartTimestamp = 0;
    private static float lastTooltipTimestamp = 0;
    private static ItemStack lastItemStack = ItemStack.EMPTY;

    private static void registerTooltip() {
        ItemTooltipEvent.BUS.addListener((ItemTooltipEvent event) -> {
            List<List<Component>> tooltipMessages = getTooltipMessages(event);
            animateTooltipMessages(tooltipMessages, event);
        });
    }

    private static List<List<Component>> getTooltipMessages(ItemTooltipEvent event) {
        List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages = new ArrayList<>();
        if (event.getEntity() != null) {
            ItemStack itemStack = event.getItemStack();
            AnimatedItemTooltipEvent.Add addEvent = new AnimatedItemTooltipEvent.Add(event.getEntity(), itemStack, tooltipMessages);
            WeightDataComponent.onTooltip(addEvent);
            BrewingTooltips.onBrewingTooltip(addEvent);
            PotionEffectTooltips.onPotionEffectTooltip(addEvent);
        }

        return AnimatedItemTooltipEvent.getPriorityOrderTooltipLines(tooltipMessages);
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, ItemTooltipEvent event) {
        if (!EffectComparison.matches(lastItemStack, event.getItemStack(), EffectComparison.MatchCriteria.EXACT_MATCH) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
            animationStartTimestamp = ClientTickHandler.total();
        }

        for (int i = 0; i < tooltipMessages.size(); i++) {
            List<Component> tooltipMessage = tooltipMessages.get(i);

            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = ItemListenersGame.animateComponentText(tooltipMessage, ItemListenersGame.durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            if (animatedComponent.getSecond() > 0 || i < 2) {
                event.getToolTip().add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = event.getItemStack();
    }

    // ----- NeoItemListeners.onLivingUseItem (shorten use duration for use-speed-bonus items) -----

    private static void registerUseItem() {
        LivingEntityUseItemEvent.Tick.BUS.addListener((LivingEntityUseItemEvent.Tick event) -> {
            ItemStack itemStack = event.getItem();
            ItemAttributeModifiers modifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                ResourceKey<Attribute> attributeKey = entry.attribute().unwrapKey().orElse(null);
                if (attributeKey != null && attributeKey.equals(Attributes.USE_SPEED_BONUS.unwrapKey().orElseThrow())) {
                    float useSpeedBonus = (float) entry.modifier().amount();
                    int skipTickEveryTicks = Math.round(1.0F / useSpeedBonus);
                    if (event.getDuration() % skipTickEveryTicks == 0) {
                        event.setDuration(event.getDuration() - 1);
                    }
                }
            }
        });
    }
}
