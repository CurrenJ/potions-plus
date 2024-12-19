package grill24.potionsplus.core;

import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponents {
    // The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModInfo.MOD_ID);

    public static final Supplier<DataComponentType<EdibleRewardGranterDataComponent>> CHOICE_ITEM_DATA = DATA_COMPONENTS.registerComponentType(
            "choice_item_data", builder -> builder
                    .persistent(EdibleRewardGranterDataComponent.CODEC)
                    .networkSynchronized(EdibleRewardGranterDataComponent.STREAM_CODEC)
    );
}
