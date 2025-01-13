package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.MenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SkillsMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public SkillsMenu(int containerId, Inventory playerInventory) {
        super(MenuTypes.SKILLS.get(), containerId);
        access = null;
    }

    public SkillsMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(MenuTypes.SKILLS.get(), containerId);
        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.access == null) {
            return true;
        }
        return AbstractContainerMenu.stillValid(this.access, player, Blocks.DIRT);
    }
}
