
package grill24.potionsplus.gui;

@FunctionalInterface
public interface DragListener {
    void onDrag(double mouseX, double mouseY, int button, double dragX, double dragY, IRenderableScreenElement element);
}
