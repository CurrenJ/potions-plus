package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AbyssalTroveBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    public AbyssalTroveBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new AbyssalTroveBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_151964_, @NotNull BlockGetter p_151965_, @NotNull BlockPos p_151966_, @NotNull CollisionContext p_151967_) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState p_151955_, @NotNull BlockGetter p_151956_, @NotNull BlockPos p_151957_) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Optional<AbyssalTroveBlockEntity> blockEntity = level.getBlockEntity(pos, Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get());
        if (blockEntity.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }
        AbyssalTroveBlockEntity abyssalTroveBlockEntity = blockEntity.get();

        if (player.isCrouching()) {
            return ItemInteractionResult.FAIL;
        }

        // Do interaction
        InvUtil.InteractionResult result = InvUtil.InteractionResult.PASS;
        if (!player.getMainHandItem().isEmpty() && !blockEntity.get().getStoredIngredients().contains(PpIngredient.of(player.getMainHandItem()))) {
            result = InvUtil.insertOnPlayerUseItem(level, pos, player, hand, SoundEvents.ITEM_FRAME_ADD_ITEM);
            if (result == InvUtil.InteractionResult.INSERT) {
                if (level.isClientSide) {
                    Vec3 posVec = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    for (int i = 0; i < 10; i++) {
                        Vec3 particlePos = posVec.add(Utility.nextGaussian(0, 0.5, level.random), 0.8 + Utility.nextDouble(-0.125, 0.125, level.random), Utility.nextGaussian(0, 0.5, level.random));
                        level.addParticle(ParticleTypes.HAPPY_VILLAGER, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
                    }
                    level.playSound(player, pos, Sounds.ABYSSAL_TROVE_DEPOSIT.value(), player.getSoundSource(), 1.0F, 1.0F);
                }

                // Pair to this abyssal trove if we just inserted an item
                SavedData.instance.getData(player).pairAbyssalTroveAtPos(pos);
            }
        }

        if (!blockEntity.get().getItem(0).isEmpty()) {
            if (abyssalTroveBlockEntity.rendererData.renderedItemTiers.isEmpty()) {
                abyssalTroveBlockEntity.updateRendererData();
            }

            blockEntity.get().setChanged();
            level.updateNeighborsAt(pos, this);
            level.sendBlockUpdated(pos, state, state, 3);
            blockEntity.get().onPlayerInsertItem(player);
            result = InvUtil.InteractionResult.INTERACT;
        }

        return InvUtil.getMinecraftItemInteractionResult(result);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Sneak clicking with an empty hand manually pairs you to the abyssal trove
        // Pairing used for showing ingredient tooltips and onitempickup notifications
        if (player.getMainHandItem().isEmpty() && player.isCrouching()) {
            if (!SavedData.instance.getData(player).getPairedAbyssalTrovePos().equals(pos)) {
                SavedData.instance.updateDataForPlayer(player, (data) -> data.pairAbyssalTroveAtPos(pos));

                Vec3 posVec = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                for (int i = 0; i < 20; i++) {
                    Vec3 particlePos = posVec.add(Utility.nextGaussian(0, 0.5, level.random), 0.8 + Utility.nextDouble(-0.125, 0.125, level.random), Utility.nextGaussian(0, 0.5, level.random));
                    Vec3 away = particlePos.subtract(posVec).normalize().scale(0.5);
                    level.addParticle(ParticleTypes.ENCHANT, particlePos.x, particlePos.y, particlePos.z, away.x, away.y, away.z);
                }
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, player.getSoundSource(), 1.0F, 1.0F);

            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get(), AbyssalTroveBlockEntity::tick);
    }

    @Override
    @Deprecated
    public void onRemove(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        Utility.dropContents(level, blockPos, blockState, newState);
        super.onRemove(blockState, level, blockPos, newState, isMoving);
    }
}
