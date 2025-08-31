package grill24.potionsplus.utility.cache;

import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for ResourceLocation objects to avoid repeated string parsing and object creation.
 * ResourceLocation creation involves string parsing and validation which can be expensive
 * when done frequently during texture variant generation.
 */
public class ResourceLocationCache {
    private static final ConcurrentHashMap<String, ResourceLocation> LOCATION_CACHE = new ConcurrentHashMap<>();
    
    // Statistics
    private static long hits = 0;
    private static long misses = 0;
    
    /**
     * Gets or creates a ResourceLocation from namespace and path with caching.
     */
    public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        String key = namespace + ":" + path;
        ResourceLocation cached = LOCATION_CACHE.get(key);
        
        if (cached != null) {
            hits++;
            return cached;
        }
        
        misses++;
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, path);
        LOCATION_CACHE.put(key, location);
        return location;
    }
    
    /**
     * Gets or creates a ResourceLocation from a full string with caching.
     */
    public static ResourceLocation parse(String locationString) {
        ResourceLocation cached = LOCATION_CACHE.get(locationString);
        
        if (cached != null) {
            hits++;
            return cached;
        }
        
        misses++;
        ResourceLocation location = ResourceLocation.parse(locationString);
        LOCATION_CACHE.put(locationString, location);
        return location;
    }
    
    /**
     * Clears the cache. Should be called when resource packs are reloaded.
     */
    public static void clear() {
        int size = LOCATION_CACHE.size();
        LOCATION_CACHE.clear();
        if (size > 0) {
            logStatistics();
            hits = misses = 0;
        }
    }
    
    /**
     * Logs cache performance statistics.
     */
    public static void logStatistics() {
        long totalRequests = hits + misses;
        if (totalRequests > 0) {
            double hitRate = (double) hits / totalRequests * 100;
            System.out.printf("ResourceLocation Cache: %d hits, %d misses, %.1f%% hit rate%n", 
                hits, misses, hitRate);
        }
    }
}