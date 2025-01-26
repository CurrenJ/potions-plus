package grill24.potionsplus.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.blockentity.ITimestampSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.*;
import grill24.potionsplus.blockentity.ISingleStackDisplayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

import java.awt.geom.Rectangle2D;
import java.lang.Math;
import java.util.function.Function;

public class RUtil {
    public static void renderInputItemAnimation(ItemStack stack, float scale, int ticksDelay, boolean hideOnFinish, ISingleStackDisplayer iSingleStackDisplayer, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        float ticks = ClientTickHandler.total();
        float lerpFactor = (ticks - iSingleStackDisplayer.getTimeItemPlaced() - ticksDelay) / iSingleStackDisplayer.getInputAnimationDuration();

        if (!stack.isEmpty() && (!hideOnFinish || lerpFactor <= 1)) {
            matrices.pushPose();

//            matrices.scale(scale, scale, scale);

            // Lerp the item from the player's hand to the resting position
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));
            Quaternionf rotationStart = new Quaternionf().identity();
            Quaternionf rotation = fromXYZDegrees(iSingleStackDisplayer.getRestingRotation());

            if (lerpFactor < 1) {
                Vector3d startAnimationTranslation = iSingleStackDisplayer.getStartAnimationWorldPos();

                Vector3d lerped = lerp3d(startAnimationTranslation, iSingleStackDisplayer.getRestingPosition(), lerpFactor, RUtil::easeOutExpo);
                rotation = slerp(rotationStart, rotation, easeOutExpo(lerpFactor));

                matrices.translate(lerped.x, lerped.y, lerped.z);
            } else {
                matrices.translate(iSingleStackDisplayer.getRestingPosition().x, iSingleStackDisplayer.getRestingPosition().y, iSingleStackDisplayer.getRestingPosition().z);
            }

