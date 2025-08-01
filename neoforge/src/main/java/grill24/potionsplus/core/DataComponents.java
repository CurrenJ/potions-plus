package grill24.potionsplus.core;

import grill24.potionsplus.item.*;
import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import grill24.potionsplus.skill.reward.OwnerDataComponent;
import grill24.potionsplus.utility.Genotype;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponents {
    // The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModInfo.MOD_ID);

    public static final Supplier<DataComponentType<EdibleRewardGranterDataComponent>> CHOICE_ITEM = DATA_COMPONENTS.registerComponentType(
            "choice_item_data", builder -> builder
                    .persistent(EdibleRewardGranterDataComponent.CODEC)
                    .networkSynchronized(EdibleRewardGranterDataComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<OwnerDataComponent>> OWNER = DATA_COMPONENTS.registerComponentType(
            "owner_data", builder -> builder
                    .persistent(OwnerDataComponent.CODEC)
                    .networkSynchronized(OwnerDataComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<PlayerLockedItemModifiersDataComponent>> PLAYER_LOCKED_ITEM_MODIFIERS = DATA_COMPONENTS.registerComponentType(
            "player_locked_item_modifiers", builder -> builder
                    .persistent(PlayerLockedItemModifiersDataComponent.CODEC)
                    .networkSynchronized(PlayerLockedItemModifiersDataComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<FishSizeDataComponent>> FISH_SIZE = DATA_COMPONENTS.registerComponentType(
            "fish_size_data", builder -> builder
                    .persistent(FishSizeDataComponent.CODEC)
                    .networkSynchronized(FishSizeDataComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<FishingRodDataComponent>> FISHING_ROD = DATA_COMPONENTS.registerComponentType(
            "fishing_rod_data", builder -> builder
                    .persistent(FishingRodDataComponent.CODEC)
                    .networkSynchronized(FishingRodDataComponent.STREAM_CODEC)
                    .cacheEncoding()
    );

    public static final Supplier<DataComponentType<Genotype>> GENETIC_DATA = DATA_COMPONENTS.registerComponentType(
            "genetic_data", builder -> builder
                    .persistent(Genotype.CODEC)
                    .networkSynchronized(Genotype.STREAM_CODEC)
                    .cacheEncoding()
    );

    public static final Supplier<DataComponentType<WeightDataComponent>> WEIGHT = DATA_COMPONENTS.registerComponentType(
            "weight_data", builder -> builder
                    .persistent(WeightDataComponent.CODEC)
                    .networkSynchronized(WeightDataComponent.STREAM_CODEC)
                    .cacheEncoding()
    );

    public static final Supplier<DataComponentType<RuntimeVariantItemDataComponent>> RUNTIME_VARIANT_ITEM = DATA_COMPONENTS.registerComponentType(
            "runtime_variant_item_data", builder -> builder
                    .persistent(RuntimeVariantItemDataComponent.CODEC)
                    .networkSynchronized(RuntimeVariantItemDataComponent.STREAM_CODEC)
                    .cacheEncoding()
    );
}
