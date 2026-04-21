package grill24.potionsplus.event;

import grill24.potionsplus.entity.Grungler;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EntityListeners {
    @SubscribeEvent
    public static void onBreakBlock(final BlockDropsEvent event) {
        boolean cancel = Grungler.onBreakBlock(event.getState(), event.getDrops(), event.getBreaker(), event.getPos());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(final LivingDeathEvent event) {
        Grungler.onEntityDeath(event.getEntity());
    }
}
