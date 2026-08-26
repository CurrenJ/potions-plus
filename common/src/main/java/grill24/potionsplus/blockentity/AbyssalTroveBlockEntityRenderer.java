package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.items.DynamicIconItems;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AbyssalTroveBlockEntityRenderer implements BlockEntityRenderer<AbyssalTroveBlockEntity, AbyssalTroveRenderState> {
    private final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public AbyssalTroveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    @Override
    public AbyssalTroveRenderState createRenderState() {
        return new AbyssalTroveRenderState();
    }

    @Override
    public void extractRenderState(final AbyssalTroveBlockEntity blockEntity, final AbyssalTroveRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        blockEntity.currentDisplayRotation = RUtil.lerpAngle(blockEntity.currentDisplayRotation, blockEntity.degreesTowardsPlayer, partialTicks * 0.02f);
        state.currentDisplayRotation = blockEntity.currentDisplayRotation;

        state.renderedItemTiers.clear();

        for (Map.Entry<Integer, List<AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem>> entry : blockEntity.rendererData.renderedItemTiers.entrySet()) {
            int row = entry.getKey();

            float horizontalPaddingScalar = RUtil.ease(blockEntity, 0.5F, 1F, row * 4, 1F) * ICON_SCALE;
            float verticalPaddingScalar = 1F * ICON_SCALE;
            float verticalOffset = RUtil.ease(blockEntity, 1F, 1.25F, row * 4, 1F);

            AbyssalTroveBlockEntity.RendererData.State rendererState = blockEntity.rendererData.getState();
            int startTime = blockEntity.getTimeLastStateChange(rendererState);

            List<AbyssalTroveRenderState.RenderedItem> renderedItems = state.renderedItemTiers.computeIfAbsent(row, k -> new ArrayList<>());

            for (AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem item : entry.getValue()) {
                boolean isUnknownIngredient = item.icon.is(DynamicIconItems.GENERIC_ICON.getValue());

                Vector3d position = new Vector3d(item.position.x * horizontalPaddingScalar, item.position.y * verticalPaddingScalar, item.position.z * horizontalPaddingScalar);
                position.add(new Vector3d(0.5, verticalOffset, 0.5));
                position = RUtil.rotateAroundY(position, state.currentDisplayRotation + 90, new Vector3d(0.5, 0.5, 0.5));

                item.scale = RUtil.ease(() -> (float) startTime, item.scale, getTargetScale(rendererState, isUnknownIngredient, item.icon), getAnimationDuration(rendererState, row), 1F);
                item.subIconScale = RUtil.ease(() -> (float) startTime, item.subIconScale, getTargetSubIconScale(rendererState, isUnknownIngredient, item.icon), getAnimationDuration(rendererState, row), 1F);

                boolean isBlockItem = item.icon.getItem() instanceof BlockItem;
                AbyssalTroveRenderState.RenderedItem renderedItem = new AbyssalTroveRenderState.RenderedItem(position, item.scale, item.subIconScale, isBlockItem);
                Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderedItem.icon, item.icon, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);

                if (renderedItem.subIconScale > 0) {
                    for (ItemStack subIcon : item.subIcon) {
                        ItemStackRenderState subIconState = new ItemStackRenderState();
                        Minecraft.getInstance().getItemModelResolver().updateForTopItem(subIconState, subIcon, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
                        renderedItem.subIcons.add(subIconState);
                    }
                }

                renderedItems.add(renderedItem);
            }
        }
    }

    @Override
    public void submit(final AbyssalTroveRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        profiler.push("abyssal_trove_render");

        for (List<AbyssalTroveRenderState.RenderedItem> items : state.renderedItemTiers.values()) {
            for (AbyssalTroveRenderState.RenderedItem item : items) {
                poseStack.pushPose();

                poseStack.translate(item.position.x, item.position.y, item.position.z);
                poseStack.mulPose(RUtil.rotateY(-90 - state.currentDisplayRotation));

                poseStack.scale(item.scale, item.scale, item.scale);
                item.icon.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

                if (item.subIconScale > 0) {
                    for (ItemStackRenderState subIcon : item.subIcons) {
                        poseStack.scale(item.subIconScale, item.subIconScale, item.subIconScale);
                        if (item.isBlockItem) {
                            poseStack.translate(SUB_ICON_OFFSET_BLOCK.x, SUB_ICON_OFFSET_BLOCK.y, SUB_ICON_OFFSET_BLOCK.z);
                        } else {
                            poseStack.translate(SUB_ICON_OFFSET.x, SUB_ICON_OFFSET.y, SUB_ICON_OFFSET.z);
                        }
                        subIcon.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    }
                }

                poseStack.popPose();
            }
        }

        profiler.pop();
    }

    private static final float SUB_ICON_SCALE = 0.25f;
    private static final float ICON_SCALE = 0.125F;
    private static final Vector3d SUB_ICON_OFFSET = new Vector3d(-1.2, 1.2, -0.2);
    private static final Vector3d SUB_ICON_OFFSET_BLOCK = new Vector3d(-1.2, 1.2, -1);

    private static final float UNKNOWN_INGREDIENT_SCALE = 0.5F * ICON_SCALE;

    private float getTargetScale(AbyssalTroveBlockEntity.RendererData.State state, boolean isUnknownIngredient, ItemStack icon) {
        if (!AbyssalTroveBlockEntity.isIngredientVisibleInState(state, isUnknownIngredient, icon)) {
            return 0;
        }
        return state == AbyssalTroveBlockEntity.RendererData.State.ALL_LABELED_INGREDIENTS && isUnknownIngredient ? UNKNOWN_INGREDIENT_SCALE : ICON_SCALE;
    }

    private float getTargetSubIconScale(AbyssalTroveBlockEntity.RendererData.State state, boolean isUnknownIngredient, ItemStack icon) {
        return switch (state) {
            case HIDDEN, ALL_INGREDIENTS -> 0;
            case ALL_LABELED_INGREDIENTS, ONLY_COMMON_INGREDIENTS, ONLY_RARE_INGREDIENTS, ONLY_DURATION_UPGRADES,
                 ONLY_AMPLIFICATION_UPGRADES -> SUB_ICON_SCALE;
        };
    }

    private int getAnimationDuration(AbyssalTroveBlockEntity.RendererData.State state, int row) {
        return switch (state) {
            case HIDDEN, ALL_INGREDIENTS -> row * 4;
            case ALL_LABELED_INGREDIENTS, ONLY_COMMON_INGREDIENTS, ONLY_RARE_INGREDIENTS, ONLY_DURATION_UPGRADES,
                 ONLY_AMPLIFICATION_UPGRADES -> row * 2;
        };
    }
}
