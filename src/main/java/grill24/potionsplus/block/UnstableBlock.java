package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

public class UnstableBlock extends Block {
    public static BooleanProperty PRIMED = BooleanProperty.create("primed");
    public static IntegerProperty LIFESPAN = IntegerProperty.create("lifespan", 0, 600);
    protected static final int DEFAULT_LIFESPAN = 2;

    public UnstableBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PRIMED, false).setValue(LIFESPAN, DEFAULT_LIFESPAN));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(PRIMED, false).setValue(LIFESPAN, DEFAULT_LIFESPAN);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(PRIMED).add(LIFESPAN);
    }

    @Override
    public void onPlace(BlockState myState, Level level, BlockPos blockPos, BlockState blockState, boolean p_53237_) {
        if (!myState.getValue(PRIMED)) {
            level.scheduleTick(blockPos, this, this.getDelayAfterPlace(myState));
        }
    }

    @Override
    public void stepOn(Level p_53238_, BlockPos p_53239_, BlockState p_53240_, net.minecraft.world.entity.Entity entity) {
        if (!p_53238_.isClientSide() && entity instanceof Player) {
            p_53238_.scheduleTick(p_53239_, this, this.getDelayAfterPlace(p_53240_));
        }
    }

    @Override
    public BlockState updateShape(BlockState myState, Direction direction, BlockState otherState, LevelAccessor levelAccessor, BlockPos myPos, BlockPos otherPos) {
        if(!levelAccessor.isClientSide()) {
            if (!(otherState.getBlock() instanceof UnstableBlock)) {
                levelAccessor.scheduleTick(myPos, this, this.getDelayAfterPlace(myState));
            }
        }
        return super.updateShape(myState, direction, otherState, levelAccessor, myPos, otherPos);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        if(blockState.getValue(PRIMED)) {
            serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            serverLevel.levelEvent(2001, blockPos, Block.getId(defaultBlockState()));
        } else {
            serverLevel.setBlock(blockPos, blockState.setValue(PRIMED, true), 3);
        }
    }

    protected int getDelayAfterPlace(BlockState blockState) {
        return blockState.getValue(LIFESPAN);
    }
}
