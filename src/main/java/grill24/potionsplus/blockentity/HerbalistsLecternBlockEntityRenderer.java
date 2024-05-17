package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

public class HerbalistsLecternBlockEntityRenderer implements BlockEntityRenderer<HerbalistsLecternBlockEntity> {

    public final BlockRenderDispatcher blockRenderDispatcher;

    public HerbalistsLecternBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }


    @Override
    public void render(HerbalistsLecternBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        double ticks = ClientTickHandler.total();

        //  Profiler
        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
        profiler.push("herbalists_lectern_render");

        // Render
        ItemStack stack = blockEntity.getItemHandler().getItem(0);
        if (!stack.isEmpty()) {
            // Item on lectern
            matrices.pushPose();

            // Lerp the item from the player's hand to the resting position
            final float animationDuration = 20F;
            float lerpFactor = (float) (ticks - blockEntity.getTimeItemPlaced()) / animationDuration;
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));

            int rotationDegreesStart = 0;
            int rotationDegreesEnd = 90;
            Quaternion rotation = Vector3f.XP.rotationDegrees(rotationDegreesEnd);

            if (lerpFactor < 1) {
                Vector3d startAnimationTranslation = blockEntity.getStartAnimationWorldPos();

                Vector3d lerped = Utility.lerp3d(startAnimationTranslation, HerbalistsLecternBlockEntity.RendererData.itemRestingPositionTranslation, lerpFactor, Utility::easeOutExpo);
                rotation = Vector3f.XP.rotationDegrees(Utility.lerp(rotationDegreesStart, rotationDegreesEnd, Utility.easeOutExpo(lerpFactor)));

                matrices.translate(lerped.x, lerped.y, lerped.z);
            } else {
                matrices.translate(HerbalistsLecternBlockEntity.RendererData.itemRestingPositionTranslation.x, HerbalistsLecternBlockEntity.RendererData.itemRestingPositionTranslation.y, HerbalistsLecternBlockEntity.RendererData.itemRestingPositionTranslation.z);
            }

            matrices.mulPose(rotation);
            matrices.scale(1.25F, 1.25F, 1.25F);

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND,
                    light, overlay, matrices, vertexConsumers, 0);
            matrices.popPose();


            // Circle of Items
            ItemStack[] infoStacks = blockEntity.getItemStacksToDisplay();
            Vector3f offset = new Vector3f(0.5f, 1.75F, 0.5f);
            Vector3f axis = new Vector3f(1f, 0, 0);
            float radius = 0.4F * Utility.easeOutExpo(lerpFactor);
            if (infoStacks.length > 10)
                radius *= 1.5F;
            Vector3f[] points = distributePointsOnCircle(infoStacks.length, axis, offset, (float) Math.toRadians(ticks), radius, (float) Math.toDegrees(Math.atan2(blockEntity.getNearbyPlayer().z(), blockEntity.getNearbyPlayer().x())));
            for (int p = 0; p < points.length; p++) {
                Vector3f point = points[p];
                matrices.pushPose();

                Vector3f direction = blockEntity.getNearbyPlayer();
                direction.sub(point);
                direction.normalize();

                // Calculate yaw and pitch to face the player
                Quaternion itemRotation = Vector3f.YP.rotationDegrees(90 - (float) Math.toDegrees(Math.atan2(direction.z(), direction.x())));
                itemRotation.mul(Vector3f.ZP.rotationDegrees((float) Math.toDegrees(Math.asin(direction.y()))));
                matrices.mulPose(itemRotation);

                // Rotate the translation vector by the negative of the initial rotation
                Quaternion negativeRotation = itemRotation.copy();
                negativeRotation.conj(); // conjugate (equivalent to inverse for unit quaternions) to get the negative rotation
                point.transform(negativeRotation);

                // Apply the rotated translation to the pose
                matrices.translate(point.x(), point.y(), point.z());

                matrices.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getInstance().getItemRenderer().renderStatic(infoStacks[p], ItemTransforms.TransformType.GROUND,
                        light, overlay, matrices, vertexConsumers, 0);

                matrices.scale(0.35F, 0.35F, 0.35F);
                if (blockEntity.rendererData.isAmpUpgrade[p]) {
                    matrices.pushPose();
                    matrices.translate(-0.75F, 1F, 0);
                    Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GENERIC_ICON.get(), 1), ItemTransforms.TransformType.GROUND,
                            light, overlay, matrices, vertexConsumers, 0);
                    matrices.popPose();
                }
                if (blockEntity.rendererData.isDurationUpgrade[p]) {
                    matrices.pushPose();
                    matrices.translate(0.75F, 1F, 0);
                    Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GENERIC_ICON.get(), 2), ItemTransforms.TransformType.GROUND,
                            light, overlay, matrices, vertexConsumers, 0);
                    matrices.popPose();
                }

                matrices.popPose();
            }

            // Roman numerals for tier
            if (blockEntity.rendererData.ingredientTier > -1) {
                matrices.pushPose();
                Vector3f numeralsOffset = new Vector3f(0.5f, 1.75F, 0.5f);
                matrices.translate(numeralsOffset.x(), numeralsOffset.y(), numeralsOffset.z());

                Vector3f direction = blockEntity.getNearbyPlayer();
                direction.sub(numeralsOffset);
                direction.normalize();

                // Calculate yaw and pitch to face the player
                Quaternion numeralsRotation = Vector3f.YP.rotationDegrees(90 - (float) Math.toDegrees(Math.atan2(direction.z(), direction.x())));
                // Lerp quaternion rotation from blockEntity.rendererData.ingredientTierNumeralsRotation to numeralsQuaternion
                blockEntity.rendererData.ingredientTierNumeralsRotation = slerp(blockEntity.rendererData.ingredientTierNumeralsRotation, numeralsRotation, tickDelta * 0.02f);
                matrices.mulPose(blockEntity.rendererData.ingredientTierNumeralsRotation);

                matrices.scale(0.5F, 0.5F, 0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.GENERIC_ICON.get(), blockEntity.rendererData.ingredientTier + 3), ItemTransforms.TransformType.GROUND,
                        light, overlay, matrices, vertexConsumers, 0);
                matrices.popPose();
            }
        }
        profiler.pop();
    }

    public Vector3f[] distributePointsOnCircle(int numPoints, Vector3f axis, Vector3f offset, float radiansOffset, float radius, float spinDegrees) {
        Vector3f[] points = new Vector3f[numPoints];

        // Normalize the axis of rotation
        axis.normalize();

        // Find a vector perpendicular to the axis
        Vector3f perp;
        if (Math.abs(axis.x()) > Math.abs(axis.y())) {
            perp = new Vector3f(-axis.z(), 0, axis.x());
        } else {
            perp = new Vector3f(0, -axis.z(), axis.y());
        }
        perp.normalize();

        // Find a vector that is perpendicular to both the axis and the perp
        Vector3f spinAxis = axis.copy();
        spinAxis.cross(perp);
        spinAxis.normalize();

        // Rotate the perpendicular vector around the axis to generate the points
        for (int i = 0; i < numPoints; i++) {
            float angle = (float) (2 * Math.PI * i / numPoints) + radiansOffset;
            Quaternion rotation = axis.rotationDegrees(angle * 180 / (float) Math.PI);
            Vector3f point = new Vector3f(perp.x() * radius, perp.y() * radius, perp.z() * radius);
            point.transform(rotation);

            // Apply the spin rotation
            Quaternion spinRotation = spinAxis.rotationDegrees(spinDegrees);
            point.transform(spinRotation);

            point.add(offset);

            points[i] = point;
        }

        return points;
    }

    public Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        // Calculate the cosine of the angle between the two vectors.
        float dot = q1.i() * q2.i() + q1.j() * q2.j() + q1.k() * q2.k() + q1.r() * q2.r();

        // If the dot product is negative, slerp won't take the shorter path.
        // Note that v1 and -v1 are equivalent when the negation is applied to all four components.
        // Fix by reversing one quaternion.
        if (dot < 0.0f) {
            q2 = new Quaternion(-q2.i(), -q2.j(), -q2.k(), -q2.r());
            dot = -dot;
        }

        final float DOT_THRESHOLD = 0.9995f;
        if (dot > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.

            Quaternion result = new Quaternion(
                    q1.i() + t * (q2.i() - q1.i()),
                    q1.j() + t * (q2.j() - q1.j()),
                    q1.k() + t * (q2.k() - q1.k()),
                    q1.r() + t * (q2.r() - q1.r())
            );
            result.normalize();
            return result;
        }

        // Since dot is in range [0, DOT_THRESHOLD], acos is safe
        float theta_0 = (float) Math.acos(dot);  // theta_0 = angle between input vectors
        float theta = theta_0 * t;    // theta = angle between v0 and result
        float sin_theta = (float) Math.sin(theta);  // compute this value only once
        float sin_theta_0 = (float) Math.sin(theta_0); // compute this value only once

        float s0 = (float) Math.cos(theta) - dot * sin_theta / sin_theta_0;  // == sin(theta_0 - theta) / sin(theta_0)
        float s1 = sin_theta / sin_theta_0;

        // Perform the slerp
        Quaternion result = new Quaternion(
                s0 * q1.i() + s1 * q2.i(),
                s0 * q1.j() + s1 * q2.j(),
                s0 * q1.k() + s1 * q2.k(),
                s0 * q1.r() + s1 * q2.r()
        );
        return result;
    }
}
