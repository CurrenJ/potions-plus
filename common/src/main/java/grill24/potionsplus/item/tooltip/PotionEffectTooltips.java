package grill24.potionsplus.item.tooltip;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.effect.IEffectTooltipDetails;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PotionEffectTooltips {
    @SubscribeEvent
    public static void onPotionEffectTooltip(final AnimatedItemTooltipEvent.Add event) {
        // Potion Effect Details Tooltip
        PpIngredient ppIngredient = PpIngredient.of(event.getItemStack().copyWithCount(1));
        if (ppIngredient.getItemStack().has(DataComponents.POTION_CONTENTS)) {
            PotionContents potionContents = ppIngredient.getItemStack().get(DataComponents.POTION_CONTENTS);
            List<MobEffectInstance> potionEffectTextComponents = PUtil.getAllEffects(potionContents);
            for (MobEffectInstance effect : potionEffectTextComponents) {
                if (effect.getEffect().value() instanceof IEffectTooltipDetails effectTooltipDetails) {
                    event.addTooltipMessage(effectTooltipDetails.getTooltipDetails(effect));
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
                    text.add(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_IMBUED_WITH).withStyle(ChatFormatting.GOLD));
                    text.add(Component.translatable(effect.getEffect().value().getDescriptionId()).withStyle(ChatFormatting.LIGHT_PURPLE));
                    text.add(Component.literal(" (" + timeString + ")").withStyle(ChatFormatting.GRAY));

                    AnimatedItemTooltipEvent.TooltipLines tooltipLines = AnimatedItemTooltipEvent.TooltipLines.of(ppId("passive_effect"), TooltipPriorities.PASSIVE_ITEM_POTION_EFFECTS, text);
                    event.addTooltipMessage(tooltipLines);
                }
            }
        }
    }
}
