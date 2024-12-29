package grill24.potionsplus.render.animation.keyframe;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.*;

import static grill24.potionsplus.utility.Utility.ppId;

public class SpatialAnimations {
    public static final Map<ResourceLocation, SpatialAnimationData> CURVES = new HashMap<>();
    public static ResourceLocation WHEEL_WINNER = register(ppId("wheel_winner"), new SpatialAnimationData()
            // Scale
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(180).value(1F).interp(Interpolation.Mode.EASE_OUT_BOUNCE).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(200).value(1.25F).interp(Interpolation.Mode.EASE_OUT_BOUNCE).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(230).value(1.25F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(240).value(0F).build())
            // Rotation
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(180).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_IN_OUT_SINE).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(200).value(new Vector3f(0, 1080, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(230).value(new Vector3f(0, 1080, 0)).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(240).value(new Vector3f(0, 1440, 0)).build())
    );
    public static ResourceLocation WHEEL_LOSERS = register(ppId("wheel_losers"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(180).value(1F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(190).value(0F).build())
            // Rotation
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(190).value(new Vector3f(0, 1080, 0)).build())
    );
    public static ResourceLocation WHEEL_SPIN = register(ppId("wheel_spin"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(180).value(new Vector3f(1F, 1F, 1F)).build())
    );
    public static ResourceLocation WHEEL_POSITION_OFFSET = register(ppId("wheel_position_offset"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 1F, 0)).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(20).value(new Vector3f(0, 0, 0)).build())
    );

    public static ResourceLocation register(ResourceLocation id, SpatialAnimationData curve) {
        CURVES.put(id, curve);
        return id;
    }

    public static SpatialAnimationData get(ResourceLocation id) {
        return CURVES.get(id);
    }

    public static SpatialAnimationData getOrDefault(ResourceLocation id, SpatialAnimationData defaultCurve) {
        return CURVES.getOrDefault(id, defaultCurve);
    }

    public static SpatialAnimationData computeIfAbsent(ResourceLocation id, SpatialAnimationData defaultCurve) {
        return CURVES.computeIfAbsent(id, k -> defaultCurve);
    }

    public static Collection<ResourceLocation> getIds() {
        return CURVES.keySet();
    }
}
