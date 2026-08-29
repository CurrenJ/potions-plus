package grill24.potionsplus.core.fabric;

import grill24.potionsplus.item.WeightDataComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class DataComponents {
    public static void init() {
        // No-op: forces class loading so the static initializer (below) runs.
    }

    static {
        DataComponentType<WeightDataComponent> weight = DataComponentType.<WeightDataComponent>builder()
                .persistent(WeightDataComponent.CODEC)
                .networkSynchronized(WeightDataComponent.STREAM_CODEC)
                .cacheEncoding()
                .build();
        Holder<DataComponentType<WeightDataComponent>> holder =
                FabricRegistration.register(BuiltInRegistries.DATA_COMPONENT_TYPE, "weight_data", () -> weight);
        // Holder.Reference is not a Supplier, so wrap it for the common Supplier stub.
        grill24.potionsplus.core.DataComponents.WEIGHT = holder::value;
    }
}
