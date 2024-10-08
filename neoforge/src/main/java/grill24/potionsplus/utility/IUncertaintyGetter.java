package grill24.potionsplus.utility;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;

public interface IUncertaintyGetter {
    float potions_plus$getUncertainty(BlockSource blockSource, ItemStack stack);
}
