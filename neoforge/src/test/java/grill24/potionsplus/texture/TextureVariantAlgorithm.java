package grill24.potionsplus.texture;

import grill24.potionsplus.texture.MockClasses.*;

import java.util.*;

/**
 * Extracted and testable version of the texture variant algorithms.
 * This allows testing the core logic without Minecraft dependencies.
 */
public class TextureVariantAlgorithm {
    
    /**
     * Extracted version of the permutation generation algorithm from ClientRuntimeTextureVariantUtility
     */
    public static List<List<Integer>> generatePermutations(List<MockPair<MockProperty<Integer>, List<Integer>>> properties) {
        if (properties.isEmpty()) {
            return List.of();
        }
        
        // Calculate total number of permutations to pre-size array
        int totalPermutations = 1;
        for (MockPair<MockProperty<Integer>, List<Integer>> property : properties) {
            totalPermutations *= property.getB().size();
        }
        
        List<List<Integer>> permutations = new ArrayList<>(totalPermutations);
        
        // Use iterative approach instead of recursive to reduce memory allocation
        generatePermutationsIterative(properties, permutations);
        return permutations;
    }
    
    /**
     * Iterative function to generate all permutations of the given properties.
     * This is more memory efficient than the recursive approach as it doesn't 
     * create deep call stacks and temporary array copies.
     */
    private static void generatePermutationsIterative(
            List<MockPair<MockProperty<Integer>, List<Integer>>> properties,
            List<List<Integer>> result) {
        
        // Check if any property has empty values - if so, no permutations possible
        for (MockPair<MockProperty<Integer>, List<Integer>> property : properties) {
            if (property.getB().isEmpty()) {
                return; // No permutations possible
            }
        }
        
        // Convert to arrays for faster access
        List<Integer>[] propertyValues = new List[properties.size()];
        int[] indices = new int[properties.size()];
        
        for (int i = 0; i < properties.size(); i++) {
            propertyValues[i] = properties.get(i).getB();
        }
        
        // Generate all combinations using counter-like approach
        do {
            // Create current permutation
            List<Integer> permutation = new ArrayList<>(properties.size());
            for (int i = 0; i < properties.size(); i++) {
                permutation.add(propertyValues[i].get(indices[i]));
            }
            result.add(permutation);
            
            // Increment indices like an odometer
            int carry = 1;
            for (int i = properties.size() - 1; i >= 0 && carry > 0; i--) {
                indices[i] += carry;
                if (indices[i] >= propertyValues[i].size()) {
                    indices[i] = 0;
                    carry = 1;
                } else {
                    carry = 0;
                }
            }
            
            // Stop when we've rolled over all counters
            if (carry > 0) {
                break;
            }
        } while (true);
    }
    
    /**
     * Extracted and simplified version of prepareGenerationData that focuses on mapping logic
     */
    public static class TextureMappingResult {
        private final List<MockPair<String, MockBlockHolder>> textureBlocks;
        private final String blockStateKey;
        
        public TextureMappingResult(List<MockPair<String, MockBlockHolder>> textureBlocks, String blockStateKey) {
            this.textureBlocks = textureBlocks;
            this.blockStateKey = blockStateKey;
        }
        
        public List<MockPair<String, MockBlockHolder>> getTextureBlocks() {
            return textureBlocks;
        }
        
        public String getBlockStateKey() {
            return blockStateKey;
        }
        
        @Override
        public String toString() {
            return "TextureMappingResult{" +
                    "textureBlocks=" + textureBlocks +
                    ", blockStateKey='" + blockStateKey + '\'' +
                    '}';
        }
    }
    
    /**
     * This is the extracted and testable version of the problematic prepareGenerationData method.
     * It focuses on the texture mapping logic that was causing the ore texture issue.
     */
    public static TextureMappingResult prepareTextureMapping(
            MockPropertyTexVariant[] propertyTexVariants, 
            List<Integer> permutation) {
        
        List<MockPair<String, MockBlockHolder>> textureBlocks = new ArrayList<>();
        StringBuilder blockStatePermutationKey = new StringBuilder();
        
        for (int i = 0; i < permutation.size(); i++) {
            MockPropertyTexVariant variant = propertyTexVariants[i];
            final int propertyValue = permutation.get(i);
            
            // Create the blockstate key representing all the property values of this permutation.
            if (!blockStatePermutationKey.isEmpty()) {
                blockStatePermutationKey.append(",");
            }
            blockStatePermutationKey.append(variant.property().getName()).append("=").append(propertyValue);
            
            // Get the blocks from the variant directly, not from shared mappings
            // This ensures each ore type only uses its own designated texture blocks
            Collection<MockBlockHolder> variantBlocks = variant.blocks().get();
            List<MockBlockHolder> blockList = variantBlocks.stream().toList();
            
            // Use direct index-based lookup instead of shared property mappings
            // This prevents cross-contamination between different ore types
            Optional<MockBlockHolder> variantBlock = Optional.empty();
            if (propertyValue < blockList.size()) {
                variantBlock = Optional.of(blockList.get(propertyValue));
            }
            
            // Skip if block is not available
            if (variantBlock.isEmpty()) {
                System.out.println("Warning: Block not available for property: " + 
                    variant.property().getName() + ", value: " + propertyValue + 
                    ", available blocks: " + blockList.size());
                continue;
            }
            
            // Record the texture block mapping
            textureBlocks.add(new MockPair<>(variant.textureKeyInBaseModel(), variantBlock.get()));
        }
        
        return new TextureMappingResult(textureBlocks, blockStatePermutationKey.toString());
    }
    
    /**
     * Validation utility to check if texture mapping results contain only expected block types
     */
    public static class TextureMappingValidator {
        
        /**
         * Validate that copper ore mappings only contain copper blocks
         */
        public static boolean validateCopperOreMapping(TextureMappingResult result) {
            return result.getTextureBlocks().stream()
                    .allMatch(pair -> pair.getB().getValue().getName().contains("copper"));
        }
        
        /**
         * Validate that iron ore mappings only contain iron blocks
         */
        public static boolean validateIronOreMapping(TextureMappingResult result) {
            return result.getTextureBlocks().stream()
                    .allMatch(pair -> pair.getB().getValue().getName().contains("iron"));
        }
        
        /**
         * Validate that no cross-contamination occurs between ore types
         */
        public static boolean validateNoCrossContamination(List<TextureMappingResult> results, String expectedOreType) {
            return results.stream().allMatch(result -> 
                result.getTextureBlocks().stream()
                    .allMatch(pair -> pair.getB().getValue().getName().contains(expectedOreType))
            );
        }
        
        /**
         * Validate that each property value maps to a unique block consistently
         */
        public static boolean validateConsistentMapping(List<TextureMappingResult> results) {
            Map<String, Set<String>> propertyValueToBlocks = new HashMap<>();
            
            for (TextureMappingResult result : results) {
                String[] keyParts = result.getBlockStateKey().split(",");
                for (String keyPart : keyParts) {
                    String[] propValue = keyPart.split("=");
                    if (propValue.length == 2) {
                        String propertyKey = propValue[0] + "=" + propValue[1];
                        
                        Set<String> blocks = propertyValueToBlocks.computeIfAbsent(propertyKey, k -> new HashSet<>());
                        result.getTextureBlocks().forEach(pair -> blocks.add(pair.getB().getValue().getName()));
                    }
                }
            }
            
            // Each property value should consistently map to the same block types
            return propertyValueToBlocks.values().stream()
                    .allMatch(blocks -> blocks.size() == 1);
        }
    }
}