package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
