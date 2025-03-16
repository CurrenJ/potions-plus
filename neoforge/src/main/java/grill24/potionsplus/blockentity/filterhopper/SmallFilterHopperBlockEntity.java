package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class SmallFilterHopperBlockEntity extends FilterHopperBlockEntity {
    public SmallFilterHopperBlockEntity(BlockPos pos, BlockState blockState) {
        super(Blocks.SMALL_FILTER_HOPPER_BLOCK_ENTITY.value(), pos, blockState, 27);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new SmallFilterHopperMenu(id, player, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Translations.CONTAINER_POTIONSPLUS_SMALL_FILTER_HOPPER);
    }
}
