package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.InventoryBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class SanguineAltarBlock extends HorizontalDirectionalBlock implements EntityBlock {

    private static final VoxelShape BASE0 = Block.box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape BASE1 = Block.box(1, 1, 1, 15, 2, 15);
    private static final VoxelShape LEG0 = Block.box(1, 2, 6, 5, 3, 10);
    private static final VoxelShape LEG1 = Block.box(2, 3, 7, 4, 13, 9);
    private static final VoxelShape LEG2 = Block.box(11, 2, 6, 15, 3, 10);
    private static final VoxelShape LEG3 = Block.box(12, 3, 7, 14, 13, 9);
    private static final VoxelShape LEG4 = Block.box(6, 2, 11, 10, 3, 15);
    private static final VoxelShape LEG5 = Block.box(7, 3, 12, 9, 13, 14);
    private static final VoxelShape LEG6 = Block.box(6, 2, 1, 10, 3, 5);
    private static final VoxelShape LEG7 = Block.box(7, 3, 2, 9, 13, 4);

    // Join all shapes
    private static final VoxelShape ALL_SHAPES = Shapes.or(BASE0, BASE1, LEG0, LEG1, LEG2, LEG3, LEG4, LEG5, LEG6, LEG7);

    public SanguineAltarBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SanguineAltarBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_151964_, @NotNull BlockGetter p_151965_, @NotNull BlockPos p_151966_, @NotNull CollisionContext p_151967_) {
        return ALL_SHAPES;
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState p_151955_, @NotNull BlockGetter p_151956_, @NotNull BlockPos p_151957_) {
        return ALL_SHAPES;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult p_151974_) {
        Optional<SanguineAltarBlockEntity> blockEntity = level.getBlockEntity(blockPos, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get());
        if (blockEntity.isEmpty()) {
            return InteractionResult.FAIL;
        }
        SanguineAltarBlockEntity sanguineAltarBlockEntity = blockEntity.get();

        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        } else if (!player.getMainHandItem().isEmpty() && !sanguineAltarBlockEntity.canPlaceItem(0, player.getMainHandItem())) {
            return InteractionResult.FAIL;
        }

        // Do interaction
        InvUtil.InteractionResult result = InvUtil.giveAndTakeFromPlayerOnUseBlock(level, blockPos, player, interactionHand, true, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundEvents.ITEM_FRAME_REMOVE_ITEM);
        switch (result) {
            case INSERT:
                updateStateAndNotify(sanguineAltarBlockEntity, level, blockPos, player, sanguineAltarBlockEntity::onPlayerInsertItem);
                break;
            case EXTRACT:
                updateStateAndNotify(sanguineAltarBlockEntity, level, blockPos, player, sanguineAltarBlockEntity::onPlayerRemoveItem);
                break;
        }

        return InvUtil.getMinecraftInteractionResult(result);
    }

    private void updateStateAndNotify(InventoryBlockEntity blockEntity, Level level, BlockPos blockPos, Player player, Consumer<Player> action) {
        blockEntity.setChanged();
        level.updateNeighborsAt(blockPos, this);
        action.accept(player);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get(), SanguineAltarBlockEntity::tick);
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos) {
        Optional<SanguineAltarBlockEntity> SanguineAltarBlockEntity = level.getBlockEntity(blockPos, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get());
        return 0;
    }

    @Override
    @Deprecated
    public void onRemove(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (!blockState.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof InventoryBlockEntity inventoryBlockEntity) {
                Containers.dropContents(level, blockPos, inventoryBlockEntity);
                level.updateNeighbourForOutputSignal(blockPos, this);
            }

            super.onRemove(blockState, level, blockPos, newState, isMoving);
        }
    }
}
