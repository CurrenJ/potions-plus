package grill24.potionsplus.utility.registration.item;

import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Map;

public class ItemOverrideCommonUtility {
    public static class EdibleChoiceItemOverrideData {
        private final Identifier overridePropertyId;
        private final Identifier layer0;
        private final List<Identifier> layer1;
        private final Map<Identifier, Integer> textureIndexMap;

        public EdibleChoiceItemOverrideData(Identifier overridePropertyId, Identifier layer0, List<Identifier> layer1) {
            this.overridePropertyId = overridePropertyId;
            this.layer0 = layer0;
            this.layer1 = layer1;
            this.layer1.addFirst(layer0); // Default to no flag

            this.textureIndexMap = new java.util.HashMap<>();
            for (int i = 0; i < layer1.size(); i++) {
                textureIndexMap.put(layer1.get(i), i + 1);
            }
        }

        public Identifier getRandomFlag(RandomSource randomSource) {
            return layer1.get(randomSource.nextInt(layer1.size() - 1) + 1);
        }

        public Identifier getOverridePropertyId() {
            return overridePropertyId;
        }

        public Identifier getLayer0() {
            return layer0;
        }

        public List<Identifier> getLayer1() {
            return layer1;
        }

        private int getIndex(Identifier textureLocation) {
            return textureIndexMap.getOrDefault(textureLocation, 0);
        }

        public float getOverrideValue(Identifier textureLocation) {
            return getIndex(textureLocation) / 64F;
        }
    }
}
