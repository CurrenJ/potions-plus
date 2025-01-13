package grill24.potionsplus.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ColoredRectangleScreenElement extends RenderableScreenElement {
    private float width;
    private float height;
    private Color color;

    public ColoredRectangleScreenElement(Screen screen, @Nullable RenderableScreenElement parent, float width, float height, Color color) {
        super(screen, parent, Settings.DEFAULT);

        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    protected float getWidth() {
        return this.width;
    }

    @Override
    protected float getHeight() {
        return this.height;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        Rectangle2D bounds = getGlobalBounds();

        int x = Math.round((float) bounds.getMinX());
        int y = Math.round((float) bounds.getMinY());
        int maxX = Math.round((float) bounds.getMaxX());
        int maxY = Math.round((float) bounds.getMaxY());
        graphics.fill(
                x,
                y,
                maxX,
                maxY,
                this.color.getRGB()
        );
    }
}
