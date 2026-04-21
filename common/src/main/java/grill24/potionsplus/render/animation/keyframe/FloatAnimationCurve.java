package grill24.potionsplus.render.animation.keyframe;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.AnimationCurveSerializers;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.TreeSet;

public class FloatAnimationCurve extends AnimationCurve<Float> {
    public FloatAnimationCurve() {
        super();
    }

    public FloatAnimationCurve(TreeSet<Keyframe<Float>> keyframes) {
        super(keyframes);
    }

    public FloatAnimationCurve(List<Keyframe<Float>> keyframes) {
        super(new TreeSet<>(keyframes));
    }

    @Override
    protected Float multiply(Float a, float b) {
        return a * b;
    }

    @Override
    protected Float add(Float a, Float b) {
        return a + b;
    }

    @Override
    protected Float defaultValue() {
        return 0f;
    }

    @Override
    public Codec<AnimationCurve<Float>> codec() {
        return AnimationCurveSerializers.FLOAT.value().codec;
    }

    @Override
    public StreamCodec<ByteBuf, AnimationCurve<Float>> streamCodec() {
        return AnimationCurveSerializers.FLOAT.value().streamCodec;
    }
}
