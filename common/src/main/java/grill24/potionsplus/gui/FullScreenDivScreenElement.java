package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;

public class FullScreenDivScreenElement<E extends RenderableScreenElement> extends DivScreenElement<E> {
    public FullScreenDivScreenElement(Screen screen, Settings settings, Anchor childAlignment, E child) {
        super(screen, null, settings, childAlignment, child);
    }

    @Override
    protected float getWidth() {
        return screen.width;
    }

    @Override
    protected float getHeight() {
        return screen.height;
    }
}
