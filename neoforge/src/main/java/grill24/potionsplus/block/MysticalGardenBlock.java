package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.MysticalGardenBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MysticalGardenBlock extends Block implements EntityBlock {
    
    public MysticalGardenBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new MysticalGardenBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Utility.createTickerHelper(type, Blocks.MYSTICAL_GARDEN_BLOCK_ENTITY.get(), MysticalGardenBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MysticalGardenBlockEntity gardenEntity) {
                // Allow charging with potion bottles
                if (stack.getItem() == Items.POTION) {
                    PotionContents potionContents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                    if (potionContents != null && gardenEntity.chargeWithPotion(potionContents)) {
                        if (!player.getAbilities().instabuild) {
                            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                player.setItemInHand(hand, emptyBottle);
                            } else if (!player.getInventory().add(emptyBottle)) {
                                player.drop(emptyBottle, false);
                            }
                        }
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MysticalGardenBlockEntity gardenEntity) {
            gardenEntity.performRandomTick(level, pos, random);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MysticalGardenBlockEntity gardenEntity) {
            return gardenEntity.getRedstoneSignal();
        }
        return 0;
    }
}