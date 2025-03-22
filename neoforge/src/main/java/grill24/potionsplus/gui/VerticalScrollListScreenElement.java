package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;

public class VerticalScrollListScreenElement<E extends RenderableScreenElement> extends VerticalListScreenElement<E> {
    @SafeVarargs
    public VerticalScrollListScreenElement(Screen screen, Settings settings, XAlignment alignment, float paddingBetweenElements, E... elements) {
        super(screen, settings, alignment, paddingBetweenElements, elements);

        this.scrollListeners.add((x, y, delta, element) -> scroll(delta));
    }

    @SafeVarargs
    public VerticalScrollListScreenElement(Screen screen, Settings settings, XAlignment alignment, E... elements) {
        super(screen, settings, alignment, elements);

        this.scrollListeners.add((x, y, delta, element) -> scroll(delta));
    }

    private void scroll(double scrollDelta) {
        this.offsetY += (float) scrollDelta * 5;
    }
}
