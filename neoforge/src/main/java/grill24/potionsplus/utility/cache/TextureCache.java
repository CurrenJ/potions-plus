package grill24.potionsplus.utility.cache;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multi-level cache for texture operations to improve runtime texture variant performance.
 * Provides thread-safe caching for:
 * - BufferedImage conversion from Resource objects
 * - PNG byte arrays for FakePngResource
 * - Overlay result caching for common texture combinations
 */
public class TextureCache {
    // L1 Cache: Resource -> BufferedImage conversions
    private static final ConcurrentHashMap<ResourceCacheKey, BufferedImage> BUFFERED_IMAGE_CACHE = new ConcurrentHashMap<>();
    
    // L2 Cache: BufferedImage -> PNG byte arrays  
    private static final ConcurrentHashMap<ImageCacheKey, byte[]> PNG_BYTES_CACHE = new ConcurrentHashMap<>();
    
    // L3 Cache: Overlay combinations -> final BufferedImage
    private static final ConcurrentHashMap<OverlayCacheKey, BufferedImage> OVERLAY_CACHE = new ConcurrentHashMap<>();
    
    // Cache statistics for monitoring
    private static long bufferedImageHits = 0;
    private static long bufferedImageMisses = 0;
    private static long pngBytesHits = 0;
    private static long pngBytesMisses = 0;
    private static long overlayHits = 0;
    private static long overlayMisses = 0;
    
    /**
     * Gets a BufferedImage from cache or creates it from the Resource.
     */
    public static BufferedImage getOrCreateBufferedImage(ResourceLocation location, Resource resource, BufferedImageCreator creator) {
        ResourceCacheKey key = new ResourceCacheKey(location, resource.sourcePackId());
        BufferedImage cached = BUFFERED_IMAGE_CACHE.get(key);
        
        if (cached != null) {
            bufferedImageHits++;
            return cached;
        }
        
        bufferedImageMisses++;
        BufferedImage image = creator.create(resource);
        if (image != null) {
            BUFFERED_IMAGE_CACHE.put(key, image);
        }
        return image;
    }
    
    /**
     * Gets PNG byte array from cache or creates it from the BufferedImage.
     */
    public static byte[] getOrCreatePngBytes(BufferedImage image, PngBytesCreator creator) {
        ImageCacheKey key = new ImageCacheKey(image);
        byte[] cached = PNG_BYTES_CACHE.get(key);
        
        if (cached != null) {
            pngBytesHits++;
            return cached;
        }
        
        pngBytesMisses++;
        byte[] bytes = creator.create(image);
        if (bytes != null) {
            PNG_BYTES_CACHE.put(key, bytes);
        }
        return bytes;
    }
    
    /**
     * Gets overlay result from cache or creates it by blending images.
     */
    public static BufferedImage getOrCreateOverlayResult(ResourceLocation baseLocation, OverlaySpec[] overlays, OverlayCreator creator) {
        OverlayCacheKey key = new OverlayCacheKey(baseLocation, overlays);
        BufferedImage cached = OVERLAY_CACHE.get(key);
        
        if (cached != null) {
            overlayHits++;
            return cached;
        }
        
        overlayMisses++;
        BufferedImage result = creator.create();
        if (result != null) {
            OVERLAY_CACHE.put(key, result);
        }
        return result;
    }
    
    /**
     * Clears all caches. Called when resource packs are reloaded.
     */
    public static void clearAll() {
        long totalEntries = BUFFERED_IMAGE_CACHE.size() + PNG_BYTES_CACHE.size() + OVERLAY_CACHE.size();
        BUFFERED_IMAGE_CACHE.clear();
        PNG_BYTES_CACHE.clear();
        OVERLAY_CACHE.clear();
        
        PotionsPlus.LOGGER.info("TextureCache cleared {} total entries", totalEntries);
        logStatistics();
        resetStatistics();
    }
    
    /**
     * Logs cache performance statistics.
     */
    public static void logStatistics() {
        long totalBufferedRequests = bufferedImageHits + bufferedImageMisses;
        long totalPngRequests = pngBytesHits + pngBytesMisses;
        long totalOverlayRequests = overlayHits + overlayMisses;
        
        if (totalBufferedRequests > 0) {
            double bufferedHitRate = (double) bufferedImageHits / totalBufferedRequests * 100;
            PotionsPlus.LOGGER.info("BufferedImage Cache: {} hits, {} misses, {:.1f}% hit rate", 
                bufferedImageHits, bufferedImageMisses, bufferedHitRate);
        }
        
        if (totalPngRequests > 0) {
            double pngHitRate = (double) pngBytesHits / totalPngRequests * 100;
            PotionsPlus.LOGGER.info("PNG Bytes Cache: {} hits, {} misses, {:.1f}% hit rate", 
                pngBytesHits, pngBytesMisses, pngHitRate);
        }
        
        if (totalOverlayRequests > 0) {
            double overlayHitRate = (double) overlayHits / totalOverlayRequests * 100;
            PotionsPlus.LOGGER.info("Overlay Cache: {} hits, {} misses, {:.1f}% hit rate", 
                overlayHits, overlayMisses, overlayHitRate);
        }
    }
    
    private static void resetStatistics() {
        bufferedImageHits = bufferedImageMisses = 0;
        pngBytesHits = pngBytesMisses = 0;
        overlayHits = overlayMisses = 0;
    }
    
    // Cache key classes
    private record ResourceCacheKey(ResourceLocation location, String sourcePackId) {}
    
    private static class ImageCacheKey {
        private final int hashCode;
        
        public ImageCacheKey(BufferedImage image) {
            // Create hash based on image properties and a sample of pixels
            int hash = Objects.hash(image.getWidth(), image.getHeight(), image.getType());
            
            // Sample a few pixels for more specific hashing without full pixel comparison
            int width = image.getWidth();
            int height = image.getHeight();
            if (width > 0 && height > 0) {
                hash = hash * 31 + image.getRGB(0, 0);
                if (width > 1 && height > 1) {
                    hash = hash * 31 + image.getRGB(width - 1, height - 1);
                }
                if (width > 2 && height > 2) {
                    hash = hash * 31 + image.getRGB(width / 2, height / 2);
                }
            }
            
            this.hashCode = hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ImageCacheKey other)) return false;
            return hashCode == other.hashCode;
        }
        
        @Override
        public int hashCode() {
            return hashCode;
        }
    }
    
    private record OverlayCacheKey(ResourceLocation baseLocation, OverlaySpec[] overlays) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof OverlayCacheKey other)) return false;
            return Objects.equals(baseLocation, other.baseLocation) && 
                   java.util.Arrays.equals(overlays, other.overlays);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(baseLocation, java.util.Arrays.hashCode(overlays));
        }
    }
    
    // Specification for overlay operations
    public record OverlaySpec(ResourceLocation overlayLocation, String blendMode) {}
    
    // Functional interfaces for cache operations
    @FunctionalInterface
    public interface BufferedImageCreator {
        BufferedImage create(Resource resource);
    }
    
    @FunctionalInterface
    public interface PngBytesCreator {
        byte[] create(BufferedImage image);
    }
    
    @FunctionalInterface
    public interface OverlayCreator {
        BufferedImage create();
    }
}