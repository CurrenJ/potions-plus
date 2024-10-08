package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
        Utility.dropContents(level, blockPos, blockState, newState);
        super.onRemove(blockState, level, blockPos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get(), BrewingCauldronBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        InvUtil.InteractionResult result = InvUtil.insertOnPlayerUseItem(level, pos, player, hand, SoundEvents.GENERIC_SPLASH);

        if (result == InvUtil.InteractionResult.INSERT) {
            level.getBlockEntity(pos, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get()).ifPresent(cauldronBlockEntity -> cauldronBlockEntity.onPlayerInsertItem(player));
        }

        return InvUtil.getMinecraftItemInteractionResult(result);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InvUtil.InteractionResult result = InvUtil.extractOnPlayerUseWithoutItem(level, pos, player, true, SoundEvents.ITEM_FRAME_REMOVE_ITEM);

        return InvUtil.getMinecraftInteractionResult(result);
    }

        @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos) {
        Optional<BrewingCauldronBlockEntity> brewingCauldronBlockEntity = level.getBlockEntity(blockPos, Blocks.BREWING_CAULDRON_BLOCK_ENTITY.get());
        return brewingCauldronBlockEntity.map(AbstractContainerMenu::getRedstoneSignalFromContainer).orElse(0);
    }
}
