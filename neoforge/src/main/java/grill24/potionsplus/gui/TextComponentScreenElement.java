package grill24.potionsplus.gui;

import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

public class TextComponentScreenElement extends RenderableScreenElement {
    protected Component component;
    private Color currentColor;
    private Color targetColor;

    public TextComponentScreenElement(Screen screen, Settings settings, Color defaultColor, Component component) {
        super(screen, null, settings);

        this.component = component;
        this.currentColor = defaultColor;
        this.targetColor = defaultColor;
    }

    public Color getColor() {
        return this.currentColor;
    }

    public void setTargetColor(Color targetColor) {
        this.targetColor = targetColor;
    }

    @Override
    protected float getWidth() {
        return screen.getMinecraft().font.width(component);
    }

    @Override
    protected float getHeight() {
        return screen.getMinecraft().font.lineHeight;
    }

    @Override
    protected void onTick(float partialTick) {
        tickColor(partialTick);

        super.onTick(partialTick);
    }

    private void tickColor(float partialTick) {
        // Lerp color
        float lerp = Math.clamp(partialTick * this.settings.animationSpeed(), 0, 1);
        int r = (int) RUtil.lerp(this.currentColor.getRed(), this.targetColor.getRed(), lerp);
        int g = (int) RUtil.lerp(this.currentColor.getGreen(), this.targetColor.getGreen(), lerp);
        int b = (int) RUtil.lerp(this.currentColor.getBlue(), this.targetColor.getBlue(), lerp);
        this.currentColor = new Color(r, g, b);
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Animate and render text
        List<List<Component>> abilitiesComponents = Collections.singletonList(Collections.singletonList(this.component));
        List<Component> animatedComponents = ItemListenersGame.animateComponentText(abilitiesComponents, shownTimestamp);

        for (Component component : animatedComponents) {
            Rectangle2D bounds = getGlobalBounds();

            int x = (int) Math.round(bounds.getMinX());
            int y = (int) Math.round(bounds.getMinY());

            // Render text
            graphics.drawString(this.screen.getMinecraft().font, component,
                    x, y, this.currentColor.getRGB());
        }
    }

    public void setComponent(Component component) {
        this.component = component;
        this.shownTimestamp = ClientTickHandler.total();
    }
}
