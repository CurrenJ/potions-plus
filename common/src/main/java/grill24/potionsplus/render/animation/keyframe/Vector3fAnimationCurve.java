package grill24.potionsplus.render.animation.keyframe;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.AnimationCurveSerializers;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

import java.util.List;
import java.util.TreeSet;

public class Vector3fAnimationCurve extends AnimationCurve<Vector3f> {
    public Vector3fAnimationCurve() {
        super();
    }

    public Vector3fAnimationCurve(TreeSet<Keyframe<Vector3f>> keyframes) {
        super(keyframes);
    }

    public Vector3fAnimationCurve(List<Keyframe<Vector3f>> keyframes) {
        super(new TreeSet<>(keyframes));
    }

    @Override
    protected Vector3f multiply(Vector3f a, float b) {
        return new Vector3f(a).mul(b);
    }

    @Override
    protected Vector3f add(Vector3f a, Vector3f b) {
        return new Vector3f(a).add(b);
    }

    @Override
    protected Vector3f defaultValue() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    public Codec<AnimationCurve<Vector3f>> codec() {
        return AnimationCurveSerializers.VECTOR3F.value().codec;
    }

    @Override
    public StreamCodec<?, AnimationCurve<Vector3f>> streamCodec() {
        return AnimationCurveSerializers.VECTOR3F.value().streamCodec;
    }
}
