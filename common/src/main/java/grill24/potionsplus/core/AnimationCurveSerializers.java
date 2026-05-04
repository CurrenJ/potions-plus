package grill24.potionsplus.core;

import com.mojang.serialization.Codec;
import grill24.potionsplus.render.animation.keyframe.AnimationCurveSerializer;
import grill24.potionsplus.render.animation.keyframe.FloatAnimationCurve;
import grill24.potionsplus.render.animation.keyframe.Vector3fAnimationCurve;
import grill24.potionsplus.render.animation.keyframe.Vector4fAnimationCurve;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AnimationCurveSerializers {
    public static Holder<AnimationCurveSerializer<Float>> FLOAT;
    public static Holder<AnimationCurveSerializer<Vector3f>> VECTOR3F;
    public static Holder<AnimationCurveSerializer<Vector4f>> VECTOR4F;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<AnimationCurveSerializer<?>>, Holder<AnimationCurveSerializer<?>>> register) {
        FLOAT = (Holder<AnimationCurveSerializer<Float>>) (Holder<?>) register.apply("float",
                () -> new AnimationCurveSerializer<>(
                        Codec.FLOAT,
                        ByteBufCodecs.FLOAT,
                        FloatAnimationCurve::new
                ));

        VECTOR3F = (Holder<AnimationCurveSerializer<Vector3f>>) (Holder<?>) register.apply("vector3f",
                () -> new AnimationCurveSerializer<Vector3f>(
                        ExtraCodecs.VECTOR3F.xmap(Vector3f::new, v -> (Vector3fc) v),
                        ByteBufCodecs.VECTOR3F.map(Vector3f::new, v -> (Vector3fc) v),
                        Vector3fAnimationCurve::new
                ));

        VECTOR4F = (Holder<AnimationCurveSerializer<Vector4f>>) (Holder<?>) register.apply("vector4f",
                () -> new AnimationCurveSerializer<Vector4f>(
                        ExtraCodecs.VECTOR4F.xmap(Vector4f::new, v -> (Vector4fc) v),
                        Vector4fAnimationCurve.VECTOR4F_STREAM_CODEC,
                        Vector4fAnimationCurve::new
                ));
    }
}
