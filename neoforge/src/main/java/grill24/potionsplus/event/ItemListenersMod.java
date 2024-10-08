package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.PotionItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ItemListenersMod {
    @SubscribeEvent
    private static void modifyDefaultComponents(final ModifyDefaultComponentsEvent event) {
        event.modifyMatching((item) -> item instanceof PotionItem, (item) -> {
            item.set(DataComponents.MAX_STACK_SIZE, 16);
        });
    }
}
