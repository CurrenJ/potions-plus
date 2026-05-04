package grill24.potionsplus.core;

import grill24.potionsplus.render.animation.keyframe.AnimationCurveSerializer;
import net.minecraft.core.Holder;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class AnimationCurveSerializers {
    public static Holder<AnimationCurveSerializer<Float>> FLOAT;
    public static Holder<AnimationCurveSerializer<Vector3f>> VECTOR3F;
    public static Holder<AnimationCurveSerializer<Vector4f>> VECTOR4F;
}
