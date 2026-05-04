package grill24.potionsplus.core;

import grill24.potionsplus.worldgen.OffsetPlacement;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PlacementModifierTypes {
    public static PlacementModifierType<OffsetPlacement> OFFSET = () -> OffsetPlacement.CODEC;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<PlacementModifierType<?>>, Holder<PlacementModifierType<?>>> register) {
        register.apply("offset", () -> (PlacementModifierType<OffsetPlacement>) OFFSET);
    }
}
