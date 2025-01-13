package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public class DivScreenElement<E extends RenderableScreenElement> extends ScreenElementWithChildren<E> {
    protected final Anchor childAlignment;

    public DivScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Anchor childAlignment, @Nullable E child) {
        super(screen, parent, settings, child);

        this.childAlignment = childAlignment;
    }

    @Override
    protected float getWidth() {
        return getChildren().iterator().next().getWidth();
    }

    @Override
    protected float getHeight() {
        return getChildren().iterator().next().getHeight();
    }

    @Override
    protected void onTick(float partialTick) {
        Rectangle2D bounds = this.getGlobalBounds();
        float width = (float) bounds.getWidth();
        float height = (float) bounds.getHeight();

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

        super.onTick(partialTick);
    }
}
