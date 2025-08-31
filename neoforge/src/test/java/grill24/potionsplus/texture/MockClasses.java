package grill24.potionsplus.texture;

import java.util.*;
import java.util.function.Supplier;

/**
 * Mock classes to replace Minecraft dependencies for self-contained testing.
 * This allows testing the texture algorithms without requiring Minecraft runtime.
 */
public class MockClasses {
    
    /**
     * Mock for Minecraft's Property<Integer> class
     */
    public static class MockProperty<T extends Comparable<T>> {
        private final String name;
        private final Collection<T> possibleValues;
        
        public MockProperty(String name, Collection<T> possibleValues) {
            this.name = name;
            this.possibleValues = new ArrayList<>(possibleValues);
        }
        
        public String getName() {
            return name;
        }
        
        public Collection<T> getPossibleValues() {
            return possibleValues;
        }
        
        @Override
        public String toString() {
            return "MockProperty{name='" + name + "', values=" + possibleValues + "}";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MockProperty<?> other)) return false;
            return Objects.equals(name, other.name) && Objects.equals(possibleValues, other.possibleValues);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, possibleValues);
        }
    }
    
    /**
     * Mock for Minecraft's Block class
     */
    public static class MockBlock {
        private final String name;
        private final String namespace;
        
        public MockBlock(String namespace, String name) {
            this.namespace = namespace;
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public String getNamespace() {
            return namespace;
        }
        
        public MockResourceLocation getResourceLocation() {
            return new MockResourceLocation(namespace, name);
        }
        
        @Override
        public String toString() {
            return namespace + ":" + name;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MockBlock other)) return false;
            return Objects.equals(name, other.name) && Objects.equals(namespace, other.namespace);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, namespace);
        }
    }
    
    /**
     * Mock for Minecraft's Holder<Block> class
     */
    public static class MockBlockHolder {
        private final MockBlock block;
        private final MockResourceKey<MockBlock> key;
        
        public MockBlockHolder(MockBlock block) {
            this.block = block;
            this.key = new MockResourceKey<>(block.getResourceLocation());
        }
        
        public MockBlock getValue() {
            return block;
        }
        
        public MockResourceKey<MockBlock> getKey() {
            return key;
        }
        
        @Override
        public String toString() {
            return "Holder[" + block + "]";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MockBlockHolder other)) return false;
            return Objects.equals(block, other.block);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(block);
        }
    }
    
    /**
     * Mock for Minecraft's ResourceLocation class
     */
    public static class MockResourceLocation {
        private final String namespace;
        private final String path;
        
        public MockResourceLocation(String namespace, String path) {
            this.namespace = namespace;
            this.path = path;
        }
        
        public String getNamespace() {
            return namespace;
        }
        
        public String getPath() {
            return path;
        }
        
        @Override
        public String toString() {
            return namespace + ":" + path;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MockResourceLocation other)) return false;
            return Objects.equals(namespace, other.namespace) && Objects.equals(path, other.path);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(namespace, path);
        }
    }
    
    /**
     * Mock for Minecraft's ResourceKey class
     */
    public static class MockResourceKey<T> {
        private final MockResourceLocation location;
        
        public MockResourceKey(MockResourceLocation location) {
            this.location = location;
        }
        
        public MockResourceLocation location() {
            return location;
        }
        
        @Override
        public String toString() {
            return "ResourceKey[" + location + "]";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MockResourceKey<?> other)) return false;
            return Objects.equals(location, other.location);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(location);
        }
    }
    
    /**
     * Mock for Pair utility class
     */
    public static class MockPair<A, B> {
        private final A first;
        private final B second;
        
        public MockPair(A first, B second) {
            this.first = first;
            this.second = second;
        }
        
        public A getA() {
            return first;
        }
        
        public B getB() {
            return second;
        }
        
        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof MockPair<?, ?> other)) return false;
            return Objects.equals(first, other.first) && Objects.equals(second, other.second);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
    
    /**
     * Mock PropertyTexVariant for testing
     */
    public static class MockPropertyTexVariant {
        private final MockProperty<Integer> property;
        private final Supplier<Collection<MockBlockHolder>> blocks;
        private final String textureKeyInBaseModel;
        
        public MockPropertyTexVariant(MockProperty<Integer> property, 
                                     Supplier<Collection<MockBlockHolder>> blocks, 
                                     String textureKeyInBaseModel) {
            this.property = property;
            this.blocks = blocks;
            this.textureKeyInBaseModel = textureKeyInBaseModel;
        }
        
        public MockProperty<Integer> property() {
            return property;
        }
        
        public Supplier<Collection<MockBlockHolder>> blocks() {
            return blocks;
        }
        
        public String textureKeyInBaseModel() {
            return textureKeyInBaseModel;
        }
        
        @Override
        public String toString() {
            return "MockPropertyTexVariant{" +
                    "property=" + property.getName() +
                    ", blocks=" + blocks.get() +
                    ", textureKeyInBaseModel='" + textureKeyInBaseModel + '\'' +
                    '}';
        }
    }
    
    /**
     * Test data factory for creating common mock objects
     */
    public static class TestDataFactory {
        
        /**
         * Create a mock property with integer values 0 to maxValue
         */
        public static MockProperty<Integer> createIntegerProperty(String name, int maxValue) {
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i <= maxValue; i++) {
                values.add(i);
            }
            return new MockProperty<>(name, values);
        }
        
        /**
         * Create mock ore blocks (copper, iron, gold, coal)
         */
        public static List<MockBlockHolder> createOreBlocks() {
            return List.of(
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "gold_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "coal_ore"))
            );
        }
        
        /**
         * Create mock stone blocks (stone, deepslate, sand, gravel)
         */
        public static List<MockBlockHolder> createStoneBlocks() {
            return List.of(
                new MockBlockHolder(new MockBlock("minecraft", "stone")),
                new MockBlockHolder(new MockBlock("minecraft", "deepslate")),
                new MockBlockHolder(new MockBlock("minecraft", "sand")),
                new MockBlockHolder(new MockBlock("minecraft", "gravel"))
            );
        }
        
        /**
         * Create a copper ore variant with its own blocks (copper only)
         */
        public static MockPropertyTexVariant createCopperOreVariant() {
            MockProperty<Integer> textureProperty = createIntegerProperty("texture", 3);
            Supplier<Collection<MockBlockHolder>> copperBlocks = () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "copper_ore"))
            );
            return new MockPropertyTexVariant(textureProperty, copperBlocks, "ore");
        }
        
        /**
         * Create an iron ore variant with its own blocks (iron only)
         */
        public static MockPropertyTexVariant createIronOreVariant() {
            MockProperty<Integer> textureProperty = createIntegerProperty("texture", 3);
            Supplier<Collection<MockBlockHolder>> ironBlocks = () -> List.of(
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore")),
                new MockBlockHolder(new MockBlock("minecraft", "iron_ore"))
            );
            return new MockPropertyTexVariant(textureProperty, ironBlocks, "ore");
        }
        
        /**
         * Create a stone base variant
         */
        public static MockPropertyTexVariant createStoneBaseVariant() {
            MockProperty<Integer> baseProperty = createIntegerProperty("base", 3);
            return new MockPropertyTexVariant(baseProperty, TestDataFactory::createStoneBlocks, "base");
        }
    }
}