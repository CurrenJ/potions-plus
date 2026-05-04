package grill24.potionsplus.core;

import com.mojang.serialization.Codec;
import grill24.potionsplus.render.animation.keyframe.AnimationCurveSerializer;
import grill24.potionsplus.render.animation.keyframe.FloatAnimationCurve;
import grill24.potionsplus.render.animation.keyframe.Vector3fAnimationCurve;
import grill24.potionsplus.render.animation.keyframe.Vector4fAnimationCurve;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class AnimationCurveSerializers {
    public static final DeferredRegister<AnimationCurveSerializer<?>> SERIALIZERS = DeferredRegister.create(PotionsPlusRegistries.ANIMATION_CURVE_SERIALIZER_REGISTRY_KEY, ModInfo.MOD_ID);

    public static final DeferredHolder<AnimationCurveSerializer<?>, AnimationCurveSerializer<Float>> FLOAT = SERIALIZERS.register("float",
            () -> new AnimationCurveSerializer<>(
                    Codec.FLOAT,
                    ByteBufCodecs.FLOAT,
                    FloatAnimationCurve::new
            ));

    public static final DeferredHolder<AnimationCurveSerializer<?>, AnimationCurveSerializer<Vector3f>> VECTOR3F = SERIALIZERS.register("vector3f",
            () -> new AnimationCurveSerializer<Vector3f>(
                    ExtraCodecs.VECTOR3F.xmap(Vector3f::new, v -> (Vector3fc) v),
                    ByteBufCodecs.VECTOR3F.map(Vector3f::new, v -> (Vector3fc) v),
                    Vector3fAnimationCurve::new
            ));

    public static final DeferredHolder<AnimationCurveSerializer<?>, AnimationCurveSerializer<Vector4f>> VECTOR4F = SERIALIZERS.register("vector4f",
            () -> new AnimationCurveSerializer<Vector4f>(
                    ExtraCodecs.VECTOR4F.xmap(Vector4f::new, v -> (Vector4fc) v),
                    Vector4fAnimationCurve.VECTOR4F_STREAM_CODEC,
                    Vector4fAnimationCurve::new
            ));
}
