package grill24.potionsplus.render.animation.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;

public class AnimationCurveSerializer<V> {
    public final Codec<AnimationCurve<V>> codec;
    public final StreamCodec<ByteBuf, AnimationCurve<V>> streamCodec;

    public AnimationCurveSerializer(Codec<V> valueCodec, StreamCodec<ByteBuf, V> streamCodec, Function<List<AnimationCurve.Keyframe<V>>, AnimationCurve<V>> createFromSerializedData) {
        this.codec = createCodec(AnimationCurve.Keyframe.createCodec(valueCodec), createFromSerializedData);
        this.streamCodec = createStreamCodec(AnimationCurve.Keyframe.streamCodec(streamCodec), createFromSerializedData);
    }

    private Codec<AnimationCurve<V>> createCodec(Codec<AnimationCurve.Keyframe<V>> keyframeCodec, Function<List<AnimationCurve.Keyframe<V>>, AnimationCurve<V>> createFromSerializedData) {
        return RecordCodecBuilder.create(instance -> instance.group(
                keyframeCodec.listOf().fieldOf("keyframes").forGetter(c -> c.keyframes.stream().toList())
        ).apply(instance, createFromSerializedData));
    }

    private StreamCodec<ByteBuf, AnimationCurve<V>> createStreamCodec(StreamCodec<ByteBuf, AnimationCurve.Keyframe<V>> keyframeStreamCodec, Function<List<AnimationCurve.Keyframe<V>>, AnimationCurve<V>> createFromSerializedData) {
        return StreamCodec.composite(
                keyframeStreamCodec.apply(ByteBufCodecs.list()),
                instance -> instance.keyframes.stream().toList(),
                createFromSerializedData
        );
    }
}
