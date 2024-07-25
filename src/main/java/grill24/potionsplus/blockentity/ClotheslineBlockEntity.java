package grill24.potionsplus.blockentity;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ClotheslineBlockEntity extends InventoryBlockEntity {
    public ClotheslineBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.CLOTHESLINE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new PotionsPlusContainer(3, 1);
    }

    @Override
    public Container getItemHandler() {
        if(level != null && !ClotheslineBlock.isLeftEnd(getBlockState())) {
            BlockPos leftEnd = ClotheslineBlock.getLeftEnd(worldPosition, getBlockState());
            BlockState leftEndState = level.getBlockState(leftEnd);
            if(leftEndState.getBlock() instanceof ClotheslineBlock) {
                return ((ClotheslineBlockEntity) level.getBlockEntity(leftEnd)).getItemHandler();
            }
        }
        return super.getItemHandler();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ClotheslineBlockEntity blockEntity) {

    }
}
