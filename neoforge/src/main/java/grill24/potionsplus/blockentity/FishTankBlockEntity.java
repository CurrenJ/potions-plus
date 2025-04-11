package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import static grill24.potionsplus.utility.Utility.ppId;

public class FishTankBlockEntity extends InventoryBlockEntity {
    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.FISH_TANK_BLOCK_ENTITY.value(), pos, state);
    }

    @Override
    protected int getSlots() {
        return 1;
    }
}
