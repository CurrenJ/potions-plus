package grill24.potionsplus.event.runtimeresource;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ResourceListeners {
    @SubscribeEvent
    public static void generateRuntimeResourcesCache(final GenerateRuntimeResourceInjectionsCacheEvent event) {
        PotionsPlus.LOGGER.info("Generating runtime resource cache");
        long sysTime = System.currentTimeMillis();
        RegistrationUtility.generateRuntimeResourceInjectionsCache(event);
        PotionsPlus.LOGGER.info("Done generating runtime resource cache" + " in " + (System.currentTimeMillis() - sysTime) + "ms");
    }

    @SubscribeEvent
    public static void onResourcesReloaded(final ClientInjectResourcesEvent event) {
        for (GenerateRuntimeResourceInjectionsCacheEvent.ResourceInjectionCacheEntry entry : GenerateRuntimeResourceInjectionsCacheEvent.RESOURCE_INJECTION_CACHE) {
            ResourceLocation targetResourceLocation = entry.targetResourceLocation();
            ResourceLocation newResourceLocation = entry.newResourceLocation();
            Map<ResourceLocation, Resource> resources = event.getResources();

            if (resources.containsKey(targetResourceLocation)) {
                resources.put(newResourceLocation, entry.resource().getFirst());
            }
        }
    }

    @SubscribeEvent
    public static void onResourceStackReloaded(final ClientInjectResourceStacksEvent event) {
        for (GenerateRuntimeResourceInjectionsCacheEvent.ResourceInjectionCacheEntry entry : GenerateRuntimeResourceInjectionsCacheEvent.RESOURCE_STACKS_INJECTION_CACHE) {
            ResourceLocation targetResourceLocation = entry.targetResourceLocation();
            ResourceLocation newResourceLocation = entry.newResourceLocation();
            Map<ResourceLocation, List<Resource>> resources = event.getResources();

            if (resources.containsKey(targetResourceLocation)) {
                resources.put(newResourceLocation, entry.resource());
            }
        }
    }
}
