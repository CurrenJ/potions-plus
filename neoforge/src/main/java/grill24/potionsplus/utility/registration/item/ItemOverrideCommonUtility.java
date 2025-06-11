package grill24.potionsplus.utility.registration.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ItemOverrideCommonUtility {
    public static class EdibleChoiceItemOverrideData {
        private final ResourceLocation overridePropertyId;
        private final ResourceLocation layer0;
        private final List<ResourceLocation> layer1;
        private final Map<ResourceLocation, Integer> textureIndexMap;

        public EdibleChoiceItemOverrideData(ResourceLocation overridePropertyId, ResourceLocation layer0, List<ResourceLocation> layer1) {
            this.overridePropertyId = overridePropertyId;
            this.layer0 = layer0;
            this.layer1 = layer1;
            this.layer1.addFirst(layer0); // Default to no flag

            this.textureIndexMap = new java.util.HashMap<>();
            for (int i = 0; i < layer1.size(); i++) {
                textureIndexMap.put(layer1.get(i), i + 1);
            }
        }

        public ResourceLocation getRandomFlag(RandomSource randomSource) {
            return layer1.get(randomSource.nextInt(layer1.size() - 1) + 1);
        }

        public ResourceLocation getOverridePropertyId() {
            return overridePropertyId;
        }

        public ResourceLocation getLayer0() {
            return layer0;
        }

        public List<ResourceLocation> getLayer1() {
            return layer1;
        }

        private int getIndex(ResourceLocation textureLocation) {
            return textureIndexMap.getOrDefault(textureLocation, 0);
        }

        public float getOverrideValue(ResourceLocation textureLocation) {
            return getIndex(textureLocation) / 64F;
        }
    }
}
