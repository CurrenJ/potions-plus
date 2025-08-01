package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.filterhopper.FilterHopperBlockEntity;
import grill24.potionsplus.blockentity.filterhopper.HugeFilterHopperBlockEntity;
import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class HugeFilterHopperBlock extends FilterHopperBlock {
    public HugeFilterHopperBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, Blocks.HUGE_FILTER_HOPPER_BLOCK_ENTITY.value(), FilterHopperBlockEntity::pushItemsTick);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HugeFilterHopperBlockEntity(pos, state);
    }
}
