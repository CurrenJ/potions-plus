package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

            // ----- Render Circle of Items -----
            List<HerbalistsLecternBlockEntity.RendererData.IconData> iconsToDisplay = blockEntity.rendererData.allIcons;

            Vector3f offset = new Vector3f(0.5f, 1.75F, 0.5f);
            Vector3f axis = new Vector3f(1f, 0, 0);
            float radius = 0.4F * RUtil.easeOutExpo(lerpFactor);

            // Increase radius if there are more than 10 icons to display
            if (iconsToDisplay.size() > 10)
                radius *= 1.5F;

            // Distribute the icons on a circle around the lectern
            Vector3f[] points = RUtil.distributePointsOnCircle(iconsToDisplay.size(), axis, offset, (float) Math.toRadians(ticks), radius, (float) Math.toDegrees(Math.atan2(blockEntity.getLocalPlayerRelativePosition().z(), blockEntity.getLocalPlayerRelativePosition().x())));
            for (int p = 0; p < points.length; p++) {
                matrices.pushPose();
                Vector3f point = points[p];

                // Calculate direction to local player
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

                // Render icon
                matrices.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderStatic(iconsToDisplay.get(p).displayStack().getItemStack(), ItemDisplayContext.GROUND,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);


                // Render sub-icons
                matrices.scale(0.2F, 0.2F, 0.2F);
                Vector3f[] positions = new Vector3f[] {new Vector3f(1F, 1.25F, 0.25F), new Vector3f(1F, 0.7F, 0.25F), new Vector3f(1F, 0.15F, 0.25F)};
                List<PpIngredient> subIcons = iconsToDisplay.get(p).subIcons();
                for (int s = 0;  s < subIcons.size() && s < positions.length; s++) {
                    matrices.pushPose();
                    Vector3f subIconPosition = positions[s];
                    matrices.translate(subIconPosition.x(), subIconPosition.y(), subIconPosition.z());
                    Minecraft.getInstance().getItemRenderer().renderStatic(subIcons.get(s).getItemStack(), ItemDisplayContext.GROUND,
                            light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                    matrices.popPose();
                }

                matrices.popPose();
            }


            if (!blockEntity.rendererData.centerDisplayStack.isEmpty()) {
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

                Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.rendererData.centerDisplayStack, ItemDisplayContext.GROUND,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                matrices.popPose();
            }


        }
        profiler.pop();
    }

}
