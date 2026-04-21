package grill24.potionsplus.render.animation.keyframe;

import grill24.potionsplus.gui.skill.SkillsScreen;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static grill24.potionsplus.utility.Utility.ppId;

public class SpatialAnimations {
    public static final Map<ResourceLocation, SpatialAnimationData> CURVES = new HashMap<>();

    /**
     * Used in {@link grill24.potionsplus.render.animation.WheelItemActivationAnimation}
     */
    public static ResourceLocation WHEEL_WINNER = register(ppId("wheel_winner"), new SpatialAnimationData()
            // Scale
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(180).value(1F).interp(Interpolation.Mode.EASE_OUT_BOUNCE).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(200).value(1.25F).interp(Interpolation.Mode.EASE_OUT_BOUNCE).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(230).value(1.25F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(240).value(0F).build())
            // Rotation
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(180).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_IN_OUT_SINE).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(200).value(new Vector3f(0, 1080, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(230).value(new Vector3f(0, 1080, 0)).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(240).value(new Vector3f(0, 1440, 0)).build())
    );
    public static ResourceLocation WHEEL_LOSERS = register(ppId("wheel_losers"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(180).value(1F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(190).value(0F).build())
            // Rotation
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(190).value(new Vector3f(0, 1080, 0)).build())
    );
    public static ResourceLocation WHEEL_SPIN = register(ppId("wheel_spin"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(180).value(new Vector3f(1F, 1F, 1F)).build())
    );
    public static ResourceLocation WHEEL_POSITION_OFFSET = register(ppId("wheel_position_offset"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 1F, 0)).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(20).value(new Vector3f(0, 0, 0)).build())
    );

    /**
     * Used in {@link SkillsScreen}
     */
    public static ResourceLocation SKILL_ICON_HOVER = register(ppId("skill_icon_hover"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).value(1F).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(1F).value(1.5F).build())
    );
    public static ResourceLocation SKILL_ICON_UNHOVER = register(ppId("skill_icon_unhover"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).value(1.5F).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(1F).value(1F).build())
    );

    public static ResourceLocation SKILL_ICON_WHEEL_SELECTED = register(ppId("skill_icon_wheel_selected"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, 0, 0)).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(1F).value(new Vector3f(0, -0.2F, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).value(0.33F).interp(Interpolation.Mode.EASE_OUT_BOUNCE).build()) // Scale used for wheel radius in SkillsScreen
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(1F).value(0.17F).build()) // Scale used for wheel radius in SkillsScreen
    );
    public static ResourceLocation SKILL_ICON_WHEEL_DESELECTED = register(ppId("skill_icon_wheel_deselected"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).value(new Vector3f(0, -0.2F, 0)).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.POSITION, AnimationCurve.Keyframe.<Vector3f>builder().time(1F).value(new Vector3f(0, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).value(0.17F).build()) // Scale used for wheel radius in SkillsScreen
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(1F).value(0.33F).build()) // Scale used for wheel radius in SkillsScreen
    );

    public static ResourceLocation SCALE_IN_BACK = register(ppId("scale_in_back"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).value(0F).interp(Interpolation.Mode.EASE_OUT_BACK).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(1F).value(1F).build())
    );
    public static ResourceLocation SCALE_OUT_QUAD = register(ppId("scale_out_back"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).value(1F).interp(Interpolation.Mode.EASE_OUT_QUAD).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(1F).value(0F).build())
    );

    // Skills Journal Ability Buttons Wobble
    public static ResourceLocation BUTTON_WOBBLE = register(ppId("button_wobble"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(new Vector3f(0, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(2).interp(Interpolation.Mode.EASE_OUT_QUAD).value(new Vector3f(8, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(4).interp(Interpolation.Mode.EASE_OUT_QUAD).value(new Vector3f(-8, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(6).value(new Vector3f(0, 0, 0)).build())
    );

    public static ResourceLocation FISHING_REWARD_WIN_SPIN = register(ppId("fishing_reward_win_spin"), new SpatialAnimationData()
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(0).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(new Vector3f(0, 0, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.ROTATION, AnimationCurve.Keyframe.<Vector3f>builder().time(15).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(new Vector3f(0, 720, 0)).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(0).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(1F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(20).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(5F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(70).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(5F).build())
            .addKeyframe(SpatialAnimationData.Property.SCALE, AnimationCurve.Keyframe.<Float>builder().time(110).interp(Interpolation.Mode.EASE_IN_OUT_QUAD).value(0F).build())
    );

    public static ResourceLocation register(ResourceLocation id, SpatialAnimationData curve) {
        CURVES.put(id, curve);
        return id;
    }

    public static SpatialAnimationData get(ResourceLocation id) {
        return CURVES.get(id);
    }

    public static SpatialAnimationData getOrDefault(ResourceLocation id, SpatialAnimationData defaultCurve) {
        return CURVES.getOrDefault(id, defaultCurve);
    }

    public static SpatialAnimationData computeIfAbsent(ResourceLocation id, SpatialAnimationData defaultCurve) {
        return CURVES.computeIfAbsent(id, k -> defaultCurve);
    }

    public static Collection<ResourceLocation> getIds() {
        return CURVES.keySet();
    }
}
