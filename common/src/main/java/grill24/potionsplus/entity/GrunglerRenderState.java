package grill24.potionsplus.entity;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class GrunglerRenderState extends LivingEntityRenderState {
    private BlockState blockState;
    private final BlockModelRenderState blockRenderState = new BlockModelRenderState();

    public GrunglerRenderState() {
        super();
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public BlockModelRenderState getBlockRenderState() {
        return blockRenderState;
    }
}
