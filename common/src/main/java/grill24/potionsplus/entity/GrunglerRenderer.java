package grill24.potionsplus.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

import static grill24.potionsplus.utility.Utility.mc;

public class GrunglerRenderer extends LivingEntityRenderer<Grungler, GrunglerRenderState, GrunglerModel> {
    public static final Identifier GRUNGLER_TEXTURE_LOCATION = mc("textures/block/stone.png");

    private final BlockModelResolver blockModelResolver;

    public GrunglerRenderer(EntityRendererProvider.Context context) {
        super(context, new GrunglerModel(context.bakeLayer(LayerDefinitions.GRUNGLER)), 0.5F);

        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public Identifier getTextureLocation(GrunglerRenderState entityRenderState) {
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
        // TODO: Update block model in state using blockModelResolver
        // this.blockModelResolver.update(state.getBlockRenderState(), state.getBlockState(), ...);
    }

    @Override
    public void submit(GrunglerRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, pose, submitNodeCollector, camera);

        float yRot = state.bodyRot;
        pose.pushPose();
        pose.mulPose(RUtil.rotateY(yRot));
        pose.translate(-0.5F, 0.2, -0.5F);
        // TODO: Submit block model rendering using submitNodeCollector
        // state.getBlockRenderState().submit(pose, submitNodeCollector, state.lightCoords, ...);
        pose.popPose();
    }
}
