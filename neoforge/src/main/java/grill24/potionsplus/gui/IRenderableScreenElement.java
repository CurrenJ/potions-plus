package grill24.potionsplus.gui;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public interface IRenderableScreenElement {
    void tick(float partialTick, int mouseX, int mouseY);
    void tryRender(GuiGraphics graphics, float partialTick, int mouseX, int mouseY);

    void click(int mouseX, int mouseY);

    void show();
    void hide();
    boolean isVisible();
    boolean isHovering();
    void setTargetPosition(Vector3f targetPosition, RenderableScreenElement.Scope targetPositionScope, boolean instant);

    Rectangle2D getGlobalBounds();
    Vector3f getCurrentPosition();
}
