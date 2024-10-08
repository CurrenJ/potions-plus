package grill24.potionsplus.event;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        // Tooltip messages
        List<List<Component>> tooltipMessages = new ArrayList<>();
        if (event.getEntity() != null) {
            PpIngredient ppIngredient = PpIngredient.of(event.getItemStack().copyWithCount(1));
            if(Recipes.ALL_BCR_RECIPES_ANALYSIS.isAnyBrewingIngredientNotPotion(PpIngredient.of(event.getItemStack())))
            {
                // If the item is not in the abyssal trove, display a message indicating that the ingredient is unknown
                if (!Utility.isItemInLinkedAbyssalTrove(event.getEntity(), event.getItemStack())) {
                    MutableComponent text = Component.translatable("tooltip.potionsplus.unknown_ingredient");
                    text = text.withStyle(ChatFormatting.GRAY);
                    tooltipMessages.add(Collections.singletonList(text));
                }
                // If the item is in the abyssal trove, display the appropriate tooltip messages for the ingredient
                else {
                    if (Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(ppIngredient)) {
                        MutableComponent text = Component.translatable("tooltip.potionsplus.duration_ingredient", Utility.formatTicksAsSeconds(Recipes.DURATION_UPGRADE_ANALYSIS.getRecipeForIngredient(ppIngredient).get().value().getDurationToAdd()));
                        text = text.withStyle(ChatFormatting.LIGHT_PURPLE);
                        tooltipMessages.add(Collections.singletonList(text));
                    }
                    if (Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(ppIngredient)) {
                        MutableComponent text = Component.translatable("tooltip.potionsplus.amplification_ingredient", Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.getRecipeForIngredient(ppIngredient).get().value().getAmplifierToAdd());
                        text = text.withStyle(ChatFormatting.AQUA);
                        tooltipMessages.add(Collections.singletonList(text));
                    }


                    List<Component> textComponents = new ArrayList<>();

                    // Item rarity text
                    if (SeededIngredientsLootTables.COMMON_INGREDIENTS_SET.get().contains(ppIngredient)) {
                        textComponents.add(Component.translatable("tooltip.potionsplus.common").withStyle(ChatFormatting.WHITE));
                    } else if (SeededIngredientsLootTables.RARE_INGREDIENTS_SET.get().contains(ppIngredient)) {
                        textComponents.add(Component.translatable("tooltip.potionsplus.rare").withStyle(ChatFormatting.LIGHT_PURPLE));
                    }

                    // Flavor text
                    if (textComponents.isEmpty()) {
                        textComponents.add(Component.translatable("tooltip.potionsplus.ingredient").withStyle(ChatFormatting.GOLD));
                    } else {
                        textComponents.addFirst(Component.translatable("tooltip.potionsplus.ingredient_a").withStyle(ChatFormatting.GOLD));
                        textComponents.addLast(Component.translatable("tooltip.potionsplus.ingredient_b").withStyle(ChatFormatting.GOLD));
                    }

                    tooltipMessages.add(textComponents);
                }
            }
        }
        return tooltipMessages;
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, ItemTooltipEvent event) {
        if (!PUtil.isSameItemOrPotion(lastItemStack, event.getItemStack(), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH)) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
            animationStartTimestamp = ClientTickHandler.total();
        }

        for (int i = 0; i < tooltipMessages.size(); i++) {
            List<Component> tooltipMessage = tooltipMessages.get(i);

            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = animateComponentText(tooltipMessage, durationUpgradeTextAnimationDurationTicks, delayTicks);
            if (animatedComponent.getSecond() > 0 || i < 2) { // First two components are vanilla tooltip components - don't remove
                event.getToolTip().add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = event.getItemStack();
    }

    private static Pair<MutableComponent, Integer> animateComponentText(List<Component> component, float duration, int delayTicks) {
        // Join all the components passed into one string, then split it at the appropriate index according to the animation progress
        String totalString = component.stream().map(Component::getString).collect(Collectors.joining());
        float f = RUtil.lerp(0.0F, 1.0F, RUtil.easeOutSine(Math.clamp((ClientTickHandler.total() - animationStartTimestamp - delayTicks) / duration, 0.0F, 1.0F)));
        f = Math.clamp(f, 0.0F, 1.0F);
        int splitIndex = Math.round(f * totalString.length());

        // Iterate over the components and split them at the appropriate index
        // Add any components with remaining text to our final list
        int index = 0;
        List<MutableComponent> components = new ArrayList<>();
        for (Component c : component) {
            String text = c.getString();
            if (index < splitIndex) {
                int splitIndexInComponent = Math.clamp(splitIndex - index, 0, text.length());
                String truncatedText = text.substring(0, splitIndexInComponent);
                if(!truncatedText.isEmpty()) {
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
}
