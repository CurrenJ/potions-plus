package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ItemListeners {

    @SubscribeEvent
    public static void onToolTipEvent(final ItemTooltipEvent event) {
        if (event.getEntity() != null) {
            if (Utility.isPotionBrewingIngredientNoPotions(event.getItemStack()) && !Utility.isItemInLinkedAbyssalTrove(event.getEntity(), event.getItemStack())) {
                // JEI shall not reveal my secrets!!!
                if (Minecraft.getInstance().screen instanceof AbstractContainerScreen) {
                    MutableComponent text = Component.translatable("tooltip.potionsplus.ingredient");
                    event.getToolTip().add(text.withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }
}