            matrices.mulPose(rotation);
            matrices.scale(scale, scale, scale);

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                    light, overlay, matrices, vertexConsumers, null, 0);

            matrices.popPose();
        }
    }

    public static void renderBobbingItem(ItemStack stack, Vector3d restingPosition, float yawDegrees, float scale, float bobbingHeight, float bobbingHertz, float tickDelay, float tickDelta, ISingleStackDisplayer singleStackDisplayer, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        float ticks = ClientTickHandler.total();
        if (!stack.isEmpty() && isAnimationActive(singleStackDisplayer, (int) tickDelay, (int) tickDelta)) {
            matrices.pushPose();
            float bobbing = (float) Math.sin((ticks - singleStackDisplayer.getTimeItemPlaced() - tickDelay) / 20 * bobbingHertz * Math.PI * 2) * bobbingHeight;
            Quaternionf rotation = RUtil.rotateY(yawDegrees);

            matrices.translate(restingPosition.x, restingPosition.y + bobbing, restingPosition.z);
            matrices.mulPose(rotation);
            matrices.scale(scale, scale, scale);


            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND,
                    light, overlay, matrices, vertexConsumers, null, 0);
            matrices.popPose();
        }
    }

    public static float getBobbingOffset(ISingleStackDisplayer singleStackDisplayer, float bobbingHeight, float bobbingHertz, int tickDelay) {
        float ticks = ClientTickHandler.total();
        float elapsedTimeSinceItemPlaced = ticks - singleStackDisplayer.getTimeItemPlaced();
        if (isAnimationActive(singleStackDisplayer, tickDelay, 0)) {
            return (float) Math.sin((elapsedTimeSinceItemPlaced - tickDelay) / 20 * bobbingHertz * Math.PI * 2) * bobbingHeight;
        }
        return 0;
    }

    public static void renderItemWithYaw(ISingleStackDisplayer singleStackDisplayer, ItemStack stack, Vector3d position, int tickDelay, int tickDuration, float yawDegrees, float scale, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (isAnimationActive(singleStackDisplayer, tickDelay, tickDuration)) {
            matrices.pushPose();
            Quaternionf rotation = RUtil.rotateY(yawDegrees);
            matrices.translate(position.x, position.y, position.z);
            matrices.mulPose(rotation);
            matrices.scale(scale, scale, scale);

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                    light, overlay, matrices, vertexConsumers, null, 0);
            matrices.popPose();
        }
    }

    public static boolean isAnimationActive(ISingleStackDisplayer singleStackDisplayer, float tickDelay, float tickDuration) {
        float ticks = ClientTickHandler.total();
        float elapsedTicksSinceItemPlaced = ticks - singleStackDisplayer.getTimeItemPlaced();
        return elapsedTicksSinceItemPlaced > tickDelay && (tickDuration <= 0 || elapsedTicksSinceItemPlaced <= tickDuration + tickDelay);
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

    // ----- Easing Utility -----

    public static float ease(ITimestampSupplier startTimeSupplier, float a, float b, int tickDelay, float hertz, Function<Float, Float> easing) {
        return ease(startTimeSupplier.getTimestamp(), a, b, tickDelay, hertz, easing);
    }

    public static float ease(ITimestampSupplier startTimeSupplier, float a, float b, int tickDelay, float hertz) {
        return ease(startTimeSupplier, a, b, tickDelay, hertz, x -> x);
    }

    public static float ease(ISingleStackDisplayer singleStackDisplayer, float a, float b, int tickDelay, float hertz) {
        return ease(singleStackDisplayer, a, b, tickDelay, hertz, RUtil::easeInOutQuint);
    }

    public static float ease(ISingleStackDisplayer displayer, float a, float b, int delay, float hertz, Function<Float, Float> easing) {
        return ease(displayer.getTimeItemPlaced(), a, b, delay, hertz, easing);
    }

    public static float ease(float timeStamp, float a, float b, int delay, float hertz, Function<Float, Float> easing) {
        float elapsedTicks = ClientTickHandler.total() - timeStamp - delay;
        float lerpFactor = Math.clamp(elapsedTicks / (20 / hertz), 0, 1);
        return Utility.lerp(a, b, easing.apply(lerpFactor));
    }

    // ----- Lerp Utility -----

    public static Vector3d lerp3d(Vector3d a, Vector3d b, float t, Function<Float, Float> easingFunction) {
        t = easingFunction.apply(t);
        return new Vector3d(lerp(a.x, b.x, t), lerp(a.y, b.y, t), lerp(a.z, b.z, t));
    }

    public static Vector3d lerp3d(Vector3d a, Vector3d b, float t) {
        return lerp3d(a, b, t, x -> x);
    }

    public static Vector3f lerp3f(Vector3f a, Vector3f b, float t) {
        return new Vector3f(lerp(a.x(), b.x(), t), lerp(a.y(), b.y(), t), lerp(a.z(), b.z(), t));
    }

    public static double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float lerpAngle(float startAngle, float endAngle, float t) {
        // Normalize angles to be within 0 to 360 degrees
        startAngle = normalizeAngle(startAngle);
        endAngle = normalizeAngle(endAngle);

        // Determine the difference and adjust angles if necessary
        float difference = endAngle - startAngle;

        if (Math.abs(difference) > 180) {
            // If the difference is greater than 180 degrees, adjust to take the shorter path
            if (difference > 0) {
                startAngle += 360;
            } else {
                endAngle += 360;
            }
        }

        // Perform the linear interpolation
        float result = startAngle + (endAngle - startAngle) * t;

        // Normalize the result
        return normalizeAngle(result);
    }

    public static float normalizeAngle(float angle) {
        // Normalize the angle to be within 0 to 360 degrees
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    // ----- Easing Functions -----

    public static float easeOutBack(float x) {
        final double c1 = 1.70158;
        final double c3 = c1 + 1;

        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    public static float easeInSine(float x) {
        return (float) (1 - Math.cos((x * Math.PI) / 2));
    }

    public static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2);
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }

    public static float easeInExpo(float x) {
        return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
    }

    public static float easeInOutQuint(float x) {
        return (float) (x < 0.5 ? 16 * Math.pow(x, 5) : 1 - Math.pow(-2 * x + 2, 5) / 2);
    }

    public static float easeOutElastic(float x) {
        final double c4 = (2 * Math.PI) / 3;

        return x <= 0 ? 0 :
                x >= 1 ? 1 :
                        (float) (Math.pow(2, -12 * x) * Math.sin((x * 6 - 0.75) * c4) + 1);
    }

    public static float easeOutElastic2(float x) {
        final double c4 = (2 * Math.PI) / 3;

        return x <= 0 ? 0 :
                x >= 1 ? 1 :
                        (float) (Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);
    }

    public static float getProgress(float tickDelay, float tickDuration, float startTime, float currentTime) {
        float elapsedTicks = currentTime - startTime - tickDelay;
        return Math.max(0, Math.min(elapsedTicks / tickDuration, 1));
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

    public static Vector3f[] distributePointsOnCircle(int numPoints, Vector3f axis, Vector3f offset, float radiansOffset, float radius, float spinDegrees) {
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
        Vector3f spinAxis = new Vector3f(axis);
        spinAxis.cross(perp);
        spinAxis.normalize();

        // Rotate the perpendicular vector around the axis to generate the points
        for (int i = 0; i < numPoints; i++) {
            float angle = (float) (2 * Math.PI * i / numPoints) + radiansOffset;
            Quaternionf rotation = new Quaternionf().rotateAxis(angle, axis);
            Vector3f point = new Vector3f(perp.x() * radius, perp.y() * radius, perp.z() * radius);
            point.rotate(rotation);

            // Apply the spin rotation
            Quaternionf spinRotation = new Quaternionf().rotateAxis(toRadians(spinDegrees), spinAxis);
            point.rotate(spinRotation);

            point.add(offset);

            points[i] = point;
        }

        return points;
    }

    public static Quaternionf slerp(Quaternionf q1, Quaternionf q2, float t) {
        // Calculate the cosine of the angle between the two vectors.
        float dot = q1.x() * q2.x() + q1.y() * q2.y() + q1.z() * q2.z() + q1.w() * q2.w();

        // If the dot product is negative, slerp won't take the shorter path.
        // Note that v1 and -v1 are equivalent when the negation is applied to all four components.
        // Fix by reversing one Quaternionf.
        if (dot < 0.0f) {
            q2 = new Quaternionf(-q2.x(), -q2.y(), -q2.z(), -q2.w());
            dot = -dot;
        }

        final float DOT_THRESHOLD = 0.9995f;
        if (dot > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.

            Quaternionf result = new Quaternionf(
                    q1.x() + t * (q2.x() - q1.x()),
                    q1.y() + t * (q2.y() - q1.y()),
                    q1.z() + t * (q2.z() - q1.z()),
                    q1.w() + t * (q2.w() - q1.w())
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
        Quaternionf result = new Quaternionf(
                s0 * q1.x() + s1 * q2.x(),
                s0 * q1.y() + s1 * q2.y(),
                s0 * q1.z() + s1 * q2.z(),
                s0 * q1.w() + s1 * q2.w()
        );
        return result;
    }


    public static float toRadians(float degrees) {
        return (float) (degrees / 180F * Math.PI);
    }

    public static Quaternionf rotateX(float degrees) {
        return new Quaternionf().rotateX(toRadians(degrees));
    }

    public static Quaternionf rotateY(float degrees) {
        return new Quaternionf().rotateY(toRadians(degrees));
    }

    public static Quaternionf rotateZ(float degrees) {
        return new Quaternionf().rotateZ(toRadians(degrees));
    }

    public static Quaternionf rotate(Vector3fc degrees) {
        return fromXYZDegrees(degrees);
    }

    public static Quaternionf fromXYZDegrees(Vector3fc degrees) {
        // Convert degrees to radians
        float radX = toRadians(degrees.x());
        float radY = toRadians(degrees.y());
        float radZ = toRadians(degrees.z());

        // Create a quaternion from the radians
        Quaternionf q = new Quaternionf();
        q.rotateX(radX).rotateY(radY).rotateZ(radZ);
        return q;
    }

    private static final int OUTLINE_COLOR_DARK = FastColor.ARGB32.color(255, 85, 85, 85);
    private static final int OUTLINE_COLOR_LIGHT = FastColor.ARGB32.color(255, 255, 255, 255);
    private static final int OUTLINE_Z = 100;
    private static final int FILL_Z = 99;
    public static void drawInventoryBackgroundRect(GuiGraphics graphics, Rectangle2D rect, float rotationDegrees, float outlineWidth, float outlineHeight) {
        float minX = (float) rect.getMinX();
        float minY = (float) rect.getMinY();
        float width = (float) rect.getWidth();
        float height = (float) rect.getHeight();

        Vector2f center = new Vector2f((float) rect.getCenterX(), (float) rect.getCenterY());

        // Draw outline entirely inside the bounds
        IGuiGraphicsMixin graphicsMixin = (IGuiGraphicsMixin) graphics;
        graphicsMixin.potions_plus$fill(minX, minY, minX + outlineWidth, minY + height, center, rotationDegrees, OUTLINE_Z, OUTLINE_COLOR_DARK);
        graphicsMixin.potions_plus$fill(minX, minY, minX + width, minY + outlineHeight, center, rotationDegrees, OUTLINE_Z, OUTLINE_COLOR_DARK);
        graphicsMixin.potions_plus$fill(minX + width - outlineWidth, minY, minX + width, minY + height, center, rotationDegrees, OUTLINE_Z, OUTLINE_COLOR_LIGHT);
        graphicsMixin.potions_plus$fill(minX, minY + height - outlineHeight, minX + width, minY + height, center, rotationDegrees, OUTLINE_Z, OUTLINE_COLOR_LIGHT);

        // Fill background
        graphicsMixin.potions_plus$fill(minX, minY, minX + width, minY + height, center, rotationDegrees, FILL_Z, FastColor.ARGB32.color(255, 198, 198, 198));
    }

    public static void rotatePointsAround(Vector2f[] points, Vector2f origin, float degrees) {
        degrees = (float) Math.toRadians(degrees);
        // 1. Get center point of original rectangle
        // 2. Rotate all points around the center point
        for (Vector2f point : points) {
            point.sub(origin);
            float x = point.x() * (float) Math.cos(degrees) - point.y() * (float) Math.sin(degrees);
            float y = point.x() * (float) Math.sin(degrees) + point.y() * (float) Math.cos(degrees);
            point.set(x, y);
            point.add(origin);
        }
    }
}