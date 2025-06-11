package grill24.potionsplus.gui;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.render.animation.keyframe.AnimationCurve;
import grill24.potionsplus.render.animation.keyframe.FloatAnimationCurve;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

public class TextComponentScreenElement extends RenderableScreenElement {
    protected List<List<Component>> components;

    protected float scale;

    private Color currentColor;
    private Color targetColor;

    private FloatAnimationCurve shownAnimation;
    private FloatAnimationCurve hiddenAnimation;

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, Component component) {
        this(screen, settings, defaultColor, component, null, null);

        this.scale = 1F;
    }

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, List<List<Component>> component) {
        this(screen, settings, defaultColor, component, null, null);

        this.scale = 1F;
    }

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, Component component, FloatAnimationCurve shownAnimation, FloatAnimationCurve hiddenAnimation) {
        this(screen, settings, defaultColor, Collections.singletonList(Collections.singletonList(component)), shownAnimation, hiddenAnimation);
    }

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, List<List<Component>> components, FloatAnimationCurve shownAnimation, FloatAnimationCurve hiddenAnimation) {
        super(screen, null, settings);

        this.components = components;
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
        Font font = screen.getMinecraft().font;
        float width = (float) (components.stream().map(row -> row.stream().mapToDouble(font::width).sum()).max(Double::compare).orElse(0D) * this.scale);
        return Float.max(Float.min(width, settings.maxWidth()), settings.minWidth());
    }

    @Override
    protected float getHeight() {
        float height = getWidth() == 0 ? 0 : (float) (components.stream().mapToDouble(this::getRowHeight).sum() * this.scale);
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
        Rectangle2D bounds = getGlobalBounds();
        float x = (float) bounds.getMinX();
        float y = (float) bounds.getMinY();
        for (List<Component> row : components) {
            Pair<MutableComponent, Integer> animatedRow = ItemListenersGame.animateComponentText(row, getAnimationProgress(getShownAnimation(), getHiddenAnimation()));

            // Render text
            IGuiGraphicsExtension guiGraphics = (IGuiGraphicsExtension) graphics;
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(this.scale, this.scale, 1F);
            guiGraphics.potions_plus$drawString(this.screen.getMinecraft().font, animatedRow.getFirst(),
                    0, 0, this.currentColor.getRGB());
            graphics.pose().popPose();

            y += getRowHeight(row);
        }


    }

    public float getRowHeight(List<Component> row) {
        float height = this.screen.getMinecraft().font.lineHeight * this.scale;
        if (row.isEmpty()) {
            height *= 0.5F;
        }
        return height;
    }

    public void setComponent(Component component, boolean restartAnimation) {
        setComponent(List.of(List.of(component)), restartAnimation);
    }

    public void setComponent(List<List<Component>> components, boolean restartAnimation) {
        this.components = components;
        if (restartAnimation) {
            this.shownTimestamp = ClientTickHandler.total();
        }
    }

    public void setCurrentScale(float scale) {
        this.scale = scale;
    }
}
