package grill24.potionsplus.event;

import grill24.potionsplus.utility.ClientItemStacksTooltip;
import grill24.potionsplus.utility.ItemStacksTooltip;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientTooltipComponentFactoriesListeners {
    @SubscribeEvent
    public static void on(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, (itemStacksTooltip) -> new ClientItemStacksTooltip(itemStacksTooltip.items()));
    }
}
