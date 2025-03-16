package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class HugeFilterHopperMenu extends FilterHopperMenu {
    public HugeFilterHopperMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.HUGE_FILTER_HOPPER.get(), containerId, playerInventory, 5, 84, 44, 151, 3, 40, 14, 80, 19);
    }

    public HugeFilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.HUGE_FILTER_HOPPER.get(), containerId, playerInventory, container, 5, 84, 8, 151, 4, 40, 14, 80, 19);
    }
}
