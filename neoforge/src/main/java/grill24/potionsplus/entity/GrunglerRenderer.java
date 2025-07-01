package grill24.potionsplus.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import static grill24.potionsplus.utility.Utility.mc;

public class GrunglerRenderer extends LivingEntityRenderer<Grungler, GrunglerRenderState, GrunglerModel> {
    public static final ResourceLocation GRUNGLER_TEXTURE_LOCATION = mc("textures/block/stone.png");

    BlockRenderDispatcher blockRenderer;

    public GrunglerRenderer(EntityRendererProvider.Context context) {
        super(context, new GrunglerModel(context.bakeLayer(LayerDefinitions.GRUNGLER)), 0.5F);

        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public ResourceLocation getTextureLocation(GrunglerRenderState entityRenderState) {
        return GRUNGLER_TEXTURE_LOCATION;
    }

    @Override
    public GrunglerRenderState createRenderState() {
        return new GrunglerRenderState();
    }

    @Override
    public void extractRenderState(Grungler entity, GrunglerRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.setBlockState(entity.getBlockState());
    }

    @Override
    public void render(GrunglerRenderState state, PoseStack pose, MultiBufferSource buffer, int i) {
        super.render(state, pose, buffer, i);

        float yRot = state.bodyRot;
        pose.pushPose();
        pose.mulPose(RUtil.rotateY(yRot));
        pose.translate(-0.5F, 0.2, -0.5F);
        this.blockRenderer.renderSingleBlock(
                state.getBlockState(),
                pose,
                buffer,
                i,
                LivingEntityRenderer.getOverlayCoords(state, 0F)
        );
        pose.popPose();
    }
}
