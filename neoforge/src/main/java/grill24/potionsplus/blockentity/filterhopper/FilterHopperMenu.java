package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class FilterHopperMenu extends AbstractContainerMenu {
    private final Container hopper;

    public FilterHopperMenu(MenuType<? extends FilterHopperMenu> menuType, int containerId, Inventory playerInventory, FilterHopperMenuLayout layout) {
        this(menuType, containerId, playerInventory, new SimpleContainer(layout.getTotalSlots()), layout);
    }

    public FilterHopperMenu(MenuType<? extends FilterHopperMenu> menuType, int containerId, Inventory playerInventory, Container container, FilterHopperMenuLayout layout) {
        super(menuType, containerId);
        this.hopper = container;
        checkContainerSize(container, layout.getTotalSlots());
        container.startOpen(playerInventory.player);

        // Hopper slots
        addSlotsInRows(container, 0, layout.hopperSlots(), layout.hopperSlots(), 0, 0, layout.hopperSlotsTexX(), layout.hopperSlotsTexY(), Slot::new);
        // Filter slots
        addSlotsInRows(container, layout.hopperSlots() + layout.upgradeSlots(), layout.filterSlots(), layout.maxFilterRowLength(), 0, 0, layout.hoppperFilterSlotsTexX(), layout.hoppperFilterSlotsTexY(), OneItemSlot::new);
        // Upgrade Slots
        addSlotsInRows(container, layout.hopperSlots(), layout.upgradeSlots(), layout.maxUpgradeRowLength(), layout.upgradeSlotsPaddingBetweenRows(), layout.upgradeSlotsPaddingBetweenColumns(), layout.upgradeSlotsX(), layout.upgradeSlotsY(), FilterHopperUpgradeSlot::new);
        // Player inventory slots
        addSlotsInRows(playerInventory, 9, 27, 9, 0, 0, layout.playerInventoryTexX(), layout.playerInventoryTexY(), Slot::new);
        // Player hotbar slots
        addSlotsInRows(playerInventory, 0, 9, 9, layout.playerInventoryTexX(), 0, layout.playerInventoryTexX(), layout.playerInventoryTexY() + 58, Slot::new);
    }

    private void addSlotsInRows(Container container, int slotStartingIndex, int numSlots, int maxRowLength, int paddingBetweenRows, int paddingBetweenColumns, int texX, int texY, SlotFactory slotFactory) {
        final int rows = (int) Math.ceil(numSlots / (float) maxRowLength);
        for (int f = 0; f < rows; f++) {
            final int rowLength = Math.min(numSlots - f * maxRowLength, maxRowLength);
            for (int x = 0; x < rowLength; x++) {
                final int slotIndex = slotStartingIndex + f * maxRowLength + x;
                this.addSlot(slotFactory.create(container, slotIndex, texX + x * (18 + paddingBetweenColumns), texY + f * (18 + paddingBetweenRows)));
            }
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean stillValid(Player player) {
        return this.hopper.stillValid(player);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.hopper.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.hopper.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.hopper.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.hopper.stopOpen(player);
    }

    public Optional<FilterHopperBlockEntity> getFilterHopperBlockEntity() {
        if (this.hopper instanceof FilterHopperBlockEntity filterHopperBlockEntity) {
            return Optional.of(filterHopperBlockEntity);
        } else {
            return Optional.empty();
        }
    }
}
