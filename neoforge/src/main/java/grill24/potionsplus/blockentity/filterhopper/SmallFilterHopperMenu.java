package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class SmallFilterHopperMenu extends FilterHopperMenu {
    private static final FilterHopperMenuLayout LAYOUT = FilterHopperMenuLayout.Builder.create()
            .playerInv(8, 123)
            .filterSlots(8, 53, 9, SmallFilterHopperBlockEntity.FILTER_SLOTS_SIZE)
            .hopperSlots(44, 19, 5)
            .upgradeSlots(175, 18, 1, SmallFilterHopperBlockEntity.UPGRADE_SLOTS_SIZE)
            .build();

    public SmallFilterHopperMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.SMALL_FILTER_HOPPER.get(), containerId, playerInventory, LAYOUT);
    }

    public SmallFilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.SMALL_FILTER_HOPPER.get(), containerId, playerInventory, container, LAYOUT);
    }
}
