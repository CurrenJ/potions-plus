package grill24.potionsplus.core;

import grill24.potionsplus.item.PlayerLockedItemModifiersDataComponent;
import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import grill24.potionsplus.skill.reward.OwnerDataComponent;
import grill24.potionsplus.utility.Genotype;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class DataComponents {
    public static Supplier<DataComponentType<EdibleRewardGranterDataComponent>> CHOICE_ITEM;
    public static Supplier<DataComponentType<OwnerDataComponent>> OWNER;
    public static Supplier<DataComponentType<PlayerLockedItemModifiersDataComponent>> PLAYER_LOCKED_ITEM_MODIFIERS;
    public static Supplier<DataComponentType<Genotype>> GENETIC_DATA;
    public static Supplier<DataComponentType<WeightDataComponent>> WEIGHT;
}
