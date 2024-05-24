package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemListeners {

    @SubscribeEvent
    public static void onToolTipEvent(final ItemTooltipEvent event) {
        if (event.getPlayer() != null) {
            if (Utility.isItemPotionsPlusIngredient(event.getItemStack()) && !Utility.isItemInLinkedAbyssalTrove(event.getPlayer(), event.getItemStack())) {
                // JEI shall not reveal my secrets!!!
                if (Minecraft.getInstance().screen instanceof AbstractContainerScreen) {
                    TranslatableComponent text = new TranslatableComponent("tooltip.potionsplus.ingredient");
                    event.getToolTip().add(text.withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }
}