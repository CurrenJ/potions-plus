package grill24.potionsplus.block;

import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ClotheslineBlock extends HorizontalDirectionalBlock implements EntityBlock, WorldlyContainerHolder {
    private static final VoxelShape CENTER_POST = Block.box(6, 0, 6, 10, 16, 10);

    public static final EnumProperty<ClotheslinePart> PART = EnumProperty.create("part", ClotheslinePart.class);
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 2, 6);

    public ClotheslineBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(PART, ClotheslinePart.LEFT).setValue(DISTANCE, 2));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(PART);
        blockStateBuilder.add(DISTANCE);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        final int distance = 3;
        BlockPos left = context.getClickedPos().relative(direction.getCounterClockWise(), distance / 2);
        BlockPos right = context.getClickedPos().relative(direction.getClockWise(), (int) Math.ceil(distance / 2f));
        Level level = context.getLevel();
        boolean isValid = level.getBlockState(right).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(right) && level.getBlockState(left).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(left);
        return isValid ? this.defaultBlockState().setValue(FACING, direction).setValue(DISTANCE, 3) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos placedAt, BlockState blockState, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, placedAt, blockState, placer, itemStack);
        if (!level.isClientSide) {
            int distance = getDistance(blockState);
            BlockPos left = placedAt.relative(blockState.getValue(FACING).getCounterClockWise(), distance / 2);
            BlockPos right = placedAt.relative(blockState.getValue(FACING).getClockWise(), (int) Math.ceil(distance / 2f));

            level.setBlock(left, blockState.setValue(PART, ClotheslinePart.LEFT), 3);
            level.setBlock(right, blockState.setValue(PART, ClotheslinePart.RIGHT), 3);
            level.setBlock(placedAt, Blocks.AIR.defaultBlockState(), 3);

            blockState.updateNeighbourShapes(level, left, 3);
            blockState.updateNeighbourShapes(level, right, 3);

            if(placer instanceof ServerPlayer serverPlayer) {
                CreatePotionsPlusBlockTrigger.INSTANCE.trigger(serverPlayer, BlockEntityBlocks.CLOTHESLINE.value().defaultBlockState());
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
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

        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public void onRemove(BlockState before, Level level, BlockPos blockPos, BlockState after, boolean p_60519_) {
        Utility.dropContents(level, blockPos, before, after);

        if (!level.isClientSide) {
            if (!this.areBothPartsValid(blockPos, level)) {
                BlockPos middle = getOneTowardsMiddle(blockPos, before);
                BlockState middleBlockState = level.getBlockState(middle);

                BlockPos other = getOtherEnd(blockPos, before);
                BlockState otherBlockState = level.getBlockState(other);
                BlockPos otherMiddle = getOneTowardsMiddle(other, otherBlockState);
                BlockState otherMiddleBlockState = level.getBlockState(otherMiddle);

                // Force an update on the middle block to trigger updateShape on both ends
                level.blockUpdated(middle, Blocks.AIR);
                middleBlockState.updateNeighbourShapes(level, middle, 3);

                level.blockUpdated(otherMiddle, Blocks.AIR);
                otherMiddleBlockState.updateNeighbourShapes(level, otherMiddle, 3);
            }
        }

        super.onRemove(before, level, blockPos, after, p_60519_);
    }

    public PushReaction getPistonPushReaction(BlockState p_49556_) {
        return PushReaction.DESTROY;
    }

    public BlockState updateShape(BlockState me, Direction direction, BlockState other, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos mutableBlockPos) {
        if (!this.areBothPartsValid(blockPos, levelAccessor)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(me, direction, other, levelAccessor, blockPos, mutableBlockPos);
    }

    public boolean areBothPartsValid(BlockPos pos, LevelReader levelAccessor) {
        BlockState blockState = levelAccessor.getBlockState(pos);
        if (!blockState.is(this))
            return false;

        ClotheslinePart clotheslinePart = blockState.getValue(PART);
        BlockPos otherPos = getOtherEnd(pos, blockState);
        BlockState otherState = levelAccessor.getBlockState(otherPos);
        return otherState.is(this) && blockState.getValue(FACING) == otherState.getValue(FACING) && clotheslinePart != otherState.getValue(PART);
    }

    public static BlockPos getOtherEnd(BlockPos blockPos, BlockState blockState) {
        if (!blockState.is(BlockEntityBlocks.CLOTHESLINE.value())) {
            PotionsPlus.LOGGER.warn("getOtherEnd called on " + blockState.getBlock() + ". Expected a clothesline block.");
            return blockPos;
        }

        int distance = getDistance(blockState);
        if (blockState.getValue(PART) == ClotheslinePart.LEFT)
            return blockPos.relative(blockState.getValue(FACING).getClockWise(), distance);
        else
            return blockPos.relative(blockState.getValue(FACING).getCounterClockWise(), distance);
    }

    public static BlockPos getLeftEnd(BlockPos blockPos, BlockState blockState) {
        if (!blockState.is(BlockEntityBlocks.CLOTHESLINE.value())) {
            PotionsPlus.LOGGER.warn("getLeftEnd called on " + blockState.getBlock() + ". Expected a clothesline block.");
            return blockPos;
        }

        if (blockState.getValue(PART) == ClotheslinePart.LEFT)
            return blockPos;
        else
            return getOtherEnd(blockPos, blockState);
    }

    public static boolean isLeftEnd(BlockState blockState) {
        if (!blockState.is(BlockEntityBlocks.CLOTHESLINE.value())) {
            PotionsPlus.LOGGER.warn("isLeftEnd called on " + blockState.getBlock() + ". Expected a clothesline block.");
            return false;
        }

        return blockState.getValue(PART) == ClotheslinePart.LEFT;
    }

    public static BlockPos getOneTowardsMiddle(BlockPos blockPos, BlockState blockState) {
        if (!blockState.is(BlockEntityBlocks.CLOTHESLINE.value())) {
            PotionsPlus.LOGGER.warn("getOneTowardsMiddle called on " + blockState.getBlock() + ". Expected a clothesline block.");
            return blockPos;
        }

        if (blockState.getValue(PART) == ClotheslinePart.LEFT)
            return blockPos.relative(blockState.getValue(FACING).getClockWise());
        else
            return blockPos.relative(blockState.getValue(FACING).getCounterClockWise());
    }

    public static int getDistance(BlockState blockState) {
        if (!blockState.is(BlockEntityBlocks.CLOTHESLINE.value())) {
            PotionsPlus.LOGGER.warn("getDistance called on " + blockState.getBlock() + ". Expected a clothesline block.");
            return -1;
        }

        return blockState.getValue(DISTANCE);
    }

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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos left = getLeftEnd(pos, state);
        InvUtil.InteractionResult result = InvUtil.insertOnPlayerUseItem(level, left, player, hand, SoundEvents.ITEM_FRAME_ADD_ITEM);

        return InvUtil.getMinecraftItemInteractionResult(result);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos left = getLeftEnd(pos, state);
        InvUtil.InteractionResult result = InvUtil.extractOnPlayerUseWithoutItem(level, left, player, true, SoundEvents.ITEM_FRAME_REMOVE_ITEM);

        return InvUtil.getMinecraftInteractionResult(result);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (isLeftEnd(state))
            return Utility.createTickerHelper(type, grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), ClotheslineBlockEntity::tick);

        return null;
    }

    @Override
    public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        return (WorldlyContainer) levelAccessor.getBlockEntity(getLeftEnd(blockPos, blockState));
    }
}
