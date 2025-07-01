package grill24.potionsplus.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class GrunglerRenderLayer extends RenderLayer<GrunglerRenderState, GrunglerModel> {
    private final GrunglerModel model;

    public GrunglerRenderLayer(GrunglerRenderer renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new GrunglerModel(modelSet.bakeLayer(LayerDefinitions.GRUNGLER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, GrunglerRenderState entityRenderState, float yRot, float xRot) {
    }
}
