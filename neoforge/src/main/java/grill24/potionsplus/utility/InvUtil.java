package grill24.potionsplus.utility;

import grill24.potionsplus.blockentity.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class InvUtil {
    public enum InteractionResult {
        INSERT,
        EXTRACT,
        INTERACT,
        PASS
    }

    @NotNull
    public static InteractionResult extractOnPlayerUseWithoutItem(Level level, BlockPos blockPos, Player player, boolean allowTaking, SoundEvent remove) {
        if (allowTaking) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                for (int i = 0; i < inventoryBlockEntity.getContainerSize(); i++) {
                    ItemStack stack = inventoryBlockEntity.getItem(i);
                    if (!stack.isEmpty() && player.canTakeItem(stack)) {
                        player.addItem(stack);
                        inventoryBlockEntity.setItem(i, ItemStack.EMPTY);

                        level.playSound(null, blockPos, remove, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.EXTRACT;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @NotNull
    public static InteractionResult insertOnPlayerUseItem(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, SoundEvent insert) {
        ItemStack hand = player.getItemInHand(interactionHand);
        if (!hand.isEmpty()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                for (int i = 0; i < inventoryBlockEntity.getContainerSize(); i++) {
                    ItemStack toInsert = hand.copy();
                    toInsert.setCount(1);
                    if (inventoryBlockEntity.canPlaceItem(i, toInsert)) {
                        hand.shrink(toInsert.getCount());
                        toInsert.grow(inventoryBlockEntity.getItem(i).getCount());
                        inventoryBlockEntity.setItem(i, toInsert);

                        level.playSound(null, blockPos, insert, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.INSERT;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static net.minecraft.world.ItemInteractionResult getMinecraftItemInteractionResult(InteractionResult result) {
        if (result == InteractionResult.PASS) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            return ItemInteractionResult.SUCCESS;
        }
    }

    public static net.minecraft.world.InteractionResult getMinecraftInteractionResult(InteractionResult result) {
        return getMinecraftItemInteractionResult(result).result();
    }
}
