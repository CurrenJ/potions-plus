package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.logging.Logger;

public class ClotheslineBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final VoxelShape CENTER_POST = Block.box(6, 0, 6, 10, 16, 10);

    public static final EnumProperty<ClotheslinePart> PART = EnumProperty.create("part", ClotheslinePart.class);

    public ClotheslineBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(PART, ClotheslinePart.LEFT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(PART);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos left = context.getClickedPos().relative(direction.getCounterClockWise());
        BlockPos right = context.getClickedPos().relative(direction.getClockWise());
        Level level = context.getLevel();
        boolean isValid = level.getBlockState(right).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(right) && level.getBlockState(left).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(left);
        return isValid ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos placedAt, BlockState blockState, @Nullable LivingEntity p_49502_, ItemStack itemStack) {
        super.setPlacedBy(level, placedAt, blockState, p_49502_, itemStack);
        if (!level.isClientSide) {
            BlockPos left = placedAt.relative(blockState.getValue(FACING).getCounterClockWise());
            BlockPos right = placedAt.relative(blockState.getValue(FACING).getClockWise());

            level.setBlock(left, blockState.setValue(PART, ClotheslinePart.LEFT), 3);
            level.setBlock(right, blockState.setValue(PART, ClotheslinePart.RIGHT), 3);
            level.setBlock(placedAt, Blocks.AIR.defaultBlockState(), 3);

            blockState.updateNeighbourShapes(level, left, 3);
            blockState.updateNeighbourShapes(level, right, 3);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            // Prevent creative drops
            ClotheslinePart clotheslinePart = blockState.getValue(PART);
            if (clotheslinePart == ClotheslinePart.RIGHT) {
                BlockPos blockpos = getOtherEnd(blockPos, blockState);
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == ClotheslinePart.LEFT) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public void onRemove(BlockState before, Level level, BlockPos blockPos, BlockState after, boolean p_60519_) {
        super.onRemove(before, level, blockPos, after, p_60519_);
        if(!level.isClientSide) {
            if(!this.areBothPartsValid(blockPos, level)) {
                BlockPos middle = getMiddle(blockPos, before);
                BlockState middleBlockState = level.getBlockState(middle);

                // Force an update on the middle block to trigger updateShape on both ends
                level.blockUpdated(blockPos, Blocks.AIR);
                middleBlockState.updateNeighbourShapes(level, middle, 3);
            }
        }
    }

    public PushReaction getPistonPushReaction(BlockState p_49556_) {
        return PushReaction.DESTROY;
    }

    public BlockState updateShape(BlockState me, Direction direction, BlockState other, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos mutableBlockPos) {
        if(!this.areBothPartsValid(blockPos, levelAccessor)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(me, direction, other, levelAccessor, blockPos, mutableBlockPos);
    }

    public boolean areBothPartsValid(BlockPos pos, LevelReader levelAccessor) {
        BlockState blockState = levelAccessor.getBlockState(pos);
        if(!blockState.is(this))
            return false;

        ClotheslinePart clotheslinePart = blockState.getValue(PART);
        BlockPos otherPos = getOtherEnd(pos, blockState);
        BlockState otherState = levelAccessor.getBlockState(otherPos);
        return otherState.is(this) && blockState.getValue(FACING) == otherState.getValue(FACING) && clotheslinePart != otherState.getValue(PART);
    }

    public static BlockPos getOtherEnd(BlockPos blockPos, BlockState blockState) {
        if(!blockState.is(grill24.potionsplus.core.Blocks.CLOTHESLINE.get())) {
            PotionsPlus.LOGGER.warn("getOtherEnd called on " + blockState.getBlock().getRegistryName() + ". Expected a clothesline block.");
            return blockPos;
        }

        if(blockState.getValue(PART) == ClotheslinePart.LEFT)
            return blockPos.relative(blockState.getValue(FACING).getClockWise(), 2);
        else
            return blockPos.relative(blockState.getValue(FACING).getCounterClockWise(), 2);
    }

    public static BlockPos getLeftEnd(BlockPos blockPos, BlockState blockState) {
        if (!blockState.is(grill24.potionsplus.core.Blocks.CLOTHESLINE.get())) {
            PotionsPlus.LOGGER.warn("getLeftEnd called on " + blockState.getBlock().getRegistryName() + ". Expected a clothesline block.");
            return blockPos;
        }

        if(blockState.getValue(PART) == ClotheslinePart.LEFT)
            return blockPos;
        else
            return getOtherEnd(blockPos, blockState);
    }

    public static boolean isLeftEnd(BlockState blockState) {
        if (!blockState.is(grill24.potionsplus.core.Blocks.CLOTHESLINE.get())) {
            PotionsPlus.LOGGER.warn("isLeftEnd called on " + blockState.getBlock().getRegistryName() + ". Expected a clothesline block.");
            return false;
        }

        return blockState.getValue(PART) == ClotheslinePart.LEFT;
    }

    public static BlockPos getMiddle(BlockPos blockPos, BlockState blockState) {
        if (!blockState.is(grill24.potionsplus.core.Blocks.CLOTHESLINE.get())) {
            PotionsPlus.LOGGER.warn("getMiddle called on " + blockState.getBlock().getRegistryName() + ". Expected a clothesline block.");
            return blockPos;
        }

        if(blockState.getValue(PART) == ClotheslinePart.LEFT)
            return blockPos.relative(blockState.getValue(FACING).getClockWise());
        else
            return blockPos.relative(blockState.getValue(FACING).getCounterClockWise());    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return CENTER_POST;
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState p_151955_, @NotNull BlockGetter p_151956_, @NotNull BlockPos p_151957_) {
        return getShape(p_151955_, p_151956_, p_151957_, CollisionContext.empty());
    }

    // ----- Block Entity -----

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ClotheslineBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult p_151974_) {
        BlockPos left = getLeftEnd(blockPos, blockState);
        InvUtil.InteractionResult result = InvUtil.giveAndTakeFromPlayerOnUseBlock(level, left, player, interactionHand, true, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundEvents.ITEM_FRAME_REMOVE_ITEM);

        return InvUtil.getMinecraftInteractionResult(result);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(isLeftEnd(state))
            return Utility.createTickerHelper(type, grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), ClotheslineBlockEntity::tick);

        return null;
    }
}
