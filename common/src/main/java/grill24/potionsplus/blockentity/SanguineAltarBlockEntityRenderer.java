package grill24.potionsplus.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SanguineAltarBlockEntityRenderer implements BlockEntityRenderer<SanguineAltarBlockEntity, SanguineAltarRenderState> {
    private final BlockModelResolver BlockModelResolver;
    private static final int CONVERTED_ITEM_DESCENT_TICKS = 20;
    private static final int CONVERTED_ITEM_SHRINK_DELAY_TICKS = 200;

    public SanguineAltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.getBlockModelResolver();
    }

    @Deprecated
    public void renderLegacy(@NotNull SanguineAltarBlockEntity blockEntity, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, int overlay, Vec3 cameraPos) {
        // Old render implementation removed. Rendering now uses state-based pipeline with extractRenderState() and submit().
    }

    @Override
    public SanguineAltarRenderState createRenderState() {
        return new SanguineAltarRenderState();
    }

    @Override
    public void extractRenderState(final SanguineAltarBlockEntity blockEntity, final SanguineAltarRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        // TODO: Extract altar data from blockEntity into state
    }

    @Override
    public void submit(final SanguineAltarRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        // TODO: Submit rendering using state data via submitNodeCollector
    }

    private static void drawRuneCircle(SanguineAltarBlockEntity blockEntity, Vector3f offset, Vector3f axis, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, float rollHertz, float itemScale, float radius, float spinDegrees) {
        // Get radians of rotation from spinHertz and total ticks (time)

        float healthDrain = blockEntity.getHealthDrainProgress();
        Vector3f[] points = RUtil.distributePointsOnCircle(8, axis, offset, ((float) Math.PI * 2) * ClientTickHandler.total() / 20 * rollHertz, radius, spinDegrees);
        for (int p = 0; p < points.length; p++) {
            if ((float) p / (float) points.length > healthDrain)
                break;

            Vector3f point = points[p];
            // Added 4 runes to the generic icon, so pick a different one for each point.
            ItemStack runeStack = RUNES[p % RUNES.length];
            RUtil.renderItemWithYaw(blockEntity, runeStack, new Vector3d(point.x(), point.y(), point.z()), 20, 0, p * 10, itemScale, matrices, vertexConsumers, light, overlay);
        }
    }

    private static final ItemStack[] RUNES = new ItemStack[]{
            DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_A_TEX_LOC),
            DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_B_TEX_LOC),
            DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_C_TEX_LOC),
            DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_D_TEX_LOC)
    };
}
