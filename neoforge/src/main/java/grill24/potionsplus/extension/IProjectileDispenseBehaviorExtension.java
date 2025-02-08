package grill24.potionsplus.extension;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;

public interface IProjectileDispenseBehaviorExtension {
    float potions_plus$getUncertainty(BlockSource blockSource, ItemStack stack);
}
