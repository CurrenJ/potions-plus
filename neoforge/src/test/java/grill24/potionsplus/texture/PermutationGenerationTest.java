package grill24.potionsplus.texture;

import grill24.potionsplus.texture.MockClasses.*;
import grill24.potionsplus.texture.TextureVariantAlgorithm;

import java.util.*;

/**
 * Test suite for the permutation generation algorithm.
 * This validates the iterative permutation approach that replaced the recursive one.
 */
public class PermutationGenerationTest {
    
    /**
     * Test basic permutation generation functionality
     */
    public static void testBasicPermutations() {
        System.out.println("=== Testing Basic Permutation Generation ===");
        
        // Test single property
        MockProperty<Integer> singleProp = TestDataFactory.createIntegerProperty("test", 2);
        List<MockPair<MockProperty<Integer>, List<Integer>>> singleProps = List.of(
            new MockPair<>(singleProp, List.of(0, 1, 2))
        );
        
        List<List<Integer>> singleResult = TextureVariantAlgorithm.generatePermutations(singleProps);
        assert singleResult.size() == 3 : "Expected 3 permutations for single property, got " + singleResult.size();
        assert singleResult.equals(List.of(List.of(0), List.of(1), List.of(2))) : 
            "Unexpected single property permutations: " + singleResult;
        
        System.out.println("✓ Single property test passed: " + singleResult);
        
        // Test two properties
        MockProperty<Integer> prop1 = TestDataFactory.createIntegerProperty("prop1", 1);
        MockProperty<Integer> prop2 = TestDataFactory.createIntegerProperty("prop2", 1);
        List<MockPair<MockProperty<Integer>, List<Integer>>> twoProps = List.of(
            new MockPair<>(prop1, List.of(0, 1)),
            new MockPair<>(prop2, List.of(0, 1))
        );
        
        List<List<Integer>> twoResult = TextureVariantAlgorithm.generatePermutations(twoProps);
        assert twoResult.size() == 4 : "Expected 4 permutations for two properties, got " + twoResult.size();
        
        List<List<Integer>> expected = List.of(
            List.of(0, 0), List.of(0, 1), List.of(1, 0), List.of(1, 1)
        );
        assert twoResult.equals(expected) : "Unexpected two property permutations: " + twoResult;
        
        System.out.println("✓ Two property test passed: " + twoResult);
    }
    
    /**
     * Test edge cases for permutation generation
     */
    public static void testEdgeCases() {
        System.out.println("\n=== Testing Edge Cases ===");
        
        // Test empty properties list
        List<MockPair<MockProperty<Integer>, List<Integer>>> emptyProps = List.of();
        List<List<Integer>> emptyResult = TextureVariantAlgorithm.generatePermutations(emptyProps);
        assert emptyResult.isEmpty() : "Empty properties should return empty result";
        System.out.println("✓ Empty properties test passed");
        
        // Test property with single value
        MockProperty<Integer> singleValProp = TestDataFactory.createIntegerProperty("single", 0);
        List<MockPair<MockProperty<Integer>, List<Integer>>> singleValProps = List.of(
            new MockPair<>(singleValProp, List.of(0))
        );
        List<List<Integer>> singleValResult = TextureVariantAlgorithm.generatePermutations(singleValProps);
        assert singleValResult.size() == 1 : "Single value property should return one permutation";
        assert singleValResult.get(0).equals(List.of(0)) : "Single permutation should match input";
        System.out.println("✓ Single value property test passed");
        
        // Test property with empty values (edge case)
        MockProperty<Integer> emptyValProp = new MockProperty<>("empty", new ArrayList<Integer>());
        List<MockPair<MockProperty<Integer>, List<Integer>>> emptyValProps = List.of(
            new MockPair<>(emptyValProp, new ArrayList<Integer>())
        );
        List<List<Integer>> emptyValResult = TextureVariantAlgorithm.generatePermutations(emptyValProps);
        assert emptyValResult.isEmpty() : "Empty values should return empty result";
        System.out.println("✓ Empty values test passed");
    }
    
