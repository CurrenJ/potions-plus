package grill24.potionsplus.render.animation.keyframe;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.AnimationCurveSerializers;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector4f;

import java.util.List;
import java.util.TreeSet;

public class Vector4fAnimationCurve extends AnimationCurve<Vector4f> {
    public static final StreamCodec<ByteBuf, Vector4f> VECTOR4F_STREAM_CODEC = new StreamCodec<ByteBuf, Vector4f>() {
        public Vector4f decode(ByteBuf byteBuf) {
            return readVector4f(byteBuf);
        }

        public void encode(ByteBuf byteBuf, Vector4f vector4f) {
            writeVector4f(byteBuf, vector4f);
        }
    };

    public Vector4fAnimationCurve() {
        super();
    }

    public Vector4fAnimationCurve(TreeSet<Keyframe<Vector4f>> keyframes) {
        super(keyframes);
    }

    public Vector4fAnimationCurve(List<Keyframe<Vector4f>> keyframes) {
        super(new TreeSet<>(keyframes));
    }

    @Override
    protected Vector4f multiply(Vector4f a, float b) {
        return new Vector4f(a).mul(b);
    }

    @Override
    protected Vector4f add(Vector4f a, Vector4f b) {
        return new Vector4f(a).add(b);
    }

    @Override
    protected Vector4f defaultValue() {
        return new Vector4f(0, 0, 0, 0);
    }

    private static Vector4f readVector4f(ByteBuf buffer) {
        return new Vector4f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    private static void writeVector4f(ByteBuf buffer, Vector4f vector4f) {
        buffer.writeFloat(vector4f.x());
        buffer.writeFloat(vector4f.y());
        buffer.writeFloat(vector4f.z());
        buffer.writeFloat(vector4f.w());
    }

    @Override
    public Codec<AnimationCurve<Vector4f>> codec() {
        return AnimationCurveSerializers.VECTOR4F.value().codec;
    }

    @Override
    public StreamCodec<?, AnimationCurve<Vector4f>> streamCodec() {
        return AnimationCurveSerializers.VECTOR4F.value().streamCodec;
    }
}
