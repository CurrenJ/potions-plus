package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class HugeFilterHopperBlockEntity extends FilterHopperBlockEntity {
    public static final int FILTER_SLOTS_SIZE = 84;
    public static final int UPGRADE_SLOTS_SIZE = 3;

    public HugeFilterHopperBlockEntity(BlockPos pos, BlockState blockState) {
        super(Blocks.HUGE_FILTER_HOPPER_BLOCK_ENTITY.value(), pos, blockState, FILTER_SLOTS_SIZE, UPGRADE_SLOTS_SIZE);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new HugeFilterHopperMenu(id, player, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Translations.CONTAINER_POTIONSPLUS_HUGE_FILTER_HOPPER);
    }
}
