package grill24.potionsplus.utility;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerPlayerUtility
{
    @SubscribeEvent
    public static void onTossItemEvent(final ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(server, serverPlayer, event.getEntity().getItem(), ItemStack.EMPTY));
        }
    }
}
