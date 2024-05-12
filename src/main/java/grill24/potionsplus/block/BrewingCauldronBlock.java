package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.blockentity.InventoryBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BrewingCauldronBlock extends CauldronBlock implements EntityBlock {
    public BrewingCauldronBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new BrewingCauldronBlockEntity(blockPos, blockState);
    }

    @Override
    @Deprecated
    public void onRemove(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (!blockState.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof InventoryBlockEntity inventoryBlockEntity) {
                Containers.dropContents(level, blockPos, inventoryBlockEntity.getItemHandler());
                level.updateNeighbourForOutputSignal(blockPos, this);
            }

            super.onRemove(blockState, level, blockPos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), BrewingCauldronBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult p_151974_) {
        ItemStack hand = player.getItemInHand(interactionHand);
        if (!hand.isEmpty()) {
            Optional<BrewingCauldronBlockEntity> blockEntity = level.getBlockEntity(blockPos, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get());
            if (blockEntity.isPresent()) {
                Container itemHandler = blockEntity.get().getItemHandler();
                for (int i = 0; i < itemHandler.getContainerSize(); i++) {
                    ItemStack toInsert = hand.copy();
                    toInsert.setCount(1);
                    if (blockEntity.get().canPlaceItem(i, toInsert)) {
                        hand.shrink(toInsert.getCount());
                        toInsert.grow(itemHandler.getItem(i).getCount());
                        itemHandler.setItem(i, toInsert);

                        level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        } else {
            Optional<BrewingCauldronBlockEntity> blockEntity = level.getBlockEntity(blockPos, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get());
            if (blockEntity.isPresent()) {
                Container itemHandler = blockEntity.get().getItemHandler();
                for (int i = 0; i < itemHandler.getContainerSize(); i++) {
                    ItemStack stack = itemHandler.getItem(i);
                    if (!stack.isEmpty() && player.canTakeItem(stack)) {
                        player.addItem(stack);
                        itemHandler.setItem(i, ItemStack.EMPTY);

                        level.playSound(null, blockPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos) {
        Optional<BrewingCauldronBlockEntity> brewingCauldronBlockEntity = level.getBlockEntity(blockPos, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get());
        return brewingCauldronBlockEntity.map(cauldronBlockEntity -> AbstractContainerMenu.getRedstoneSignalFromContainer(cauldronBlockEntity.getItemHandler())).orElse(0);
    }
}
