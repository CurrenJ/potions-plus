package grill24.potionsplus.event;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.RUtil;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ItemListenersGame {
    private static float animationStartTimestamp = 0;
    private static float lastTooltipTimestamp = 0;
    private static final int durationUpgradeTextAnimationDurationTicks = 10;

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
            NeoForge.EVENT_BUS.post(new AnimatedItemTooltipEvent.Add(player, itemStack, tooltipMessages));
            NeoForge.EVENT_BUS.post(new AnimatedItemTooltipEvent.Modify(player, itemStack, tooltipMessages));
        }

        // Sort the tooltip messages by priority
        return AnimatedItemTooltipEvent.getPriorityOrderTooltipLines(tooltipMessages);
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, ItemTooltipEvent event) {
        if (!PUtil.isSameItemOrPotion(lastItemStack, event.getItemStack(), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
            animationStartTimestamp = ClientTickHandler.total();
        }

        for (int i = 0; i < tooltipMessages.size(); i++) {
            List<Component> tooltipMessage = tooltipMessages.get(i);

            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = animateComponentText(tooltipMessage, durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            if (animatedComponent.getSecond() > 0 || i < 2) { // First two components are vanilla tooltip components - don't remove
                event.getToolTip().add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = event.getItemStack();
    }

    public static List<Component> animateComponentTextStartTime(List<List<Component>> components, float animationStartTimestamp) {
        List<Component> animatedComponents = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            List<Component> component = components.get(i);
            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = animateComponentText(component, durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            animatedComponents.add(animatedComponent.getFirst());
        }

        return animatedComponents;
    }

    private static Pair<MutableComponent, Integer> animateComponentText(List<Component> component, float duration, int delayTicks, float animationStartTimestamp) {
        float f = RUtil.lerp(0.0F, 1.0F, RUtil.easeOutSine(Math.clamp((ClientTickHandler.total() - animationStartTimestamp - delayTicks) / duration, 0.0F, 1.0F)));
        f = Math.clamp(f, 0.0F, 1.0F);

        return animateComponentText(component, f);
    }

    public static Pair<MutableComponent, Integer> animateComponentText(List<Component> component, float animationProgress) {
        // Join all the components passed into one string, then split it at the appropriate index according to the animation progress
        String totalString = component.stream().map(Component::getString).collect(Collectors.joining());
        int splitIndex = Math.round(animationProgress * totalString.length());

        // Iterate over the components and split them at the appropriate index
        // Add any components with remaining text to our final list
        int index = 0;
        List<MutableComponent> components = new ArrayList<>();
        for (Component c : component) {
            String text = c.getString();
            if (index < splitIndex) {
                int splitIndexInComponent = Math.clamp(splitIndex - index, 0, text.length());
                String truncatedText = text.substring(0, splitIndexInComponent);
                if (!truncatedText.isEmpty()) {
                    MutableComponent mutableComponent = Component.literal(truncatedText).withStyle(c.getStyle());
                    components.add(mutableComponent);
                }
            }
            index += text.length();
        }

        if (components.isEmpty()) {
            return Pair.of(Component.empty(), 0);
        }

        // Combine components into one using .append(), preserving all styles
        MutableComponent finalComponent = Component.empty();
        for (MutableComponent mutableComponent : components) {
            finalComponent = finalComponent.append(mutableComponent);
        }
        return Pair.of(finalComponent, splitIndex);
    }

    /**
     * This event is used to shorten the duration of the item use animation.
     *
     * @param event the event
     */
    @SubscribeEvent
    public static void onLivingUseItem(final LivingEntityUseItemEvent.Tick event) {
        ItemStack itemStack = event.getItem();
        for (ItemAttributeModifiers.Entry entry : itemStack.getAttributeModifiers().modifiers()) {
            ResourceKey<Attribute> attributeKey = entry.attribute().getKey();
            if (attributeKey != null && attributeKey.equals(Attributes.USE_SPEED_BONUS.getKey())) {
                float useSpeedBonus = (float) entry.modifier().amount(); // 0.05 = 5% faster = skip every 20th tick
                int skipTickEveryTicks = Math.round(1.0F / useSpeedBonus);
                if (event.getDuration() % skipTickEveryTicks == 0) {
                    event.setDuration(event.getDuration() - 1);
                }
            }
        }
    }
}
