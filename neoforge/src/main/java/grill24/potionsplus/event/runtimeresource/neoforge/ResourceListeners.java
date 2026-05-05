package grill24.potionsplus.event.runtimeresource.neoforge;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
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
            Identifier targetResourceLocation = entry.targetResourceLocation();
            Identifier newResourceLocation = entry.newResourceLocation();
            Map<Identifier, Resource> resources = event.getResources();

            if (resources.containsKey(targetResourceLocation)) {
                resources.put(newResourceLocation, entry.resource().getFirst());
            }
        }
    }

    @SubscribeEvent
    public static void onResourceStackReloaded(final ClientInjectResourceStacksEvent event) {
        for (GenerateRuntimeResourceInjectionsCacheEvent.ResourceInjectionCacheEntry entry : GenerateRuntimeResourceInjectionsCacheEvent.RESOURCE_STACKS_INJECTION_CACHE) {
            Identifier targetResourceLocation = entry.targetResourceLocation();
            Identifier newResourceLocation = entry.newResourceLocation();
            Map<Identifier, List<Resource>> resources = event.getResources();

            if (resources.containsKey(targetResourceLocation)) {
                resources.put(newResourceLocation, entry.resource());
            }
        }
    }
}
