package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.ColoredRectangleRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin implements IGuiGraphicsExtension {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @Shadow
    @Final
    public GuiRenderState guiRenderState;

    @Shadow
    @Final
    public GuiGraphicsExtractor.ScissorStack scissorStack;

    private static final float PIX = 16;

    @Override
    public void potions_plus$renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor, int seed, float guiOffset) {
        if (!stack.isEmpty()) {
            this.pose.pushMatrix();
            this.pose.translate(x, y);

            float actualScale = PIX * scale;
            float xOffset = switch (anchor.xAlignment()) {
                case LEFT -> actualScale / 2F;
                case CENTER -> 0;
                case RIGHT -> -actualScale / 2F;
            };
            float yOffset = switch (anchor.yAlignment()) {
                case TOP -> actualScale / 2F;
                case CENTER -> 0;
                case BOTTOM -> -actualScale / 2F;
            };
            this.pose.translate(xOffset, yOffset);

            if (rotation.z() != 0) {
                this.pose.rotate(rotation.z() * (float) Math.PI / 180.0F);
            }

            this.pose.scale(actualScale, -actualScale);

            TrackingItemStackRenderState renderState = new TrackingItemStackRenderState();
            this.minecraft.getItemModelResolver().updateForTopItem(renderState, stack, ItemDisplayContext.GUI, level, entity, seed);
            this.guiRenderState.addItem(new GuiItemRenderState(new Matrix3x2f(this.pose), renderState, 0, 0, this.scissorStack.peek()));

            this.pose.popMatrix();
        }
    }

    @Override
    public void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor) {
        this.potions_plus$renderItem(this.minecraft.player, this.minecraft.level, stack, rotation, x, y, scale, anchor, 0, 0);
    }

    @Override
    public void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float zOffset, float scale, RenderableScreenElement.Anchor anchor) {
        this.potions_plus$renderItem(this.minecraft.player, this.minecraft.level, stack, rotation, x, y, scale, anchor, 0, zOffset);
    }

    @Override
    public void potions_plus$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color) {
        this.pose.pushMatrix();
        this.pose.translate(origin.x(), origin.y());
        if (rotationDegrees != 0) {
            this.pose.rotate(rotationDegrees * (float) Math.PI / 180.0F);
        }
        this.pose.translate(-origin.x(), -origin.y());

        this.guiRenderState.addGuiElement(new ColoredRectangleRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(),
                new Matrix3x2f(this.pose),
                (int) minX, (int) minY, (int) maxX, (int) maxY,
                color, color, this.scissorStack.peek()));

        this.pose.popMatrix();
    }

    @Override
    public void potions_plus$fill(float minX, float minY, float maxX, float maxY, float rotationDegrees, int z, int color) {
        Vector2f center = new Vector2f(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2);
        this.potions_plus$fill(null, minX, minY, maxX, maxY, center, rotationDegrees, z, color);
    }

    @Override
    public void potions_plus$fill(float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color) {
        this.potions_plus$fill(null, minX, minY, maxX, maxY, origin, rotationDegrees, z, color);
    }

    @Override
    public void potions_plus$fill(float minX, float minY, float maxX, float maxY, int color) {
        this.guiRenderState.addGuiElement(new ColoredRectangleRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(),
                new Matrix3x2f(this.pose),
                (int) minX, (int) minY, (int) maxX, (int) maxY,
                color, color, this.scissorStack.peek()));
    }

    @Override
    public int potions_plus$drawString(Font font, Component text, float x, float y, int color) {
        return this.potions_plus$drawString(font, text, x, y, color, true);
    }

    @Override
    public int potions_plus$drawString(Font font, Component text, float x, float y, int color, boolean dropShadow) {
        this.guiRenderState.addText(new GuiTextRenderState(
                font, text.getVisualOrderText(), new Matrix3x2f(this.pose),
                (int) x, (int) y, color, 0, dropShadow, false, this.scissorStack.peek()));
        return (int) x + font.width(text);
    }

    @Override
    public void potions_plus$blit(Function<Identifier, RenderType> renderTypeGetter, Identifier atlasLocation, float x, float y, float uOffset, float vOffset, float uWidth, float vHeight, int width, int height, int textureWidth, int textureHeight, int color) {
        AbstractTexture texture = this.minecraft.getTextureManager().getTexture(atlasLocation);
        this.guiRenderState.addGuiElement(new BlitRenderState(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()),
                new Matrix3x2f(this.pose),
                (int) x, (int) (x + uWidth), (int) y, (int) (y + vHeight),
                (uOffset) / (float) textureWidth,
                (uOffset + width) / (float) textureWidth,
                (vOffset) / (float) textureHeight,
                (vOffset + height) / (float) textureHeight,
                color, this.scissorStack.peek()));
    }

    @Override
    public void potions_plus$fillQuad(RenderType renderType, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int z, int color) {
        float minX = Math.min(Math.min(Math.min(x1, x2), x3), x4);
        float minY = Math.min(Math.min(Math.min(y1, y2), y3), y4);
        float maxX = Math.max(Math.max(Math.max(x1, x2), x3), x4);
        float maxY = Math.max(Math.max(Math.max(y1, y2), y3), y4);
        this.guiRenderState.addGuiElement(new ColoredRectangleRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(),
                new Matrix3x2f(this.pose),
                (int) minX, (int) minY, (int) maxX, (int) maxY,
                color, color, this.scissorStack.peek()));
    }

    @Override
    public void potions_plus$setShaderColor(int color) {
        // No-op in MC 26.1.2. Color is embedded in render state.
    }
}
