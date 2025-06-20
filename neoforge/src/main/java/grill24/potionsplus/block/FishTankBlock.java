package grill24.potionsplus.block;

import grill24.potionsplus.blockentity.FishTankBlockEntity;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FishTankBlock extends Block implements EntityBlock {
    private final Component tooltip;

    public FishTankBlock(Properties properties, Component tooltip) {
        super(properties);
        this.tooltip = tooltip;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new FishTankBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        ItemStack usedItem = stack.copy();
        FishTankBlockEntity fishTankBlockEntity = (FishTankBlockEntity) level.getBlockEntity(pos);

        if (fishTankBlockEntity != null) {
            InvUtil.InteractionResult result = InvUtil.insertOnPlayerUseItem(level, pos, player, hand, SoundEvents.GENERIC_SPLASH);
            if (result != InvUtil.InteractionResult.PASS) {
                fishTankBlockEntity.onItemInserted(player, usedItem);
                return InvUtil.getMinecraftInteractionResult(result);
            }
        }

        return updateRenderBlockStates(stack, level, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InvUtil.InteractionResult result = InvUtil.extractOnPlayerUseWithoutItem(level, pos, player, true, SoundEvents.ITEM_FRAME_REMOVE_ITEM);

        return InvUtil.getMinecraftInteractionResult(result);
    }

    private InteractionResult updateRenderBlockStates(ItemStack usedItem, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FishTankBlockEntity fishTankBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (fishTankBlockEntity.updateRenderStates(usedItem)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public Component getAnimatedTooltip() {
        return tooltip;
    }

    @SubscribeEvent
    public static void onItemTooltip(final AnimatedItemTooltipEvent.Add event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FishTankBlock fishTankBlock) {
            event.addTooltipMessage(AnimatedItemTooltipEvent.TooltipLines.of(
                    ppId("fish_tank"), 0, fishTankBlock.getAnimatedTooltip()));
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, Orientation neighborOrientation, boolean b) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof FishTankBlockEntity fishTankBlockEntity) {
            fishTankBlockEntity.updateFaces();
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos placedAt, BlockState blockState, @javax.annotation.Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, placedAt, blockState, placer, itemStack);
        BlockEntity blockEntity = level.getBlockEntity(placedAt);
        if (blockEntity instanceof FishTankBlockEntity fishTankBlockEntity) {
            fishTankBlockEntity.updateFaces();
        }
    }
}
