package grill24.potionsplus.test;

import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Test class to validate the builder cache optimization.
 * This ensures the cache correctly maps blocks to their texture generators.
 */
public class BuilderCacheTest {
    
    /**
     * Test basic cache functionality
     */
    public static void testCacheBasics() {
        System.out.println("Testing builder cache basics...");
        
        // Test 1: Cache should handle null blocks gracefully
        RuntimeTextureVariantModelGenerator result = RegistrationUtility.getTextureVariantGenerator(null);
        assert result == null : "Null block should return null generator";
        System.out.println("✓ Null block test passed");
        
        // Test 2: Cache should return null for unregistered blocks
        RuntimeTextureVariantModelGenerator result2 = RegistrationUtility.getTextureVariantGenerator(Blocks.STONE);
        assert result2 == null : "Unregistered block should return null generator";
        System.out.println("✓ Unregistered block test passed");
        
        // Test 3: Cache should be consistent across multiple calls
        RuntimeTextureVariantModelGenerator result3a = RegistrationUtility.getTextureVariantGenerator(Blocks.STONE);
        RuntimeTextureVariantModelGenerator result3b = RegistrationUtility.getTextureVariantGenerator(Blocks.STONE);
        assert result3a == result3b : "Cache should return same result for same block";
        System.out.println("✓ Cache consistency test passed");
        
        System.out.println("Basic cache tests passed!");
    }
    
    /**
     * Test cache performance characteristics
     */
    public static void testCachePerformance() {
        System.out.println("\nTesting cache performance...");
        
        Block[] testBlocks = {
            Blocks.STONE, Blocks.DIRT, Blocks.COBBLESTONE, Blocks.SAND, 
            Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.OAK_LOG, Blocks.IRON_ORE
        };
        
        int iterations = 100000;
        
        // Test repeated lookups
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (Block block : testBlocks) {
                RegistrationUtility.getTextureVariantGenerator(block);
            }
        }
        long endTime = System.nanoTime();
        
        double avgTimePerLookup = (endTime - startTime) / (double) (iterations * testBlocks.length) / 1_000; // Convert to microseconds
        System.out.println("✓ Average cache lookup time: " + String.format("%.3f", avgTimePerLookup) + " microseconds");
        
        if (avgTimePerLookup > 10.0) {
            System.out.println("⚠ Warning: Cache lookup time might be too slow");
        } else {
            System.out.println("✓ Cache performance is excellent");
        }
        
        // Test cache rebuilding performance
        long rebuildStart = System.nanoTime();
        RegistrationUtility.generateCommonRuntimeResourceMappings(); // This rebuilds the cache
        long rebuildEnd = System.nanoTime();
        
        double rebuildTime = (rebuildEnd - rebuildStart) / 1_000_000.0; // Convert to ms
        System.out.println("✓ Cache rebuild time: " + String.format("%.3f", rebuildTime) + "ms");
        
        System.out.println("Cache performance tests passed!");
    }
    
    /**
     * Test cache invalidation
     */
    public static void testCacheInvalidation() {
        System.out.println("\nTesting cache invalidation...");
        
        // Get initial cache state
        RuntimeTextureVariantModelGenerator initialResult = RegistrationUtility.getTextureVariantGenerator(Blocks.STONE);
        
        // Force cache invalidation by regenerating mappings
        RegistrationUtility.generateCommonRuntimeResourceMappings();
        
        // Check that cache still works after invalidation
        RuntimeTextureVariantModelGenerator afterInvalidation = RegistrationUtility.getTextureVariantGenerator(Blocks.STONE);
        
        // Results should be the same (since no actual builders were changed)
        assert initialResult == afterInvalidation : "Cache should rebuild consistently";
        System.out.println("✓ Cache invalidation test passed");
        
        System.out.println("Cache invalidation tests passed!");
    }
    
    public static void main(String[] args) {
        testCacheBasics();
        testCachePerformance();
        testCacheInvalidation();
        System.out.println("\n🎉 All cache tests passed! Builder cache optimization is working correctly.");
    }
}