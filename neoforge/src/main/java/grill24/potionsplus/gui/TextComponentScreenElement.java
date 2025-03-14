package grill24.potionsplus.gui;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.render.animation.keyframe.AnimationCurve;
import grill24.potionsplus.render.animation.keyframe.FloatAnimationCurve;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;

public class TextComponentScreenElement extends RenderableScreenElement {
    protected Component component;

    protected float scale;

    private Color currentColor;
    private Color targetColor;

    private FloatAnimationCurve shownAnimation;
    private FloatAnimationCurve hiddenAnimation;

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, Component component) {
        super(screen, null, settings);

        this.component = component;
        this.scale = 1F;
        this.currentColor = defaultColor;
        this.targetColor = defaultColor;

        this.shownAnimation = null;
        this.hiddenAnimation = null;
    }

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, Component component, FloatAnimationCurve shownAnimation, FloatAnimationCurve hiddenAnimation) {
        super(screen, null, settings);

        this.component = component;
        this.currentColor = defaultColor;
        this.targetColor = defaultColor;

        this.shownAnimation = shownAnimation;
        this.hiddenAnimation = hiddenAnimation;
    }

    public Color getColor() {
        return this.currentColor;
    }

    public void setTargetColor(Color targetColor) {
        this.targetColor = targetColor;
    }

    @Override
    protected float getWidth() {
        float width = screen.getMinecraft().font.width(component) * this.scale;
        return Float.max(Float.min(width, settings.maxWidth()), settings.minWidth());
    }

    @Override
    protected float getHeight() {
        float height = getWidth() == 0 ? 0 : screen.getMinecraft().font.lineHeight * this.scale;
        return Float.max(Float.min(height, settings.maxHeight()), settings.minHeight());
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        tickColor(partialTick);

        super.onTick(partialTick, mouseX, mouseY);
    }

    private void tickColor(float partialTick) {
        // Lerp color
        float lerp = Math.clamp(partialTick * this.settings.animationSpeed(), 0, 1);
        int r = (int) RUtil.lerp(this.currentColor.getRed(), this.targetColor.getRed(), lerp);
        int g = (int) RUtil.lerp(this.currentColor.getGreen(), this.targetColor.getGreen(), lerp);
        int b = (int) RUtil.lerp(this.currentColor.getBlue(), this.targetColor.getBlue(), lerp);
        this.currentColor = new Color(r, g, b);
    }

    private static final AnimationCurve<Float> defaultShownAnimation = new FloatAnimationCurve();
    static {
        defaultShownAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(0F)
                .value(0F)
                .build());
        defaultShownAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(10F)
                .value(1F)
                .build());
    }
    private static final AnimationCurve<Float> defaultHiddenAnimation = new FloatAnimationCurve();
    static {
        defaultHiddenAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(0F)
                .value(1F)
                .build());
        defaultHiddenAnimation.addKeyframe(AnimationCurve.Keyframe.<Float>builder()
                .time(5F)
                .value(0F)
                .build());
    }

    public AnimationCurve<Float> getShownAnimation() {
        return shownAnimation != null ? shownAnimation : defaultShownAnimation;
    }

    public AnimationCurve<Float> getHiddenAnimation() {
        return hiddenAnimation != null ? hiddenAnimation : defaultHiddenAnimation;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Animate and render text
        Pair<MutableComponent, Integer> animatedComponent = ItemListenersGame.animateComponentText(Collections.singletonList(this.component), getAnimationProgress(getShownAnimation(), getHiddenAnimation()));

        Rectangle2D bounds = getGlobalBounds();
        float x = (float) bounds.getMinX();
        float y = (float) bounds.getMinY();

        // Render text
        IGuiGraphicsExtension guiGraphics = (IGuiGraphicsExtension) graphics;
        graphics.setColor(1F, 1F, 1F, this.currentColor.getAlpha() / 255F);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(this.scale, this.scale, 1F);
        guiGraphics.potions_plus$drawString(this.screen.getMinecraft().font, animatedComponent.getFirst(),
                0, 0, this.currentColor.getRGB());
        graphics.pose().popPose();
        graphics.setColor(1F, 1F, 1F, 1F);
    }

    public void setComponent(Component component, boolean restartAnimation) {
        this.component = component.copy();
        if (restartAnimation) {
            this.shownTimestamp = ClientTickHandler.total();
        }
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
