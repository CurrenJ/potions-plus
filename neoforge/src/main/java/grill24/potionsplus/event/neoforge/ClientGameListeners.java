package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class ClientGameListeners {
    @SubscribeEvent
    public static void renderTick(final RenderFrameEvent.Post event) {
        ClientTickHandler.renderTick(event.getPartialTick().getGameTimeDeltaPartialTick(true));
    }

    @SubscribeEvent
    public static void clientTickEnd(final ClientTickEvent.Post event) {
        ClientTickHandler.clientTickEnd();
    }
}
