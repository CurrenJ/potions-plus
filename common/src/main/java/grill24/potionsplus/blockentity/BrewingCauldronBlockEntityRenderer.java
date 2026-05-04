package grill24.potionsplus.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.Vec3;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class BrewingCauldronBlockEntityRenderer implements BlockEntityRenderer<BrewingCauldronBlockEntity, BrewingCauldronRenderState> {

    public final BlockModelResolver BlockModelResolver;

    public BrewingCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.getBlockModelResolver();
    }


    @Deprecated
    public void renderLegacy(BrewingCauldronBlockEntity blockEntity, float partialTick, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, Vec3 cameraPos) {
        // Old render implementation removed. Rendering now uses state-based pipeline with extractRenderState() and submit().
    }

    @Override
    public BrewingCauldronRenderState createRenderState() {
        return new BrewingCauldronRenderState();
    }

    @Override
    public void extractRenderState(final BrewingCauldronBlockEntity blockEntity, final BrewingCauldronRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        // TODO: Extract brew data from blockEntity into state
    }

    @Override
    public void submit(final BrewingCauldronRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        // TODO: Submit rendering using state data via submitNodeCollector
    }

    public static double sin(double ticks, double amplitude, double hertz, double phase) {
        return Math.sin(ticks / 20F * hertz * Math.PI * 2 + phase) * amplitude;
    }

    public static double sin(double ticks, double amplitude, double hertz) {
        return sin(ticks, amplitude, hertz, 0);
    }
}
