package grill24.potionsplus.gui;

@FunctionalInterface
public interface MouseListener {
    void onClick(int x, int y, int button, IRenderableScreenElement element);
}
