package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class LargeFilterHopperMenu extends FilterHopperMenu {
    private static final FilterHopperMenuLayout LAYOUT = FilterHopperMenuLayout.Builder.create()
            .playerInv(8, 151)
            .filterSlots(8, 40, 9, LargeFilterHopperBlockEntity.FILTER_SLOTS_SIZE)
            .hopperSlots(44, 19, 5)
            .upgradeSlots(175, 10, 1, LargeFilterHopperBlockEntity.UPGRADE_SLOTS_SIZE, 8, 0)
            .build();

    public LargeFilterHopperMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.LARGE_FILTER_HOPPER.get(), containerId, playerInventory, LAYOUT);
    }

    public LargeFilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.LARGE_FILTER_HOPPER.get(), containerId, playerInventory, container, LAYOUT);
    }
}
