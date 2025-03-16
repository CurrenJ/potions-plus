package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class SmallFilterHopperMenu extends FilterHopperMenu {
    public SmallFilterHopperMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.SMALL_FILTER_HOPPER.get(), containerId, playerInventory, 5, 27, 8, 173, 8, 53);
    }

    public SmallFilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.SMALL_FILTER_HOPPER.get(), containerId, playerInventory, container, 5, 27, 8, 173, 8 , 53);
    }
}
