package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.render.LeashRenderer;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
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

import java.util.Optional;


public class ClotheslineBlockEntityRenderer implements BlockEntityRenderer<ClotheslineBlockEntity, ClotheslineRenderState> {
    public final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public static final Vector3f OFFSET_IN_POST_BLOCKS = new Vector3f(0.5f, 0.9375f, 0.5f);
    public static final Vector3f ITEM_OFFSET = new Vector3f(0, -0.2f, 0);

    public ClotheslineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    @Override
    public ClotheslineRenderState createRenderState() {
        return new ClotheslineRenderState();
    }

    @Override
    public void extractRenderState(final ClotheslineBlockEntity blockEntity, final ClotheslineRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        state.hangingItems.clear();
        state.hasFencePost = false;

        Level level = blockEntity.getLevel();
        // A clothesline spans two block entities. Only the left end draws the rope, posts and items, so the
        // shared geometry isn't emitted twice. (Pre-26.1 this was a per-frame "already rendered" set cleared
        // by a render-stage event, which no longer lines up with extract/submit.)
        state.isLeftEnd = level != null && ClotheslineBlock.isLeftEnd(blockEntity.getBlockState());
        if (!state.isLeftEnd) {
            return;
        }

        BlockPos left = blockEntity.getBlockPos();
        BlockPos right = ClotheslineBlock.getOtherEnd(left, blockEntity.getBlockState());

        state.facing = blockEntity.getBlockState().getValue(ClotheslineBlock.FACING);

        state.blockLightStart = level.getBrightness(LightLayer.BLOCK, left);
        state.blockLightEnd = level.getBrightness(LightLayer.BLOCK, right);
        state.skyLightStart = level.getBrightness(LightLayer.SKY, left);
        state.skyLightEnd = level.getBrightness(LightLayer.SKY, right);

        state.leftRelative = left.subtract(blockEntity.getBlockPos());
        state.rightRelative = right.subtract(blockEntity.getBlockPos());

        // Rope endpoints relative to the block entity origin - submit() has no access to world coordinates
        state.leftVec = Vec3.atLowerCornerOf(state.leftRelative).add(OFFSET_IN_POST_BLOCKS.x(), OFFSET_IN_POST_BLOCKS.y(), OFFSET_IN_POST_BLOCKS.z());
        state.rightVec = Vec3.atLowerCornerOf(state.rightRelative).add(OFFSET_IN_POST_BLOCKS.x(), OFFSET_IN_POST_BLOCKS.y(), OFFSET_IN_POST_BLOCKS.z());

        Optional<BlockState> post = blockEntity.getFencePostBlockState();
        state.hasFencePost = post.isPresent();
        if (state.hasFencePost) {
            BlockModelResolver.update(state.fencePostModel, post.get(), BlockDisplayContext.create());
            state.fenceLightCoords = LightCoordsUtil.pack(state.blockLightStart, state.skyLightStart);
        }

        Quaternionf itemOrientation = orientItemToClotheslineOrientation(blockEntity.getBlockState());
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            ClotheslineRenderState.HangingItem hangingItem = new ClotheslineRenderState.HangingItem();
            hangingItem.slot = i;
            hangingItem.position = ClotheslineBlockEntityBakedRenderData.getItemPoint(blockEntity.getBlockPos(), blockEntity.getBlockState(), i, false);
            hangingItem.orientation = itemOrientation;
            // Items hang slack while drying and settle as the recipe completes
            hangingItem.swingAmplitude = 15 * (1 - blockEntity.getProgress(i));

            // TODO: Duplicate code from LeashRenderer - optimize
            float stepFraction = (i + 1f) / (blockEntity.getContainerSize() + 1);
            int mixedBlockLight = (int) Mth.lerp(stepFraction, state.blockLightStart, state.blockLightEnd);
            int mixedSkyLight = (int) Mth.lerp(stepFraction, state.skyLightStart, state.skyLightEnd);
            hangingItem.lightCoords = LightCoordsUtil.pack(mixedBlockLight, mixedSkyLight);

            Minecraft.getInstance().getItemModelResolver().updateForTopItem(hangingItem.state, stack, ItemDisplayContext.FIXED, level, null, 0);
            state.hangingItems.add(hangingItem);
        }
    }

    @Override
    public void submit(final ClotheslineRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if (!state.isLeftEnd) {
            return;
        }

        profiler.push("clothesline_render");

        // The "clothesline" itself (repurposed lead rendering code from vanilla) between the two posts
        LeashRenderer.submitLeashBetweenPoints(BlockPos.ZERO,
                state.leftVec, state.rightVec, poseStack, submitNodeCollector,
                state.blockLightStart, state.blockLightEnd, state.skyLightStart, state.skyLightEnd);

        if (state.hasFencePost) {
            submitPost(state, poseStack, submitNodeCollector, state.leftRelative);
            submitPost(state, poseStack, submitNodeCollector, state.rightRelative);
        }

        for (ClotheslineRenderState.HangingItem hangingItem : state.hangingItems) {
            poseStack.pushPose();

            poseStack.translate(hangingItem.position.x(), hangingItem.position.y(), hangingItem.position.z());
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(hangingItem.orientation);
            // Swing the item a bit :)
            float swing = (float) (Math.sin(ClientTickHandler.total() / 10 + hangingItem.slot * 7) * hangingItem.swingAmplitude);
            poseStack.mulPose(RUtil.rotateX(swing));
            poseStack.translate(ITEM_OFFSET.x(), ITEM_OFFSET.y(), ITEM_OFFSET.z());

            hangingItem.state.submit(poseStack, submitNodeCollector, hangingItem.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        profiler.pop();
    }

    private void submitPost(ClotheslineRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, BlockPos relative) {
        poseStack.pushPose();
        poseStack.translate(relative.getX(), relative.getY(), relative.getZ());
        state.fencePostModel.submit(poseStack, submitNodeCollector, state.fenceLightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private Quaternionf orientItemToClotheslineOrientation(BlockState clothesLine) {
        Direction property = clothesLine.getValue(ClotheslineBlock.FACING);
        if (property.getAxis() == Direction.Axis.X) {
            return RUtil.rotateY(90);
        }
        return new Quaternionf().identity();
    }

}
