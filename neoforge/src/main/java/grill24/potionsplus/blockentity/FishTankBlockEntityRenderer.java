package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FishTankBlockEntityRenderer implements BlockEntityRenderer<FishTankBlockEntity> {
    public final BlockRenderDispatcher blockRenderDispatcher;

    public FishTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(FishTankBlockEntity fishTankBlockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int i, int i1) {}
}
