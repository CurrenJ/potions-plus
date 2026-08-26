package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class HerbalistsLecternBlockEntityRenderer implements BlockEntityRenderer<HerbalistsLecternBlockEntity, HerbalistsLecternRenderState> {

    public final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public HerbalistsLecternBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    @Override
    public HerbalistsLecternRenderState createRenderState() {
        return new HerbalistsLecternRenderState();
    }

    @Override
    public void extractRenderState(final HerbalistsLecternBlockEntity blockEntity, final HerbalistsLecternRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        ItemStack inputStack = blockEntity.getItem(0);
        state.hasInputItem = !inputStack.isEmpty();

        if (!state.hasInputItem) {
            return;
        }

        state.timeItemPlaced = blockEntity.getTimeItemPlaced();
        state.startAnimationWorldPos.set(blockEntity.getStartAnimationWorldPos());
        state.restingPosition.set(blockEntity.getRestingPosition());
        state.restingRotation.set(blockEntity.getRestingRotation());
        state.inputAnimationDuration = blockEntity.getInputAnimationDuration();

        float ticks = ClientTickHandler.total();
        state.lerpFactor = Math.max(0, Math.min((ticks - state.timeItemPlaced) / state.inputAnimationDuration, 1));

        Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.inputItem, inputStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);

        state.icons.clear();
        for (HerbalistsLecternBlockEntity.RendererData.IconData iconData : blockEntity.rendererData.allIcons) {
            ItemStackRenderState iconState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(iconState, iconData.displayStack().getItemStack(), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);

            List<ItemStackRenderState> subIconStates = new ArrayList<>();
            for (PpIngredient subIcon : iconData.subIcons()) {
                ItemStackRenderState subIconState = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().updateForTopItem(subIconState, subIcon.getItemStack(), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
                subIconStates.add(subIconState);
            }
            state.icons.add(new HerbalistsLecternRenderState.IconEntry(iconState, subIconStates));
        }

        state.hasCenterDisplay = !blockEntity.rendererData.centerDisplayStack.isEmpty();
        if (state.hasCenterDisplay) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.centerDisplayStack, blockEntity.rendererData.centerDisplayStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);

            Vector3f numeralsOffset = new Vector3f(0.5f, 1.75F, 0.5f);
            Vector3f direction = new Vector3f(blockEntity.getLocalPlayerRelativePosition());
            direction.sub(numeralsOffset);
            direction.normalize();
            Quaternionf targetRotation = RUtil.rotateY(90 - (float) Math.toDegrees(Math.atan2(direction.z(), direction.x())));
            blockEntity.rendererData.ingredientTierNumeralsRotation = RUtil.slerp(blockEntity.rendererData.ingredientTierNumeralsRotation, targetRotation, partialTicks * 0.02f);
            state.numeralsRotation.set(blockEntity.rendererData.ingredientTierNumeralsRotation);
        }

        state.playerRelativePosition.set(blockEntity.getLocalPlayerRelativePosition());
    }

    @Override
    public void submit(final HerbalistsLecternRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if (!state.hasInputItem) {
            return;
        }

        profiler.push("herbalists_lectern_render");

        // Lerp the item from the player's hand to the resting position
        poseStack.pushPose();
        Quaternionf itemRotation = RUtil.fromXYZDegrees(state.restingRotation);
        if (state.lerpFactor < 1) {
            Vector3d lerpedPos = RUtil.lerp3d(state.startAnimationWorldPos, state.restingPosition, state.lerpFactor, RUtil::easeOutExpo);
            itemRotation = RUtil.slerp(new Quaternionf().identity(), itemRotation, RUtil.easeOutExpo(state.lerpFactor));
            poseStack.translate(lerpedPos.x, lerpedPos.y, lerpedPos.z);
        } else {
            poseStack.translate(state.restingPosition.x, state.restingPosition.y, state.restingPosition.z);
        }
        poseStack.mulPose(itemRotation);
        poseStack.scale(0.75F, 0.75F, 0.75F);
        state.inputItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        // ----- Render circle of icons -----
        List<HerbalistsLecternRenderState.IconEntry> iconsToDisplay = state.icons;

        Vector3f offset = new Vector3f(0.5f, 1.75F, 0.5f);
        Vector3f axis = new Vector3f(1f, 0, 0);
        float radius = 0.4F * RUtil.easeOutExpo(state.lerpFactor);

        // Increase radius if there are more than 10 icons to display
        if (iconsToDisplay.size() > 10)
            radius *= 1.5F;

        float ticks = ClientTickHandler.total();

        // Distribute the icons on a circle around the lectern
        Vector3f[] points = RUtil.distributePointsOnCircle(iconsToDisplay.size(), axis, offset, (float) Math.toRadians(ticks), radius, (float) Math.toDegrees(Math.atan2(state.playerRelativePosition.z(), state.playerRelativePosition.x())));
        Vector3f[] subIconPositions = new Vector3f[]{new Vector3f(1F, 1.25F, 0.25F), new Vector3f(1F, 0.7F, 0.25F), new Vector3f(1F, 0.15F, 0.25F)};
        for (int p = 0; p < points.length; p++) {
            poseStack.pushPose();
            Vector3f point = points[p];

            // Calculate direction to local player
            Vector3f direction = new Vector3f(state.playerRelativePosition);
            direction.sub(point);
            direction.normalize();
            // Calculate yaw and pitch to face the player
            Quaternionf pointRotation = RUtil.rotateY(90 - (float) Math.toDegrees(Math.atan2(direction.z(), direction.x())));
            pointRotation.mul(RUtil.rotateZ((float) Math.toDegrees(Math.asin(direction.y()))));
            poseStack.mulPose(pointRotation);
            // Rotate the translation vector by the negative of the initial rotation
            Quaternionf negativeRotation = new Quaternionf(pointRotation);
            negativeRotation.conjugate();
            point.rotate(negativeRotation);
            // Apply the rotated translation to the pose
            poseStack.translate(point.x(), point.y(), point.z());

            // Render icon
            poseStack.scale(0.5F, 0.5F, 0.5F);
            HerbalistsLecternRenderState.IconEntry iconEntry = iconsToDisplay.get(p);
            iconEntry.icon().submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            // Render sub-icons
            poseStack.scale(0.2F, 0.2F, 0.2F);
            List<ItemStackRenderState> subIcons = iconEntry.subIcons();
            for (int s = 0; s < subIcons.size() && s < subIconPositions.length; s++) {
                poseStack.pushPose();
                Vector3f subIconPosition = subIconPositions[s];
                poseStack.translate(subIconPosition.x(), subIconPosition.y(), subIconPosition.z());
                subIcons.get(s).submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }

            poseStack.popPose();
        }

        if (state.hasCenterDisplay) {
            poseStack.pushPose();
            Vector3f numeralsOffset = new Vector3f(0.5f, 1.75F, 0.5f);
            poseStack.translate(numeralsOffset.x(), numeralsOffset.y(), numeralsOffset.z());
            poseStack.mulPose(state.numeralsRotation);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            state.centerDisplayStack.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        profiler.pop();
    }

}
