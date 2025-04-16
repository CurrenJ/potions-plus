package grill24.potionsplus.event.runtimeresource;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TagUpdateListeners {
    @SubscribeEvent
    public static void onTagUpdate(final TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
            ModLoader.postEvent(new GenerateRuntimeResourceInjectionsCacheEvent());
            Minecraft.getInstance().reloadResourcePacks();
        }
    }
}
