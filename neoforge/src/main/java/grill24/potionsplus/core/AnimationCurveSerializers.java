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
import org.joml.Vector4f;

public class AnimationCurveSerializers {
    public static final DeferredRegister<AnimationCurveSerializer<?>> SERIALIZERS = DeferredRegister.create(PotionsPlusRegistries.ANIMATION_CURVE_SERIALIZER_REGISTRY_KEY, ModInfo.MOD_ID);

    public static final DeferredHolder<AnimationCurveSerializer<?>, AnimationCurveSerializer<Float>> FLOAT = SERIALIZERS.register("float",
            () -> new AnimationCurveSerializer<>(
                    Codec.FLOAT,
                    ByteBufCodecs.FLOAT,
                    FloatAnimationCurve::new
            ));

    public static final DeferredHolder<AnimationCurveSerializer<?>, AnimationCurveSerializer<Vector3f>> VECTOR3F = SERIALIZERS.register("vector3f",
            () -> new AnimationCurveSerializer<>(
                    ExtraCodecs.VECTOR3F,
                    ByteBufCodecs.VECTOR3F,
                    Vector3fAnimationCurve::new
            ));

    public static final DeferredHolder<AnimationCurveSerializer<?>, AnimationCurveSerializer<Vector4f>> VECTOR4F = SERIALIZERS.register("vector4f",
            () -> new AnimationCurveSerializer<>(
                    ExtraCodecs.VECTOR4F,
                    Vector4fAnimationCurve.VECTOR4F_STREAM_CODEC,
                    Vector4fAnimationCurve::new
            ));
}
