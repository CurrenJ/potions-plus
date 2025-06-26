package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class VerticalScrollListScreenElement<E extends RenderableScreenElement> extends VerticalListScreenElement<E> {
    private final Supplier<Boolean> canScroll;

    @SafeVarargs
    public VerticalScrollListScreenElement(Screen screen, Settings settings, XAlignment alignment, float paddingBetweenElements, E... elements) {
        super(screen, settings, alignment, paddingBetweenElements, elements);

        this.scrollListeners.add((x, y, delta, element) -> scroll(delta));
        this.canScroll = () -> true;
    }

    @SafeVarargs
    public VerticalScrollListScreenElement(Screen screen, Settings settings, XAlignment alignment, Supplier<Boolean> canScroll, E... elements) {
        super(screen, settings, alignment, elements);
        this.canScroll = canScroll;

        this.scrollListeners.add((x, y, delta, element) -> scroll(delta));
    }

    private void scroll(double scrollDelta) {
        if (this.canScroll.get()) {
            this.offsetY += (float) scrollDelta * 5;
        }
    }
}
