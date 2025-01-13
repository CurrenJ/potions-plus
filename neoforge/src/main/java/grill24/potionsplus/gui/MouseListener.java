package grill24.potionsplus.gui;

@FunctionalInterface
public interface MouseListener<E extends IRenderableScreenElement> {
    void onClick(int x, int y, E element);
}
