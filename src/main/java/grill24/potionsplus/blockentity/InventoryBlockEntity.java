package grill24.potionsplus.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class InventoryBlockEntity extends BlockEntity {
    private final SimpleContainer container = createContainer();

    public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract SimpleContainer createContainer();

    public final Container getContainer() {
        return container;
    }
}
