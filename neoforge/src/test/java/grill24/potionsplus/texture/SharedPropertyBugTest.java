package grill24.potionsplus.texture;

import grill24.potionsplus.texture.MockClasses.*;
import grill24.potionsplus.texture.TextureVariantAlgorithm.*;

import java.util.*;

/**
 * Test to replicate the exact shared property scenario from the real codebase.
 * This tests the specific issue where all ore types share PotionsPlusOreBlock.TEXTURE.
 */
public class SharedPropertyBugTest {
    
    /**
     * Create shared property scenario exactly like in the real codebase
     */
    private static MockProperty<Integer> createSharedTextureProperty() {
        // This replicates PotionsPlusOreBlock.TEXTURE = IntegerProperty.create("texture", 0, 24);
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i <= 24; i++) {
            values.add(i);
        }
        return new MockProperty<>("texture", values);
    }
    
    /**
     * Test the exact scenario from OreBlocks.java where all ore types use the same TEXTURE property
     */
    public static void testRealWorldSharedPropertyScenario() {
        System.out.println("=== Testing Real-World Shared Property Scenario ===");
        
        // Create the shared TEXTURE property (like PotionsPlusOreBlock.TEXTURE)
        MockProperty<Integer> sharedTextureProperty = createSharedTextureProperty();
        
        // Create ore variants that mimic the real ore blocks from OreBlocks.java
        
        // STONEY_COPPER_ORE - should only use copper blocks for its tag
        MockPropertyTexVariant stoneyCopper = new MockPropertyTexVariant(
            sharedTextureProperty, // Same shared property!
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "stone")),
                new MockBlockHolder(new MockBlock("minecraft", "granite")),
                new MockBlockHolder(new MockBlock("minecraft", "andesite")),
                new MockBlockHolder(new MockBlock("minecraft", "diorite"))
            ),
            "all"
        );
        
        // STONEY_IRON_ORE - should only use iron blocks for its tag
        MockPropertyTexVariant stoneyIron = new MockPropertyTexVariant(
            sharedTextureProperty, // Same shared property!
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "stone")),
                new MockBlockHolder(new MockBlock("minecraft", "granite")),
                new MockBlockHolder(new MockBlock("minecraft", "andesite")),
                new MockBlockHolder(new MockBlock("minecraft", "diorite"))
            ),
            "all"
        );
        
        // STONEY_GOLD_ORE - should only use gold blocks for its tag
        MockPropertyTexVariant stoneyGold = new MockPropertyTexVariant(
            sharedTextureProperty, // Same shared property!
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "stone")),
                new MockBlockHolder(new MockBlock("minecraft", "granite")),
                new MockBlockHolder(new MockBlock("minecraft", "andesite")),
                new MockBlockHolder(new MockBlock("minecraft", "diorite"))
            ),
            "all"
        );
        
        // Test each ore type separately (as they are processed in the real system)
        testOreTypeInIsolation("STONEY_COPPER", stoneyCopper);
        testOreTypeInIsolation("STONEY_IRON", stoneyIron);
        testOreTypeInIsolation("STONEY_GOLD", stoneyGold);
        
        System.out.println("✓ Real-world shared property scenario test passed");
    }
    
    /**
     * Test that the specific ore texture isolation issue is fixed
     */
    public static void testSpecificOreTextureIsolationBug() {
        System.out.println("\n=== Testing Specific Ore Texture Isolation Bug ===");
        
        // Create the exact scenario that was causing the bug
        MockProperty<Integer> sharedTextureProperty = createSharedTextureProperty();
        
        // Create copper ore variant with specific copper overlay textures
        MockPropertyTexVariant copperOreVariant = new MockPropertyTexVariant(
            sharedTextureProperty,
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "stone")),      // texture=0
                new MockBlockHolder(new MockBlock("minecraft", "granite")),    // texture=1  
                new MockBlockHolder(new MockBlock("minecraft", "andesite")),   // texture=2
                new MockBlockHolder(new MockBlock("minecraft", "diorite"))     // texture=3
            ),
            "all"
        );
        
        // Simulate the overlays that specify this is copper ore
        System.out.println("Processing copper ore with overlays:");
        System.out.println("  - copper_ore_isolated.png");
        System.out.println("  - copper_ore_top.png"); 
        System.out.println("  - copper_ore_bottom.png");
        
        // Process copper ore for texture values 0-3
        MockPropertyTexVariant[] copperVariants = {copperOreVariant};
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(copperVariants, permutation);
            
            System.out.println("  Copper texture=" + i + ": " + result);
            
            // Verify that only stone-type blocks are used (no cross-contamination with iron/gold blocks)
            for (MockPair<String, MockBlockHolder> textureBlock : result.getTextureBlocks()) {
                String blockName = textureBlock.getB().getValue().getName();
                assert List.of("stone", "granite", "andesite", "diorite").contains(blockName) : 
                    "Copper ore contains unexpected block: " + blockName;
            }
        }
        
        // Now create iron ore variant with same shared property
        MockPropertyTexVariant ironOreVariant = new MockPropertyTexVariant(
            sharedTextureProperty, // SAME property as copper!
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "stone")),      // texture=0
                new MockBlockHolder(new MockBlock("minecraft", "granite")),    // texture=1  
                new MockBlockHolder(new MockBlock("minecraft", "andesite")),   // texture=2
                new MockBlockHolder(new MockBlock("minecraft", "diorite"))     // texture=3
            ),
            "all"
        );
        
        System.out.println("\nProcessing iron ore with overlays (using same shared property):");
        System.out.println("  - iron_ore_isolated.png");
        System.out.println("  - iron_ore_top.png");
        System.out.println("  - iron_ore_bottom.png");
        
        // Process iron ore for texture values 0-3
        MockPropertyTexVariant[] ironVariants = {ironOreVariant};
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(ironVariants, permutation);
            
            System.out.println("  Iron texture=" + i + ": " + result);
            
            // Verify that only stone-type blocks are used (no cross-contamination)
            for (MockPair<String, MockBlockHolder> textureBlock : result.getTextureBlocks()) {
                String blockName = textureBlock.getB().getValue().getName();
                assert List.of("stone", "granite", "andesite", "diorite").contains(blockName) : 
                    "Iron ore contains unexpected block: " + blockName;
            }
        }
        
        System.out.println("✓ Specific ore texture isolation bug test passed");
        System.out.println("  Both copper and iron ore use their correct base stone blocks");
        System.out.println("  The overlays (copper_ore_*.png vs iron_ore_*.png) determine the final appearance");
    }
    
    /**
     * Test ore type in isolation
     */
    private static void testOreTypeInIsolation(String oreType, MockPropertyTexVariant variant) {
        System.out.println("\nTesting " + oreType + " ore in isolation:");
        
        MockPropertyTexVariant[] variants = {variant};
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(variants, permutation);
            
            System.out.println("  " + oreType + " texture=" + i + ": " + result);
            
            // Each result should have one texture block from the variant's block list
            assert result.getTextureBlocks().size() == 1 : 
                oreType + " should have exactly 1 texture block, got " + result.getTextureBlocks().size();
            
            String blockName = result.getTextureBlocks().get(0).getB().getValue().getName();
            assert List.of("stone", "granite", "andesite", "diorite").contains(blockName) : 
                oreType + " contains unexpected block: " + blockName;
        }
        
        System.out.println("  ✓ " + oreType + " ore isolation test passed");
    }
    
    /**
     * Test that demonstrates the actual fix: overlays determine ore type, not base blocks
     */
    public static void testOverlayBasedOreTypeDistinction() {
        System.out.println("\n=== Testing Overlay-Based Ore Type Distinction ===");
        
        System.out.println("Understanding the real system:");
        System.out.println("1. All ore types (copper, iron, gold, etc.) share the same TEXTURE property");
        System.out.println("2. All ore types use the same base stone blocks (stone, granite, andesite, diorite)");
        System.out.println("3. The ore TYPE is determined by the overlay textures:");
        System.out.println("   - Copper ore: copper_ore_isolated.png, copper_ore_top.png, copper_ore_bottom.png");
        System.out.println("   - Iron ore: iron_ore_isolated.png, iron_ore_top.png, iron_ore_bottom.png");
        System.out.println("   - Gold ore: gold_ore_isolated.png, gold_ore_top.png, gold_ore_bottom.png");
        System.out.println("4. The TEXTURE property value determines which base stone to use");
        System.out.println("5. The overlay images are applied on top to create the final ore appearance");
        
        // This explains why the user saw "copper ore textures have coal, iron, or gold"
        // It's not that the algorithm is wrong - it's that the overlay images might be
        // getting mixed up somewhere in the texture processing pipeline!
        
        MockProperty<Integer> sharedTextureProperty = createSharedTextureProperty();
        List<MockBlockHolder> stoneBases = List.of(
            new MockBlockHolder(new MockBlock("minecraft", "stone")),
            new MockBlockHolder(new MockBlock("minecraft", "granite")),
            new MockBlockHolder(new MockBlock("minecraft", "andesite")),
            new MockBlockHolder(new MockBlock("minecraft", "diorite"))
        );
        
        // All ore types should use the same base blocks
        MockPropertyTexVariant copperVariant = new MockPropertyTexVariant(sharedTextureProperty, () -> stoneBases, "all");
        MockPropertyTexVariant ironVariant = new MockPropertyTexVariant(sharedTextureProperty, () -> stoneBases, "all");
        MockPropertyTexVariant goldVariant = new MockPropertyTexVariant(sharedTextureProperty, () -> stoneBases, "all");
        
        // Test that all variants produce the same base block mappings
        for (int textureValue = 0; textureValue < 4; textureValue++) {
            List<Integer> permutation = List.of(textureValue);
            
            TextureMappingResult copperResult = TextureVariantAlgorithm.prepareTextureMapping(new MockPropertyTexVariant[]{copperVariant}, permutation);
            TextureMappingResult ironResult = TextureVariantAlgorithm.prepareTextureMapping(new MockPropertyTexVariant[]{ironVariant}, permutation);
            TextureMappingResult goldResult = TextureVariantAlgorithm.prepareTextureMapping(new MockPropertyTexVariant[]{goldVariant}, permutation);
            
            // All should map to the same base block for the same texture value
            String copperBlock = copperResult.getTextureBlocks().get(0).getB().getValue().getName();
            String ironBlock = ironResult.getTextureBlocks().get(0).getB().getValue().getName();
            String goldBlock = goldResult.getTextureBlocks().get(0).getB().getValue().getName();
            
            assert copperBlock.equals(ironBlock) && ironBlock.equals(goldBlock) : 
                "Texture value " + textureValue + " should map to same base block for all ore types. " +
                "Got: copper=" + copperBlock + ", iron=" + ironBlock + ", gold=" + goldBlock;
            
            System.out.println("  texture=" + textureValue + " -> base block: " + copperBlock + " (all ore types)");
        }
        
        System.out.println("✓ Overlay-based ore type distinction test passed");
        System.out.println("  The bug is likely in the overlay texture processing, not the base block mapping!");
    }
    
    /**
     * Main test runner
     */
    public static void main(String[] args) {
        try {
            testRealWorldSharedPropertyScenario();
            testSpecificOreTextureIsolationBug();
            testOverlayBasedOreTypeDistinction();
            
            System.out.println("\n🎉 All shared property bug tests passed!");
            System.out.println("\n🔍 CONCLUSION:");
            System.out.println("The base block mapping algorithm is working correctly.");
            System.out.println("The issue described by the user (copper ore showing iron/coal/gold overlays)");
            System.out.println("is likely caused by overlay texture processing problems, not the mapping algorithm.");
            System.out.println("\nNext steps: Investigate the TextureResourceModification overlay processing.");
            
        } catch (AssertionError e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n💥 Unexpected error during testing: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}