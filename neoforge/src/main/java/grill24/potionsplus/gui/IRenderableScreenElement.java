package grill24.potionsplus.gui;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public interface IRenderableScreenElement {
    void tick(float partialTick, int mouseX, int mouseY);
    void tryRender(GuiGraphics graphics, float partialTick, int mouseX, int mouseY);

    void tryClick(int mouseX, int mouseY, int button);
    void onClick(int mouseX, int mouseY, int button);
    void tryScroll(int mouseX, int mouseY, double scrollDelta);
    void onScroll(int mouseX, int mouseY, double scrollDelta);
    void tryDrag(double mouseX, double mouseY, int button, double dragX, double dragY);
    void onDrag(double mouseX, double mouseY, int button, double dragX, double dragY);

    void show();
    void hide(boolean hidePlayAnimation);
    boolean isVisible();
    boolean isHovering();
    float getMouseEnterTimestamp();
    float getMouseExitTimestamp();
    void setTargetPosition(Vector3f targetPosition, RenderableScreenElement.Scope targetPositionScope, boolean instant);
    void setCurrentScale(float scale);

    Rectangle2D getGlobalBounds();
    Vector3f getCurrentPosition();
    float getCurrentScale();
}
