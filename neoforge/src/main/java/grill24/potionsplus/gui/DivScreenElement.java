package grill24.potionsplus.gui;

import grill24.potionsplus.utility.Utility;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.geom.Rectangle2D;

public class DivScreenElement<E extends RenderableScreenElement> extends ScreenElementWithChildren<E> {
    protected final Anchor childAlignment;
    protected Vector4f padding = new Vector4f(0, 0, 0, 0); // top, right, bottom, left

    public DivScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Anchor childAlignment, @Nullable E child) {
        super(screen, parent, settings, child);

        this.childAlignment = childAlignment;
    }

    @Override
    protected float getWidth() {
        float width = getChildren().iterator().next().getWidth();
        return width + this.padding.x() + this.padding.z();
    }

    @Override
    protected float getHeight() {
        float height = getChildren().iterator().next().getHeight();
        return height + this.padding.y() + this.padding.w();
    }

    public void setPadding(Vector4f padding) {
        this.padding = padding;
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        Rectangle2D bounds = this.getGlobalBounds();
        float width = (float) bounds.getWidth();
        float height = (float) bounds.getHeight();


        if(!this.getChildren().isEmpty()) {
            RenderableScreenElement child = this.getChildren().iterator().next();
            Rectangle2D childBounds = child.getGlobalBounds();

            float childWidth = (float) childBounds.getWidth();
            float childHeight = (float) childBounds.getHeight();

            float xOffset = switch (this.childAlignment.xAlignment()) {
                case LEFT -> 0;
                case CENTER -> (width - childWidth) / 2;
                case RIGHT -> width - childWidth;
            };
            float yOffset = switch (this.childAlignment.yAlignment()) {
                case TOP -> 0;
                case CENTER -> (height - childHeight) / 2;
                case BOTTOM -> height - childHeight;
            };

            Vector3f targetPosition = new Vector3f(xOffset, yOffset, 0);
            child.setTargetPosition(targetPosition, Scope.LOCAL, false);
        }

        super.onTick(partialTick, mouseX, mouseY);
    }

    protected E getChild() {
        return getChildren().iterator().next();
    }
}
