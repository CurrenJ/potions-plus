package grill24.potionsplus.utility.performance;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Advanced resource reload optimization that attempts to use Minecraft's internal APIs
 * for selective reloading of specific resource types.
 */
public class AdvancedResourceReloadOptimizer {
    
    private static boolean isReflectionAvailable = false;
    private static Method reloadMethod;
    private static Field listenersField;
    
    static {
        initializeReflection();
    }
    
    private static void initializeReflection() {
        try {
            // Try to get access to ReloadableResourceManager's internal methods
            Class<?> reloadableManagerClass = ReloadableResourceManager.class;
            
            // Look for reload methods that accept specific listeners
            Method[] methods = reloadableManagerClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().contains("reload") && method.getParameterCount() > 0) {
                    PotionsPlus.LOGGER.debug("Found potential reload method: {}", method.getName());
                }
            }
            
            // Try to access the listeners field to see what reload listeners are registered
            Field[] fields = reloadableManagerClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().toString().contains("List") || field.getType().toString().contains("listener")) {
                    PotionsPlus.LOGGER.debug("Found potential listeners field: {}", field.getName());
                    field.setAccessible(true);
                    listenersField = field;
                    break;
                }
            }
            
            isReflectionAvailable = true;
            PotionsPlus.LOGGER.info("Reflection-based resource reload optimization initialized");
            
        } catch (Exception e) {
            PotionsPlus.LOGGER.warn("Could not initialize reflection-based optimization: {}", e.getMessage());
            isReflectionAvailable = false;
        }
    }
    
    /**
     * Attempts to perform a targeted reload of specific resource managers.
     * This is experimental and may not work in all Minecraft versions.
     */
    public static CompletableFuture<Void> attemptTargetedReload(Set<ResourceLocation> modifiedResources) {
        if (!isReflectionAvailable) {
            PotionsPlus.LOGGER.info("Reflection not available, falling back to full reload");
            return performStandardReload();
        }
        
        try {
            return performExperimentalTargetedReload(modifiedResources);
        } catch (Exception e) {
            PotionsPlus.LOGGER.error("Experimental targeted reload failed", e);
            return performStandardReload();
        }
    }
    
    private static CompletableFuture<Void> performExperimentalTargetedReload(Set<ResourceLocation> modifiedResources) {
        PotionsPlus.LOGGER.info("Attempting experimental targeted reload for {} resources", modifiedResources.size());
        
        Minecraft mc = Minecraft.getInstance();
        ResourceManager resourceManager = mc.getResourceManager();
        
        if (!(resourceManager instanceof ReloadableResourceManager reloadableManager)) {
            PotionsPlus.LOGGER.warn("Resource manager is not reloadable, falling back to standard reload");
            return performStandardReload();
        }
        
        // Analyze what types of reload listeners we need to trigger
        ReloadTarget target = analyzeReloadTarget(modifiedResources);
        
        return switch (target) {
            case MODELS_AND_BLOCKSTATES -> reloadModelsAndBlockstates(reloadableManager);
            case TEXTURES_ONLY -> reloadTexturesOnly(reloadableManager);
            case SELECTIVE_LISTENERS -> reloadSelectiveListeners(reloadableManager, modifiedResources);
            case FULL_RELOAD -> performStandardReload();
        };
    }
    
    private enum ReloadTarget {
        MODELS_AND_BLOCKSTATES,
        TEXTURES_ONLY,
        SELECTIVE_LISTENERS,
        FULL_RELOAD
    }
    
    private static ReloadTarget analyzeReloadTarget(Set<ResourceLocation> modifiedResources) {
        boolean hasTextures = false;
        boolean hasModels = false;
        boolean hasBlockstates = false;
        boolean hasOtherResources = false;
        
        for (ResourceLocation resource : modifiedResources) {
            String path = resource.getPath();
            if (path.startsWith("textures/")) {
                hasTextures = true;
            } else if (path.startsWith("models/")) {
                hasModels = true;
            } else if (path.startsWith("blockstates/")) {
                hasBlockstates = true;
            } else {
                hasOtherResources = true;
            }
        }
        
        PotionsPlus.LOGGER.info("Reload analysis: textures={}, models={}, blockstates={}, other={}", 
            hasTextures, hasModels, hasBlockstates, hasOtherResources);
        
        if (hasOtherResources) {
            return ReloadTarget.FULL_RELOAD;
        }
        
        if (hasTextures && !hasModels && !hasBlockstates) {
            return ReloadTarget.TEXTURES_ONLY;
        }
        
        if ((hasModels || hasBlockstates) && !hasTextures) {
            return ReloadTarget.MODELS_AND_BLOCKSTATES;
        }
        
        return ReloadTarget.SELECTIVE_LISTENERS;
    }
    
    private static CompletableFuture<Void> reloadModelsAndBlockstates(ReloadableResourceManager manager) {
        PotionsPlus.LOGGER.info("Attempting models and blockstates reload");
        
        try {
            // Try to reload only the ModelManager
            Minecraft mc = Minecraft.getInstance();
            ModelManager modelManager = mc.getModelManager();
            
            // Models and blockstates are tightly coupled, so we need both
            // Unfortunately, there's no clean way to reload just these without reflection
            
            return performStandardReload();
            
        } catch (Exception e) {
            PotionsPlus.LOGGER.error("Failed to reload models and blockstates selectively", e);
            return performStandardReload();
        }
    }
    
    private static CompletableFuture<Void> reloadTexturesOnly(ReloadableResourceManager manager) {
        PotionsPlus.LOGGER.info("Attempting textures-only reload");
        
        try {
            // Texture reloading is very complex because it affects atlases
            // and requires coordination with the ModelManager
            
            return performStandardReload();
            
        } catch (Exception e) {
            PotionsPlus.LOGGER.error("Failed to reload textures selectively", e);
            return performStandardReload();
        }
    }
    
    private static CompletableFuture<Void> reloadSelectiveListeners(ReloadableResourceManager manager, Set<ResourceLocation> modifiedResources) {
        PotionsPlus.LOGGER.info("Attempting selective listeners reload");
        
        try {
            if (listenersField == null) {
                PotionsPlus.LOGGER.warn("Cannot access listeners field, falling back to standard reload");
                return performStandardReload();
            }
            
            // Try to get the list of reload listeners
            Object listenersObj = listenersField.get(manager);
            if (listenersObj instanceof List<?> listeners) {
                PotionsPlus.LOGGER.info("Found {} reload listeners", listeners.size());
                
                // Log the types of listeners for debugging
                for (Object listener : listeners) {
                    PotionsPlus.LOGGER.debug("Reload listener: {}", listener.getClass().getSimpleName());
                }
                
                // For now, we don't have a safe way to reload individual listeners
                // This would require deep knowledge of Minecraft's internal dependencies
                return performStandardReload();
            }
            
            return performStandardReload();
            
        } catch (Exception e) {
            PotionsPlus.LOGGER.error("Failed to perform selective listeners reload", e);
            return performStandardReload();
        }
    }
    
    private static CompletableFuture<Void> performStandardReload() {
        PotionsPlus.LOGGER.info("Performing standard resource pack reload");
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.runAsync(() -> {
            Minecraft.getInstance().reloadResourcePacks();
        }).thenRun(() -> {
            long duration = System.currentTimeMillis() - startTime;
            PotionsPlus.LOGGER.info("Standard resource reload completed in {}ms", duration);
        });
    }
    
    /**
     * Creates a custom reload listener that only processes specific resource types.
     * This is a more targeted approach than full resource reloading.
     */
    public static PreparableReloadListener createSelectiveReloadListener(Set<ResourceLocation> targetResources) {
        return new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                PotionsPlus.LOGGER.info("Preparing selective reload for {} resources", targetResources.size());
                
                for (ResourceLocation resource : targetResources) {
                    if (resourceManager.getResource(resource).isPresent()) {
                        PotionsPlus.LOGGER.debug("Found target resource for selective reload: {}", resource);
                    }
                }
                
                return null;
            }
            
            @Override
            protected void apply(Void prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
                PotionsPlus.LOGGER.info("Applying selective reload");
                
                // This is where we would implement the selective reload logic
                // For now, this serves as a framework for future implementation
            }
        };
    }
    
    /**
     * Checks if advanced reload optimization is available.
     */
    public static boolean isAdvancedOptimizationAvailable() {
        return isReflectionAvailable;
    }
    
    /**
     * Logs debug information about the resource reload system.
     */
    public static void logReloadSystemInfo() {
        try {
            Minecraft mc = Minecraft.getInstance();
            ResourceManager resourceManager = mc.getResourceManager();
            
            PotionsPlus.LOGGER.info("Resource Manager Type: {}", resourceManager.getClass().getSimpleName());
            PotionsPlus.LOGGER.info("Reflection Available: {}", isReflectionAvailable);
            
            if (resourceManager instanceof ReloadableResourceManager reloadable && listenersField != null) {
                try {
                    Object listeners = listenersField.get(reloadable);
                    if (listeners instanceof List<?> list) {
                        PotionsPlus.LOGGER.info("Registered Reload Listeners: {}", list.size());
                        for (int i = 0; i < Math.min(list.size(), 10); i++) {
                            PotionsPlus.LOGGER.info("  [{}] {}", i, list.get(i).getClass().getSimpleName());
                        }
                    }
                } catch (Exception e) {
                    PotionsPlus.LOGGER.debug("Could not access listeners: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            PotionsPlus.LOGGER.error("Failed to log reload system info", e);
        }
    }
}