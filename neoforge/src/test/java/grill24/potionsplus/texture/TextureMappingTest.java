package grill24.potionsplus.texture;

import grill24.potionsplus.texture.MockClasses.*;
import grill24.potionsplus.texture.TextureVariantAlgorithm.*;

import java.util.*;

/**
 * Comprehensive test suite for debugging the texture mapping issue.
 * This specifically tests the ore texture cross-contamination problem.
 */
public class TextureMappingTest {
    
    /**
     * Test basic texture mapping functionality
     */
    public static void testBasicTextureMapping() {
        System.out.println("=== Testing Basic Texture Mapping ===");
        
        // Create a simple copper ore variant
        MockPropertyTexVariant copperVariant = TestDataFactory.createCopperOreVariant();
        MockPropertyTexVariant[] variants = {copperVariant};
        
        // Test mapping for each property value
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(variants, permutation);
            
            System.out.println("Permutation " + i + ": " + result);
            
            // Validate that copper variant only produces copper blocks
            boolean isValid = TextureMappingValidator.validateCopperOreMapping(result);
            assert isValid : "Copper ore mapping failed for permutation " + i + ": " + result;
            
            // Check blockstate key format
            assert result.getBlockStateKey().equals("texture=" + i) : 
                "Unexpected blockstate key: " + result.getBlockStateKey();
        }
        
