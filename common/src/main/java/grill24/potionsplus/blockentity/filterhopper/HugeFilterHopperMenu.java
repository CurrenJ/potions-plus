package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class HugeFilterHopperMenu extends FilterHopperMenu {
    private static final FilterHopperMenuLayout LAYOUT = FilterHopperMenuLayout.Builder.create()
            .playerInv(44, 157)
            .filterSlots(3, 46, 14, HugeFilterHopperBlockEntity.FILTER_SLOTS_SIZE)
            .hopperSlots(80, 25, 5)
            .upgradeSlots(177, 4, 3, HugeFilterHopperBlockEntity.UPGRADE_SLOTS_SIZE, 0, 8)
            .build();

    public HugeFilterHopperMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.HUGE_FILTER_HOPPER.get(), containerId, playerInventory, LAYOUT);
    }

    public HugeFilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.HUGE_FILTER_HOPPER.get(), containerId, playerInventory, container, LAYOUT);
    }
}
