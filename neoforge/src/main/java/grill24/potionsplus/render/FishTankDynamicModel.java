package grill24.potionsplus.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FishTankDynamicModel implements IDynamicBakedModel {
    private final BakedModel base;

    public FishTankDynamicModel(BakedModel base) {
        this.base = base;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();
        // Add the base model's quads. Can also do something different with the quads here, depending on what you need.
        quads.addAll(base.getQuads(state, side, rand, extraData, renderType));
        // add other elements to the quads list as needed here
        return quads;
    }

    // Apply the base model's transforms to our model as well.
    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return base.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.base.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.base.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.base.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.base.getOverrides();
    }
}
