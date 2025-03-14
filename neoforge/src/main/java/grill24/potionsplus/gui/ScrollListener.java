
package grill24.potionsplus.gui;

@FunctionalInterface
public interface ScrollListener {
    void onScroll(int x, int y, double delta, IRenderableScreenElement element);
}