    /**
     * Test realistic ore texture scenarios
     */
    public static void testOreTextureScenarios() {
        System.out.println("\n=== Testing Ore Texture Scenarios ===");
        
        // Realistic ore texture scenario: 4 ore textures x 4 base stones = 16 permutations
        MockProperty<Integer> oreTexture = TestDataFactory.createIntegerProperty("ore_texture", 3);
        MockProperty<Integer> baseStone = TestDataFactory.createIntegerProperty("base_stone", 3);
        
        List<MockPair<MockProperty<Integer>, List<Integer>>> oreProps = List.of(
            new MockPair<>(oreTexture, List.of(0, 1, 2, 3)),
            new MockPair<>(baseStone, List.of(0, 1, 2, 3))
        );
        
        List<List<Integer>> oreResult = TextureVariantAlgorithm.generatePermutations(oreProps);
        assert oreResult.size() == 16 : "Expected 16 permutations for ore scenario, got " + oreResult.size();
        
        // Validate all permutations are unique
        Set<List<Integer>> uniquePermutations = new HashSet<>(oreResult);
        assert uniquePermutations.size() == 16 : "Not all permutations are unique";
        
        // Validate that all combinations are present
        for (int ore = 0; ore < 4; ore++) {
            for (int stone = 0; stone < 4; stone++) {
                List<Integer> expected = List.of(ore, stone);
                assert oreResult.contains(expected) : "Missing permutation: " + expected;
            }
        }
        
        System.out.println("✓ Ore texture scenario test passed (16 permutations generated)");
    }
    
    /**
     * Test complex multi-property scenarios
     */
    public static void testComplexScenarios() {
        System.out.println("\n=== Testing Complex Multi-Property Scenarios ===");
        
        // Complex scenario: 3 properties with different sizes
        MockProperty<Integer> prop1 = TestDataFactory.createIntegerProperty("prop1", 1); // 2 values
        MockProperty<Integer> prop2 = TestDataFactory.createIntegerProperty("prop2", 2); // 3 values  
        MockProperty<Integer> prop3 = TestDataFactory.createIntegerProperty("prop3", 0); // 1 value
        
        List<MockPair<MockProperty<Integer>, List<Integer>>> complexProps = List.of(
            new MockPair<>(prop1, List.of(0, 1)),
            new MockPair<>(prop2, List.of(0, 1, 2)),
            new MockPair<>(prop3, List.of(0))
        );
        
        List<List<Integer>> complexResult = TextureVariantAlgorithm.generatePermutations(complexProps);
        assert complexResult.size() == 6 : "Expected 6 permutations (2*3*1), got " + complexResult.size();
        
        // Validate specific permutations
        List<List<Integer>> expectedComplex = List.of(
            List.of(0, 0, 0), List.of(0, 1, 0), List.of(0, 2, 0),
            List.of(1, 0, 0), List.of(1, 1, 0), List.of(1, 2, 0)
        );
        
        for (List<Integer> expected : expectedComplex) {
            assert complexResult.contains(expected) : "Missing expected permutation: " + expected;
        }
        
        System.out.println("✓ Complex scenario test passed: " + complexResult);
    }
    
