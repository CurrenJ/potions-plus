package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

@FunctionalInterface
public interface SlotFactory {
    Slot create(Container container, int index, int x, int y);
}
