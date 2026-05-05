package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.block.PotionBeaconBlock;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class PotionBeaconBlockEntityRenderer implements BlockEntityRenderer<PotionBeaconBlockEntity, PotionBeaconRenderState> {

    public final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public PotionBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }


    @Deprecated
    public void renderLegacy(PotionBeaconBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, Vec3 cameraPos) {
        // Old render implementation removed. Rendering now uses state-based pipeline with extractRenderState() and submit().
    }

    @Override
    public PotionBeaconRenderState createRenderState() {
        return new PotionBeaconRenderState();
    }

    @Override
    public void extractRenderState(final PotionBeaconBlockEntity blockEntity, final PotionBeaconRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        // TODO: Extract beacon data from blockEntity into state
    }

    @Override
    public void submit(final PotionBeaconRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        // TODO: Submit rendering using state data via submitNodeCollector
    }

}
