package grill24.potionsplus.mixin;

import com.google.common.collect.Lists;
import grill24.potionsplus.core.PotionsPlus;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Function;

@Mixin(FeatureSorter.class)
public abstract class FeatureSorterMixin {
    @Inject(method = "buildFeaturesPerStep", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"))
    private static <T> void buildFeaturesPerStep(List<T> p_220604_, Function<T, List<HolderSet<PlacedFeature>>> p_220605_, boolean p_220606_, CallbackInfoReturnable<List<FeatureSorter.StepFeatureData>> cir) {
        if (PotionsPlus.Debug.DEBUG) {
            Object2IntMap<PlacedFeature> object2intmap = new Object2IntOpenHashMap<>();
            MutableInt mutableint = new MutableInt(0);

            record FeatureData(int featureIndex, int step, PlacedFeature feature) {
            }

            Comparator<FeatureData> comparator = Comparator.comparingInt(FeatureData::step).thenComparingInt(FeatureData::featureIndex);
            Map<FeatureData, Set<FeatureData>> map = new TreeMap<>(comparator);
            int i = 0;

            for (T t : p_220604_) {
                List<FeatureData> list = Lists.newArrayList();
                List<HolderSet<PlacedFeature>> list1 = p_220605_.apply(t);
                i = Math.max(i, list1.size());

                for (int j = 0; j < list1.size(); ++j) {
                    for (Holder<PlacedFeature> holder : list1.get(j)) {
                        PlacedFeature placedfeature = holder.value();
                        list.add(new FeatureData(object2intmap.computeIfAbsent(placedfeature, (p_220609_) -> {
                            return mutableint.getAndIncrement();
                        }), j, placedfeature));
                    }
                }

                for (int k = 0; k < list.size(); ++k) {
                    Set<FeatureData> set2 = map.computeIfAbsent(list.get(k), (p_220602_) -> {
                        return new TreeSet<>(comparator);
                    });
                    if (k < list.size() - 1) {
                        set2.add(list.get(k + 1));
                    }
                }
            }

            for (FeatureData featureData : map.keySet()) {

                Set<FeatureData> dependencies = map.get(featureData);
                for (FeatureData dependency : dependencies) {
                    // Check for circular dependencies
                    if (map.get(dependency).contains(featureData)) {
                        PotionsPlus.LOGGER.warn("Feature: {}, Step: {}", featureData.feature(), featureData.step());
                        PotionsPlus.LOGGER.warn("  Depends on: {}, Step: {}", dependency.feature(), dependency.step());
                    }
                }
            }
        }
    }
}
