package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterHopperMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 5;
    private final Container hopper;

    public FilterHopperMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(FilterHopperBlockEntity.HOPPER_CONTAINER_SIZE + FilterHopperBlockEntity.FILTER_SIZE));
    }

    public FilterHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(MenuTypes.FILTER_HOPPER.get(), containerId);
        this.hopper = container;
        checkContainerSize(container, FilterHopperBlockEntity.HOPPER_CONTAINER_SIZE + FilterHopperBlockEntity.FILTER_SIZE);
        container.startOpen(playerInventory.player);

        // Hopper slots
        addSlotsInRows(container, 0, FilterHopperBlockEntity.HOPPER_CONTAINER_SIZE, 9, 80, 19);
        // Filter slots
        addSlotsInRows(container, FilterHopperBlockEntity.HOPPER_CONTAINER_SIZE, FilterHopperBlockEntity.FILTER_SIZE, 9, 8, 53);
        // Player inventory slots
        addSlotsInRows(playerInventory, 9, 27, 9, 8, 123);
        // Player hotbar slots
        addSlotsInRows(playerInventory, 0, 9, 9, 8, 181);
    }

    private void addSlotsInRows(Container container, int slotStartingIndex, int numSlots, int maxRowLength, int texX, int texY) {
        final int rows = (int) Math.ceil(numSlots / (float) maxRowLength);
        for (int f = 0; f < rows; f++) {
            final int rowLength = Math.min(numSlots - f * maxRowLength, maxRowLength);
            for (int x = 0; x < rowLength; x++) {
                final int slotIndex = slotStartingIndex + f * maxRowLength + x;
                this.addSlot(new Slot(container, slotIndex, texX + x * 18, texY + f * 18));
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
}
