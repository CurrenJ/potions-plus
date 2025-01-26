package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public class HorizontalListScreenElement<E extends RenderableScreenElement> extends ScreenElementWithChildren<E> {
    private YAlignment alignment;
    private float paddingBetweenElements;

    private float width;

    @SafeVarargs
    public HorizontalListScreenElement(Screen screen, Settings settings, YAlignment alignment, E... elements) {
        super(screen, null, settings, elements);

        this.alignment = alignment;
    }

    @SafeVarargs
    public HorizontalListScreenElement(Screen screen, Settings settings, YAlignment alignment, float paddingBetweenElements, E... elements) {
        super(screen, null, settings, elements);

        this.alignment = alignment;
        this.paddingBetweenElements = paddingBetweenElements;
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        super.onTick(partialTick, mouseX, mouseY);

        Rectangle2D bounds = getGlobalBounds();
        float height = (float) bounds.getHeight();

        this.width = 0;
        for (RenderableScreenElement element : getChildren()) {
            Rectangle2D childBounds = element.getGlobalBounds();
            float childHeight = (float) childBounds.getHeight();

            int yOffset = switch (alignment) {
                case TOP -> 0;
                case CENTER -> (int) (height - childHeight) / 2;
                case BOTTOM -> (int) (height - childHeight);
            };

            element.setTargetPosition(new Vector3f(width, yOffset, 0), Scope.LOCAL, false);
            width += (int) childBounds.getWidth();
            width += paddingBetweenElements;
        }
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        // Get the max of the heights of all elements
        return getChildren().stream()
                .filter(IRenderableScreenElement::isVisible)
                .map(IRenderableScreenElement::getGlobalBounds)
                .map(bounds -> (float) bounds.getHeight())
                .max(Float::compareTo).orElse(0F);
    }
}
