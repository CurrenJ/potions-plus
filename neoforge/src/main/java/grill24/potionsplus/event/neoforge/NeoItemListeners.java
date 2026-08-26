package grill24.potionsplus.event.neoforge;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.item.tooltip.BrewingTooltips;
import grill24.potionsplus.item.tooltip.PotionEffectTooltips;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
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
 * NeoForge-specific event handlers for item tooltip animation and item use speed.
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
            GeneticCropItem.onAnimatedTooltip(addEvent);
            WeightDataComponent.onTooltip(addEvent);
            BrewingTooltips.onBrewingTooltip(addEvent);
            PotionEffectTooltips.onPotionEffectTooltip(addEvent);
        }

        return AnimatedItemTooltipEvent.getPriorityOrderTooltipLines(tooltipMessages);
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, ItemTooltipEvent event) {
        if (!PUtil.isSameItemOrPotion(lastItemStack, event.getItemStack(), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
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

    /**
     * This event is used to shorten the duration of the item use animation.
     */
    @SubscribeEvent
    public static void onLivingUseItem(final LivingEntityUseItemEvent.Tick event) {
        ItemStack itemStack = event.getItem();
        for (ItemAttributeModifiers.Entry entry : itemStack.getAttributeModifiers().modifiers()) {
            ResourceKey<Attribute> attributeKey = entry.attribute().getKey();
            if (attributeKey != null && attributeKey.equals(Attributes.USE_SPEED_BONUS.getKey())) {
                float useSpeedBonus = (float) entry.modifier().amount();
                int skipTickEveryTicks = Math.round(1.0F / useSpeedBonus);
                if (event.getDuration() % skipTickEveryTicks == 0) {
                    event.setDuration(event.getDuration() - 1);
                }
            }
        }
    }
}
