package grill24.potionsplus.core;

import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.utility.Genotype;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class DataComponents {
    public static Supplier<DataComponentType<Genotype>> GENETIC_DATA;
    public static Supplier<DataComponentType<WeightDataComponent>> WEIGHT;
}
