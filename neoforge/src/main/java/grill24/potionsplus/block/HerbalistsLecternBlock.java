package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntity;
import grill24.potionsplus.blockentity.HerbalistsLecternSounds;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class HerbalistsLecternBlock extends Block implements EntityBlock {

    private static final VoxelShape SURFACE = Block.box(0, 14, 0, 16, 15.5, 16);
    private static final VoxelShape RIM0 = Block.box(0, 15.5, 0, 2, 16, 16);
    private static final VoxelShape RIM1 = Block.box(14, 15.5, 0, 16, 16, 16);
    private static final VoxelShape RIM2 = Block.box(2, 15.5, 0, 14, 16, 2);
    private static final VoxelShape RIM3 = Block.box(2, 15.5, 14, 14, 16, 16);
    private static final VoxelShape BASE = Block.box(2, 0, 2, 14, 1, 14);
    private static final VoxelShape LEG0 = Block.box(7, 0, 7, 9, 14, 9);
    private static final VoxelShape LEG_TOP0 = Block.box(11.5, 11, 3.5, 12.5, 14, 4.5);
    private static final VoxelShape LEG_MID0 = Block.box(11.25, 4, 3.25, 12.75, 11, 4.75);
    private static final VoxelShape LEG_BASE0 = Block.box(11, 1, 3, 13, 4, 5);
    private static final VoxelShape LEG_TOP1 = Block.box(11.5, 11, 11.5, 12.5, 14, 12.5);
    private static final VoxelShape LEG_MID1 = Block.box(11.25, 4, 11.25, 12.75, 11, 12.75);
    private static final VoxelShape LEG_BASE1 = Block.box(11, 1, 11, 13, 4, 13);
    private static final VoxelShape LEG_TOP2 = Block.box(3.5, 11, 11.5, 4.5, 14, 12.5);
    private static final VoxelShape LEG_MID2 = Block.box(3.25, 4, 11.25, 4.75, 11, 12.75);
    private static final VoxelShape LEG_BASE2 = Block.box(3, 1, 11, 5, 4, 13);
    private static final VoxelShape LEG_TOP3 = Block.box(3.5, 11, 3.5, 4.5, 14, 4.5);
    private static final VoxelShape LEG_MID3 = Block.box(3.25, 4, 3.25, 4.75, 11, 4.75);
    private static final VoxelShape LEG_BASE3 = Block.box(3, 1, 3, 5, 4, 5);

    // Join all shapes
    private static final VoxelShape ALL_SHAPES = Shapes.or(SURFACE, RIM0, RIM1, RIM2, RIM3, BASE, LEG0, LEG_TOP0, LEG_MID0, LEG_BASE0, LEG_TOP1, LEG_MID1, LEG_BASE1, LEG_TOP2, LEG_MID2, LEG_BASE2, LEG_TOP3, LEG_MID3, LEG_BASE3);

    public HerbalistsLecternBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new HerbalistsLecternBlockEntity(blockPos, blockState);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Cache items before interaction
        Optional<HerbalistsLecternBlockEntity> blockEntity = level.getBlockEntity(pos, Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get());
        if (blockEntity.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }
        HerbalistsLecternBlockEntity herbalistsLecternBlockEntity = blockEntity.get();

        // Do interaction
        InvUtil.InteractionResult result = InvUtil.insertOnPlayerUseItem(level, pos, player, hand, SoundEvents.ITEM_FRAME_ADD_ITEM);

        // If an item was inserted by a player, update the animation state
        if (result == InvUtil.InteractionResult.INSERT) {
            herbalistsLecternBlockEntity.setChanged();
            level.updateNeighborsAt(pos, this);
            herbalistsLecternBlockEntity.onPlayerInsertItem(player);

            if (level.isClientSide) {
                if (herbalistsLecternBlockEntity.sounds == null) {
                    herbalistsLecternBlockEntity.sounds = new HerbalistsLecternSounds();
                }
                herbalistsLecternBlockEntity.sounds.playSoundAppear(pos);
            }
        }

        return InvUtil.getMinecraftItemInteractionResult(result);

    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
// Cache items before interaction
        Optional<HerbalistsLecternBlockEntity> blockEntity = level.getBlockEntity(pos, Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get());
        if (blockEntity.isEmpty()) {
            return InteractionResult.FAIL;
        }
        HerbalistsLecternBlockEntity herbalistsLecternBlockEntity = blockEntity.get();

        // Do interaction
        InvUtil.InteractionResult result = InvUtil.extractOnPlayerUseWithoutItem(level, pos, player, true, SoundEvents.ITEM_FRAME_ADD_ITEM);
        if (result == InvUtil.InteractionResult.EXTRACT && level.isClientSide) {
            if (herbalistsLecternBlockEntity.sounds == null) {
                herbalistsLecternBlockEntity.sounds = new HerbalistsLecternSounds();
            }
            herbalistsLecternBlockEntity.sounds.playSoundDisappear(pos);
        }

        return InvUtil.getMinecraftInteractionResult(result);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get(), HerbalistsLecternBlockEntity::tick);
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos) {
        Optional<HerbalistsLecternBlockEntity> herbalistsLecternBlockEntity = level.getBlockEntity(blockPos, Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get());
        return Math.min(herbalistsLecternBlockEntity.get().rendererData.allIcons.size(), 15);
    }

    @Override
    @Deprecated
    public void onRemove(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        Utility.dropContents(level, blockPos, blockState, newState);
        super.onRemove(blockState, level, blockPos, newState, isMoving);
    }
}
