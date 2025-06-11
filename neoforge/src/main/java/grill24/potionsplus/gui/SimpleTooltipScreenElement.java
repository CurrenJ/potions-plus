package grill24.potionsplus.gui;

import grill24.potionsplus.render.animation.keyframe.AnimationCurve;
import grill24.potionsplus.render.animation.keyframe.FloatAnimationCurve;
import grill24.potionsplus.render.animation.keyframe.Interpolation;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.function.Supplier;

public class SimpleTooltipScreenElement extends DivScreenElement<TextComponentScreenElement> {
    private static final AnimationCurve<Float> shownAnimation = new FloatAnimationCurve();
    private static final AnimationCurve<Float> shownAnimationLinear = new FloatAnimationCurve();
    static {
        shownAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(0F)
                .value(0F)
                .interp(Interpolation.Mode.EASE_OUT_BACK)
                .build());
        shownAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(20F)
                .value(1F)
                .build());

        shownAnimationLinear.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(0F)
                .value(0F)
                .interp(Interpolation.Mode.EASE_OUT_QUAD)
                .build());
        shownAnimationLinear.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(5F)
                .value(1F)
                .build());
    }

    private static final AnimationCurve<Float> hiddenAnimation = new FloatAnimationCurve();
    private static final AnimationCurve<Float> hiddenAnimationLinear = new FloatAnimationCurve();
    static {
        hiddenAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(0F)
                .value(1F)
                .interp(Interpolation.Mode.EASE_OUT_QUAD)
                .build());
        hiddenAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(3F)
                .value(0F)
                .build());

        hiddenAnimationLinear.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(0F)
                .value(1F)
                .interp(Interpolation.Mode.EASE_OUT_QUAD)
                .build());
        hiddenAnimationLinear.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(3F)
                .value(0F)
                .build());
    }

    private Color textColor;
    private Supplier<List<List<Component>>> tooltipTextSupplier;

    public SimpleTooltipScreenElement(Screen screen, Settings settings, Color defaultColor, Supplier<List<List<Component>>> tooltipTextSupplier) {
        super(screen, null, settings.withAnimationSpeed(1.0F), Anchor.CENTER, new TextComponentScreenElement(screen, Settings.DEFAULT.withAnimationSpeed(1.0F), new Color(0, 0, 0, 0), tooltipTextSupplier.get()));

        getChild().setTargetPosition(new Vector3f(), Scope.LOCAL, true);

        this.outlineWidth = 0F;
        this.outlineHeight = 0F;
        this.textColor = defaultColor;
        this.tooltipTextSupplier = tooltipTextSupplier;
    }

    public SimpleTooltipScreenElement(Screen screen, Settings settings, Color defaultColor, Component component) {
        this(screen, settings, defaultColor, () -> List.of(List.of(component)));
    }

    public void update() {
        getChild().setComponent(this.tooltipTextSupplier.get(), false);
    }

    @Override
    public float getWidth() {
        float width = super.getWidth();
        width += this.outlineWidth * 4;
        return width * getAnimationProgress(shownAnimationLinear, hiddenAnimationLinear);
    }

    @Override
    public float getHeight() {
        float height = super.getHeight();
        height += this.outlineHeight * 4;
        return height * getAnimationProgress(shownAnimationLinear, hiddenAnimationLinear);
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        super.onTick(partialTick, mouseX, mouseY);

        if (this.shownTimestamp == -1) {
            this.getChild().setTargetColor(new Color(1F, 1F, 1F, 0F));
        } else {
            this.getChild().setTargetColor(textColor);
        }

        this.getChild().settings = this.getChild().settings.withMaxWidth(getWidth()).withMaxHeight(getHeight());
    }

    @Override
    public void show() {
        if (!this.getChild().components.isEmpty()) {
            super.show();
        }
    }

    @Override
    public void hide(boolean playHideAnimation) {
        if (!this.getChild().components.isEmpty()) {
            super.hide(false);
        }

        super.hide(playHideAnimation);
    }

    private float outlineWidth;
    private float outlineHeight;

    private static final int OUTLINE_COLOR_DARK = RUtil.invertColor(ARGB.color(255, 85, 85, 85));
    private static final int OUTLINE_COLOR_LIGHT = RUtil.invertColor(ARGB.color(255, 255, 255, 255));
    private static final int BACKGROUND_COLOR = RUtil.invertColor(ARGB.color(255, 198, 198, 198));
    private static final int OUTLINE_Z = 110;
    private static final int FILL_Z = 109;
    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        Rectangle2D bounds = getGlobalBounds();
        float minX = (float) bounds.getMinX();
        float minY = (float) bounds.getMinY();
        float width = (float) bounds.getWidth();
        float height = (float) bounds.getHeight();

        float animationProgress = getAnimationProgress(shownAnimationLinear, hiddenAnimationLinear);
        this.outlineWidth = animationProgress;
        this.outlineHeight = animationProgress;

        // Draw outline
        IGuiGraphicsExtension graphicsMixin = (IGuiGraphicsExtension) graphics;
        graphicsMixin.potions_plus$fill(minX, minY, minX + this.outlineWidth, minY + height, 0, OUTLINE_Z, OUTLINE_COLOR_DARK);
        graphicsMixin.potions_plus$fill(minX, minY, minX + width, minY + this.outlineHeight, 0, OUTLINE_Z, OUTLINE_COLOR_DARK);
        graphicsMixin.potions_plus$fill(minX + width - this.outlineWidth, minY, minX + width, minY + height, 0, OUTLINE_Z, OUTLINE_COLOR_LIGHT);
        graphicsMixin.potions_plus$fill(minX, minY + height - this.outlineHeight, minX + width, minY + height, 0, OUTLINE_Z, OUTLINE_COLOR_LIGHT);

        // Fill background
        graphicsMixin.potions_plus$fill(minX, minY, minX + width, minY + height, 0, FILL_Z, BACKGROUND_COLOR);

        // Render tooltip text in front of the background.
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, OUTLINE_Z);
        super.render(graphics, partialTick, mouseX, mouseY);
        graphics.pose().popPose();
    }
}
