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

    public static Vector3f[] renderLeashBetweenPoints(BlockPos rendererOrigin, Vec3 start, Vec3 end, PoseStack poseStack, MultiBufferSource bufferSource, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd) {
        poseStack.pushPose();
        poseStack.translate(start.x - rendererOrigin.getX(), start.y - rendererOrigin.getY(), start.z - rendererOrigin.getZ());
        float deltaX = (float)(end.x - start.x);
        float deltaY = (float)(end.y - start.y);
        float deltaZ = (float)(end.z - start.z);
        float distance = Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float invDistance = Mth.fastInvSqrt(deltaX * deltaX + deltaZ * deltaZ) * 0.025F / 2.0F;
        float deltaZInvDistance = deltaZ * invDistance;
        float deltaXInvDistance = deltaX * invDistance;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix = poseStack.last().pose();

        Vector3f[] leashPoints = new Vector3f[25];
        for(int i = 0; i <= 24; ++i) {
            leashPoints[i] = addVertexPair(vertexConsumer, matrix, deltaX, deltaY, deltaZ, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd, 0.025F, 0.025F, deltaZInvDistance, deltaXInvDistance, i, false);
        }

        for(int i = 24; i >= 0; --i) {
            addVertexPair(vertexConsumer, matrix, deltaX, deltaY, deltaZ, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd, 0.025F, 0.0F, deltaZInvDistance, deltaXInvDistance, i, true);
        }

        poseStack.popPose();

        return leashPoints;
    }

    private static Vector3f addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix, float deltaX, float deltaY, float deltaZ, int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd, float width, float height, float deltaZInvDistance, float deltaXInvDistance, int step, boolean reverse) {
        float stepFraction = (float)step / 24.0F;
        int mixedBlockLight = (int)Mth.lerp(stepFraction, blockLightStart, blockLightEnd);
        int mixedSkyLight = (int)Mth.lerp(stepFraction, skyLightStart, skyLightEnd);
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
        vertexConsumer.vertex(matrix, interpolatedX - deltaZInvDistance, interpolatedY + height, interpolatedZ + deltaXInvDistance).color(red, green, blue, 1.0F).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix, interpolatedX + deltaZInvDistance, interpolatedY + width - height, interpolatedZ - deltaXInvDistance).color(red, green, blue, 1.0F).uv2(packedLight).endVertex();
        return new Vector3f(interpolatedX, interpolatedY, interpolatedZ);
    }
}