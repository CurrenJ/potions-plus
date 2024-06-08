package grill24.potionsplus.utility;

import grill24.potionsplus.blockentity.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public static InteractionResult giveAndTakeFromPlayerOnUseBlock(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, boolean allowTaking, SoundEvent insert, SoundEvent remove) {
        ItemStack hand = player.getItemInHand(interactionHand);
        if (!hand.isEmpty()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                Container itemHandler = inventoryBlockEntity.getItemHandler();
                for (int i = 0; i < itemHandler.getContainerSize(); i++) {
                    ItemStack toInsert = hand.copy();
                    toInsert.setCount(1);
                    if (inventoryBlockEntity.canPlaceItem(i, toInsert)) {
                        hand.shrink(toInsert.getCount());
                        toInsert.grow(itemHandler.getItem(i).getCount());
                        itemHandler.setItem(i, toInsert);

                        level.playSound(null, blockPos, insert, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.INSERT;
                    }
                }
            }
        } else if (allowTaking) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                Container itemHandler = inventoryBlockEntity.getItemHandler();
                for (int i = 0; i < itemHandler.getContainerSize(); i++) {
                    ItemStack stack = itemHandler.getItem(i);
                    if (!stack.isEmpty() && player.canTakeItem(stack)) {
                        player.addItem(stack);
                        itemHandler.setItem(i, ItemStack.EMPTY);

                        level.playSound(null, blockPos, remove, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.EXTRACT;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static net.minecraft.world.InteractionResult getMinecraftInteractionResult(InteractionResult result) {
        if(result == InteractionResult.PASS) {
            return net.minecraft.world.InteractionResult.PASS;
        } else {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
    }
}
