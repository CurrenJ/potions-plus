package grill24.potionsplus.gui;

import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class ScreenElementWithChildren<E extends RenderableScreenElement> extends RenderableScreenElement {
    private Collection<E> elements;

    @SafeVarargs
    public ScreenElementWithChildren(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, E... elements) {
        super(screen, parent, settings);

        this.elements = Arrays.stream(elements).filter(Objects::nonNull).toList();
        getChildren().forEach(child -> child.parent = this);
    }

    /**
     * Default bounds calculation for a container element:
     * The bounds of the container is the minimum rectangle that contains all children and the origin point of the container.
     * @return The width of the container.
     */
    @Override
    protected float getWidth() {
        Vector3f position = getCurrentPosition();
        float minX = position.x;
        float maxX = position.x;

        for (E child : getChildren()) {
            Rectangle2D bounds = child.getGlobalBounds();
            minX = (float) Math.min(minX, bounds.getMinX());
            maxX = (float) Math.max(maxX, bounds.getMaxX());
        }
        return maxX - minX;
    }

    /**
     * Default bounds calculation for a container element:
     * The bounds of the container is the minimum rectangle that contains all children and the origin point of the container.
     * @return The height of the container.
     */
    @Override
    protected float getHeight() {
        Vector3f position = getCurrentPosition();
        float minY = position.y;
        float maxY = position.y;

        for (E child : getChildren()) {
            Rectangle2D bounds = child.getGlobalBounds();
            minY = (float) Math.min(minY, bounds.getMinY());
            maxY = (float) Math.max(maxY, bounds.getMaxY());
        }
        return maxY - minY;
    }


    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        super.onTick(partialTick, mouseX, mouseY);

        for (RenderableScreenElement child : getChildren()) {
            if(child != null) {
                child.tick(partialTick, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        for (IRenderableScreenElement child : getChildren()) {
            child.tryRender(graphics, partialTick, mouseX, mouseY);
        }
    }

    @Override
    public void tryClick(int mouseX, int mouseY, int button) {
        if (!this.allowClicksOutsideBounds && !getGlobalBounds().contains(mouseX, mouseY)) {
            return;
        }

        onClick(mouseX, mouseY, button);
        for (E element : getChildren()) {
            element.tryClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void tryScroll(int mouseX, int mouseY, double scrollDelta) {
        if (!this.allowClicksOutsideBounds && !getGlobalBounds().contains(mouseX, mouseY)) {
            return;
        }

        onScroll(mouseX, mouseY, scrollDelta);
        for (E element : getChildren()) {
            element.tryScroll(mouseX, mouseY, scrollDelta);
        }
    }

    @Override
    public void tryDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.allowClicksOutsideBounds && !getGlobalBounds().contains(mouseX, mouseY)) {
            return;
        }

        onDrag(mouseX, mouseY, button, dragX, dragY);
        for (E element : getChildren()) {
            element.tryDrag(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    protected void updateHover(int mouseX, int mouseY) {
        boolean hovering = getGlobalBounds().contains(mouseX, mouseY);
        if (hovering && isVisible()) {
            if (this.mouseEnteredTimestamp == -1) {
                this.mouseEnteredTimestamp = ClientTickHandler.total();
                this.mouseEnterListeners.forEach(listener -> listener.onClick(mouseX, mouseY, 0, this));
                onMouseEnter(mouseX, mouseY);
            }
            this.mouseExitedTimestamp = -1;
        } else {
            if (this.mouseExitedTimestamp == -1) {
                this.mouseExitedTimestamp = ClientTickHandler.total();
                this.mouseEnterListeners.forEach(listener -> listener.onClick(mouseX, mouseY, 0, this));
                onMouseExit(mouseX, mouseY);
            }
            this.mouseEnteredTimestamp = -1;
        }

        // Cascade update children
        for (E child : getChildren()) {
            child.updateHover(mouseX, mouseY);
        }
    }

    @Override
    public void show() {
        super.show();
        for (E child : getChildren()) {
            child.show();
        }
    }

    public void show(boolean recursive) {
        if (recursive) {
            show();
        } else {
            super.show();
        }
    }

    @Override
    public void hide(boolean playHideAnimation) {
        super.hide(playHideAnimation);
        for (E child : getChildren()) {
            child.hide(playHideAnimation);
        }
    }

    public void hide(boolean playHideAnimation, boolean recursive) {
        if (recursive) {
            hide(playHideAnimation);
        } else {
            super.hide(playHideAnimation);
        }
    }

    protected Collection<E> getChildren() {
        return elements;
    }

    protected void setChildren(Collection<E> elements) {
        this.elements = elements;
        getChildren().forEach(child -> child.parent = this);
    }

    @Override
    public void snapToTarget() {
        // Children first because parent should be able to adjust to children size and position
        for (E child : getChildren()) {
            child.snapToTarget();
        }

        super.snapToTarget();
    }

    @Override
    public void setShowBounds(boolean showBounds) {
        super.setShowBounds(showBounds);
        for (E child : getChildren()) {
            child.setShowBounds(showBounds);
        }
    }

    @Override
    public void setShowGridLines(boolean showGridLines) {
        super.setShowGridLines(showGridLines);
        for (E child : getChildren()) {
            child.setShowGridLines(showGridLines);
        }
    }
}
