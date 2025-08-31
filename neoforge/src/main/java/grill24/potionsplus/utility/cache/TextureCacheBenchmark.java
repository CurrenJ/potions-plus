package grill24.potionsplus.utility.cache;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.FakePngResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple benchmark utility to demonstrate caching performance improvements.
 * Can be called from game commands for real-world performance testing.
 */
public class TextureCacheBenchmark {
    
    /**
     * Benchmarks FakePngResource performance with and without caching.
     */
    public static void benchmarkFakePngResource() {
        PotionsPlus.LOGGER.info("Starting FakePngResource benchmark...");
        
        // Create a test image
        BufferedImage testImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                testImage.setRGB(x, y, (x + y) % 2 == 0 ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        
        // Mock resource for testing
        Resource mockResource = new MockResource();
        
        // Clear cache to start fresh
        TextureCache.clearAll();
        
        // Benchmark with cache (first creates, rest use cache)
        int iterations = 100;
        long totalTime = 0;
        
        for (int i = 0; i < iterations; i++) {
            FakePngResource resource = new FakePngResource(mockResource, testImage);
            
            long start = System.nanoTime();
            try (InputStream stream = resource.open()) {
                // Read the stream to actually use it
                byte[] bytes = stream.readAllBytes();
                // Ensure we actually read something
                if (bytes.length == 0) {
                    PotionsPlus.LOGGER.warn("Empty stream in benchmark iteration {}", i);
                }
            } catch (IOException e) {
                PotionsPlus.LOGGER.error("Error in benchmark iteration {}", i, e);
            }
            long end = System.nanoTime();
            
            totalTime += (end - start);
        }
        
        double avgTimeMs = totalTime / (double) iterations / 1_000_000;
        PotionsPlus.LOGGER.info("FakePngResource benchmark completed: {} iterations, avg time: {:.3f} ms per operation", 
            iterations, avgTimeMs);
        
        // Log cache statistics
        TextureCache.logStatistics();
    }
    
    /**
     * Benchmarks texture overlay operations to demonstrate caching benefits.
     */
    public static void benchmarkOverlayOperations() {
        PotionsPlus.LOGGER.info("Starting overlay operations benchmark...");
        
        ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath("potionsplus", "textures/item/unknown.png");
        TextureCache.OverlaySpec[] overlays = {
            new TextureCache.OverlaySpec(ResourceLocation.fromNamespaceAndPath("potionsplus", "textures/block/test1.png"), "DEFAULT"),
            new TextureCache.OverlaySpec(ResourceLocation.fromNamespaceAndPath("potionsplus", "textures/block/test2.png"), "MULTIPLY")
        };
        
        // Clear cache
        TextureCache.clearAll();
        
        int iterations = 50;
        long totalTime = 0;
        
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            
            BufferedImage result = TextureCache.getOrCreateOverlayResult(baseTexture, overlays, () -> {
                // Simulate overlay operation
                BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                // Fill with test pattern
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        img.setRGB(x, y, 0xFF000000 | (x * 16 + y));
                    }
                }
                return img;
            });
            
            long end = System.nanoTime();
            totalTime += (end - start);
            
            if (result == null) {
                PotionsPlus.LOGGER.warn("Null result in overlay benchmark iteration {}", i);
            }
        }
        
        double avgTimeMs = totalTime / (double) iterations / 1_000_000;
        PotionsPlus.LOGGER.info("Overlay operations benchmark completed: {} iterations, avg time: {:.3f} ms per operation", 
            iterations, avgTimeMs);
        
        // Log cache statistics
        TextureCache.logStatistics();
    }
    
    /**
     * Runs all benchmarks.
     */
    public static void runAllBenchmarks() {
        PotionsPlus.LOGGER.info("=== TextureCache Performance Benchmarks ===");
        benchmarkFakePngResource();
        benchmarkOverlayOperations();
        PotionsPlus.LOGGER.info("=== Benchmarks Complete ===");
    }
    
    // Mock resource class for testing
    private static class MockResource extends Resource {
        public MockResource() {
            super(null, () -> null);
        }
        
        @Override
        public String sourcePackId() {
            return "test_pack";
        }
        
        @Override
        public InputStream open() throws IOException {
            return new ByteArrayOutputStream().toByteArray().length == 0 ? 
                new java.io.ByteArrayInputStream(new byte[0]) : null;
        }
    }
}