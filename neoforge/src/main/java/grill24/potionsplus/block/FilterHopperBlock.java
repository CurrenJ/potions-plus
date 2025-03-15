package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.FilterHopperBlockEntity;
import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class FilterHopperBlock extends HopperBlock {
    public FilterHopperBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FilterHopperBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, Blocks.FILTER_HOPPER_BLOCK_ENTITY.value(), FilterHopperBlockEntity::pushItemsTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof FilterHopperBlockEntity filterHopperBlockEntity) {
                player.openMenu(filterHopperBlockEntity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }

            return InteractionResult.CONSUME;
        }
    }

}
