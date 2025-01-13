package grill24.potionsplus.event;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.effect.IEffectTooltipDetails;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import grill24.potionsplus.utility.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.Collections;
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
        // Tooltip messages
        List<List<Component>> tooltipMessages = new ArrayList<>();
        if (event.getEntity() != null) {
            PpIngredient ppIngredient = PpIngredient.of(event.getItemStack().copyWithCount(1));
            if(AbyssalTroveBlockEntity.ABYSSAL_TROVE_INGREDIENTS.contains(PpIngredient.of(event.getItemStack())))
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


                    // Item rarity text
                    List<Component> textComponents = new ArrayList<>();
                    if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, ppIngredient)) {
                        textComponents.add(Component.translatable("tooltip.potionsplus.common").withStyle(ChatFormatting.WHITE));
                    } else if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, ppIngredient)) {
                        textComponents.add(Component.translatable("tooltip.potionsplus.rare").withStyle(ChatFormatting.LIGHT_PURPLE));
                    }
                    // Build text components around the ingredient rarity
                    if (textComponents.isEmpty()) {
                        textComponents.add(Component.translatable("tooltip.potionsplus.ingredient").withStyle(ChatFormatting.GOLD));
                    } else {
                        textComponents.addFirst(Component.translatable("tooltip.potionsplus.ingredient_a").withStyle(ChatFormatting.GOLD));
                        textComponents.addLast(Component.translatable("tooltip.potionsplus.ingredient_b").withStyle(ChatFormatting.GOLD));
                    }

                    if (!textComponents.isEmpty()) {
                        tooltipMessages.add(textComponents);
                    }


                    // Recipe text
                    if (Recipes.ALL_BCR_RECIPES_ANALYSIS.isIngredientUsed(ppIngredient)) {
                        // Look at all the recipes that this ingredient is used in
                        for (RecipeHolder<BrewingCauldronRecipe> recipeHolder : Recipes.ALL_BCR_RECIPES_ANALYSIS.getRecipesForIngredient(ppIngredient)) {
                            if (SavedData.instance.getData(event.getEntity()).isRecipeUnknown(recipeHolder.id().toString()))
                                continue;

                            BrewingCauldronRecipe recipe = recipeHolder.value();
                            ItemStack recipeResult = recipe.getResult();
                            if (!PUtil.isPotion(recipeResult) || !PUtil.hasPotion(recipeResult)
                                    || Potions.ANY_POTION.is(PUtil.getPotionHolder(recipeResult).getKey()) || Potions.ANY_OTHER_POTION.is(PUtil.getPotionHolder(recipeResult).getKey())
                                    || !recipe.canShowInJei())
                                continue;
                            List<MobEffectInstance> potionEffects = PUtil.getPotion(recipeResult).getEffects();
                            String effectName = !potionEffects.isEmpty() ? PUtil.getPotion(recipeResult).getEffects().get(0).getDescriptionId() : "";

                            if (!effectName.isEmpty()) {
                                long totalNonPotionIngredients = recipe.getIngredientsAsItemStacks().stream().filter(i -> !PUtil.isPotion(i)).count();
                                List<Component> recipeTextComponents = new ArrayList<>();
                                recipeTextComponents.add(Component.literal("1").withStyle(ChatFormatting.GREEN));
                                recipeTextComponents.add(Component.literal(" / ").withStyle(ChatFormatting.GRAY));
                                recipeTextComponents.add(Component.literal(String.valueOf(totalNonPotionIngredients)).withStyle(ChatFormatting.DARK_GREEN));
                                recipeTextComponents.add(Component.literal(" "));
                                recipeTextComponents.add(Component.translatable("tooltip.potionsplus.of").withStyle(ChatFormatting.GRAY));
                                recipeTextComponents.add(Component.literal(" "));
                                recipeTextComponents.add(Component.translatable(effectName).withStyle(ChatFormatting.LIGHT_PURPLE));
                                tooltipMessages.add(recipeTextComponents);
                            }
                        }
                    }
                }
            }

            // Potion Effect Details Tooltip
            if (ppIngredient.getItemStack().has(DataComponents.POTION_CONTENTS)) {
                PotionContents potionContents = ppIngredient.getItemStack().get(DataComponents.POTION_CONTENTS);
                List<MobEffectInstance> potionEffectTextComponents = PUtil.getAllEffects(potionContents);
                for (MobEffectInstance effect : potionEffectTextComponents) {
                    if(effect.getEffect().value() instanceof IEffectTooltipDetails effectTooltipDetails) {
                        tooltipMessages.add(effectTooltipDetails.getTooltipDetails(effect));
                    }
                }
            }

            // Passive Item Potion Effects Tooltip
            ItemStack stack = event.getItemStack();
            if (PUtil.isPassivePotionEffectItem(stack)) {
                PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
                List<MobEffectInstance> potionEffectTextComponents = PUtil.getAllEffects(potionContents);
                for (MobEffectInstance effect : potionEffectTextComponents) {
                    int ticksLeft = effect.getDuration();
                    if (ticksLeft > 0) {
                        int minutesLeft = (int) Math.floor(ticksLeft / 1200F);
                        int secondsLeft = (int) Math.floor(ticksLeft / 20F) % 60;
                        String timeString = String.format("%02d:%02d", minutesLeft, secondsLeft);

                        List<Component> text = new ArrayList<>();
                        text.add(Component.translatable("tooltip.potionsplus.imbued_with").withStyle(ChatFormatting.GOLD));
                        text.add(Component.translatable(effect.getEffect().value().getDescriptionId()).withStyle(ChatFormatting.LIGHT_PURPLE));
                        text.add(Component.literal(" (" + timeString + ")").withStyle(ChatFormatting.GRAY));

                        tooltipMessages.add(text);
                    }
                }
            }

            // Choice Reward Item Tooltip
            if (stack.has(grill24.potionsplus.core.DataComponents.CHOICE_ITEM_DATA)) {
                EdibleRewardGranterDataComponent choiceItemData = stack.get(grill24.potionsplus.core.DataComponents.CHOICE_ITEM_DATA);
                grill24.potionsplus.skill.reward.ConfiguredGrantableReward<?, ?> linkedOption = choiceItemData.linkedOption().value();

                tooltipMessages.add(List.of(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_CHOICE).withStyle(ChatFormatting.GOLD)));
                if (Items.BASIC_LOOT_MODEL.getOverrideValue(choiceItemData.flag()) > 0) {
                    tooltipMessages.add(List.of(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_CHOOSE_ONE).withStyle(ChatFormatting.GRAY)));
                }
                List<List<Component>> component = linkedOption.getMultiLineRichDescription();
                if (component != null) {
                    tooltipMessages.addAll(component);
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
            Pair<MutableComponent, Integer> animatedComponent = animateComponentText(tooltipMessage, durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            if (animatedComponent.getSecond() > 0 || i < 2) { // First two components are vanilla tooltip components - don't remove
                event.getToolTip().add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = event.getItemStack();
    }

    public static List<Component> animateComponentText(List<List<Component>> components, float animationStartTimestamp) {
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
