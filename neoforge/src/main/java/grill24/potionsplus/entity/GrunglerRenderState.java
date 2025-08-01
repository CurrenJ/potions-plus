package grill24.potionsplus.entity;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class GrunglerRenderState extends LivingEntityRenderState {
    private BlockState blockState;

    public GrunglerRenderState() {
        super();
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return blockState;
    }
}
