package grill24.potionsplus.event.runtimeresource;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.performance.SelectiveResourceReloadUtility;
import grill24.potionsplus.utility.performance.AdvancedResourceReloadOptimizer;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class TagUpdateListeners {
    @SubscribeEvent
    public static void onTagUpdate(final TagsUpdatedEvent event) {
        long startTime = System.currentTimeMillis();
        PotionsPlus.LOGGER.info("Processing tag update event: {}", event.getUpdateCause());
        
        RegistrationUtility.generateCommonRuntimeResourceMappings();
        
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
            // Start tracking resource modifications for selective reload optimization
            SelectiveResourceReloadUtility.startTracking();
            
            // Generate runtime resources and cache them
            ModLoader.postEvent(new GenerateRuntimeResourceInjectionsCacheEvent());
            
            // Log system information for debugging (only on first run)
            if (PotionsPlus.LOGGER.isDebugEnabled()) {
                AdvancedResourceReloadOptimizer.logReloadSystemInfo();
            }
            
            // Attempt optimized reload instead of full resource pack reload
            SelectiveResourceReloadUtility.performOptimizedReload()
                .thenRun(() -> {
                    long totalTime = System.currentTimeMillis() - startTime;
                    PotionsPlus.LOGGER.info("Tag update processing completed in {}ms", totalTime);
                    SelectiveResourceReloadUtility.logReloadStatistics();
                })
                .exceptionally(throwable -> {
                    PotionsPlus.LOGGER.error("Optimized reload failed, falling back to standard reload", throwable);
                    Minecraft.getInstance().reloadResourcePacks();
                    return null;
                });
        }
    }
}
