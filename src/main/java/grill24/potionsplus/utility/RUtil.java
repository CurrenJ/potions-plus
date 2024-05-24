package grill24.potionsplus.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import grill24.potionsplus.blockentity.ISingleStackDisplayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class RUtil {
    public static void renderInputItemAnimation(ItemStack stack, int degreesRotation, float scale, int ticksDelay, int ticksDuration, boolean hideOnFinish, float tickDelta, ISingleStackDisplayer iSingleStackDisplayer, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        float ticks = ClientTickHandler.total();
        float lerpFactor = (ticks - iSingleStackDisplayer.getTimeItemPlaced() - ticksDelay) / ticksDuration;

        if (!stack.isEmpty() && (!hideOnFinish || lerpFactor <= 1)) {
            matrices.pushPose();

            // Lerp the item from the player's hand to the resting position
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));
            int rotationDegreesStart = 0;
            Quaternion rotation = Vector3f.XP.rotationDegrees(degreesRotation);

            if (lerpFactor < 1) {
                Vector3d startAnimationTranslation = iSingleStackDisplayer.getStartAnimationWorldPos();

                Vector3d lerped = lerp3d(startAnimationTranslation, iSingleStackDisplayer.getRestingPosition(), lerpFactor, RUtil::easeOutExpo);
                rotation = Vector3f.XP.rotationDegrees(Utility.lerp(rotationDegreesStart, degreesRotation, easeOutExpo(lerpFactor)));

                matrices.translate(lerped.x, lerped.y, lerped.z);
            } else {
                matrices.translate(iSingleStackDisplayer.getRestingPosition().x, iSingleStackDisplayer.getRestingPosition().y, iSingleStackDisplayer.getRestingPosition().z);
            }

            matrices.mulPose(rotation);
            matrices.scale(scale, scale, scale);

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND,
                    light, overlay, matrices, vertexConsumers, 0);
            matrices.popPose();
        }
    }

    public static void renderBobbingItem(ItemStack stack, Vector3d restingPosition, float yawDegrees, float scale, float bobbingHeight, float bobbingHertz, float tickDelay, float tickDelta, ISingleStackDisplayer singleStackDisplayer, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        float ticks = ClientTickHandler.total();
        if (!stack.isEmpty() && ticks - singleStackDisplayer.getTimeItemPlaced() - tickDelay > 0) {
            matrices.pushPose();
            float bobbing = (float) Math.sin((ticks - singleStackDisplayer.getTimeItemPlaced() - tickDelay) / 20 * bobbingHertz * Math.PI * 2) * bobbingHeight;
            Quaternion rotation = Vector3f.YP.rotationDegrees(yawDegrees);

            matrices.translate(restingPosition.x, restingPosition.y + bobbing, restingPosition.z);
            matrices.mulPose(rotation);
            matrices.scale(scale, scale, scale);


            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND,
                    light, overlay, matrices, vertexConsumers, 0);
            matrices.popPose();
        }
    }

    /**
     * Simulats a spin. Returns the current angle in degrees.
     *
     * @param tickDelay
     * @param hertz
     * @param fullRotations
     * @return
     */
    public static float doSpin(ISingleStackDisplayer singleStackDisplayer, int tickDelay, float hertz, int fullRotations) {
        float ticks = ClientTickHandler.total();
        float spinTickDuration = 20 / hertz;
        float lerpFactor = (ticks - singleStackDisplayer.getTimeItemPlaced() - tickDelay) / spinTickDuration;
        if (lerpFactor <= 1) {
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));
            return Utility.lerp(0, 360 * fullRotations, easeInOutQuint(lerpFactor));
        } else {
            return 0;
        }
    }

    public static float ease(ISingleStackDisplayer singleStackDisplayer, float a, float b, int tickDelay, float hertz) {
        float ticks = ClientTickHandler.total();
        float duration = 20 / hertz;
        float lerpFactor = (ticks - singleStackDisplayer.getTimeItemPlaced() - tickDelay) / duration;
        if (lerpFactor <= 1) {
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));
            return Utility.lerp(a, b, easeInOutQuint(lerpFactor));
        } else {
            return b;
        }
    }

    public static Vector3d lerp3d(Vector3d a, Vector3d b, float t, Function<Float, Float> easingFunction) {
        t = easingFunction.apply(t);
        return new Vector3d(lerp(a.x, b.x, t), lerp(a.y, b.y, t), lerp(a.z, b.z, t));
    }

    public static Vector3d lerp3d(Vector3d a, Vector3d b, float t) {
        return lerp3d(a, b, t, x -> x);
    }

    public static double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }

    public static float easeOutBack(float x) {
        final double c1 = 1.70158;
        final double c3 = c1 + 1;

        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }

    public static float easeInOutQuint(float x) {
        return (float) (x < 0.5 ? 16 * Math.pow(x, 5) : 1 - Math.pow(-2 * x + 2, 5) / 2);
    }

    public static Vector3d rotateAroundY(Vector3d point, float degrees, Vector3d origin) {
        // Convert degrees to radians for the Math functions
        double radians = Math.toRadians(degrees);
        Vector3d negOrigin = new Vector3d(-origin.x, -origin.y, -origin.z);

        // Translate point back to origin
        Vector3d translatedPoint = new Vector3d(point.x, point.y, point.z);
        translatedPoint.add(negOrigin);

        // Perform rotation around y-axis
        double x = translatedPoint.x * Math.cos(radians) - translatedPoint.z * Math.sin(radians);
        double z = translatedPoint.x * Math.sin(radians) + translatedPoint.z * Math.cos(radians);

        // Translate point back
        Vector3d rotatedPoint = new Vector3d(x, translatedPoint.y, z);
        rotatedPoint.add(origin);

        return rotatedPoint;
    }
}