package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class LargeFilterHopperMenu extends FilterHopperMenu {
    public LargeFilterHopperMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.LARGE_FILTER_HOPPER.get(), containerId, playerInventory, 5, 54, 8, 151, 8, 40);
    }

    public LargeFilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.LARGE_FILTER_HOPPER.get(), containerId, playerInventory, container, 5, 54, 8, 151, 8, 40);
    }
}
