package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.FishTankBlockEntity;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.Utility;
import grill24.potionsplus.utility.registration.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishTankBlock extends Block implements EntityBlock {
    public static final IntegerProperty FRAME_VARIANT = IntegerProperty.create("frame", 0, 63);
    public static final IntegerProperty SAND_VARIANT = IntegerProperty.create("sand", 0, 23);

    public FishTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FRAME_VARIANT, 0).setValue(SAND_VARIANT, 0));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new FishTankBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(FRAME_VARIANT);
        blockStateBuilder.add(SAND_VARIANT);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        FishTankBlockEntity fishTankBlockEntity = (FishTankBlockEntity) level.getBlockEntity(pos);

        if (fishTankBlockEntity != null) {
            InvUtil.InteractionResult result = InvUtil.insertOnPlayerUseItem(level, pos, player, hand, SoundEvents.GENERIC_SPLASH);
            if (result != InvUtil.InteractionResult.PASS) {
                fishTankBlockEntity.onItemInserted(player, stack);
                return InvUtil.getMinecraftInteractionResult(result);
            }
        }

        return RuntimeTextureVariantModelGenerator.trySetTextureVariant(this, stack, state, level, pos, FRAME_VARIANT, SAND_VARIANT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InvUtil.InteractionResult result = InvUtil.extractOnPlayerUseWithoutItem(level, pos, player, true, SoundEvents.ITEM_FRAME_REMOVE_ITEM);

        return InvUtil.getMinecraftInteractionResult(result);
    }
}
