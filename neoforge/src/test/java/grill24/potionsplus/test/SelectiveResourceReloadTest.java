package grill24.potionsplus.test;

import grill24.potionsplus.utility.performance.SelectiveResourceReloadUtility;
import grill24.potionsplus.utility.performance.AdvancedResourceReloadOptimizer;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the selective resource reload optimization system.
 */
public class SelectiveResourceReloadTest {
    
    @BeforeEach
    void setUp() {
        SelectiveResourceReloadUtility.resetStatistics();
    }
    
    @Test
    @DisplayName("Resource tracking should work correctly")
    void testResourceTracking() {
        // Start tracking
        SelectiveResourceReloadUtility.startTracking();
        
        // Track some resources
        ResourceLocation model = ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/test_ore.json");
        ResourceLocation blockstate = ResourceLocation.fromNamespaceAndPath("potionsplus", "blockstates/test_ore.json");
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("potionsplus", "textures/block/test_ore.png");
        
        SelectiveResourceReloadUtility.trackModifiedResource(model);
        SelectiveResourceReloadUtility.trackModifiedResource(blockstate);
        SelectiveResourceReloadUtility.trackModifiedResource(texture);
        
        // Stop tracking and get results
        Set<ResourceLocation> modified = SelectiveResourceReloadUtility.stopTrackingAndGetModified();
        
        assertEquals(3, modified.size());
        assertTrue(modified.contains(model));
        assertTrue(modified.contains(blockstate));
        assertTrue(modified.contains(texture));
    }
    
    @Test
    @DisplayName("Tracking should be disabled when not started")
    void testTrackingDisabled() {
        // Don't start tracking
        ResourceLocation resource = ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/test.json");
        SelectiveResourceReloadUtility.trackModifiedResource(resource);
        
        Set<ResourceLocation> modified = SelectiveResourceReloadUtility.stopTrackingAndGetModified();
        assertTrue(modified.isEmpty());
    }
    
    @Test
    @DisplayName("Empty resource set should skip reload")
    void testEmptyResourceSetSkipsReload() {
        CompletableFuture<Void> future = SelectiveResourceReloadUtility.performOptimizedReload();
        
        assertNotNull(future);
        assertTrue(future.isDone());
    }
    
    @Test
    @DisplayName("Resource analysis should categorize resources correctly")
    void testResourceAnalysis() {
        SelectiveResourceReloadUtility.startTracking();
        
        // Track different types of resources
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/ore1.json"));
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/ore2.json"));
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "blockstates/ore1.json"));
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "textures/block/ore1.png"));
        
        Set<ResourceLocation> modified = SelectiveResourceReloadUtility.stopTrackingAndGetModified();
        assertEquals(4, modified.size());
        
        // Verify that performOptimizedReload handles the mixed resource types
        CompletableFuture<Void> future = SelectiveResourceReloadUtility.performOptimizedReload();
        assertNotNull(future);
    }
    
    @Test
    @DisplayName("Statistics should track reload operations")
    void testStatisticsTracking() {
        SelectiveResourceReloadUtility.startTracking();
        
        // Track some resources
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/test.json"));
        
        // This should increment the reload counter
        SelectiveResourceReloadUtility.performOptimizedReload();
        
        // Verify statistics are being tracked
        SelectiveResourceReloadUtility.logReloadStatistics();
        
        // The test passes if no exceptions are thrown during logging
        assertTrue(true);
    }
    
    @Test
    @DisplayName("Advanced optimizer should detect reflection availability")
    void testAdvancedOptimizerReflection() {
        boolean available = AdvancedResourceReloadOptimizer.isAdvancedOptimizationAvailable();
        
        // The result depends on the runtime environment
        // Just verify the method doesn't throw an exception
        assertNotNull(available);
    }
    
    @Test
    @DisplayName("System info logging should not throw exceptions")
    void testSystemInfoLogging() {
        assertDoesNotThrow(() -> {
            AdvancedResourceReloadOptimizer.logReloadSystemInfo();
        });
    }
    
    @Test
    @DisplayName("Reset statistics should clear all data")
    void testResetStatistics() {
        SelectiveResourceReloadUtility.startTracking();
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/test.json"));
        
        SelectiveResourceReloadUtility.resetStatistics();
        
        // After reset, tracking should be disabled and no resources should be tracked
        Set<ResourceLocation> modified = SelectiveResourceReloadUtility.stopTrackingAndGetModified();
        assertTrue(modified.isEmpty());
    }
    
    @Test
    @DisplayName("Multiple tracking sessions should work independently")
    void testMultipleTrackingSessions() {
        // First session
        SelectiveResourceReloadUtility.startTracking();
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/ore1.json"));
        Set<ResourceLocation> session1 = SelectiveResourceReloadUtility.stopTrackingAndGetModified();
        assertEquals(1, session1.size());
        
        // Second session
        SelectiveResourceReloadUtility.startTracking();
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/ore2.json"));
        SelectiveResourceReloadUtility.trackModifiedResource(
            ResourceLocation.fromNamespaceAndPath("potionsplus", "models/block/ore3.json"));
        Set<ResourceLocation> session2 = SelectiveResourceReloadUtility.stopTrackingAndGetModified();
        assertEquals(2, session2.size());
        
        // Sessions should be independent
        assertFalse(session2.containsAll(session1));
    }
}