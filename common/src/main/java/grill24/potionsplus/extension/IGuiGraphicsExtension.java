package grill24.potionsplus.extension;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Vector2f;

import java.util.function.Function;

public interface IGuiGraphicsExtension {
    void potions_plus$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);

    void potions_plus$fill(float minX, float minY, float maxX, float maxY, float rotationDegrees, int z, int color);

    void potions_plus$fill(float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);

    void potions_plus$fill(float minX, float minY, float maxX, float maxY, int color);

    int potions_plus$drawString(Font font, Component text, float x, float y, int color);

    int potions_plus$drawString(Font font, Component text, float x, float y, int color, boolean dropShadow);

    void potions_plus$blit(Function<Identifier, RenderType> renderTypeGetter, Identifier atlasLocation, float x, float y, float uOffset, float vOffset, float uWidth, float vHeight, int width, int height, int textureWidth, int textureHeight, int color);

    void potions_plus$fillQuad(RenderType renderType, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int z, int color);

    void potions_plus$setShaderColor(int color);
}
