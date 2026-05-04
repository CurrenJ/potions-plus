package grill24.potionsplus.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.render.LeashRenderer;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ClotheslineBlockEntityRenderer implements BlockEntityRenderer<ClotheslineBlockEntity, ClotheslineRenderState> {
    public final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public static final Vector3f OFFSET_IN_POST_BLOCKS = new Vector3f(0.5f, 0.9375f, 0.5f);
    public static final Vector3f ITEM_OFFSET = new Vector3f(0, -0.2f, 0);

    public ClotheslineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    @Deprecated
    public void renderLegacy(ClotheslineBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, Vec3 cameraPos) {
        // Old render implementation removed. Rendering now uses state-based pipeline with extractRenderState() and submit().
    }

    @Override
    public ClotheslineRenderState createRenderState() {
        return new ClotheslineRenderState();
    }

    @Override
    public void extractRenderState(final ClotheslineBlockEntity blockEntity, final ClotheslineRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        // TODO: Extract clothesline data from blockEntity into state
    }

    @Override
    public void submit(final ClotheslineRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        // TODO: Submit rendering using state data via submitNodeCollector
    }

    private Quaternionf orientItemToClotheslineOrientation(BlockState clothesLine) {
        Direction property = clothesLine.getValue(ClotheslineBlock.FACING);
        if (property.getAxis() == Direction.Axis.X) {
            return RUtil.rotateY(90);
        }
        return new Quaternionf().identity();
    }

}
