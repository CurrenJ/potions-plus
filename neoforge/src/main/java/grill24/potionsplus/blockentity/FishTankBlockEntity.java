package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Random;

import java.util.Optional;

public class FishTankBlockEntity extends InventoryBlockEntity {
    FishTankBlockEntityRenderer.Kelp[] kelpList = new FishTankBlockEntityRenderer.Kelp[0];

    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.FISH_TANK_BLOCK_ENTITY.value(), pos, state);

        kelpList = FishTankBlockEntityRenderer.generateKelp(new Random(getBlockPos().hashCode()));
    }

    @Override
    protected int getSlots() {
        return 5;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if ((stack.has(DataComponents.FISH_SIZE) || stack.is(Tags.Items.BAIT) && index == 0) ||
                (stack.is(Items.KELP) && index == 1)) {
            return super.canPlaceItem(index, stack);
        }

        return false;
    }

    public Optional<ItemStack> getFish() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack.has(DataComponents.FISH_SIZE)) {
                return Optional.of(itemStack);
            }
        }
        return Optional.empty();
    }

    public int getKelp() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack.is(Items.KELP)) {
                return itemStack.getCount();
            }
        }
        return 0;
    }

    public FishTankBlockEntityRenderer.Kelp[] getKelpRenderData() {
        return kelpList;
    }
}
