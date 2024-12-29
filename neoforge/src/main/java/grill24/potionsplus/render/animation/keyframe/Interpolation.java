package grill24.potionsplus.render.animation.keyframe;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.function.BiFunction;

public class Interpolation {
    /**
     * Apply the easing function to the given value
     * @param mode Easing mode
     * @param t Float value between 0 and 1
     * @return Eased value
     */
    public static float applyEasing(Mode mode, float t) {
        return switch (mode) {
            // Linear
            case LINEAR -> t;
            // Sinusoidal
            case EASE_IN_SINE -> (float) (1 - Math.cos((t * Math.PI) / 2));
            case EASE_OUT_SINE -> (float) Math.sin((t * Math.PI) / 2);
            case EASE_IN_OUT_SINE -> (float) (-0.5 * (Math.cos(Math.PI * t) - 1));
            // Quadratic
            case EASE_IN_QUAD -> t * t;
            case EASE_OUT_QUAD -> -t * (t - 2);
            case EASE_IN_OUT_QUAD -> t < 0.5 ? 2 * t * t : (float) (1 - Math.pow(-2 * t + 2, 2) / 2);
            // Back
            case EASE_IN_BACK -> t * t * ((1.70158f + 1) * t - 1.70158f);
            case EASE_OUT_BACK -> (t - 1) * (t - 1) * ((1.70158f + 1) * (t - 1) + 1.70158f) + 1;
            case EASE_IN_OUT_BACK -> {
                float s = 1.70158f * 1.525f;
                if (t < 0.5) {
                    yield 0.5f * (t * t * ((s + 1) * t - s));
                } else {
                    float postFix = t -= 2;
                    yield 0.5f * (postFix * t * ((s + 1) * t + s) + 2);
                }
            }
            // Elastic
            case EASE_IN_ELASTIC -> {
                if (t == 0) {
                    yield 0;
                }
                if (t == 1) {
                    yield 1;
                }
                float p = 0.3f;
                float s = p / 4;
                yield (float) -(Math.pow(2, 10 * (t -= 1)) * Math.sin((t - s) * (2 * Math.PI) / p));
            }
            case EASE_OUT_ELASTIC -> {
                if (t == 0) {
                    yield 0;
                }
                if (t == 1) {
                    yield 1;
                }
                float p = 0.3f;
                float s = p / 4;
                yield (float) (Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / p) + 1);
            }
            case EASE_IN_OUT_ELASTIC -> {
                if (t == 0) {
                    yield 0;
                }
                if ((t *= 2) == 2) {
                    yield 1;
                }
                float p = 0.3f * 1.5f;
                float s = p / 4;
                if (t < 1) {
                    yield (float) (-0.5 * (Math.pow(2, 10 * (t -= 1)) * Math.sin((t - s) * (2 * Math.PI) / p)));
                }
                yield (float) (Math.pow(2, -10 * (t -= 1)) * Math.sin((t - s) * (2 * Math.PI) / p) * 0.5 + 1);
            }
            // Bounce
            case EASE_IN_BOUNCE -> 1 - applyEasing(Mode.EASE_OUT_BOUNCE, 1 - t);
            case EASE_OUT_BOUNCE -> {
                if (t < 1 / 2.75) {
                    yield 7.5625f * t * t;
                } else if (t < 2 / 2.75) {
                    float postFix = t -= 1.5f / 2.75f;
                    yield 7.5625f * postFix * t + 0.75f;
                } else if (t < 2.5 / 2.75) {
                    float postFix = t -= 2.25f / 2.75f;
                    yield 7.5625f * postFix * t + 0.9375f;
                } else {
                    float postFix = t -= 2.625f / 2.75f;
                    yield 7.5625f * postFix * t + 0.984375f;
                }
            }
            case EASE_IN_OUT_BOUNCE -> t < 0.5 ? 0.5f - 0.5f * applyEasing(Mode.EASE_OUT_BOUNCE, 1 - t * 2) : 0.5f * applyEasing(Mode.EASE_OUT_BOUNCE, t * 2 - 1) + 0.5f;
        };
    }

    public static <T> T interpolate(Mode mode, T a, T b, BiFunction<T, Float, T> multiply, BiFunction<T, T, T> add, float t, boolean clamped) {
        float time = t;
        if (clamped) {
            time = Math.min(1, Math.max(0, time));
        }
        time = applyEasing(mode, time);
        return add.apply(a, multiply.apply(add.apply(b, multiply.apply(a, -1F)), time));
    }

    public static float interpolate(Mode mode, float a, float b, float t, boolean clamped) {
        return interpolate(mode, a, b, (f, f1) -> f * f1, Float::sum, t, clamped);
    }

    public static final SuggestionProvider<CommandSourceStack> INTERPOLATION_COMMAND_SUGGESTIONS = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(Arrays.stream(Interpolation.Mode.values()).map(Interpolation.Mode::name).map(String::toLowerCase), suggestionsBuilder);
    public enum Mode implements StringRepresentable {
        LINEAR("linear"),
        EASE_IN_SINE("easeInSine"),
        EASE_OUT_SINE("easeOutSine"),
        EASE_IN_OUT_SINE("easeInOutSine"),
        EASE_IN_QUAD("easeInQuad"),
        EASE_OUT_QUAD("easeOutQuad"),
        EASE_IN_OUT_QUAD("easeInOutQuad"),
        EASE_IN_BACK("easeInBack"),
        EASE_OUT_BACK("easeOutBack"),
        EASE_IN_OUT_BACK("easeInOutBack"),
        EASE_IN_ELASTIC("easeInElastic"),
        EASE_OUT_ELASTIC("easeOutElastic"),
        EASE_IN_OUT_ELASTIC("easeInOutElastic"),
        EASE_IN_BOUNCE("easeInBounce"),
        EASE_OUT_BOUNCE("easeOutBounce"),
        EASE_IN_OUT_BOUNCE("easeInOutBounce");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
