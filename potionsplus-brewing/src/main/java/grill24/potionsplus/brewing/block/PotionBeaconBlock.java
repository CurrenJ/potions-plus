package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.HerbalistsLecternSounds;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PotionBeaconBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public PotionBeaconBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PotionBeaconBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Cache items before interaction
        Optional<PotionBeaconBlockEntity> blockEntity = level.getBlockEntity(pos, Blocks.POTION_BEACON_BLOCK_ENTITY.get());
        if (blockEntity.isEmpty()) {
            return InteractionResult.FAIL;
        }
        PotionBeaconBlockEntity potionBeaconBlockEntity = blockEntity.get();

        // Do interaction
        InvUtil.InteractionResult result = InvUtil.InteractionResult.PASS;
        if (player.getItemInHand(hand).has(DataComponents.POTION_CONTENTS)) {
            result = InvUtil.insertOnPlayerUseItem(level, pos, player, hand, SoundEvents.ITEM_FRAME_ADD_ITEM);
        }

        // If an item was inserted by a player, update the animation state
        if (result == InvUtil.InteractionResult.INSERT) {
            potionBeaconBlockEntity.setChanged();
            level.updateNeighborsAt(pos, this);
            potionBeaconBlockEntity.onPlayerInsertItem(player);

            if (level.isClientSide) {
                if (potionBeaconBlockEntity.sounds == null) {
                    potionBeaconBlockEntity.sounds = new HerbalistsLecternSounds();
                }
                potionBeaconBlockEntity.sounds.playSoundAppear(pos);
            }
        }

        return InvUtil.getMinecraftInteractionResult(result);

    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
// Cache items before interaction
        Optional<PotionBeaconBlockEntity> blockEntity = level.getBlockEntity(pos, Blocks.POTION_BEACON_BLOCK_ENTITY.get());
        if (blockEntity.isEmpty()) {
            return InteractionResult.FAIL;
        }
        PotionBeaconBlockEntity PotionBeaconBlockEntity = blockEntity.get();

        // Do interaction
        InvUtil.InteractionResult result = InvUtil.extractOnPlayerUseWithoutItem(level, pos, player, true, SoundEvents.ITEM_FRAME_ADD_ITEM);
        if (result == InvUtil.InteractionResult.EXTRACT && level.isClientSide) {
            if (PotionBeaconBlockEntity.sounds == null) {
                PotionBeaconBlockEntity.sounds = new HerbalistsLecternSounds();
            }
            PotionBeaconBlockEntity.sounds.playSoundDisappear(pos);
        }

        if (result == InvUtil.InteractionResult.PASS && player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundDisplayAlertWithItemStackName("block.potionsplus.potion_beacon.hint"));
        }

        return InvUtil.getMinecraftInteractionResult(result);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.POTION_BEACON_BLOCK_ENTITY.get(), PotionBeaconBlockEntity::tick);
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos) {
        Optional<PotionBeaconBlockEntity> PotionBeaconBlockEntity = level.getBlockEntity(blockPos, Blocks.POTION_BEACON_BLOCK_ENTITY.get());
        return PotionBeaconBlockEntity.filter(potionBeaconBlockEntity -> !potionBeaconBlockEntity.getItem(0).isEmpty()).map(potionBeaconBlockEntity -> 15).orElse(0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LIT, false);
    }
}
