package grill24.potionsplus.item.tooltip;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BrewingTooltips {
    @SubscribeEvent
    public static void onBrewingTooltip(final AnimatedItemTooltipEvent.Add event) {
        PpIngredient ppIngredient = PpIngredient.of(event.getItemStack().copyWithCount(1));
        if(AbyssalTroveBlockEntity.ABYSSAL_TROVE_INGREDIENTS.contains(ppIngredient))
        {
            // If the item is not in the abyssal trove, display a message indicating that the ingredient is unknown
            if (!Utility.isItemInLinkedAbyssalTrove(event.getPlayer(), event.getItemStack())) {
                MutableComponent text = Component.translatable("tooltip.potionsplus.unknown_ingredient");
                text = text.withStyle(ChatFormatting.GRAY);

                AnimatedItemTooltipEvent.TooltipLines unknownIngredientLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("unknown_ingredient"), 0, text);
                event.addTooltipMessage(unknownIngredientLine);
            }
            // If the item is in the abyssal trove, display the appropriate tooltip messages for the ingredient
            else {
                if (Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(ppIngredient)) {
                    MutableComponent text = Component.translatable("tooltip.potionsplus.duration_ingredient", Utility.formatTicksAsSeconds(Recipes.DURATION_UPGRADE_ANALYSIS.getRecipeForIngredient(ppIngredient).get().value().getDurationToAdd()));
                    text = text.withStyle(ChatFormatting.LIGHT_PURPLE);

                    AnimatedItemTooltipEvent.TooltipLines durationLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("ingredient_duration"), 0, text);
                    event.addTooltipMessage(durationLine);
                }
                if (Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(ppIngredient)) {
                    MutableComponent text = Component.translatable("tooltip.potionsplus.amplification_ingredient", Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.getRecipeForIngredient(ppIngredient).get().value().getAmplifierToAdd());
                    text = text.withStyle(ChatFormatting.AQUA);

                    AnimatedItemTooltipEvent.TooltipLines amplificationLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("ingredient_amplification"), 0, text);
                    event.addTooltipMessage(amplificationLine);
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
                    AnimatedItemTooltipEvent.TooltipLines ingredientRarityLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("ingredient_rarity"), 0, textComponents);
                    event.addTooltipMessage(ingredientRarityLine);
                }


                // Recipe text
                Player player = event.getPlayer();
                if (SavedData.instance.getData(player).abyssalTroveContainsIngredient(player.level(), ppIngredient)) {
                    // Look at all the recipes that this ingredient is used in
                    for (RecipeHolder<BrewingCauldronRecipe> recipeHolder : Recipes.ALL_BCR_RECIPES_ANALYSIS.getRecipesForIngredient(ppIngredient)) {
                        if (SavedData.instance.getData(player).isRecipeUnknown(recipeHolder.id()))
                            continue;

                        BrewingCauldronRecipe recipe = recipeHolder.value();
                        ItemStack recipeResult = recipe.getResult();
                        if (!PUtil.isPotion(recipeResult) || !PUtil.hasPotion(recipeResult)
                                || Potions.ANY_POTION.is(PUtil.getPotionHolder(recipeResult).getKey()) || Potions.ANY_OTHER_POTION.is(PUtil.getPotionHolder(recipeResult).getKey())
                                || !recipe.canShowInJei())
                            continue;
                        List<MobEffectInstance> potionEffects = PUtil.getPotion(recipeResult).getEffects();
                        String effectName = !potionEffects.isEmpty() ? PUtil.getPotion(recipeResult).getEffects().getFirst().getDescriptionId() : "";

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

                            AnimatedItemTooltipEvent.TooltipLines recipeLine = AnimatedItemTooltipEvent.TooltipLines.of(ppId("brewing_cauldron_recipe"), TooltipPriorities.BREWING_INFO, recipeTextComponents);
                            event.addTooltipMessage(recipeLine);
                        }
                    }
                }
            }
        }
    }
}
