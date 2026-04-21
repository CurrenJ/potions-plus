package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class OneItemSlot extends Slot {
    public OneItemSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
