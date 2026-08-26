package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.items.DynamicIconItems;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class SanguineAltarBlockEntityRenderer implements BlockEntityRenderer<SanguineAltarBlockEntity, SanguineAltarRenderState> {
    private final BlockModelResolver BlockModelResolver;
    private static final int CONVERTED_ITEM_DESCENT_TICKS = 20;
    private static final int CONVERTED_ITEM_SHRINK_DELAY_TICKS = 200;

    public SanguineAltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
    }

    @Override
    public SanguineAltarRenderState createRenderState() {
        return new SanguineAltarRenderState();
    }

    @Override
    public void extractRenderState(final SanguineAltarBlockEntity blockEntity, final SanguineAltarRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        state.altarState = blockEntity.state;
        ItemStack stack = blockEntity.state == SanguineAltarBlockEntity.State.CONVERTED ? blockEntity.chainedIngredientToDisplay : blockEntity.getItem(0);
        state.hasInputItem = !stack.isEmpty();
        if (!state.hasInputItem) {
            return;
        }

        state.timeItemPlaced = blockEntity.getTimeItemPlaced();
        state.startAnimationWorldPos.set(blockEntity.getStartAnimationWorldPos());
        state.restingPosition.set(blockEntity.getRestingPosition());
        state.restingRotation.set(blockEntity.getRestingRotation());
        state.inputAnimationDuration = blockEntity.getInputAnimationDuration();

        state.nextSpinTickDelay = blockEntity.getNextSpinTickDelay();
        state.nextSpinTotalRevolutions = blockEntity.getNextSpinTotalRevolutions();
        state.nextSpinHertz = blockEntity.getNextSpinHertz();
        state.healthDrainProgress = blockEntity.getHealthDrainProgress();

        Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.inputItem, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);

        if (state.altarState == SanguineAltarBlockEntity.State.CONVERTING) {
            ItemStack[] runes = getRunes();
            for (int i = 0; i < runes.length; i++) {
                Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.runeItems[i], runes[i], ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
            }
        }
    }

    @Override
    public void submit(final SanguineAltarRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if (!state.hasInputItem) {
            return;
        }

        final float scale = 0.625F;

        renderResolvedItemAnimation(state.inputItem, state, scale, 0, true, poseStack, submitNodeCollector, state.lightCoords);

        switch (state.altarState) {
            case CONVERTING -> {
                float yOffset = RUtil.ease(state, 0, 1F, state.inputAnimationDuration, SanguineAltarBlockEntity.CONVERSION_DURATION_HERTZ, RUtil::easeOutExpo);
                renderResolvedItemWithYaw(state.inputItem, state, new Vector3d(state.restingPosition.x, state.restingPosition.y + yOffset, state.restingPosition.z), 20, 0, state.restingRotation.y(), scale, poseStack, submitNodeCollector, state.lightCoords);

                Vector3f offset = new Vector3f(0, yOffset, 0);
                offset.add((float) state.restingPosition.x, (float) state.restingPosition.y, (float) state.restingPosition.z);
                Vector3f axis = new Vector3f(1f, 0, 0);

                float expansion = RUtil.ease(state, 0, 1, state.inputAnimationDuration, SanguineAltarBlockEntity.CONVERSION_DURATION_HERTZ, RUtil::easeOutBack);
                float spin = RUtil.ease(state, 0, 1080, state.inputAnimationDuration, SanguineAltarBlockEntity.CONVERSION_DURATION_HERTZ, RUtil::easeInExpo);
                drawRuneCircle(state, offset, axis, poseStack, submitNodeCollector, 0.1f, 0.1F, 0.3f * expansion, 0 + spin);
                drawRuneCircle(state, offset, axis, poseStack, submitNodeCollector, 0.2f, 0.08F, 0.33f * expansion, 90 - spin * 2);
                drawRuneCircle(state, offset, axis, poseStack, submitNodeCollector, 0.08f, 0.16F, 0.36f * expansion, 45 + spin * 3);
            }
            case CONVERTED -> {
                float spin = RUtil.doSpin(state, state.nextSpinTickDelay, state.nextSpinHertz, state.nextSpinTotalRevolutions) + state.restingRotation.y();
                float bobbingOffset = RUtil.getBobbingOffset(state, 0.25f, 0.2F, state.inputAnimationDuration + SanguineAltarBlockEntity.CONVERSION_TICKS + CONVERTED_ITEM_DESCENT_TICKS);

                // Fall back down to the resting position
                float yOffset = RUtil.ease(state, 1F, 0, state.inputAnimationDuration + SanguineAltarBlockEntity.CONVERSION_TICKS, CONVERTED_ITEM_DESCENT_TICKS / 20F, RUtil::easeOutBack);

                // Linear shrink sloooowly
                float size = RUtil.ease(state, 1F, 0, state.inputAnimationDuration + SanguineAltarBlockEntity.CONVERSION_TICKS + CONVERTED_ITEM_DESCENT_TICKS + CONVERTED_ITEM_SHRINK_DELAY_TICKS, 0.033F, RUtil::easeInSine);

                Vector3d pos = new Vector3d(state.restingPosition.x, state.restingPosition.y + bobbingOffset + yOffset, state.restingPosition.z);
                renderResolvedItemWithYaw(state.inputItem, state, pos, 20, 0, spin, scale * size, poseStack, submitNodeCollector, state.lightCoords);
            }
            default -> {
            }
        }
    }

    private static void renderResolvedItemAnimation(ItemStackRenderState itemState, ISingleStackDisplayer displayer, float scale, int ticksDelay, boolean hideOnFinish, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light) {
        float ticks = ClientTickHandler.total();
        float lerpFactor = (ticks - displayer.getTimeItemPlaced() - ticksDelay) / displayer.getInputAnimationDuration();

        if (!hideOnFinish || lerpFactor <= 1) {
            poseStack.pushPose();

            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));
            Quaternionf rotationStart = new Quaternionf().identity();
            Quaternionf rotation = RUtil.fromXYZDegrees(displayer.getRestingRotation());

            if (lerpFactor < 1) {
                Vector3d lerped = RUtil.lerp3d(displayer.getStartAnimationWorldPos(), displayer.getRestingPosition(), lerpFactor, RUtil::easeOutExpo);
                rotation = RUtil.slerp(rotationStart, rotation, RUtil.easeOutExpo(lerpFactor));
                poseStack.translate(lerped.x, lerped.y, lerped.z);
            } else {
                poseStack.translate(displayer.getRestingPosition().x, displayer.getRestingPosition().y, displayer.getRestingPosition().z);
            }

            poseStack.mulPose(rotation);
            poseStack.scale(scale, scale, scale);
            itemState.submit(poseStack, submitNodeCollector, light, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }
    }

    private static void renderResolvedItemWithYaw(ItemStackRenderState itemState, ISingleStackDisplayer displayer, Vector3d position, int tickDelay, int tickDuration, float yawDegrees, float scale, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light) {
        if (RUtil.isAnimationActive(displayer, tickDelay, tickDuration)) {
            poseStack.pushPose();
            Quaternionf rotation = RUtil.rotateY(yawDegrees);
            poseStack.translate(position.x, position.y, position.z);
            poseStack.mulPose(rotation);
            poseStack.scale(scale, scale, scale);
            itemState.submit(poseStack, submitNodeCollector, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    private static void drawRuneCircle(SanguineAltarRenderState state, Vector3f offset, Vector3f axis, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, float rollHertz, float itemScale, float radius, float spinDegrees) {
        float healthDrain = state.healthDrainProgress;
        Vector3f[] points = RUtil.distributePointsOnCircle(8, axis, offset, ((float) Math.PI * 2) * ClientTickHandler.total() / 20 * rollHertz, radius, spinDegrees);
        for (int p = 0; p < points.length; p++) {
            if ((float) p / (float) points.length > healthDrain)
                break;

            Vector3f point = points[p];
            ItemStackRenderState runeIcon = state.runeItems[p % state.runeItems.length];
            renderResolvedItemWithYaw(runeIcon, state, new Vector3d(point.x(), point.y(), point.z()), 20, 0, p * 10, itemScale, poseStack, submitNodeCollector, state.lightCoords);
        }
    }

    private static ItemStack[] RUNES;

    private static ItemStack[] getRunes() {
        if (RUNES == null) {
            RUNES = new ItemStack[]{
                    DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_A_TEX_LOC),
                    DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_B_TEX_LOC),
                    DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_C_TEX_LOC),
                    DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.SGA_D_TEX_LOC)
            };
        }
        return RUNES;
    }

}
