package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public class VerticalListScreenElement<E extends RenderableScreenElement> extends ScreenElementWithChildren<E> {
    private final XAlignment alignment;
    protected final float paddingBetweenElements;

    private float height;
    protected float offsetY;

    @SafeVarargs
    public VerticalListScreenElement(Screen screen, Settings settings, XAlignment alignment, E... elements) {
        super(screen, null, settings, elements);

        this.alignment = alignment;
        this.paddingBetweenElements = 0;
    }

    @SafeVarargs
    public VerticalListScreenElement(Screen screen, Settings settings, XAlignment alignment, float paddingBetweenElements, E... elements) {
        super(screen, null, settings, elements);

        this.alignment = alignment;
        this.paddingBetweenElements = paddingBetweenElements;
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        float yOffset = getOffsetY();
        float height = 0;
        for (RenderableScreenElement element : getChildren()) {
            Rectangle2D childBounds = element.getGlobalBounds();
            float childWidth = (float) childBounds.getWidth();

            int xOffset = switch (alignment) {
                case LEFT -> 0;
                case CENTER -> (int) (getWidth() - childWidth) / 2;
                case RIGHT -> (int) (getWidth() - childWidth);
            };

            element.setTargetPosition(new Vector3f(xOffset, yOffset, 0), Scope.LOCAL, false);
            yOffset += (int) childBounds.getHeight();
            yOffset += paddingBetweenElements;

            height += (int) childBounds.getHeight();
            height += paddingBetweenElements;
        }
        this.height = height;

        super.onTick(partialTick, mouseX, mouseY);
    }

    @Override
    protected float getWidth() {
        // Get the maximum width of all elements
        return getChildren().stream()
                .filter(IRenderableScreenElement::isVisible)
                .map(IRenderableScreenElement::getGlobalBounds)
                .map(bounds -> (float) bounds.getWidth())
                .max(Float::compareTo).orElse(0F);
    }

    @Override
    protected float getHeight() {
        // Get the sum of the heights of all elements
        return this.height;
    }

    protected float getOffsetY() {
        return offsetY;
    }
}
