package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class HerbalistsLecternBlockEntityRenderer implements BlockEntityRenderer<HerbalistsLecternBlockEntity> {

    public final BlockRenderDispatcher blockRenderDispatcher;
    private ProfilerFiller profiler;

    public HerbalistsLecternBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        profiler = Minecraft.getInstance().getProfiler();
    }


    @Override
    public void render(HerbalistsLecternBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        double ticks = ClientTickHandler.total();

        //  Profiler
        profiler.push("herbalists_lectern_render");

        // Render
        ItemStack stack = blockEntity.getItem(0);
        if (!stack.isEmpty()) {
            // Lerp the item from the player's hand to the resting position
            final float animationDuration = 20F;
            float lerpFactor = (float) (ticks - blockEntity.getTimeItemPlaced()) / animationDuration;
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));


            RUtil.renderInputItemAnimation(stack, 0.75F, 0, false, blockEntity, matrices, vertexConsumers, light, overlay);


            // Circle of Items
            ItemStack[] infoStacks = blockEntity.getItemStacksToDisplay();
            Vector3f offset = new Vector3f(0.5f, 1.75F, 0.5f);
            Vector3f axis = new Vector3f(1f, 0, 0);
            float radius = 0.4F * RUtil.easeOutExpo(lerpFactor);
            if (infoStacks.length > 10)
                radius *= 1.5F;
            Vector3f[] points = RUtil.distributePointsOnCircle(infoStacks.length, axis, offset, (float) Math.toRadians(ticks), radius, (float) Math.toDegrees(Math.atan2(blockEntity.getLocalPlayerRelativePosition().z(), blockEntity.getLocalPlayerRelativePosition().x())));
            for (int p = 0; p < points.length; p++) {
                Vector3f point = points[p];
                matrices.pushPose();

                Vector3f direction = blockEntity.getLocalPlayerRelativePosition();
                direction.sub(point);
                direction.normalize();

                // Calculate yaw and pitch to face the player
                Quaternionf itemRotation = RUtil.rotateY(90 - (float) Math.toDegrees(Math.atan2(direction.z(), direction.x())));
                itemRotation.mul(RUtil.rotateZ((float) Math.toDegrees(Math.asin(direction.y()))));
                matrices.mulPose(itemRotation);

                // Rotate the translation vector by the negative of the initial rotation
                Quaternionf negativeRotation = new Quaternionf(itemRotation);
                negativeRotation.conjugate(); // conjugate (equivalent to inverse for unit Quaternionfs) to get the negative rotation
                point.rotate(negativeRotation);

                // Apply the rotated translation to the pose
                matrices.translate(point.x(), point.y(), point.z());

                matrices.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderStatic(infoStacks[p], ItemDisplayContext.GROUND,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);

                matrices.scale(0.35F, 0.35F, 0.35F);
                if (blockEntity.rendererData.isAmpUpgrade[p]) {
                    matrices.pushPose();
                    matrices.translate(-0.75F, 1F, 0);
                    Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GENERIC_ICON.value(), 1), ItemDisplayContext.GROUND,
                            light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                    matrices.popPose();
                }
                if (blockEntity.rendererData.isDurationUpgrade[p]) {
                    matrices.pushPose();
                    matrices.translate(0.75F, 1F, 0);
                    Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GENERIC_ICON.value(), 2), ItemDisplayContext.GROUND,
                            light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                    matrices.popPose();
                }

                matrices.popPose();
            }

            // Roman numerals for tier
            if (blockEntity.rendererData.ingredientTier > -1) {
                matrices.pushPose();
                Vector3f numeralsOffset = new Vector3f(0.5f, 1.75F, 0.5f);
                matrices.translate(numeralsOffset.x(), numeralsOffset.y(), numeralsOffset.z());

                Vector3f direction = blockEntity.getLocalPlayerRelativePosition();
                direction.sub(numeralsOffset);
                direction.normalize();

                // Calculate yaw and pitch to face the player
                Quaternionf numeralsRotation = RUtil.rotateY(90 - (float) Math.toDegrees(Math.atan2(direction.z(), direction.x())));
                // Lerp Quaternionf rotation from blockEntity.rendererData.ingredientTierNumeralsRotation to numeralsQuaternionf
                blockEntity.rendererData.ingredientTierNumeralsRotation = RUtil.slerp(blockEntity.rendererData.ingredientTierNumeralsRotation, numeralsRotation, tickDelta * 0.02f);
                matrices.mulPose(blockEntity.rendererData.ingredientTierNumeralsRotation);

                matrices.scale(0.5F, 0.5F, 0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GENERIC_ICON.value(), blockEntity.rendererData.ingredientTier + 3), ItemDisplayContext.GROUND,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                matrices.popPose();
            }
        }
        profiler.pop();
    }

}
