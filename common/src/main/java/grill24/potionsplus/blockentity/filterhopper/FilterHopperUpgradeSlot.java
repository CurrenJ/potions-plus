package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.item.UpgradeBaseItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterHopperUpgradeSlot extends Slot {
    public FilterHopperUpgradeSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof UpgradeBaseItem;
    }
}
