package grill24.potionsplus.render.animation.keyframe;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.AnimationCurveSerializers;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;

public class SpatialAnimationData {
    public static final Codec<SpatialAnimationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AnimationCurveSerializers.VECTOR3F.value().codec.fieldOf("position").forGetter(SpatialAnimationData::getPosition),
            AnimationCurveSerializers.VECTOR3F.value().codec.fieldOf("rotation").forGetter(SpatialAnimationData::getRotation),
            AnimationCurveSerializers.FLOAT.value().codec.fieldOf("scale").forGetter(SpatialAnimationData::getScale),
            AnimationCurveSerializers.VECTOR4F.value().codec.fieldOf("color").forGetter(SpatialAnimationData::getColor)
    ).apply(instance, (position, rotation, scale, color) ->
            new SpatialAnimationData((Vector3fAnimationCurve) position, (Vector3fAnimationCurve) rotation, (FloatAnimationCurve) scale, (Vector4fAnimationCurve) color))
    );
    public static final StreamCodec<ByteBuf, SpatialAnimationData> STREAM_CODEC = StreamCodec.composite(
            AnimationCurveSerializers.VECTOR3F.value().streamCodec,
            instance -> instance.position,
            AnimationCurveSerializers.VECTOR3F.value().streamCodec,
            instance -> instance.rotation,
            AnimationCurveSerializers.FLOAT.value().streamCodec,
            instance -> instance.scale,
            AnimationCurveSerializers.VECTOR4F.value().streamCodec,
            instance -> instance.color,
            (position, rotation, scale, color) ->
            new SpatialAnimationData((Vector3fAnimationCurve) position, (Vector3fAnimationCurve) rotation, (FloatAnimationCurve) scale, (Vector4fAnimationCurve) color)
    );

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_SPATIAL_ANIMATION_PROPERTIES = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(Arrays.stream(Property.values()).map(Property::getName), suggestionsBuilder);
    public enum Property {
        POSITION("position"),
        ROTATION("rotation"),
        SCALE("scale"),
        COLOR("color");

        private final String name;

        Property(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private Vector3fAnimationCurve position;
    private Vector3fAnimationCurve rotation;
    private FloatAnimationCurve scale;
    private Vector4fAnimationCurve color;

    public SpatialAnimationData() {
        this(new Vector3fAnimationCurve(), new Vector3fAnimationCurve(), new FloatAnimationCurve(), new Vector4fAnimationCurve());
    }

    public SpatialAnimationData(Vector3fAnimationCurve position, Vector3fAnimationCurve rotation, FloatAnimationCurve scale, Vector4fAnimationCurve color) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.color = color;
    }

    public void set(SpatialAnimationData other) {
        this.position = other.position;
        this.rotation = other.rotation;
        this.scale = other.scale;
        this.color = other.color;
    }

    public float getDuration() {
        return Math.max(
                Math.max(this.position.getDuration(), this.rotation.getDuration()),
                Math.max(this.scale.getDuration(), this.color.getDuration()));
    }

    public Vector3fAnimationCurve getPosition() {
        return this.position;
    }

    public <T> SpatialAnimationData addKeyframe(Property property, AnimationCurve.Keyframe<T> keyframe) {
        switch (property) {
            case POSITION:
                if (keyframe.value instanceof Vector3f) {
                    this.position.addKeyframe((AnimationCurve.Keyframe<Vector3f>) keyframe);
                } else {
                    throw new IllegalArgumentException("Value must be a Vector3f");
                }
                break;
            case ROTATION:
                if (keyframe.value instanceof Vector3f) {
                    this.rotation.addKeyframe((AnimationCurve.Keyframe<Vector3f>) keyframe);
                } else {
                    throw new IllegalArgumentException("Value must be a Vector3f");
                }
                break;
            case SCALE:
                if (keyframe.value instanceof Float) {
                    this.scale.addKeyframe((AnimationCurve.Keyframe<Float>) keyframe);
                } else {
                    throw new IllegalArgumentException("Value must be a Float");
                }
                break;
            case COLOR:
                if (keyframe.value instanceof Vector4f) {
                    this.color.addKeyframe((AnimationCurve.Keyframe<Vector4f>) keyframe);
                } else {
                    throw new IllegalArgumentException("Value must be a Vector4f");
                }
                break;
        }
        return this;
    }

    public void removeKeyframe(Property property, float time) {
        get(property).removeKeyframe(time);
    }

    public AnimationCurve<?> get(Property property) {
        return switch (property) {
            case POSITION -> this.position;
            case ROTATION -> this.rotation;
            case SCALE -> this.scale;
            case COLOR -> this.color;
        };
    }

    public AnimationCurve<Float> getScale() {
        return this.scale;
    }

    public Vector3fAnimationCurve getRotation() {
        return this.rotation;
    }

    public Vector4fAnimationCurve getColor() {
        return this.color;
    }

}