    /**
     * Test performance characteristics of the algorithm
     */
    public static void testPerformance() {
        System.out.println("\n=== Testing Performance ===");
        
        // Large scenario test (but not too large to avoid timeout)
        MockProperty<Integer> largeProp1 = TestDataFactory.createIntegerProperty("large1", 4); // 5 values
        MockProperty<Integer> largeProp2 = TestDataFactory.createIntegerProperty("large2", 3); // 4 values
        MockProperty<Integer> largeProp3 = TestDataFactory.createIntegerProperty("large3", 2); // 3 values
        
        List<MockPair<MockProperty<Integer>, List<Integer>>> largeProps = List.of(
            new MockPair<>(largeProp1, List.of(0, 1, 2, 3, 4)),
            new MockPair<>(largeProp2, List.of(0, 1, 2, 3)),
            new MockPair<>(largeProp3, List.of(0, 1, 2))
        );
        
        long startTime = System.nanoTime();
        List<List<Integer>> largeResult = TextureVariantAlgorithm.generatePermutations(largeProps);
        long endTime = System.nanoTime();
        
        assert largeResult.size() == 60 : "Expected 60 permutations (5*4*3), got " + largeResult.size();
        
        double timeMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("✓ Performance test passed: 60 permutations generated in " + 
                          String.format("%.3f", timeMs) + "ms");
        
        // Validate all permutations are unique
        Set<List<Integer>> uniqueLarge = new HashSet<>(largeResult);
        assert uniqueLarge.size() == 60 : "Not all large permutations are unique";
        
        if (timeMs > 100) {
            System.out.println("⚠ Warning: Performance might need optimization (>100ms for 60 permutations)");
        }
    }
    
    /**
     * Test algorithm correctness by comparing with a reference implementation
     */
    public static void testAlgorithmCorrectness() {
        System.out.println("\n=== Testing Algorithm Correctness ===");
        
        // Reference recursive implementation for comparison
        List<List<Integer>> referencePermutations = generatePermutationsRecursive(List.of(
            List.of(0, 1),
            List.of(0, 1, 2)
        ));
        
        // Our implementation
        MockProperty<Integer> prop1 = TestDataFactory.createIntegerProperty("prop1", 1);
        MockProperty<Integer> prop2 = TestDataFactory.createIntegerProperty("prop2", 2);
        List<MockPair<MockProperty<Integer>, List<Integer>>> props = List.of(
            new MockPair<>(prop1, List.of(0, 1)),
            new MockPair<>(prop2, List.of(0, 1, 2))
        );
        
        List<List<Integer>> ourPermutations = TextureVariantAlgorithm.generatePermutations(props);
        
        // Sort both for comparison
        referencePermutations.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int cmp = Integer.compare(a.get(i), b.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(a.size(), b.size());
        });
        
        ourPermutations.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int cmp = Integer.compare(a.get(i), b.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(a.size(), b.size());
        });
        
        assert referencePermutations.equals(ourPermutations) : 
            "Algorithm produces different results than reference implementation!\n" +
            "Reference: " + referencePermutations + "\n" +
            "Ours: " + ourPermutations;
        
        System.out.println("✓ Algorithm correctness test passed");
        System.out.println("  Reference: " + referencePermutations);
        System.out.println("  Our impl:  " + ourPermutations);
    }
    
    /**
     * Simple recursive reference implementation for correctness testing
     */
    private static List<List<Integer>> generatePermutationsRecursive(List<List<Integer>> properties) {
        if (properties.isEmpty()) {
            return List.of();
        }
        
        List<List<Integer>> result = new ArrayList<>();
        generatePermutationsRecursiveHelper(properties, 0, new ArrayList<>(), result);
        return result;
    }
    
    private static void generatePermutationsRecursiveHelper(
            List<List<Integer>> properties, 
            int propertyIndex, 
            List<Integer> current, 
            List<List<Integer>> result) {
        
        if (propertyIndex >= properties.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (Integer value : properties.get(propertyIndex)) {
            current.add(value);
            generatePermutationsRecursiveHelper(properties, propertyIndex + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    /**
     * Main test runner
     */
    public static void main(String[] args) {
        try {
            testBasicPermutations();
            testEdgeCases();
            testOreTextureScenarios();
            testComplexScenarios();
            testPerformance();
            testAlgorithmCorrectness();
            
            System.out.println("\n🎉 All permutation generation tests passed!");
            System.out.println("The iterative permutation algorithm is working correctly.");
            
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