package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class FixedSizeDivScreenElement<E extends RenderableScreenElement> extends DivScreenElement<E> {

    protected float width;
    protected float height;

    public FixedSizeDivScreenElement(Screen screen, Settings settings, Anchor childAlignment, @Nullable E child, float width, float height) {
        super(screen, null, settings, childAlignment, child);

        this.width = width;
        this.height = height;
    }

    @Override
    protected float getWidth() {
        return this.width;
    }

    @Override
    protected float getHeight() {
        return this.height;
    }
}
