package grill24.potionsplus.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LeashRenderer {

    public static void renderLeashBetweenPoints(BlockPos rendererOrigin, Vec3 start, Vec3 end, PoseStack poseStack, MultiBufferSource bufferSource, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd) {
        poseStack.pushPose();
        poseStack.translate(start.x - rendererOrigin.getX(), start.y - rendererOrigin.getY(), start.z - rendererOrigin.getZ());
        calculateLeashPointsAndRender(start, end, poseStack, bufferSource, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd);
        poseStack.popPose();
    }

    public static Vector3f[] calculateLeashPoints(Vec3 start, Vec3 end) {
        return calculateLeashPointsAndRender(start, end, null, null, 0, 0, 0, 0);
    }

    public static Vector3f[] calculateLeashPointsAndRender(Vec3 start, Vec3 end, PoseStack poseStack, MultiBufferSource bufferSource, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd) {
        float deltaX = (float) (end.x - start.x);
        float deltaY = (float) (end.y - start.y);
        float deltaZ = (float) (end.z - start.z);
        float invDistance = Mth.fastInvSqrt(deltaX * deltaX + deltaZ * deltaZ) * 0.025F / 2.0F;
        float deltaZInvDistance = deltaZ * invDistance;
        float deltaXInvDistance = deltaX * invDistance;
        VertexConsumer vertexConsumer = bufferSource != null ? bufferSource.getBuffer(RenderType.leash()) : null;
        Matrix4f matrix = poseStack != null ? poseStack.last().pose() : null;

        Vector3f[] leashPoints = new Vector3f[25];
        for (int i = 0; i <= 24; ++i) {
            leashPoints[i] = addVertexPair(vertexConsumer, matrix, deltaX, deltaY, deltaZ, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd, 0.025F, 0.025F, deltaZInvDistance, deltaXInvDistance, i, false);
        }

        for (int i = 24; i >= 0; --i) {
            addVertexPair(vertexConsumer, matrix, deltaX, deltaY, deltaZ, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd, 0.025F, 0.0F, deltaZInvDistance, deltaXInvDistance, i, true);
        }
        return leashPoints;
    }

    private static Vector3f addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix, float deltaX, float deltaY, float deltaZ, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd, float width, float height, float deltaZInvDistance, float deltaXInvDistance, int step, boolean reverse) {
        float stepFraction = (float) step / 24.0F;
        int mixedBlockLight = (int) Mth.lerp(stepFraction, blockLightStart, blockLightEnd);
        int mixedSkyLight = (int) Mth.lerp(stepFraction, skyLightStart, skyLightEnd);
        int packedLight = LightTexture.pack(mixedBlockLight, mixedSkyLight);
        float colorIntensity = step % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F;
        float red = 0.5F * colorIntensity;
        float green = 0.4F * colorIntensity;
        float blue = 0.3F * colorIntensity;
        float interpolatedX = deltaX * stepFraction;
        float sagIntensity = 0.25f;
        float sag = sagIntensity * 4 * stepFraction * (1 - stepFraction);
        float interpolatedY = deltaY * stepFraction - sag;
        float interpolatedZ = deltaZ * stepFraction;
        if (vertexConsumer != null && matrix != null) {
            vertexConsumer.vertex(matrix, interpolatedX - deltaZInvDistance, interpolatedY + height, interpolatedZ + deltaXInvDistance).color(red, green, blue, 1.0F).uv2(packedLight).endVertex();
            vertexConsumer.vertex(matrix, interpolatedX + deltaZInvDistance, interpolatedY + width - height, interpolatedZ - deltaXInvDistance).color(red, green, blue, 1.0F).uv2(packedLight).endVertex();
        }
        return new Vector3f(interpolatedX, interpolatedY, interpolatedZ);
    }

    private static void addVertices(VertexConsumer vertexConsumer, Matrix4f matrix, Vector3f[] points, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd, float width, float height, float deltaZInvDistance, float deltaXInvDistance, boolean reverse) {
        for (int i = 0; i < points.length; i++) {
            float stepFraction = (float) i / 24.0F;
            int mixedBlockLight = (int) Mth.lerp(stepFraction, blockLightStart, blockLightEnd);
            int mixedSkyLight = (int) Mth.lerp(stepFraction, skyLightStart, skyLightEnd);
            int packedLight = LightTexture.pack(mixedBlockLight, mixedSkyLight);
            float colorIntensity = i % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F;
            float red = 0.5F * colorIntensity;
            float green = 0.4F * colorIntensity;
            float blue = 0.3F * colorIntensity;

            Vector3f point = points[i];
            if (vertexConsumer != null && matrix != null) {
                vertexConsumer.vertex(matrix, point.x() - deltaZInvDistance, point.y() + height, point.z() + deltaXInvDistance).color(red, green, blue, 1.0F).uv2(packedLight).endVertex();
                vertexConsumer.vertex(matrix, point.x() + deltaZInvDistance, point.y() + width - height, point.z() - deltaXInvDistance).color(red, green, blue, 1.0F).uv2(packedLight).endVertex();
            }
        }
    }

    public static void renderLeashPoints(Vector3f[] leashPoints, PoseStack poseStack, MultiBufferSource bufferSource, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix = poseStack.last().pose();
        for (int i = 0; i <= 24; ++i) {
            Vector3f point = leashPoints[i];
            vertexConsumer.vertex(matrix, point.x(), point.y(), point.z()).color(0.5F, 0.4F, 0.3F, 1.0F).uv2(LightTexture.pack(blockLightStart, skyLightStart)).endVertex();
        }

        for (int i = 24; i >= 0; --i) {
            Vector3f point = leashPoints[i];
            vertexConsumer.vertex(matrix, point.x(), point.y(), point.z()).color(0.5F, 0.4F, 0.3F, 1.0F).uv2(LightTexture.pack(blockLightEnd, skyLightEnd)).endVertex();
        }
    }
}