        System.out.println("✓ Basic texture mapping test passed");
    }
    
    /**
     * Test the specific ore cross-contamination issue
     */
    public static void testOreTypeSeparation() {
        System.out.println("\n=== Testing Ore Type Separation (Critical Bug Test) ===");
        
        // Create separate copper and iron ore variants
        MockPropertyTexVariant copperVariant = TestDataFactory.createCopperOreVariant();
        MockPropertyTexVariant ironVariant = TestDataFactory.createIronOreVariant();
        
        // Test copper ore variant in isolation
        System.out.println("Testing copper ore variant isolation:");
        MockPropertyTexVariant[] copperVariants = {copperVariant};
        List<TextureMappingResult> copperResults = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(copperVariants, permutation);
            copperResults.add(result);
            System.out.println("  Copper permutation " + i + ": " + result);
        }
        
        // Test iron ore variant in isolation
        System.out.println("Testing iron ore variant isolation:");
        MockPropertyTexVariant[] ironVariants = {ironVariant};
        List<TextureMappingResult> ironResults = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(ironVariants, permutation);
            ironResults.add(result);
            System.out.println("  Iron permutation " + i + ": " + result);
        }
        
        // Validate no cross-contamination
        boolean copperValid = TextureMappingValidator.validateNoCrossContamination(copperResults, "copper");
        boolean ironValid = TextureMappingValidator.validateNoCrossContamination(ironResults, "iron");
        
        assert copperValid : "Copper ore variant contained non-copper blocks!";
        assert ironValid : "Iron ore variant contained non-iron blocks!";
        
        System.out.println("✓ Ore type separation test passed");
    }
    
    /**
     * Test the scenario that was causing the bug: multiple ore types processed together
     */
    public static void testMultipleOreTypesProcessedTogether() {
        System.out.println("\n=== Testing Multiple Ore Types Processed Together (Bug Scenario) ===");
        
        // This simulates the scenario where multiple ore types are processed in sequence
        // which was causing the cross-contamination in the original bug
        
        List<MockPropertyTexVariant> allOreVariants = List.of(
            TestDataFactory.createCopperOreVariant(),
            TestDataFactory.createIronOreVariant()
        );
        
        Map<String, List<TextureMappingResult>> resultsByOreType = new HashMap<>();
        
        // Process each ore type separately (as it should happen in the real system)
        for (MockPropertyTexVariant oreVariant : allOreVariants) {
            String oreType = extractOreType(oreVariant);
            System.out.println("Processing " + oreType + " ore variant:");
            
            MockPropertyTexVariant[] variants = {oreVariant};
            List<TextureMappingResult> results = new ArrayList<>();
            
            for (int i = 0; i < 4; i++) {
                List<Integer> permutation = List.of(i);
                TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(variants, permutation);
                results.add(result);
                System.out.println("  " + oreType + " permutation " + i + ": " + result);
            }
            
            resultsByOreType.put(oreType, results);
        }
        
        // Validate that each ore type only contains its own blocks
        for (Map.Entry<String, List<TextureMappingResult>> entry : resultsByOreType.entrySet()) {
            String oreType = entry.getKey();
            List<TextureMappingResult> results = entry.getValue();
            
            boolean isValid = TextureMappingValidator.validateNoCrossContamination(results, oreType);
            assert isValid : oreType + " ore variant contained blocks from other ore types!";
            
            System.out.println("✓ " + oreType + " ore type validation passed");
        }
        
        System.out.println("✓ Multiple ore types test passed");
    }
    
    /**
     * Test complex multi-property scenarios
     */
    public static void testMultiPropertyScenario() {
        System.out.println("\n=== Testing Multi-Property Scenario ===");
        
        // Create a scenario with both ore and base properties (like the real system)
        MockPropertyTexVariant oreVariant = TestDataFactory.createCopperOreVariant();
        MockPropertyTexVariant baseVariant = TestDataFactory.createStoneBaseVariant();
        
        MockPropertyTexVariant[] variants = {oreVariant, baseVariant};
        
        // Generate all permutations
        List<MockPair<MockProperty<Integer>, List<Integer>>> properties = List.of(
            new MockPair<>(oreVariant.property(), List.of(0, 1, 2, 3)),
            new MockPair<>(baseVariant.property(), List.of(0, 1, 2, 3))
        );
        
        List<List<Integer>> permutations = TextureVariantAlgorithm.generatePermutations(properties);
        System.out.println("Generated " + permutations.size() + " permutations");
        
        List<TextureMappingResult> allResults = new ArrayList<>();
        for (List<Integer> permutation : permutations) {
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(variants, permutation);
            allResults.add(result);
            System.out.println("  Permutation " + permutation + ": " + result);
        }
        
        // Validate that ore property always maps to copper blocks
        for (TextureMappingResult result : allResults) {
            Optional<MockPair<String, MockBlockHolder>> oreBlock = result.getTextureBlocks().stream()
                .filter(pair -> "ore".equals(pair.getA()))
                .findFirst();
            
            if (oreBlock.isPresent()) {
                String blockName = oreBlock.get().getB().getValue().getName();
                assert blockName.contains("copper") : 
                    "Ore texture mapped to non-copper block: " + blockName + " in result: " + result;
            }
        }
        
        System.out.println("✓ Multi-property scenario test passed");
    }
    
    /**
     * Test shared property handling (the root cause of the bug)
     */
    public static void testSharedPropertyHandling() {
        System.out.println("\n=== Testing Shared Property Handling (Root Cause Test) ===");
        
        // This test simulates the shared property scenario that was causing the bug
        // where multiple ore types shared the same texture property
        
        MockProperty<Integer> sharedTextureProperty = TestDataFactory.createIntegerProperty("texture", 3);
        
        // Create copper variant with shared property
        MockPropertyTexVariant copperVariant = new MockPropertyTexVariant(
            sharedTextureProperty,
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore"))
            ),
            "ore"
        );
        
        // Create iron variant with the SAME shared property (this was the bug!)
        MockPropertyTexVariant ironVariant = new MockPropertyTexVariant(
            sharedTextureProperty, // Same property object!
            () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore"))
            ),
            "ore"
        );
        
        // Test that even with shared property, each variant produces correct blocks
        System.out.println("Testing copper variant with shared property:");
        MockPropertyTexVariant[] copperVariants = {copperVariant};
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(copperVariants, permutation);
            System.out.println("  Copper permutation " + i + ": " + result);
            
            boolean isValid = TextureMappingValidator.validateCopperOreMapping(result);
            assert isValid : "Copper variant with shared property failed: " + result;
        }
        
        System.out.println("Testing iron variant with shared property:");
        MockPropertyTexVariant[] ironVariants = {ironVariant};
        for (int i = 0; i < 4; i++) {
            List<Integer> permutation = List.of(i);
            TextureMappingResult result = TextureVariantAlgorithm.prepareTextureMapping(ironVariants, permutation);
            System.out.println("  Iron permutation " + i + ": " + result);
            
            boolean isValid = TextureMappingValidator.validateIronOreMapping(result);
            assert isValid : "Iron variant with shared property failed: " + result;
        }
        
        System.out.println("✓ Shared property handling test passed");
    }
    
    /**
     * Test edge cases that might cause mapping failures
     */
    public static void testEdgeCases() {
        System.out.println("\n=== Testing Edge Cases ===");
        
        // Test empty block list
        MockPropertyTexVariant emptyVariant = new MockPropertyTexVariant(
            TestDataFactory.createIntegerProperty("empty", 0),
            Collections::emptyList,
            "empty"
        );
        
        MockPropertyTexVariant[] emptyVariants = {emptyVariant};
        List<Integer> emptyPermutation = List.of(0);
        TextureMappingResult emptyResult = TextureVariantAlgorithm.prepareTextureMapping(emptyVariants, emptyPermutation);
        
        assert emptyResult.getTextureBlocks().isEmpty() : "Empty variant should produce empty texture blocks";
        System.out.println("✓ Empty block list test passed");
        
        // Test out-of-bounds property value
        MockPropertyTexVariant smallVariant = new MockPropertyTexVariant(
            TestDataFactory.createIntegerProperty("small", 1),
            () -> List.of(new MockBlockHolder(new MockBlock("test", "single_block"))),
            "test"
        );
        
        MockPropertyTexVariant[] smallVariants = {smallVariant};
        List<Integer> oobPermutation = List.of(5); // Out of bounds!
        TextureMappingResult oobResult = TextureVariantAlgorithm.prepareTextureMapping(smallVariants, oobPermutation);
        
        assert oobResult.getTextureBlocks().isEmpty() : "Out-of-bounds permutation should produce empty texture blocks";
        System.out.println("✓ Out-of-bounds property value test passed");
        
        System.out.println("✓ Edge cases test passed");
    }
    
    /**
     * Helper method to extract ore type from variant
     */
    private static String extractOreType(MockPropertyTexVariant variant) {
        Collection<MockBlockHolder> blocks = variant.blocks().get();
        if (!blocks.isEmpty()) {
            String blockName = blocks.iterator().next().getValue().getName();
            if (blockName.contains("copper")) return "copper";
            if (blockName.contains("iron")) return "iron";
            if (blockName.contains("gold")) return "gold";
            if (blockName.contains("coal")) return "coal";
        }
        return "unknown";
    }
    
    /**
     * Main test runner
     */
    public static void main(String[] args) {
        try {
            testBasicTextureMapping();
            testOreTypeSeparation();
            testMultipleOreTypesProcessedTogether();
            testMultiPropertyScenario();
            testSharedPropertyHandling();
            testEdgeCases();
            
            System.out.println("\n🎉 All texture mapping tests passed!");
            System.out.println("The texture mapping algorithm is working correctly.");
            
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