package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponents {
    // The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModInfo.MOD_ID);

    public static final Supplier<DataComponentType<WeightDataComponent>> WEIGHT = DATA_COMPONENTS.registerComponentType(
            "weight_data", builder -> builder
                    .persistent(WeightDataComponent.CODEC)
                    .networkSynchronized(WeightDataComponent.STREAM_CODEC)
                    .cacheEncoding()
    );

    static {
        grill24.potionsplus.core.DataComponents.WEIGHT = WEIGHT;
    }
}
