package grill24.potionsplus.event;

import grill24.potionsplus.entity.Grungler;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class EntityListeners {
    @SubscribeEvent
    public static void onBreakBlock(final BlockDropsEvent event) {
        boolean cancel = Grungler.onBreakBlock(event.getState(), event.getDrops().stream().map(ItemEntity::getItem).toList(), event.getBreaker(), event.getPos());
        if (cancel) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(final LivingDeathEvent event) {
        Grungler.onEntityDeath(event.getEntity());
    }

    @SubscribeEvent
    public static void onItemToss(final ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.level().getServer();
            NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(server, serverPlayer, event.getEntity().getItem(), ItemStack.EMPTY));
        }
    }
}
