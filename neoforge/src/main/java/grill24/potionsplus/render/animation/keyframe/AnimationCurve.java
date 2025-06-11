package grill24.potionsplus.render.animation.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.utility.ISerializable;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.TreeSet;

public abstract class AnimationCurve<T> implements ISerializable<AnimationCurve<T>> {
    protected final TreeSet<Keyframe<T>> keyframes;

    protected AnimationCurve(TreeSet<Keyframe<T>> keyframes) {
        this.keyframes = new TreeSet<>(keyframes);
    }

    protected AnimationCurve(List<Keyframe<T>> keyframes) {
        this.keyframes = new TreeSet<>(keyframes);
    }

    protected AnimationCurve() {
        this.keyframes = new TreeSet<>();
    }

    public T evaluate(float time) {
        if (keyframes.isEmpty()) {
            return defaultValue();
        }
        if (keyframes.size() == 1) {
            return keyframes.first().value;
        }
        Keyframe<T> prev = null;
        Keyframe<T> next = null;
        for (Keyframe<T> keyframe : keyframes) {
            if (keyframe.time == time) {
                return keyframe.value;
            }
            if (keyframe.time < time) {
                prev = keyframe;
            } else {
                next = keyframe;
                break;
            }
        }
        if (prev == null) {
            return next.value;
        }
        if (next == null) {
            return prev.value;
        }
        float t = (time - prev.time) / (next.time - prev.time);
        return interpolate(prev, next, t);
    }

    public float getDuration() {
        if (keyframes.isEmpty()) {
            return 0;
        }
        return keyframes.last().time;
    }

    protected T interpolate(Keyframe<T> prev, Keyframe<T> next, float t) {
        return Interpolation.interpolate(prev.interpolation, prev.value, next.value, this::multiply, this::add, t, true);
    }

    protected abstract T multiply(T a, float b);
    protected abstract T add(T a, T b);
    protected abstract T defaultValue();

    protected T interpolateLinear(Keyframe<T> prev, Keyframe<T> next, float t) {
        return add(multiply(prev.value, 1 - t), multiply(next.value, t));
    }

    protected T interpolateCubicSpline(Keyframe<T> prev, Keyframe<T> next, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        return add(
                add(
                        multiply(prev.value, 2 * t3 - 3 * t2 + 1),
                        multiply(next.value, -2 * t3 + 3 * t2)
                ),
                add(
                        multiply(prev.value, t3 - 2 * t2 + t),
                        multiply(next.value, t3 - t2)
                )
        );
    }

    public void addKeyframe(Keyframe<T> keyframe) {
        keyframes.removeIf(k -> k.time == keyframe.time);
        keyframes.add(keyframe);
    }

    public void removeKeyframe(float time) {
        keyframes.removeIf(k -> k.time == time);
    }

    public void printInChat(Player player) {
        player.displayClientMessage(Component.literal("Keyframes:").withStyle(ChatFormatting.BOLD), false);
        for (Keyframe<T> keyframe : keyframes) {
            player.displayClientMessage(keyframe.toComponent(), false);
        }
    }

    public static class Keyframe<T> implements Comparable<Keyframe<T>> {
        public float time;
        public T value;
        public Interpolation.Mode interpolation;

        public Keyframe(float time, T value) {
            this.time = time;
            this.value = value;
            interpolation = Interpolation.Mode.LINEAR;
        }

        public Keyframe(float time, T value, Interpolation.Mode interpolation) {
            this.time = time;
            this.value = value;
            this.interpolation = interpolation;
        }

        @Override
        public int compareTo(@NotNull AnimationCurve.Keyframe<T> o) {
            return Float.compare(this.time, o.time);
        }

        public Component toComponent() {
            MutableComponent text = Component.empty();
            text.append(Component.literal("[T" + time + "] ").withStyle(ChatFormatting.ITALIC));
            text.append(Component.literal(value.toString()).withStyle(ChatFormatting.GREEN));
            text.append(Component.literal(" (" + interpolation.name() + ")").withStyle(net.minecraft.ChatFormatting.GRAY));
            return text;
        }

        public String toString() {
            return "[T" + time + "] " + value.toString() + " (" + interpolation.name() + ")";
        }

        public static <T> Builder<T> builder() {
            return new Builder<>();
        }

        public static <V> Codec<Keyframe<V>> createCodec(Codec<V> valueCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(k -> k.time),
                    valueCodec.fieldOf("value").forGetter(k -> k.value),
                    Codec.STRING.xmap(Interpolation.Mode::valueOf, Interpolation.Mode::name).fieldOf("interpolation").forGetter(k -> k.interpolation)
            ).apply(instance, Keyframe::new));
        }

        public static <V> StreamCodec<ByteBuf, Keyframe<V>> streamCodec(StreamCodec<ByteBuf, V> valueCodec) {
            return StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    keyframe -> keyframe.time,
                    valueCodec,
                    keyframe -> keyframe.value,
                    ByteBufCodecs.STRING_UTF8,
                    keyframe -> keyframe.interpolation.name(),
                    (time, value, interpolation) -> new Keyframe<>(time, value, Interpolation.Mode.valueOf(interpolation))
            );
        }

        public static class Builder<T> {
            private float time;
            private T value;
            private Interpolation.Mode interpolation;

            public Builder() {
                this.interpolation = Interpolation.Mode.LINEAR;
            }

            public Builder<T> time(float time) {
                this.time = time;
                return this;
            }

            public Builder<T> value(T value) {
                this.value = value;
                return this;
            }

            public Builder<T> interp(Interpolation.Mode interpolationIn) {
                this.interpolation = interpolationIn;
                return this;
            }

            public Keyframe<T> build() {
                return new Keyframe<>(time, value, interpolation);
            }
        }
    }
}
