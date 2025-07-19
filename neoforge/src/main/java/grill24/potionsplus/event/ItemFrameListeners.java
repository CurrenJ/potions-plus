package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ItemFrameListeners {
    @SubscribeEvent
    public static void onItemInFrameRender(final RenderItemInFrameEvent event) {
        //TODO: Fix fish scale rendering in item frames
//        ItemStack itemStack = event;
//        if (itemStack.has(DataComponents.FISH_SIZE)) {
//            float scale = itemStack.get(DataComponents.FISH_SIZE).getItemFrameSizeMultiplier();
//            event.getPoseStack().scale(scale, scale, scale);
//        }
    }
}
