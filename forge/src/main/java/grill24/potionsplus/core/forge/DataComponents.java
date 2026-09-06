package grill24.potionsplus.core.forge;

import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DataComponents {
    // Forge has no DeferredRegister.DataComponents specialization (NeoForge-only); use a plain
    // DeferredRegister of the wildcard element type.
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ModInfo.MOD_ID);

    public static final RegistryObject<DataComponentType<WeightDataComponent>> WEIGHT = DATA_COMPONENTS.register(
            "weight_data", () -> DataComponentType.<WeightDataComponent>builder()
                    .persistent(WeightDataComponent.CODEC)
                    .networkSynchronized(WeightDataComponent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    static {
        grill24.potionsplus.core.DataComponents.WEIGHT = WEIGHT;
    }
}
